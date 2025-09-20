package org.example.calculator.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.calculator.model.User;

public class MainMenuController {

    private User currentUser;

    public void setUser(User user) {
        this.currentUser = user;
    }

    // Переход в "Закон Ома"
    @FXML
    private void goToOhmsLaw(ActionEvent event) {
        switchScene(event, "/fxml/OhmsLaw.fxml", "Калькулятор: Закон Ома");
    }

    // Переход в "Делитель напряжения"
    @FXML
    private void goToVoltageDivider(ActionEvent event) {
        switchScene(event, "/fxml/VoltageDivider.fxml", "Калькулятор: Делитель напряжения");
    }

    // Переход в "История"
    @FXML
    private void goToHistory(ActionEvent event) {
        switchScene(event, "/fxml/History.fxml", "История вычислений");
    }

    // Выход в Login
    @FXML
    private void logout(ActionEvent event) {
        switchScene(event, "/fxml/Login.fxml", "Вход");
    }

    // 🔑 Универсальный метод для переключения сцен
    private void switchScene(ActionEvent event, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load());

            // Передача пользователя в новый контроллер, если у него есть setUser
            Object controller = loader.getController();
            try {
                controller.getClass().getMethod("setUser", User.class).invoke(controller, currentUser);
            } catch (NoSuchMethodException ignored) {
                // Если у контроллера нет setUser — ничего страшного
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
