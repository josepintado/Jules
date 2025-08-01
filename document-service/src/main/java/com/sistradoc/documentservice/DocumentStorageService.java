package com.sistradoc.documentservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class DocumentStorageService {

    private final Path rootLocation = Paths.get("uploads");

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentMetadataRepository documentMetadataRepository;

    public DocumentStorageService() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    @Transactional
    public void store(MultipartFile file, String motivo, LocalDate fechaRegistro, LocalTime horaRegistro, String ubicacion, String observacion) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }
            Path destinationFile = this.rootLocation.resolve(
                    Paths.get(file.getOriginalFilename()))
                    .normalize().toAbsolutePath();

            Files.copy(file.getInputStream(), destinationFile);

            Document document = new Document();
            document.setFileName(file.getOriginalFilename());
            document.setFilePath(destinationFile.toString());
            document.setFileType(file.getContentType());
            document.setSizeInBytes(file.getSize());
            // Hardcoding user ID for now, this would come from the security context
            document.setCreatedByUserId(1);

            DocumentMetadata metadata = new DocumentMetadata();
            metadata.setMotivo(motivo);
            metadata.setFechaRegistro(fechaRegistro);
            metadata.setHoraRegistro(horaRegistro);
            metadata.setUbicacion(ubicacion);
            metadata.setObservacion(observacion);

            document.setMetadata(metadata);

            documentRepository.save(document);

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }
}
