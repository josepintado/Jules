package com.sistradoc.documentservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentStorageService documentStorageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("motivo") String motivo,
            @RequestParam("fechaRegistro") LocalDate fechaRegistro,
            @RequestParam("horaRegistro") LocalTime horaRegistro,
            @RequestParam("ubicacion") String ubicacion,
            @RequestParam("observacion") String observacion) {

        try {
            documentStorageService.store(file, motivo, fechaRegistro, horaRegistro, ubicacion, observacion);
            return ResponseEntity.ok("Documento '" + file.getOriginalFilename() + "' guardado exitosamente.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al guardar el documento: " + e.getMessage());
        }
    }
}
