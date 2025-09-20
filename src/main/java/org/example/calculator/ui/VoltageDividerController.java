package org.example.calculator.ui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.calculator.model.User;
import org.example.calculator.service.VoltageDividerService;
import org.example.calculator.service.VoltageDividerService.Result;

public class VoltageDividerController {

    @FXML private TextField vinField;
    @FXML private TextField voutField;
    @FXML private TextField toleranceField;
    @FXML private TextField rminField;
    @FXML private TextField rmaxField;
    @FXML private ChoiceBox<String> seriesChoice;
    @FXML private TableView<Result> resultTable;
    @FXML private TableColumn<Result, String> resistorsCol;
    @FXML private TableColumn<Result, String> voutCol;
    @FXML private TableColumn<Result, String> errorCol;

    private User currentUser;

    private final VoltageDividerService service = new VoltageDividerService();

    public void setUser(User user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {
        // Заполняем выбор рядов
        seriesChoice.setItems(FXCollections.observableArrayList("E6", "E12", "E24", "E96"));
        seriesChoice.setValue("E12");

        // Привязка колонок
        resistorsCol.setCellValueFactory(data -> data.getValue().resistorsProperty());
        voutCol.setCellValueFactory(data -> data.getValue().voutProperty());
        errorCol.setCellValueFactory(data -> data.getValue().errorProperty());
    }

    @FXML
    private void handleCalculate() {
        try {
            double vin = Double.parseDouble(vinField.getText());
            double voutReq = Double.parseDouble(voutField.getText());
            double tol = Double.parseDouble(toleranceField.getText()) / 100.0;
            int rmin = Integer.parseInt(rminField.getText());
            int rmax = Integer.parseInt(rmaxField.getText());
            String series = seriesChoice.getValue();

            resultTable.setItems(FXCollections.observableArrayList(
                    service.calculate(vin, voutReq, tol, series, rmin, rmax)
            ));
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Ошибка ввода: " + e.getMessage());
            alert.showAndWait();
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
