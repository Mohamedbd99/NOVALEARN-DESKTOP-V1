package org.novalearn.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.novalearn.Entity.Cours;
import org.novalearn.Entity.Genre;
import org.novalearn.services.GenreService;
import org.novalearn.services.coursEexercice.CoursService;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenreController {

    @FXML
    private VBox rootPane;

    private final GenreService genreService;
    private ObservableList<Genre> coursObservableList;
    private byte[] imageBytes;
    private byte[] videoBytes;

    @FXML
    private TextField searchField, libelleField, descriptionField;
    @FXML
    private ListView<Genre> genreListView;

    public GenreController() throws SQLException {
        this.genreService = new GenreService();
    }
    private boolean validateTitre(){
        Pattern p = Pattern.compile("[a-zA-Z ]+");
        Matcher m = p.matcher(libelleField.getText());
        if(m.find() && m.group().equals(libelleField.getText())){
            return true;
        }else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validate Titre course");
            alert.setHeaderText(null);
            alert.setContentText("Please Enter Valid Titre");
            alert.showAndWait();

            return false;
        }
    }
    private boolean validatedesc(){
        Pattern p = Pattern.compile("[a-zA-Z ]+");
        Matcher m = p.matcher(descriptionField.getText());
        if(m.find() && m.group().equals(descriptionField.getText())){
            return true;
        }else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validate Description course");
            alert.setHeaderText(null);
            alert.setContentText("Please Enter Valid Description");
            alert.showAndWait();

            return false;
        }
    }

    @FXML
    private void addCours(ActionEvent event) {
        if ( validateTitre()&& validatedesc() ){
            String lib = libelleField.getText();
            String description = descriptionField.getText();


            Genre newCours = new Genre(lib, description);

            try {
                genreService.create(newCours);
                refreshCoursList();
                showAlert("Succès", "Le genre a été ajouté avec succès.", Alert.AlertType.INFORMATION);
                clearForm();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Une erreur est survenue lors de l'ajout du genre.", Alert.AlertType.ERROR);
            }
        }}
    @FXML
    public void initialize() {
        // Ajouter un écouteur pour détecter la sélection d'un Cours
        genreListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateForm(newSelection);
            }
        });

        // Rafraîchir la liste des Cours au démarrage
        refreshCoursList();
    }



    private void refreshCoursList() {
        try {
            List<Genre> coursList = genreService.readAll();
            coursObservableList = FXCollections.observableArrayList(coursList);

            genreListView.setItems(coursObservableList);
            genreListView.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Genre item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        GridPane grid = new GridPane();
                        grid.setHgap(15);
                        grid.setVgap(5);
                        grid.setStyle("-fx-padding: 10px; -fx-border-color: black; -fx-border-width: 1;");

                        Label lib = new Label(item.getLibelle());
                        lib.setStyle("-fx-font-weight: bold; -fx-padding: 5px;");

                        Label desc = new Label(item.getDescription());
                        desc.setStyle("-fx-padding: 5px;");





                        // Boutons
                        HBox buttonBox = new HBox(5);


                        Button deleteButton = new Button("Supprimer");
                        deleteButton.setStyle("-fx-background-color: transparent; -fx-font-size: 14px;");
                        deleteButton.setOnAction(e -> deleteCours(item));

                        buttonBox.getChildren().addAll(deleteButton);

                        // Ajouter les champs au grid
                        grid.add(lib, 0, 0);
                        grid.add(desc, 1, 0);

                        grid.add(buttonBox, 3, 0);

                        setGraphic(grid);
                    }
                }
            });

            // === Construire les en-têtes (labels une seule fois) ===
            GridPane headerGrid = new GridPane();
            headerGrid.setHgap(15);
            headerGrid.setVgap(5);
            headerGrid.setStyle("-fx-padding: 10px; -fx-background-color: #f0f0f0;");

            headerGrid.add(new Label("Libelle:"), 0, 0);
            headerGrid.add(new Label("Description:"), 1, 0);
            headerGrid.add(new Label("Actions:"), 2, 0);

            // === Mettre ListView et en-tête dans VBox ===
            VBox container = new VBox(10);
            container.getChildren().addAll(headerGrid, genreListView);

            // === Afficher container à l'endroit voulu dans ta scène ===
            // Remplace "rootPane" par le noeud parent où tu veux insérer le contenu
            rootPane.getChildren().setAll(container);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteCours(Genre cours) {
        try {
            genreService.delete(cours);
            coursObservableList.remove(cours);
            genreListView.setItems(coursObservableList);
            showAlert("Succès", "Cours supprimé avec succès.", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la suppression du Cours.", Alert.AlertType.ERROR);
        }
    }

    private void clearForm() {
        libelleField.clear();
        descriptionField.clear();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void searchCours(ActionEvent event) {
        String searchKeyword = searchField.getText().toLowerCase();
        ObservableList<Genre> filteredList = FXCollections.observableArrayList();
        for (Genre c : coursObservableList) {
            if (c.getLibelle().toLowerCase().contains(searchKeyword)) {
                filteredList.add(c);
            }
        }
        genreListView.setItems(filteredList);
    }

    private void populateForm(Genre cours) {
        libelleField.setText(cours.getLibelle());
        descriptionField.setText(cours.getDescription());

    }

    @FXML
    private void updateCours(ActionEvent event) {
        Genre selectedCours = genreListView.getSelectionModel().getSelectedItem();
        if (selectedCours == null) {
            showAlert("Erreur", "Veuillez sélectionner un Cours à mettre à jour.", Alert.AlertType.ERROR);
            return;
        }

        selectedCours.setLibelle(libelleField.getText());
        selectedCours.setDescription(descriptionField.getText());


        try {
            genreService.update(selectedCours);
            refreshCoursList();
            showAlert("Succès", "Le Genre a été mis à jour avec succès.", Alert.AlertType.INFORMATION);
            clearForm();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de la mise à jour du Genre.", Alert.AlertType.ERROR);
        }
    }

}
