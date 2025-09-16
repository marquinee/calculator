package org.example.calculator.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.calculator.model.User;

public class VoltageDividerController implements UserAwareController {

    private User currentUser;

    @FXML private TextField vinField;
    @FXML private TextField voutField;
    @FXML private TextField toleranceField;
    @FXML private Label resultLabel;

    @Override
    public void setUser(User user) {
        this.currentUser = user;
    }

    @FXML
    private void calculate() {
        // TODO: логика подбора резисторов
        resultLabel.setText("Пока заглушка: подбор комбинаций не реализован");
    }
}
