package org.example.calculator.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.calculator.model.User;

public class OhmsLawController implements UserAwareController {

    private User currentUser;

    @FXML private TextField voltageField;
    @FXML private TextField currentField;
    @FXML private TextField resistanceField;
    @FXML private Label resultLabel;

    @Override
    public void setUser(User user) {
        this.currentUser = user;
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

            // TODO: сохранить результат в БД для currentUser

        } catch (Exception e) {
            resultLabel.setText("Ошибка: " + e.getMessage());
        }
    }

    private Double parse(String text) {
        if (text == null || text.isEmpty()) return null;
        return Double.parseDouble(text);
    }
}

