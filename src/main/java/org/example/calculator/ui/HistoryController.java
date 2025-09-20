package org.example.calculator.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.example.calculator.model.User;

public class HistoryController implements UserAwareController {

    private User currentUser;

    @FXML private TableView<?> historyTable;

    @Override
    public void setUser(User user) {
        this.currentUser = user;
        // TODO: загрузить данные из БД в зависимости от роли (USER / ADMIN)
    }
    @FXML
    private void goBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
            Scene scene = new Scene(loader.load());

            MainMenuController controller = loader.getController();
            controller.setUser(currentUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Главное меню");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

