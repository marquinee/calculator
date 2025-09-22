package org.example.calculator.ui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.calculator.dao.HistoryDao;
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
    @FXML private Canvas circuitCanvas;
    @FXML private Button showCircuitButton;


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

        resultTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                drawCircuit(
                        extractR1(newSelection.resistorsProperty().get()),
                        extractR2(newSelection.resistorsProperty().get())
                );
            }
        });

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
            HistoryDao historyDao = new HistoryDao();
            for (Result r : service.calculate(vin, voutReq, tol, series, rmin, rmax)) {
                historyDao.save(
                        currentUser.getId(),
                        "DIVIDER",
                        String.format("Vin=%s, Vout=%s, tol=%s, Rmin=%s, Rmax=%s, series=%s",
                                vin, voutReq, tol*100, rmin, rmax, series),
                        r.getVout() + " В, R=" + r.getResistors()
                );
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Ошибка ввода: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void drawCircuit(String r1Desc, String r2Desc) {
        GraphicsContext gc = circuitCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, circuitCanvas.getWidth(), circuitCanvas.getHeight());

        // Рисуем Vin
        gc.strokeText("Vin", 20, 30);
        gc.strokeLine(50, 20, 100, 20);

        // Верхнее плечо (R1)
        if (r1Desc != null) {
            String[] r1Parts = r1Desc.replace("R1=", "").split("\\+|\\|\\|");
            boolean isParallel = r1Desc.contains("||");
            int x = 100, y = 10;

            for (String part : r1Parts) {
                gc.strokeRect(x, y, 40, 20);
                gc.strokeText(part.trim() + "Ω", x + 5, y + 30);
                if (isParallel) {
                    y += 0; // параллельно — рисуем на одном уровне
                    x += 50; // сдвигаем вправо
                } else {
                    y += 30; // последовательно — снизу
                }
            }
            gc.strokeLine(100, 20, 140, y); // линия к узлу Vout
        } else {
            gc.strokeRect(100, 10, 40, 20);
            gc.strokeText("R1", 105, 45);
            gc.strokeLine(140, 20, 140, 60);
        }

        // Узел Vout
        gc.strokeOval(135, 60, 10, 10);
        gc.strokeText("Vout", 150, 70);

        // Нижнее плечо (R2)
        if (r2Desc != null) {
            String[] r2Parts = r2Desc.replace("R2=", "").split("\\+|\\|\\|");
            boolean isParallel = r2Desc.contains("||");
            int x = 120, y = 70;

            for (String part : r2Parts) {
                gc.strokeRect(x, y, 40, 20);
                gc.strokeText(part.trim() + "Ω", x + 5, y + 30);
                if (isParallel) {
                    y += 0;
                    x += 50;
                } else {
                    y += 30;
                }
            }
            gc.strokeLine(140, y, 140, 180);
        } else {
            gc.strokeRect(120, 100, 40, 20);
            gc.strokeText("R2", 125, 135);
            gc.strokeLine(140, 120, 140, 180);
        }

        // Земля
        gc.strokeLine(130, 180, 150, 180);
        gc.strokeLine(132, 185, 148, 185);
        gc.strokeLine(135, 190, 145, 190);
    }

    @FXML
    private void handleShowCircuit() {
        Result selected = resultTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Парсим строку "R1=XXXΩ, R2=YYYΩ"
            String[] parts = selected.resistorsProperty().get().split(",");
            String r1 = parts[0].trim();
            String r2 = parts[1].trim();
            drawCircuit(r1, r2);
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Сначала выберите результат в таблице!");
            alert.showAndWait();
        }
    }

    private String extractR1(String desc) {
        try {
            int start = desc.indexOf("R1=") + 3;
            int end = desc.indexOf(",", start);
            return desc.substring(start, end).trim();
        } catch (Exception e) {
            return null;
        }
    }

    private String extractR2(String desc) {
        try {
            int start = desc.indexOf("R2=") + 3;
            return desc.substring(start).trim();
        } catch (Exception e) {
            return null;
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