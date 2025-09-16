package org.example.calculator.ui;

import org.example.calculator.service.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegisterController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleRegister(ActionEvent event) {
        String username = usernameField.getText();
        String pass = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (!pass.equals(confirm)) {
            messageLabel.setText("Пароли не совпадают!");
            return;
        }

        boolean ok = authService.register(username, pass, "USER");
        if (ok) {
            messageLabel.setText("Регистрация успешна!");
            goToLogin(event);
        } else {
            messageLabel.setText("Ошибка регистрации (возможно, имя занято)");
        }
    }

    @FXML
    private void goToLogin(ActionEvent event) {
        openWindow("/fxml/Login.fxml", "Вход");
    }

    private void openWindow(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(title);
            stage.show();

            // Закрываем текущее окно
            ((Stage) usernameField.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
