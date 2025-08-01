package com.sistradoc.frontendvaadin;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("")
@PageTitle("Login | Sistradoc")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm login = new LoginForm();

    public LoginView() {
        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        login.setAction("login");

        add(login);

        login.addLoginListener(e -> {
            String username = e.getUsername();
            String password = e.getPassword();

            // Create AuthRequest object (assuming you have a similar DTO here or build the body directly)
            // For simplicity, building a map directly
            var bodyValues = new java.util.HashMap<String, String>();
            bodyValues.put("username", username);
            bodyValues.put("password", password);

            org.springframework.web.reactive.function.client.WebClient client = org.springframework.web.reactive.function.client.WebClient.create("http://localhost:8080");

            client.post()
                .uri("/api/auth/login")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(bodyValues)
                .retrieve()
                .bodyToMono(AuthResponse.class)
                .subscribe(response -> {
                    getUI().ifPresent(ui -> ui.access(() -> {
                        // Store the token in sessionStorage
                        ui.getPage().executeJs("sessionStorage.setItem('jwt', $0)", response.getJwt());
                        // Navigate to the registration page
                        ui.navigate("register");
                    }));
                }, error -> {
                    getUI().ifPresent(ui -> ui.access(() -> {
                        login.setError(true);
                    }));
                });
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        // inform the user about an authentication error
        if (beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            login.setError(true);
        }
    }
}
