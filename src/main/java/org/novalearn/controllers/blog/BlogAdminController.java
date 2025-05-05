package org.novalearn.controllers.blog;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.novalearn.Entity.Blog;
import org.novalearn.service.BlogService;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlogAdminController {

    @FXML
    private VBox rootPane;

    private final BlogService blogService;
    private ObservableList<Blog> coursObservableList;
    private byte[] imageBytes;

    @FXML
    private TextField searchField, titreField, descriptionField, contenuField, authorIdField, imageField, categoryField;
    @FXML
    private ListView<Blog> coursListView;
    @FXML
    private DatePicker createdField;
    @FXML
    private CheckBox anonymeCheckBox;


    public BlogAdminController() throws SQLException {
        this.blogService = new BlogService();
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


    @FXML
    private void addCours(ActionEvent event) {
        if ( validateTitre()&& validatedesc()&& validatedcontinu() ){
            String titre = titreField.getText();
            String description = descriptionField.getText();
            String contenu = contenuField.getText();
            int authorId = Integer.parseInt(authorIdField.getText());
            String category = categoryField.getText();
            LocalDate createdAt = createdField.getValue();
            boolean estan = anonymeCheckBox.isSelected();



            Blog newCours = new Blog(titre, description, contenu,category, authorId, imageBytes,createdAt,estan);

            try {
                blogService.create(newCours);
                refreshCoursList();
                showAlert("Succès", "Le blog a été ajouté avec succès.", Alert.AlertType.INFORMATION);
                clearForm();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Une erreur est survenue lors de l'ajout du blog.", Alert.AlertType.ERROR);
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
            List<Blog> coursList = blogService.readAll();
            coursObservableList = FXCollections.observableArrayList(coursList);

            coursListView.setItems(coursObservableList);
            coursListView.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Blog item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        GridPane grid = new GridPane();
                        grid.setHgap(15);
                        grid.setVgap(5);
                        grid.setStyle("-fx-padding: 10px; -fx-border-color: black; -fx-border-width: 1;");

                        Label titre = new Label(item.getTitle());
                        titre.setStyle("-fx-font-weight: bold; -fx-padding: 5px;");

                        Label desc = new Label(item.getDescription());
                        desc.setStyle("-fx-padding: 5px;");

                        Label cont = new Label(item.getContent());
                        cont.setStyle("-fx-padding: 5px;");

                        Label auth = new Label(String.valueOf(item.getAuthorId()));
                        auth.setStyle("-fx-padding: 5px;");

                        Label cat = new Label(item.getCategory());
                        cat.setStyle("-fx-padding: 5px;");

                        Label created = new Label(String.valueOf(item.getCreatedAt()));
                        created.setStyle("-fx-padding: 5px;");
                        if (anonymeCheckBox.isSelected()) {
                            auth.setText(" Anonyme");
                        }

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

                        Button deleteButton = new Button("Supprimer");
                        deleteButton.setStyle("-fx-background-color: transparent; -fx-font-size: 14px;");
                        deleteButton.setOnAction(e -> deleteCours(item));

                        buttonBox.getChildren().addAll( deleteButton);

                        // Ajouter les champs au grid
                        grid.add(titre, 0, 0);
                        grid.add(desc, 1, 0);
                        grid.add(cont, 2, 0);
                        grid.add(auth, 3, 0);
                        grid.add(cat, 4, 0);
                        grid.add(created, 5, 0);
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
            headerGrid.add(new Label("Category:"), 4, 0);
            headerGrid.add(new Label("Created:"), 5, 0);
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
    private void deleteCours(Blog blog) {
        try {
            blogService.delete(blog);
            coursObservableList.remove(blog);
            coursListView.setItems(coursObservableList);
            showAlert("Succès", "Blog supprimé avec succès.", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la suppression du Blog.", Alert.AlertType.ERROR);
        }
    }

    private void clearForm() {
        titreField.clear();
        descriptionField.clear();
        contenuField.clear();
        authorIdField.clear();
        imageField.clear();
        categoryField.clear();
        createdField.setValue(null);
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
        ObservableList<Blog> filteredList = FXCollections.observableArrayList();
        for (Blog c : coursObservableList) {
            if (c.getTitle().toLowerCase().contains(searchKeyword)) {
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


    private void populateForm(Blog cours) {
        titreField.setText(cours.getTitle());
        descriptionField.setText(cours.getDescription());
        contenuField.setText(cours.getContent());
        authorIdField.setText(String.valueOf(cours.getAuthorId()));
        categoryField.setText(cours.getCategory());
        createdField.setValue(LocalDate.parse(String.valueOf(cours.getCreatedAt())));

        imageBytes = cours.getImage();
        imageField.setText(""); // Optionnel : afficher un message ou le nom du fichier
    }

    @FXML
    private void updateCours(ActionEvent event) {
        Blog selectedCours = coursListView.getSelectionModel().getSelectedItem();
        if (selectedCours == null) {
            showAlert("Erreur", "Veuillez sélectionner un Cours à mettre à jour.", Alert.AlertType.ERROR);
            return;
        }

        selectedCours.setTitle(titreField.getText());
        selectedCours.setDescription(descriptionField.getText());
        selectedCours.setContent(contenuField.getText());
        selectedCours.setAuthorId(Integer.parseInt(authorIdField.getText()));
        selectedCours.setCategory(categoryField.getText());
        selectedCours.setCreatedAt(Date.valueOf(createdField.getValue()));

        if (!imageField.getText().isEmpty()) {
            selectedCours.setImage(imageBytes);
        }



        try {
            blogService.update(selectedCours);
            refreshCoursList();
            showAlert("Succès", "Le Cours a été mis à jour avec succès.", Alert.AlertType.INFORMATION);
            clearForm();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de la mise à jour du Cours.", Alert.AlertType.ERROR);
        }
    }

}


