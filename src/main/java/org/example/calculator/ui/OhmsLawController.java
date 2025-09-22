package org.example.calculator.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.calculator.dao.HistoryDao;
import org.example.calculator.model.User;

public class OhmsLawController implements UserAwareController {

    private User currentUser;

    @FXML private TextField voltageField;
    @FXML private TextField currentField;
    @FXML private TextField resistanceField;
    @FXML private Label resultLabel;
    @FXML private Canvas canvas; // добавляем Canvas

    @Override
    public void setUser(User user) {
        this.currentUser = user;
    }

    @FXML private Canvas diagramCanvas;

    private void drawScheme(Double V, Double I, Double R) {
        var gc = diagramCanvas.getGraphicsContext2D();

        // очищаем перед новой отрисовкой
        gc.clearRect(0, 0, diagramCanvas.getWidth(), diagramCanvas.getHeight());

        gc.setLineWidth(2);

        // батарея
        gc.strokeLine(50, 150, 100, 150);
        gc.strokeOval(100, 130, 40, 40);
        gc.strokeText("V = " + (V != null ? V : "?") + " В", 90, 120);

        // резистор
        gc.strokeLine(140, 150, 180, 150);
        gc.strokeRect(180, 130, 60, 40);
        gc.strokeText("R = " + (R != null ? R : "?") + " Ω", 180, 120);

        // выход
        gc.strokeLine(240, 150, 300, 150);
        gc.strokeText("I = " + (I != null ? I : "?") + " А", 260, 170);

        // замыкание на землю
        gc.strokeLine(300, 150, 300, 200);
        gc.strokeLine(50, 150, 50, 200);
        gc.strokeLine(50, 200, 300, 200);
    }

    @FXML
    private void calculate() {
        try {
            Double V = parse(voltageField.getText());
            Double I = parse(currentField.getText());
            Double R = parse(resistanceField.getText());

            if (V == null && I != null && R != null) {
                V = I * R;
                voltageField.setText(String.valueOf(V));
                resultLabel.setText("Вычислено напряжение: " + V + " В");
            } else if (I == null && V != null && R != null) {
                I = V / R;
                currentField.setText(String.valueOf(I));
                resultLabel.setText("Вычислен ток: " + I + " А");
            } else if (R == null && V != null && I != null) {
                R = V / I;
                resistanceField.setText(String.valueOf(R));
                resultLabel.setText("Вычислено сопротивление: " + R + " Ом");
            } else {
                resultLabel.setText("Введите ровно 2 параметра");
            }

            // рисуем схему
            drawScheme(V, I, R);

            HistoryDao historyDao = new HistoryDao();
            historyDao.save(
                    currentUser.getId(),
                    "OHM",
                    String.format("V=%s, I=%s, R=%s", voltageField.getText(), currentField.getText(), resistanceField.getText()),
                    String.format("V=%s, I=%s, R=%s", voltageField.getText(), currentField.getText(), resistanceField.getText())
            );


        } catch (Exception e) {
            resultLabel.setText("Ошибка: " + e.getMessage());
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

    private Double parse(String text) {
        if (text == null || text.isEmpty()) return null;
        return Double.parseDouble(text);
    }
}
