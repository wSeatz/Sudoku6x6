package com.example.sudoku6x6;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 690, 698);
        stage.setTitle("Sudoku 6x6");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
