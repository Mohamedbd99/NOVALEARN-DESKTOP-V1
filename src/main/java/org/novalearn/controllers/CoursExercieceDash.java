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

public class CoursExercieceDash {

    @FXML
    private VBox rootPane;

    private final CoursService coursService;
    private ObservableList<Cours> coursObservableList;
    private byte[] imageBytes;
    private byte[] videoBytes;

    @FXML
    private TextField searchField, titreField, descriptionField, contenuField, authorIdField, imageField, nbrLikeField, nombreDeTelechargementsField, videoField;
    @FXML
    private ListView<Cours> coursListView;

    public CoursExercieceDash() throws SQLException {
        this.coursService = new CoursService();
    }
    private boolean validateTitre(){
        Pattern p = Pattern.compile("[a-zA-Z ]+");
        Matcher m = p.matcher(titreField.getText());
        if(m.find() && m.group().equals(titreField.getText())){
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
    private boolean validatedcontinu(){
        Pattern p = Pattern.compile("[a-zA-Z ]+");
        Matcher m = p.matcher(contenuField.getText());
        if(m.find() && m.group().equals(contenuField.getText())){
            return true;
        }else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validate continue course");
            alert.setHeaderText(null);
            alert.setContentText("Please Enter Valid continue");
            alert.showAndWait();

            return false;
        }
    }
    private boolean validateEtoiles(){
        Pattern p = Pattern.compile("[1-9]");
        Matcher m = p.matcher(nbrLikeField.getText());
        if(m.find() && m.group().equals(nbrLikeField.getText())){
            return true;
        }else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validate nombre etoiles");
            alert.setHeaderText(null);
            alert.setContentText("Please Enter Valid nombres etoiles");
            alert.showAndWait();

            return false;
        }

    }

