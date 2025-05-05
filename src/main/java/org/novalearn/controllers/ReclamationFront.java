package org.novalearn.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.novalearn.Entity.Exercice;
import org.novalearn.Entity.Reclamation;
import org.novalearn.database.DatabaseConnection;
import org.novalearn.services.ExerciceService;
import org.novalearn.services.ReclamationService;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReclamationFront {


        private Connection cnx;


        private final ReclamationService reclamationService;
        private ObservableList<Reclamation> reclamationObservableList;
        final ObservableList data= FXCollections.observableArrayList();

        @FXML
        private VBox rootPane;





        @FXML
        private TextField searchField, titreField, descriptionField,perioField;
        @FXML
        private DatePicker createdField;
        @FXML
        private ComboBox<?> cmbo;
        @FXML
        private ListView<Reclamation> reclamionListView;



        public ReclamationFront() throws SQLException {
            cnx = DatabaseConnection.getConnection();

            this.reclamationService = new ReclamationService();
        }

        private void selectnom() {

            // data.clear();
            try {

                String requete="SELECT id,libelle FROM genre";
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
                String perio = perioField.getText();
                String statue = "EN_attente";

                String sl=cmbo.getSelectionModel().getSelectedItem().toString();
                int i=Integer.parseInt(sl);
                LocalDate createdAt = createdField.getValue();


                Reclamation newCours = new Reclamation(titre, description,perio,statue, createdAt, i);

                try {
                    reclamationService.create(newCours);
                    refreshCoursList();
                    showAlert("Succès", "La reclamation a été ajouté avec succès.", Alert.AlertType.INFORMATION);
                    clearForm();
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Erreur", "Une erreur est survenue lors de l'ajout du ruclation.", Alert.AlertType.ERROR);
                }
            }}
        @FXML
        public void initialize() {
            // Charger les cours existants
            selectnom();

            // Ajouter un écouteur pour détecter la sélection d'un exercice
            reclamionListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    populateForm(newSelection);
                }
            });

            // Rafraîchir la liste des exercices au démarrage
            refreshCoursList();
        }
        private void refreshCoursList() {
            try {
                List<Reclamation> coursList = reclamationService.readAll();
                reclamationObservableList = FXCollections.observableArrayList(coursList);

                reclamionListView.setItems(reclamationObservableList);
                reclamionListView.setCellFactory(param -> new ListCell<>() {
                    @Override
                    protected void updateItem(Reclamation item, boolean empty) {
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
                            Label perio = new Label(item.getPriority());
                            perio.setStyle("-fx-padding: 5px;");
                            Label statue = new Label(item.getStatus());
                            statue.setStyle("-fx-padding: 5px;");

                            Label Genre = new Label(String.valueOf(item.getGenreId()));
                            Genre.setStyle("-fx-padding: 5px;");

                            Label created = new Label(String.valueOf(item.getCreatedAt()));
                            created.setStyle("-fx-padding: 5px;");


                            HBox buttonBox = new HBox(5);


                            Button deleteButton = new Button("Supprimer");
                            deleteButton.setStyle("-fx-background-color: transparent; -fx-font-size: 14px;");
                            deleteButton.setOnAction(e -> deleteCours(item));

                            buttonBox.getChildren().addAll(deleteButton);

                            // Ajouter les champs au grid
                            grid.add(titre, 0, 0);
                            grid.add(desc, 1, 0);
                            grid.add(perio, 2, 0);
                            grid.add(statue, 3, 0);
                            grid.add(Genre, 4, 0);
                            grid.add(created, 5, 0);
                            grid.add(buttonBox, 6, 0);

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
                headerGrid.add(new Label("Piorite:"), 2, 0);
                headerGrid.add(new Label("Statue"), 3, 0);
                headerGrid.add(new Label("Date Creation"), 4, 0);
                headerGrid.add(new Label("Actions:"), 5, 0);

                // === Mettre ListView et en-tête dans VBox ===
                VBox container = new VBox(10);
                container.getChildren().addAll(headerGrid, reclamionListView);

                // === Afficher container à l'endroit voulu dans ta scène ===
                // Remplace "rootPane" par le noeud parent où tu veux insérer le contenu
                rootPane.getChildren().setAll(container);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }




        private void deleteCours(Reclamation cours) {
            try {
                reclamationService.delete(cours);
                reclamationObservableList.remove(cours);
                reclamionListView.setItems(reclamationObservableList);
                showAlert("Succès", "Reclamation supprimé avec succès.", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors de la suppression du Reclamation.", Alert.AlertType.ERROR);
            }
        }

        private void clearForm() {
            titreField.clear();
            descriptionField.clear();
            perioField.clear();
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
        void searchR(ActionEvent event) {
            String searchKeyword = searchField.getText().toLowerCase();
            ObservableList<Reclamation> filteredList = FXCollections.observableArrayList();
            for (Reclamation c : reclamationObservableList) {
                if (c.getTitle().toLowerCase().contains(searchKeyword)) {
                    filteredList.add(c);
                }
            }
            reclamionListView.setItems(filteredList);
        }



        private void populateForm(Reclamation cours) {
            titreField.setText(cours.getTitle());
            descriptionField.setText(cours.getDescription());
            perioField.setText(cours.getPriority());
            createdField.setValue(LocalDate.parse(String.valueOf(cours.getCreatedAt())));

        }

    @FXML
    private void updateCours(ActionEvent event) {
        Reclamation selectedCours = reclamionListView.getSelectionModel().getSelectedItem();
        if (selectedCours == null) {
            showAlert("Erreur", "Veuillez sélectionner une réclamation à mettre à jour.", Alert.AlertType.ERROR);
            return;
        }

        // Vérifier que la réclamation est en cours pour pouvoir modifier
        if (!selectedCours.getStatus().equalsIgnoreCase("En_Attent")) {
            showAlert("Erreur", "Vous ne pouvez modifier la réclamation que si son statut est 'En_Attent'.", Alert.AlertType.ERROR);
            clearForm();
            return;
        }

        // Si le statut est correct, alors on continue
        selectedCours.setTitle(titreField.getText());
        selectedCours.setDescription(descriptionField.getText());
        selectedCours.setPriority(perioField.getText());
        selectedCours.setCreatedAt(Date.valueOf(createdField.getValue()));

        try {
            reclamationService.update(selectedCours);
            refreshCoursList();
            showAlert("Succès", "La réclamation a été mise à jour avec succès.", Alert.AlertType.INFORMATION);
            clearForm();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de la mise à jour de la réclamation.", Alert.AlertType.ERROR);
        }
    }

}
