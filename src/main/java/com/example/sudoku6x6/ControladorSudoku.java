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
import java.util.Collections;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ControladorSudoku {

    @FXML
    private GridPane SudokuGrid;

    // --- VARIABLES Y CONSTANTES CORREGIDAS ---
    // 1. PRIMERO declaramos las constantes
    private static final int TAMANO = 6;
    private static final int MAX_PISTAS = 6;

    // 2. LUEGO usamos las constantes para declarar las otras variables
    private final List<TextField> tableroList = new ArrayList<>();
    private int[][] solucion = new int[TAMANO][TAMANO];
    private int pistasRestantes;

    // ----------------------------------------------------------------------
    // INICIALIZACIÓN Y GENERACIÓN DEL TABLERO
    // ----------------------------------------------------------------------

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            cargarTableroDesdeGridPane();
            if (tableroList.size() == TAMANO * TAMANO) {
                generarSudokuAleatorio();
                validacionInstantanea(tableroList);
            } else {
                System.err.println("Error: Se encontraron " + tableroList.size() + " TextField en lugar de 36. Revisar FXML.");
            }
        });
    }

    private void cargarTableroDesdeGridPane() {
        tableroList.clear();
        for (Node node : SudokuGrid.getChildren()) {
            if (node instanceof TextField) {
                tableroList.add((TextField) node);
            }
        }
        tableroList.sort(Comparator
                .comparingInt(tf -> getIndexOrDefault(GridPane.getRowIndex((Node) tf)))
                .thenComparingInt(tf -> getIndexOrDefault(GridPane.getColumnIndex((Node) tf))));
    }

    private int getIndexOrDefault(Integer index) {
        return (index == null) ? 0 : index;
    }

    @FXML
    public void generarSudokuAleatorio() {
        // Reseteamos el contador de pistas cada vez que se genera un nuevo tablero.
        this.pistasRestantes = MAX_PISTAS;

        // Limpiamos la matriz de solución anterior.
        this.solucion = new int[TAMANO][TAMANO];

        // Resolvemos el Sudoku y guardamos el resultado en la variable de la clase 'this.solucion'.
        if (!resolverTablero(this.solucion)) {
            System.err.println("No se pudo generar un tablero Sudoku válido.");
            return;
        }

        // Limpiamos el tablero gráfico.
        for (TextField tf : tableroList) {
            tf.setText("");
            tf.setEditable(true);
            tf.setStyle("");
        }

        // --- NUEVA LÓGICA PARA MOSTRAR 2 PISTAS POR REGIÓN ---

        // 1. Definimos las 6 regiones por los índices de sus celdas.
        List<List<Integer>> regiones = List.of(
                List.of(0, 1, 2, 6, 7, 8),      // Región 1 (Arriba-Izquierda)
                List.of(3, 4, 5, 9, 10, 11),     // Región 2 (Arriba-Derecha)
                List.of(12, 13, 14, 18, 19, 20), // Región 3 (Medio-Izquierda)
                List.of(15, 16, 17, 21, 22, 23), // Región 4 (Medio-Derecha)
                List.of(24, 25, 26, 30, 31, 32), // Región 5 (Abajo-Izquierda)
                List.of(27, 28, 29, 33, 34, 35)  // Región 6 (Abajo-Derecha)
        );


        for (List<Integer> regionActual : regiones) {

            List<Integer> indicesEnRegion = new ArrayList<>(regionActual);
            Collections.shuffle(indicesEnRegion);

            // 3. Mostramos las pistas para las primeras 2 celdas de la lista barajada.
            for (int i = 0; i < 2; i++) {
                int indiceParaMostrar = indicesEnRegion.get(i);
                int fila = indiceParaMostrar / TAMANO;
                int col = indiceParaMostrar % TAMANO;

                TextField celda = tableroList.get(indiceParaMostrar);
                celda.setText(String.valueOf(this.solucion[fila][col]));
                celda.setEditable(false);
            }
        }

        System.out.println("Tablero Sudoku válido generado con 2 pistas por región. Pistas restantes: " + pistasRestantes);
    }

    private boolean resolverTablero(int[][] valores) {
        for (int fila = 0; fila < TAMANO; fila++) {
            for (int col = 0; col < TAMANO; col++) {
                if (valores[fila][col] == 0) {
                    List<Integer> numeros = IntStream.rangeClosed(1, TAMANO).boxed().collect(Collectors.toList());
                    Collections.shuffle(numeros);
                    for (int valor : numeros) {
                        if (esValido(valores, fila, col, valor)) {
                            valores[fila][col] = valor;
                            if (resolverTablero(valores)) {
                                return true;
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

    // ----------------------------------------------------------------------
    // LÓGICA DE LOS BOTONES
    // ----------------------------------------------------------------------

    @FXML
    public void validarTablero(ActionEvent event) {
        boolean estaCompleto = true;
        boolean hayErrores = false;

        for (int i = 0; i < tableroList.size(); i++) {
            TextField campo = tableroList.get(i);
            String valorUsuario = campo.getText();

            if (valorUsuario.isEmpty()) {
                estaCompleto = false;
                continue;
            }

            try {
                int fila = i / TAMANO;
                int col = i % TAMANO;
                int valorCorrecto = solucion[fila][col];

                if (Integer.parseInt(valorUsuario) != valorCorrecto) {
                    hayErrores = true;
                    campo.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                } else {
                    campo.setStyle("");
                }
            } catch (NumberFormatException e) {
                hayErrores = true;
                campo.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            }
        }

        if (hayErrores) {
            mostrarAlerta("Resultado de la Validación", "¡Hay errores! Algunos números son incorrectos.");
        } else if (!estaCompleto) {
            mostrarAlerta("Resultado de la Validación", "¡Vas bien, pero falta! El tablero aún no está completo.");
        } else {
            mostrarAlerta("¡Felicitaciones!", "Has resuelto el Sudoku correctamente.");
        }
    }

    @FXML
    public void generarPista(ActionEvent event) {
        if (pistasRestantes <= 0) {
            mostrarAlerta("Sin Pistas", "Ya has utilizado todas tus pistas disponibles.");
            return;
        }

        List<Integer> celdasVacias = new ArrayList<>();
        for (int i = 0; i < tableroList.size(); i++) {
            if (tableroList.get(i).isEditable() && tableroList.get(i).getText().isEmpty()) {
                celdasVacias.add(i);
            }
        }

        if (celdasVacias.isEmpty()) {
            mostrarAlerta("Tablero Lleno", "No hay celdas vacías para dar una pista.");
            return;
        }

        Collections.shuffle(celdasVacias);
        int indicePista = celdasVacias.get(0);

        int fila = indicePista / TAMANO;
        int col = indicePista % TAMANO;

        TextField campoPista = tableroList.get(indicePista);
        campoPista.setText(String.valueOf(solucion[fila][col]));
        campoPista.setEditable(false);
        campoPista.setStyle("-fx-background-color: #d3ffd3; -fx-border-color: black;");

        pistasRestantes--;
        mostrarAlerta("Pista Generada", "Se ha colocado un número correcto. Te quedan " + pistasRestantes + " pistas.");
    }

    @FXML
    public void reiniciarTablero(ActionEvent event) {
        for (TextField campo : tableroList) {
            if (campo.isEditable()) {
                campo.setText("");
                campo.setStyle("");
            }
        }
        mostrarAlerta("Tablero Reiniciado", "Se han borrado tus números.");
    }

    private void validacionInstantanea(List<TextField> campos){
        for (int k = 0; k < TAMANO * TAMANO; k++) {
            int i = k;
            campos.get(k).textProperty().addListener((observable, oldValue, newValue) -> {
                TextField campoActual = campos.get(i);
                if (!campoActual.isEditable()) return;

                campoActual.setStyle("");

                if (!newValue.matches("[1-6]?")) {
                    campoActual.setText(oldValue);
                }
                if (!newValue.isEmpty() && !validarReglas(i)){
                    System.out.println("Error");
                }
            });
        }
    }

    public boolean validarReglas(int indice) {
        TextField tfIndice = tableroList.get(indice);
        String valor = tfIndice.getText();
        if (valor == null || valor.isEmpty()) return true;

        // Validar Fila
        int filaInicio = (indice / TAMANO) * TAMANO;
        for (int i = filaInicio; i < filaInicio + TAMANO; i++) {
            TextField campo =  tableroList.get(i);
            if (i != indice && tableroList.get(i).getText().equals(valor)){
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

        // Validar Columna
        int col = indice % TAMANO;
        for (int i = col; i < TAMANO * TAMANO; i += TAMANO) {
            TextField campo =  tableroList.get(i);
            if (i != indice && tableroList.get(i).getText().equals(valor)) {
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

        // Validar Subcuadrícula
        List<Integer> subcuadricula = identificarSubcuadricula(indice);
        for (Integer idx : subcuadricula) {
            TextField campo =  tableroList.get(idx);
            if (idx != indice && tableroList.get(idx).getText().equals(valor)) {
                Alert alerta = new Alert(AlertType.WARNING);
                alerta.setTitle("ERROR");
                alerta.setHeaderText("Entrada inválida");
                alerta.setContentText("El número no puede ser ingresado en esta celda porque colinda con otro en la misma subcuadricula");
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

    public List<Integer> identificarSubcuadricula(int indice){
        List <Integer> subcuadricula1 = List.of(0,1,2,6,7,8);
        List <Integer> subcuadricula2 = List.of(3,4,5,9,10,11);
        List <Integer> subcuadricula3 = List.of(12,13,14,18,19,20);
        List <Integer> subcuadricula4 = List.of(15,16,17,21,22,23);
        List <Integer> subcuadricula5 = List.of(24,25,26,30,31,32);
        List <Integer> subcuadricula6 = List.of(27,28,29,33,34,35);
        if (subcuadricula1.contains(indice)){
            return subcuadricula1;
        } else if (subcuadricula2.contains(indice)){
            return subcuadricula2;
        } else if (subcuadricula3.contains(indice)){
            return subcuadricula3;
        } else if (subcuadricula4.contains(indice)){
            return subcuadricula4;
        }  else if (subcuadricula5.contains(indice)){
            return subcuadricula5;
        }  else {
            return subcuadricula6;
        }
    }

    // ----------------------------------------------------------------------
    // MÉTODOS DE UTILIDAD
    // ----------------------------------------------------------------------

    private boolean esValido(int[][] valores, int fila, int col, int valor) {
        for (int i = 0; i < TAMANO; i++) {
            if (valores[fila][i] == valor) return false;
            if (valores[i][col] == valor) return false;
        }

        int inicioFila = fila - fila % 2;
        int inicioCol = col - col % 3;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                if (valores[inicioFila + i][inicioCol + j] == valor) return false;
            }
        }
        return true;
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alerta = new Alert(AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }
}