    @FXML
    private void addCours(ActionEvent event) {
        if (  validateTitre()&& validateEtoiles()&& validatedesc()&& validatedcontinu() ){
            String titre = titreField.getText();
            String description = descriptionField.getText();
            String contenu = contenuField.getText();
            int authorId = Integer.parseInt(authorIdField.getText());
            int nbrLike = Integer.parseInt(nbrLikeField.getText());
            int nombreDeTelechargements = Integer.parseInt(nombreDeTelechargementsField.getText());

            if (imageBytes == null || videoBytes == null) {
                showAlert("Erreur", "Veuillez choisir une image et une vidéo.", Alert.AlertType.ERROR);
                return;
            }

            Cours newCours = new Cours(titre, description, contenu, authorId, imageBytes, nbrLike, nombreDeTelechargements, videoBytes);

            try {
                coursService.create(newCours);
                refreshCoursList();
                showAlert("Succès", "Le Cours a été ajouté avec succès.", Alert.AlertType.INFORMATION);
                clearForm();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Une erreur est survenue lors de l'ajout du Cours.", Alert.AlertType.ERROR);
            }
        }}
    @FXML
    public void initialize() {
        // Ajouter un écouteur pour détecter la sélection d'un Cours
        coursListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateForm(newSelection);
            }
        });

        // Rafraîchir la liste des Cours au démarrage
        refreshCoursList();
    }



    private void refreshCoursList() {
        try {
            List<Cours> coursList = coursService.readAll();
            coursObservableList = FXCollections.observableArrayList(coursList);

            coursListView.setItems(coursObservableList);
            coursListView.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Cours item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        GridPane grid = new GridPane();
                        grid.setHgap(15);
                        grid.setVgap(5);
                        grid.setStyle("-fx-padding: 10px; -fx-border-color: black; -fx-border-width: 1;");

                        Label titre = new Label(item.getTitre());
                        titre.setStyle("-fx-font-weight: bold; -fx-padding: 5px;");

                        Label desc = new Label(item.getDescription());
                        desc.setStyle("-fx-padding: 5px;");

                        Label cont = new Label(item.getContenu());
                        cont.setStyle("-fx-padding: 5px;");

                        Label auth = new Label(String.valueOf(item.getAuthorId()));
                        auth.setStyle("-fx-padding: 5px;");

                        Label nbtel = new Label(String.valueOf(item.getNombreDeTelechargements()));
                        nbtel.setStyle("-fx-padding: 5px;");

                        Label nblike = new Label(String.valueOf(item.getNbrLike()));
                        nblike.setStyle("-fx-padding: 5px;");

                        // Image
                        if (item.getImage() != null) {
                            Image image = new Image(new ByteArrayInputStream(item.getImage()));
                            ImageView imageView = new ImageView(image);
                            imageView.setFitHeight(50);
                            imageView.setFitWidth(50);
                            grid.add(imageView, 6, 0);
                        }

                        // Boutons
                        HBox buttonBox = new HBox(5);
                        Button playButton = new Button("Lire Vidéo");
                        playButton.setOnAction(e -> playVideo(item.getVideo()));

                        Button deleteButton = new Button("Supprimer");
                        deleteButton.setStyle("-fx-background-color: transparent; -fx-font-size: 14px;");
                        deleteButton.setOnAction(e -> deleteCours(item));

                        buttonBox.getChildren().addAll(playButton, deleteButton);

                        // Ajouter les champs au grid
                        grid.add(titre, 0, 0);
                        grid.add(desc, 1, 0);
                        grid.add(cont, 2, 0);
                        grid.add(auth, 3, 0);
                        grid.add(nbtel, 4, 0);
                        grid.add(nblike, 5, 0);
                        grid.add(buttonBox, 7, 0);

                        setGraphic(grid);
                    }
                }
            });

            // === Construire les en-têtes (labels une seule fois) ===
            GridPane headerGrid = new GridPane();
            headerGrid.setHgap(15);
            headerGrid.setVgap(5);
            headerGrid.setStyle("-fx-padding: 10px; -fx-background-color: #f0f0f0;");

            headerGrid.add(new Label("Titre:"), 0, 0);
            headerGrid.add(new Label("Description:"), 1, 0);
            headerGrid.add(new Label("Contenu:"), 2, 0);
            headerGrid.add(new Label("Auteur ID:"), 3, 0);
            headerGrid.add(new Label("Téléchargements:"), 4, 0);
            headerGrid.add(new Label("Likes:"), 5, 0);
            headerGrid.add(new Label("Image:"), 6, 0);
            headerGrid.add(new Label("Actions:"), 7, 0);

            // === Mettre ListView et en-tête dans VBox ===
            VBox container = new VBox(10);
            container.getChildren().addAll(headerGrid, coursListView);

            // === Afficher container à l'endroit voulu dans ta scène ===
            // Remplace "rootPane" par le noeud parent où tu veux insérer le contenu
            rootPane.getChildren().setAll(container);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void playVideo(byte[] videoBytes) {
        try {
            File tempVideo = File.createTempFile("video", ".mp4");
            tempVideo.deleteOnExit();

            try (FileOutputStream fos = new FileOutputStream(tempVideo)) {
                fos.write(videoBytes);
            }

            Media media = new Media(tempVideo.toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            MediaView mediaView = new MediaView(mediaPlayer);
            mediaView.setFitWidth(800);
            mediaView.setFitHeight(600);

            Stage videoStage = new Stage();
            VBox root = new VBox(mediaView);
            Scene scene = new Scene(root, 800, 600);
            videoStage.setScene(scene);
            videoStage.setTitle("Lecture de la vidéo");
            videoStage.show();

            mediaPlayer.play();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de lire la vidéo.", Alert.AlertType.ERROR);
        }
    }
    private void deleteCours(Cours cours) {
        try {
            coursService.delete(cours);
            coursObservableList.remove(cours);
            coursListView.setItems(coursObservableList);
            showAlert("Succès", "Cours supprimé avec succès.", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la suppression du Cours.", Alert.AlertType.ERROR);
        }
    }

    private void clearForm() {
        titreField.clear();
        descriptionField.clear();
        contenuField.clear();
        authorIdField.clear();
        imageField.clear();
        nbrLikeField.clear();
        nombreDeTelechargementsField.clear();
        videoField.clear();
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
        ObservableList<Cours> filteredList = FXCollections.observableArrayList();
        for (Cours c : coursObservableList) {
            if (c.getTitre().toLowerCase().contains(searchKeyword)) {
                filteredList.add(c);
            }
        }
        coursListView.setItems(filteredList);
    }

    @FXML
    private void chooseImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        Stage stage = (Stage) imageField.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            imageField.setText(file.getAbsolutePath());
            try {
                imageBytes = Files.readAllBytes(file.toPath());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    private void chooseVideo(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.avi", "*.mov"));
        Stage stage = (Stage) videoField.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            videoField.setText(file.getAbsolutePath());
            try {
                videoBytes = Files.readAllBytes(file.toPath());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    private void populateForm(Cours cours) {
        titreField.setText(cours.getTitre());
        descriptionField.setText(cours.getDescription());
        contenuField.setText(cours.getContenu());
        authorIdField.setText(String.valueOf(cours.getAuthorId()));
        nbrLikeField.setText(String.valueOf(cours.getNbrLike()));
        nombreDeTelechargementsField.setText(String.valueOf(cours.getNombreDeTelechargements()));
        imageBytes = cours.getImage();
        videoBytes = cours.getVideo();
        imageField.setText(""); // Optionnel : afficher un message ou le nom du fichier
        videoField.setText(""); // Optionnel : afficher un message ou le nom du fichier
    }

    @FXML
    private void updateCours(ActionEvent event) {
        Cours selectedCours = coursListView.getSelectionModel().getSelectedItem();
        if (selectedCours == null) {
            showAlert("Erreur", "Veuillez sélectionner un Cours à mettre à jour.", Alert.AlertType.ERROR);
            return;
        }

        selectedCours.setTitre(titreField.getText());
        selectedCours.setDescription(descriptionField.getText());
        selectedCours.setContenu(contenuField.getText());
        selectedCours.setAuthorId(Integer.parseInt(authorIdField.getText()));
        selectedCours.setNbrLike(Integer.parseInt(nbrLikeField.getText()));
        selectedCours.setNombreDeTelechargements(Integer.parseInt(nombreDeTelechargementsField.getText()));

        if (!imageField.getText().isEmpty()) {
            selectedCours.setImage(imageBytes);
        }

        if (!videoField.getText().isEmpty()) {
            selectedCours.setVideo(videoBytes);
        }

        try {
            coursService.update(selectedCours);
            refreshCoursList();
            showAlert("Succès", "Le Cours a été mis à jour avec succès.", Alert.AlertType.INFORMATION);
            clearForm();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de la mise à jour du Cours.", Alert.AlertType.ERROR);
        }
    }

}
