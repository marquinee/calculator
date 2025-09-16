package org.example.calculator.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import org.example.calculator.model.User;

public class MainMenuController {

    private User currentUser; // текущий вошедший пользователь

    public void setUser(User user) {
        this.currentUser = user;
    }

    @FXML
    private void openOhmsLaw(ActionEvent event) {
        openWindow("/fxml/OhmsLaw.fxml", "Калькулятор: Закон Ома");
    }

    @FXML
    private void openVoltageDivider(ActionEvent event) {
        openWindow("/fxml/VoltageDivider.fxml", "Калькулятор делителя напряжения");
    }

    @FXML
    private void openHistory(ActionEvent event) {
        openWindow("/fxml/History.fxml", "История вычислений");
    }

    @FXML
    private void logout(ActionEvent event) {
        openWindow("/fxml/Login.fxml", "Вход");
    }

    private void openWindow(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(title);

            // Если нужно передать пользователя в следующее окно:
            Object controller = loader.getController();
            if (controller instanceof UserAwareController) {
                ((UserAwareController) controller).setUser(currentUser);
            }

            stage.show();
            // закрываем текущее окно
            Stage currentStage = (Stage) stage.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
