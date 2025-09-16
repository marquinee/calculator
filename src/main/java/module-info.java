module org.example.calculator {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires jbcrypt;

    opens org.example.calculator to javafx.fxml;
    opens org.example.calculator.ui to javafx.fxml;
    exports org.example.calculator;
    exports org.example.calculator.ui;

}