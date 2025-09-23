package org.example.calculator.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.calculator.dao.HistoryDao;
import org.example.calculator.model.HistoryEntry;
import org.example.calculator.model.User;

import java.util.List;

public class HistoryController implements UserAwareController {

    private User currentUser;

    @FXML private TableView<HistoryEntry> historyTable;
    @FXML private TableColumn<HistoryEntry, String> colType;
    @FXML private TableColumn<HistoryEntry, String> colParams;
    @FXML private TableColumn<HistoryEntry, String> colResult;
    @FXML private TableColumn<HistoryEntry, String> colDate;

    @Override
    public void setUser(User user) {
        this.currentUser = user;

        // Привязка колонок
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colParams.setCellValueFactory(new PropertyValueFactory<>("inputParams"));
        colResult.setCellValueFactory(new PropertyValueFactory<>("result"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        HistoryDao dao = new HistoryDao();
        List<HistoryEntry> entries;

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            entries = dao.findAll();
        } else {
            entries = dao.findByUser(user.getId());
        }

        ObservableList<HistoryEntry> data = FXCollections.observableArrayList(entries);
        historyTable.setItems(data);
    }

    @FXML
    private void handleClearHistory() {
        try {
            HistoryDao dao = new HistoryDao();
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Вы уверены, что хотите очистить историю?",
                    ButtonType.YES, ButtonType.NO);
            confirm.showAndWait();

            if (confirm.getResult() == ButtonType.YES) {
                if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
                    dao.clearAll();
                } else {
                    dao.clearByUser(currentUser.getId());
                }
                historyTable.getItems().clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
