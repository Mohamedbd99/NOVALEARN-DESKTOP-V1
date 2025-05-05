package org.novalearn.controllers.cours;

import databaseConnection.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.novalearn.Entity.Exercice;
import org.novalearn.service.ExerciceService;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExerciceAdmin {

    private Connection cnx;


    private final ExerciceService exerciceService;
    private ObservableList<Exercice> coursObservableList;
    final ObservableList data= FXCollections.observableArrayList();

    @FXML
    private VBox rootPane;





    @FXML
    private TextField searchField, titreField, descriptionField;
    @FXML
    private DatePicker createdField;
    @FXML
    private ComboBox<?> cmbo;
    @FXML
    private ListView<Exercice> coursListView;



    public ExerciceAdmin() throws SQLException {
        cnx = DatabaseConnection.getConnection();

        this.exerciceService = new ExerciceService();
    }

    private void selectnom() {

        // data.clear();
        try {

            String requete="SELECT id,titre FROM course";
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(requete);
            while(rs.next()){


                data.add(rs.getString(1));
                data.add(rs.getString(2));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        cmbo.setItems(null);
        cmbo.setItems(data);

    }
    private boolean validateTitre(){
        Pattern p = Pattern.compile("[a-zA-Z]+");
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
        Pattern p = Pattern.compile("[a-zA-Z]+");
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
        if (validateTitre()&& validatedesc()){
        String titre = titreField.getText();
        String description = descriptionField.getText();
        String sl=cmbo.getSelectionModel().getSelectedItem().toString();
        int i=Integer.parseInt(sl);
        LocalDate createdAt = createdField.getValue();


        Exercice newCours = new Exercice(titre, description, i, createdAt);

        try {
            exerciceService.create(newCours);
            refreshCoursList();
            showAlert("Succès", "L'exercice a été ajouté avec succès.", Alert.AlertType.INFORMATION);
            clearForm();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de l'ajout du exercice.", Alert.AlertType.ERROR);
        }
    }}
    @FXML
    public void initialize() {
        // Charger les cours existants
        selectnom();

        // Ajouter un écouteur pour détecter la sélection d'un exercice
        coursListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateForm(newSelection);
            }
        });

        // Rafraîchir la liste des exercices au démarrage
        refreshCoursList();
    }
    private void refreshCoursList() {
        try {
            List<Exercice> coursList = exerciceService.readAll();
            coursObservableList = FXCollections.observableArrayList(coursList);

            coursListView.setItems(coursObservableList);
            coursListView.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Exercice item, boolean empty) {
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

                        Label cours = new Label(String.valueOf(item.getCourseId()));
                        cours.setStyle("-fx-padding: 5px;");

                        Label created = new Label(String.valueOf(item.getCreatedAt()));
                        created.setStyle("-fx-padding: 5px;");



                        // Image


                        // Boutons
                        HBox buttonBox = new HBox(5);


                        Button deleteButton = new Button("Supprimer");
                        deleteButton.setStyle("-fx-background-color: transparent; -fx-font-size: 14px;");
                        deleteButton.setOnAction(e -> deleteCours(item));

                        buttonBox.getChildren().addAll(deleteButton);

                        // Ajouter les champs au grid
                        grid.add(titre, 0, 0);
                        grid.add(desc, 1, 0);
                        grid.add(cours, 2, 0);
                        grid.add(created, 3, 0);
                        grid.add(buttonBox, 4, 0);

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
            headerGrid.add(new Label("Course:"), 2, 0);
            headerGrid.add(new Label("Date Creation"), 3, 0);
            headerGrid.add(new Label("Actions:"), 4, 0);

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




    private void deleteCours(Exercice cours) {
        try {
            exerciceService.delete(cours);
            coursObservableList.remove(cours);
            coursListView.setItems(coursObservableList);
            showAlert("Succès", "Cours supprimé avec succès.", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la suppression du cours.", Alert.AlertType.ERROR);
        }
    }

    private void clearForm() {
        titreField.clear();
        descriptionField.clear();
        createdField.setValue(null);
        cmbo.setItems(null);

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
        ObservableList<Exercice> filteredList = FXCollections.observableArrayList();
        for (Exercice c : coursObservableList) {
            if (c.getTitre().toLowerCase().contains(searchKeyword)) {
                filteredList.add(c);
            }
        }
        coursListView.setItems(filteredList);
    }



    private void populateForm(Exercice cours) {
        titreField.setText(cours.getTitre());
        descriptionField.setText(cours.getDescription());
        createdField.setValue(LocalDate.parse(String.valueOf(cours.getCreatedAt())));

    }

    @FXML
    private void updateCours(ActionEvent event) {
        Exercice selectedCours = coursListView.getSelectionModel().getSelectedItem();
        if (selectedCours == null) {
            showAlert("Erreur", "Veuillez sélectionner un cours à mettre à jour.", Alert.AlertType.ERROR);
            return;
        }

        selectedCours.setTitre(titreField.getText());
        selectedCours.setDescription(descriptionField.getText());
        selectedCours.setCreatedAt(Date.valueOf(createdField.getValue()));


        try {
            exerciceService.update(selectedCours);
            refreshCoursList();
            showAlert("Succès", "Le cours a été mis à jour avec succès.", Alert.AlertType.INFORMATION);
            clearForm();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de la mise à jour du cours.", Alert.AlertType.ERROR);
        }
    }
}
