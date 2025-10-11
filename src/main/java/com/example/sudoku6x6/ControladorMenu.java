package com.example.sudoku6x6;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;

public class ControladorMenu {


    @FXML
    private Button InstruccionesBotonMenu;

    @FXML
    private Button IniciarBotonMenu;


    public void ClickInstrucciones(ActionEvent event) {
        // 1. Usa la clase estándar Alert y especifica el tipo.
        Alert alert = new Alert(AlertType.INFORMATION); // Puedes usar INFORMATION, WARNING, etc.

        alert.setTitle("Instrucciones");
        alert.setHeaderText("Instrucciones para jugar al Sudoku 6x6");
        alert.setContentText(
                "1. El objetivo del juego es llenar una cuadrícula de 6x6 con números del 1 al 6.\n" +
                        "2. Cada fila, columna y región (2x3) debe contener todos los números del 1 al 6 sin repetirse.\n" +
                        "3. Haz clic en una celda vacía para seleccionar un número del 1 al 6.\n" +
                        "4. Usa el botón 'Validar' para comprobar si tu solución es correcta.\n" +
                        "5. Si cometes un error, el juego te lo indicará.\n" +
                        "6. ¡Diviértete y desafía tu mente!");

        alert.showAndWait();
    }

    public void ClickIniciar(ActionEvent ActionEvent) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 888, 500);
        Stage setstage = new Stage();
        setstage.setTitle("Sudoku 6x6");
        setstage.setScene(scene);
        setstage.setResizable(false);
        setstage.show();
    }


}
