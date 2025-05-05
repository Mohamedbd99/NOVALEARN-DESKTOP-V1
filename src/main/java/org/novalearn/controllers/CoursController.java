package org.novalearn.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import org.novalearn.Entity.Cours;
import org.novalearn.Entity.Exercice;
import org.novalearn.services.coursEexercice.CoursService;
import javafx.geometry.Pos;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CoursController {

    @FXML
    private ComboBox<String> sortComboBox;
    private final CoursService coursService;
    private ObservableList<Cours> coursObservableList;

    @FXML
    private ListView<Cours> coursListView;
    @FXML
    private AnchorPane mainContent;

    public CoursController() throws SQLException {
        this.coursService = new CoursService();
    }

    @FXML
    public void initialize() {


        // Rafraîchir la liste des Cours au démarrage
        refreshCoursList();
    }

    @FXML
    private FlowPane coursFlowPane;
    public void refreshCoursList() {
        try {
            coursFlowPane.getChildren().clear();

            List<Cours> coursList = coursService.readAll();

            for (Cours item : coursList) {
                VBox card = new VBox(10);
                card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ccc; -fx-border-radius: 10px;" +
                        " -fx-background-radius: 10px; -fx-padding: 10px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 4);");
                card.setPrefWidth(280);

                // === Badge (Populaire أو Nouveau) ===
                Label badge = new Label();
                badge.setStyle("-fx-background-color: #c7a1f6; -fx-text-fill: white; -fx-padding: 3px 8px; -fx-background-radius: 5px;");
                if (item.getNombreDeTelechargements() > 10) {
                    badge.setText("🔥 Populaire");
                } else {
                    badge.setText("🆕 Nouveau");
                }

                // === Image ===
                if (item.getImage() != null) {
                    Image image = new Image(new ByteArrayInputStream(item.getImage()));
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(260);
                    imageView.setFitHeight(150);
                    imageView.setPreserveRatio(true);
                    card.getChildren().addAll(badge, imageView);
                }

              
                Label titre = new Label(item.getTitre());
                titre.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #222;");

                // === Description ===
                Label desc = new Label(item.getDescription());
                desc.setWrapText(true);
                desc.setMaxWidth(260);
                desc.setStyle("-fx-font-size: 13px; -fx-text-fill: #444;");

                // === Contenu ===
                Label contenu = new Label("Contenu: " + item.getContenu());
                contenu.setWrapText(true);
                contenu.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

                // === Author ID ===
                Label author = new Label("Auteur ID: " + item.getAuthorId());
                author.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

                // === Téléchargements ===
                Label telechargements = new Label("Téléchargements: " + item.getNombreDeTelechargements());
                telechargements.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

                // === Likes ===
                Label likes = new Label("Likes: " + item.getNbrLike());
                likes.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
                Label duration = new Label("Durée: 3h 20m");
                duration.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

                int rating = Math.min(5, Math.max(1, item.getNbrLike() / 10));
                HBox stars = new HBox(2);
                for (int i = 0; i < 5; i++) {
                    Label star = new Label(i < rating ? "★" : "☆");
                    star.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 14px;");
                    stars.getChildren().add(star);
                }

                Button voirPlus = new Button("Voir Plus");
                voirPlus.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                voirPlus.setOnAction(e -> showCoursDetails(item));
                HBox likeBox = new HBox(5);
                likeBox.setAlignment(Pos.CENTER_LEFT);

                ImageView heartIcon = new ImageView(new Image("org/novalearn/ic_love_.png"));
                heartIcon.setFitWidth(20);
                heartIcon.setFitHeight(20);

                Label likeLabel = new Label(String.valueOf(item.getNbrLike()));
                likeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #e74c3c;");

                heartIcon.setOnMouseClicked(e -> {
                    try {
                        item.setNbrLike(item.getNbrLike() + 1);
                        coursService.incrementLikes(item.getId()); // تحتاج تنفذها في الخدمة
                        likeLabel.setText(String.valueOf(item.getNbrLike()));
                        heartIcon.setImage(new Image("org/novalearn/ic_love_.png")); // تغير للقلب المملوء
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });

                likeBox.getChildren().addAll(heartIcon, likeLabel);


                card.getChildren().addAll(titre, desc,  likeBox,voirPlus);

                coursFlowPane.getChildren().add(card);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showCoursDetails(Cours item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/novalearn/cours_views/cours-details.fxml"));
            AnchorPane detailsView = loader.load();

            CoursDetails controller = loader.getController();
            controller.setCoursData(item);

            mainContent.getChildren().setAll(detailsView);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleSortSelection() {
        String selectedOption = sortComboBox.getValue();
        if (selectedOption == null) return;

        try {
            List<Cours> sortedList;
            if (selectedOption.equals("Par Likes")) {
                sortedList = coursService.readAllSortedByLikes();
            } else if (selectedOption.equals("Par Téléchargements")) {
                sortedList = coursService.readAllSortedByDownloads();
            } else {
                sortedList = coursService.readAll(); // fallback
            }

            // تحديث العرض
            updateCoursFlowPane(sortedList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private VBox buildCoursCard(Cours item) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ccc; -fx-border-radius: 10px;" +
                " -fx-background-radius: 10px; -fx-padding: 10px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 4);");
        card.setPrefWidth(280);

        Label badge = new Label();
        badge.setStyle("-fx-background-color: #c7a1f6; -fx-text-fill: white; -fx-padding: 3px 8px; -fx-background-radius: 5px;");
        if (item.getNombreDeTelechargements() > 10) {
            badge.setText("🔥 Populaire");
        } else {
            badge.setText("🆕 Nouveau");
        }

        if (item.getImage() != null) {
            Image image = new Image(new ByteArrayInputStream(item.getImage()));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(260);
            imageView.setFitHeight(150);
            imageView.setPreserveRatio(true);
            card.getChildren().addAll(badge, imageView);
        }

        Label titre = new Label(item.getTitre());
        titre.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #222;");

        Label desc = new Label(item.getDescription());
        desc.setWrapText(true);
        desc.setMaxWidth(260);
        desc.setStyle("-fx-font-size: 13px; -fx-text-fill: #444;");

        Label contenu = new Label("Contenu: " + item.getContenu());
        contenu.setWrapText(true);
        contenu.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        Label author = new Label("Auteur ID: " + item.getAuthorId());
        author.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        Label telechargements = new Label("Téléchargements: " + item.getNombreDeTelechargements());
        telechargements.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        Label likes = new Label("Likes: " + item.getNbrLike());
        likes.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        Label duration = new Label("Durée: 3h 20m");
        duration.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");



        Button voirPlus = new Button("Voir Plus");
        voirPlus.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        voirPlus.setOnAction(e -> showCoursDetails(item));
        HBox likeBox = new HBox(5);
        likeBox.setAlignment(Pos.CENTER_LEFT);

        ImageView heartIcon = new ImageView(new Image("org/novalearn/ic_love_.png")); // البداية بالقلب الفارغ
        heartIcon.setFitWidth(20);
        heartIcon.setFitHeight(20);

        Label likeLabel = new Label(String.valueOf(item.getNbrLike()));
        likeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #e74c3c;");

        heartIcon.setOnMouseClicked(e -> {
            try {
                item.setNbrLike(item.getNbrLike() + 1);
                coursService.incrementLikes(item.getId());
                likeLabel.setText(String.valueOf(item.getNbrLike()));
                heartIcon.setImage(new Image("org/novalearn/ic_love_.png"));
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        likeBox.getChildren().addAll(heartIcon, likeLabel);

        card.getChildren().addAll(titre, desc, likeBox, voirPlus);

        return card;
    }

    private void updateCoursFlowPane(List<Cours> coursList) {
        coursFlowPane.getChildren().clear();
        for (Cours item : coursList) {
            VBox card = buildCoursCard(item);
            coursFlowPane.getChildren().add(card);
        }
    }


}
