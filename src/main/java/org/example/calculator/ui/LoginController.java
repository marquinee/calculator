package org.example.calculator.ui;
import org.example.calculator.service.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        var user = authService.login(username, password);
        if (user != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(loader.load()));
                stage.setTitle("Главное меню");

                // передаем пользователя в контроллер
                MainMenuController controller = loader.getController();
                controller.setUser(user);

                stage.show();

                // закрываем окно входа
                ((Stage) usernameField.getScene().getWindow()).close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            messageLabel.setText("Неверный логин или пароль");
        }
    }


    @FXML
    private void goToRegister(ActionEvent event) {
        openWindow("/fxml/Register.fxml", "Регистрация");
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
