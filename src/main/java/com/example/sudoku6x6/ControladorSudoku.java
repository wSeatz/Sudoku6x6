package com.example.sudoku6x6;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.Random;

public class ControladorSudoku {

    // El fx:id del GridPane es SudokuGrid
    @FXML
    private GridPane SudokuGrid;

    private final List<TextField> tableroList = new ArrayList<>();

    private static final int TAMANO = 6;

    // ----------------------------------------------------------------------
    // INICIALIZACIÓN
    // ----------------------------------------------------------------------

    @FXML
    public void initialize() {
        // Ejecutamos la lógica después para asegurar que el GridPane esté listo.
        Platform.runLater(() -> {
            cargarTableroDesdeGridPane();

            if (tableroList.size() == TAMANO * TAMANO) {
                // Generar el tablero la primera vez que se carga la ventana
                generarSudokuAleatorio();
                validacionInstantanea(tableroList);
            } else {
                // Si esto sucede, revisa que todos los 36 TextField tengan rowIndex/columnIndex
                System.err.println("Error: Se encontraron " + tableroList.size() + " TextField en lugar de 36. Revisar FXML.");
            }
        });
    }

    private void cargarTableroDesdeGridPane() {
        tableroList.clear();

        // 1. Filtrar y añadir los TextField a la lista
        for (Node node : SudokuGrid.getChildren()) {
            if (node instanceof TextField) {
                tableroList.add((TextField) node);
            }
        }
        // 2. Ordenar la lista por fila y luego por columna
        tableroList.sort(Comparator
                // Ordena por Fila
                .comparingInt(tf -> getIndexOrDefault(GridPane.getRowIndex((Node) tf)))
                // Luego por Columna
                .thenComparingInt(tf -> getIndexOrDefault(GridPane.getColumnIndex((Node) tf))));
    }

    private int getIndexOrDefault(Integer index) {
        return (index == null) ? 0 : index;
    }

    @FXML
    public void generarSudokuAleatorio() {
        Random rand = new Random();

        // Matriz temporal para la lógica de Sudoku
        int[][] valores = new int[TAMANO][TAMANO];

        if (!resolverTablero(valores)) {
            System.err.println("No se pudo generar un tablero Sudoku válido.");
            return;
        }

        // Luego, oculta la mayoría de los números para crear el puzzle (solo dejando las pistas)
        int celdasParaMostrar = 12; // Número de pistas visibles

        // Limpia y restablece todos los TextField
        for (TextField tf : tableroList) {
            tf.setText("");
            tf.setEditable(true);
        }

        // Transfiere solo las 'pistas' al tablero visible y las marca como no editables
        for (int k = 0; k < TAMANO * TAMANO; k++) {
            // Llenamos todas las celdas y luego quitamos la mayoría para crear el desafío.
            int fila = k / TAMANO;
            int col = k % TAMANO;
            int indice = k;

            // El número está en la matriz 'valores'
            String valorStr = String.valueOf(valores[fila][col]);

            // Decide si ocultar o mostrar (esto determina la dificultad)
            if ( rand.nextInt(TAMANO * TAMANO) < celdasParaMostrar) {
                tableroList.get(indice).setText(valorStr);
                tableroList.get(indice).setEditable(false);
            }
        }
        System.out.println("Tablero Sudoku válido generado.");
    }

    private boolean resolverTablero(int[][] valores) {
        for (int fila = 0; fila < TAMANO; fila++) {
            for (int col = 0; col < TAMANO; col++) {

                if (valores[fila][col] == 0) {


                    for (int valor = 1; valor <= TAMANO; valor++) {

                        if (esValido(valores, fila, col, valor)) {
                            valores[fila][col] = valor;

                            if (resolverTablero(valores)) {
                                return true; //
                            } else {
                                valores[fila][col] = 0;
                            }
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    @FXML
    public void validarTablero(ActionEvent event) {
        System.out.println("Validando tablero...");
    }
    private void validacionInstantanea(List<TextField> campos){
        for (int k = 0; k < TAMANO * TAMANO; k++) {
            int i= k;
            campos.get(k).textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("[1-6]?")) {
                    campos.get(i).setText(oldValue);
                    Alert alerta = new Alert(AlertType.WARNING);
                    alerta.setTitle("ERROR");
                    alerta.setHeaderText("Entrada inválida");
                    alerta.setContentText("Solo se permiten números del 1 al 6.");
                    alerta.showAndWait();
                }
                if (!validarReglas(i)){
                    System.out.println("ERROR");
                }
            });
        }
    }
    public boolean validarReglas(int indice) {
        TextField tfIndice = tableroList.get(indice);
        for (int c = (indice%6); c < TAMANO * TAMANO; c=(c+6)) {
            TextField campo = tableroList.get(c);
         if ((campo.getText().matches(tfIndice.getText())) & (!campo.getText().matches("")) & (c!=indice)) {
             Alert alerta = new Alert(AlertType.WARNING);
             alerta.setTitle("ERROR");
             alerta.setHeaderText("Entrada inválida");
             alerta.setContentText("El número no puede ser ingresado en esta celda porque colinda con otro en la misma columna");
             alerta.showAndWait();
             String estiloOriginal = campo.getStyle();
             campo.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
             tfIndice.setOnKeyReleased(event -> {
                String texto = tfIndice.getText();
                if (texto.isEmpty()){
                    campo.setStyle(estiloOriginal);
                }
                });
             return false;
         }
        }
        for (int i = indice-(indice%6); i< (indice+6-(indice%6)); i++) {
            TextField campo = tableroList.get(i);
            if((campo.getText().matches(tfIndice.getText())) & (!campo.getText().matches("")) & (i!=indice)){
                Alert alerta = new Alert(AlertType.WARNING);
                alerta.setTitle("ERROR");
                alerta.setHeaderText("Entrada inválida");
                alerta.setContentText("El número no puede ser ingresado en esta celda porque colinda con otro en la misma fila");
                alerta.showAndWait();
                String estiloOriginal = campo.getStyle();
                campo.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                tfIndice.setOnKeyReleased(event -> {
                    String texto = tfIndice.getText();
                    if (texto.isEmpty()){
                        campo.setStyle(estiloOriginal);
                    }
                });
                return false;
            }
        }
        return true;
    }
    @FXML
    public void generarPista(ActionEvent event) {
        System.out.println("Generando pista...");
    }

    private boolean esValido(int[][] valores, int fila, int col, int valor) {
        // 1. Verificar Fila y Columna
        for (int i = 0; i < TAMANO; i++) {
            // Verifica la fila actual
            if (valores[fila][i] == valor) return false;
            // Verifica la columna actual
            if (valores[i][col] == valor) return false;
        }

        // 2. Verificar Subcuadrícula (Región 2x3)
        int inicioFila = fila - fila % 2; // Calcula el inicio de la región de 2 filas
        int inicioCol = col - col % 3;   // Calcula el inicio de la región de 3 columnas

        for (int i = 0; i < 2; i++) { // Bucle por las 2 filas de la región
            for (int j = 0; j < 3; j++) { // Bucle por las 3 columnas de la región
                if (valores[inicioFila + i][inicioCol + j] == valor) return false;
            }
        }

        return true; // Si pasa todas las pruebas, es válido.
    }
}