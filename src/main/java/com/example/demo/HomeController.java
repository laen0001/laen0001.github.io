package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    @GetMapping(value="/asegui")
    public String modoInicio1(HttpServletRequest req, Model model) {
        model.addAttribute("frase1", "");
        model.addAttribute("frase2", "");
        model.addAttribute("frase3", "");
        model.addAttribute("frase4", "ASEGUI");
        return "home";
    }

    @GetMapping(value="/asociacion")
    public String modoInicio2(HttpServletRequest req, Model model) {
        return "asociacion";
    }

    @GetMapping(value="/nosotros")
    public String modoInicio3(HttpServletRequest req, Model model, HttpSession session) {
        cargarListaDePdfs(req, model); // Cargar la lista de PDFs
        return "nosotros";
    }

    @GetMapping(value="/actividades")
    public String modoInicio4(HttpServletRequest req, Model model,HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        cargarListaDePdfs(req, model); // Cargar la lista de PDFs
        return "actividades";
    }

    @GetMapping(value="/galeria")
    public String modoInicio5(HttpServletRequest req, Model model) {
        return "galeria";
    }

    @GetMapping(value="/contacto")
    public String modoInicio6(HttpServletRequest req, Model model) {
        return "contacto";
    }

    @PostMapping("/uploadPdf")
    public String uploadPdf(@RequestParam("fileToUpload") MultipartFile file, HttpServletRequest request, Model model) {
        if (!file.isEmpty()) {
            String uploadDir = request.getServletContext().getRealPath("/uploads/");
            File uploadDirectory = new File(uploadDir);

            if (!uploadDirectory.exists()) {
                uploadDirectory.mkdirs(); // Crear el directorio si no existe
            }

            try {
                String filePath = uploadDir + file.getOriginalFilename();
                File dest = new File(filePath);
                file.transferTo(dest); // Guardar el archivo en el directorio de uploads

                model.addAttribute("message", "Archivo subido correctamente: " + file.getOriginalFilename());
            } catch (IOException e) {
                model.addAttribute("message", "Error al subir el archivo: " + e.getMessage());
            }
        } else {
            model.addAttribute("message", "Por favor, selecciona un archivo para subir.");
        }

        return "redirect:/reuniones"; // Redirigir a la vista de resúmenes de reuniones
    }

    @GetMapping("/reuniones")
    public String listMeetingSummaries(HttpServletRequest request, Model model) {
        cargarListaDePdfs(request, model);  // Cargar la lista de PDFs cada vez que se accede a "reuniones"
        return "actividades"; // Mostrar la vista con la lista de archivos PDF
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        HttpSession session,
                        Model model) {
        // Puedes implementar aquí una lógica más compleja para validar los usuarios
        if ("admin".equals(username) && "123456".equals(password)) {
            session.setAttribute("user", username);
            return "redirect:/reuniones";
        } else {
            model.addAttribute("error", "Usuario o contraseña incorrectos.");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @PostMapping("/deletePdf")
    public String deletePdf(@RequestParam("fileName") String fileName, HttpServletRequest request, Model model) {
        String uploadDir = request.getServletContext().getRealPath("/uploads/");
        File fileToDelete = new File(uploadDir + fileName);

        if (fileToDelete.exists()) {
            if (fileToDelete.delete()) {
                model.addAttribute("message", "Archivo eliminado correctamente: " + fileName);
            } else {
                model.addAttribute("message", "No se pudo eliminar el archivo: " + fileName);
            }
        } else {
            model.addAttribute("message", "Archivo no encontrado: " + fileName);
        }

        return "redirect:/reuniones"; // Redirigir a la vista de resúmenes de reuniones
    }

    private void cargarListaDePdfs(HttpServletRequest request, Model model) {
        String uploadDir = request.getServletContext().getRealPath("/uploads/");
        File uploadDirectory = new File(uploadDir);

        List<String> pdfFiles = new ArrayList<>();
        if (uploadDirectory.exists() && uploadDirectory.isDirectory()) {
            File[] files = uploadDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
            if (files != null) {
                for (File file : files) {
                    pdfFiles.add(file.getName());
                }
            }
        }

        model.addAttribute("pdfFiles", pdfFiles);
    }
}
