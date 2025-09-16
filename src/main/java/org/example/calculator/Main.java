package org.example.calculator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.calculator.db.DatabaseInit;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        DatabaseInit.init();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
        Scene scene = new Scene(loader.load(), 400, 250);

        stage.setTitle("Вход");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
