package com.sistradoc.frontendvaadin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;

@Route("") // Mapear esta vista a la ruta raíz
public class RegistrationView extends VerticalLayout {

    public RegistrationView() {
        H1 title = new H1("Registro de Nuevo Expediente");

        // Buffer para recibir el archivo subido en memoria
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("application/pdf", ".pdf");
        upload.setMaxFiles(1);
        upload.setDropLabel(new com.vaadin.flow.component.html.Span("Arrastra el documento aquí"));

        // Formulario para los metadatos
        FormLayout formLayout = new FormLayout();
        TextField motivo = new TextField("Motivo");
        DatePicker fechaRegistro = new DatePicker("Fecha de Registro");
        TimePicker horaRegistro = new TimePicker("Hora de Registro");
        TextField ubicacion = new TextField("Ubicación Física (si aplica)");
        TextArea observacion = new TextArea("Observación");

        formLayout.add(motivo, fechaRegistro, horaRegistro, ubicacion, observacion);
        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("500px", 2)
        );
        formLayout.setColspan(observacion, 2);

        Button submitButton = new Button("Registrar Expediente");
        submitButton.addClickListener(e -> {
            try {
                if (buffer.getFileName() == null || buffer.getFileName().isEmpty()) {
                    com.vaadin.flow.component.notification.Notification.show("Por favor, suba un archivo.");
                    return;
                }

                byte[] fileBytes = buffer.getInputStream().readAllBytes();
                org.springframework.util.LinkedMultiValueMap<String, Object> body = new org.springframework.util.LinkedMultiValueMap<>();

                org.springframework.core.io.ByteArrayResource fileResource = new org.springframework.core.io.ByteArrayResource(fileBytes) {
                    @Override
                    public String getFilename() {
                        return buffer.getFileName();
                    }
                };

                body.add("file", fileResource);
                body.add("motivo", motivo.getValue());
                body.add("fechaRegistro", fechaRegistro.getValue().toString());
                body.add("horaRegistro", horaRegistro.getValue().toString());
                body.add("ubicacion", ubicacion.getValue());
                body.add("observacion", observacion.getValue());

                org.springframework.web.reactive.function.client.WebClient client = org.springframework.web.reactive.function.client.WebClient.create("http://localhost:8080");

                client.post()
                    .uri("/api/documents/upload")
                    .contentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA)
                    .body(org.springframework.web.reactive.function.BodyInserters.fromMultipartData(body))
                    .retrieve()
                    .toBodilessEntity()
                    .subscribe(
                        response -> getUI().ifPresent(ui -> ui.access(() -> com.vaadin.flow.component.notification.Notification.show("Expediente registrado con éxito."))),
                        error -> getUI().ifPresent(ui -> ui.access(() -> com.vaadin.flow.component.notification.Notification.show("Error al registrar: " + error.getMessage())))
                    );

            } catch (java.io.IOException ex) {
                com.vaadin.flow.component.notification.Notification.show("Error al leer el archivo: " + ex.getMessage());
            }
        });

        add(title, upload, formLayout, submitButton);
        setAlignItems(Alignment.CENTER);
    }
}
