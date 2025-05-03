package org.novalearn.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.novalearn.Entity.Blog;
import org.novalearn.Entity.Commentaire;
import org.novalearn.services.CommentaireService;

import java.io.ByteArrayInputStream;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class BlogItem {
    @FXML
    private Button voirPlusBtn;
    @FXML
    private VBox commentContainer;
    @FXML
    private void handleVoirPlusCommentaires() {
        loadCommentsPage();
    }
    @FXML
    private Label username,commentCountLabel;
    @FXML
    private Label date;
    @FXML
    private Label desc;
    @FXML
    private ImageView imgPost;
    @FXML private TextField commentField;
    private static final int COMMENTS_PER_PAGE = 3;
    private int currentPage = 0;

    private Blog currentBlog;
    private Blog b;
    private final CommentaireService commentaireService = new CommentaireService();

    private void loadCommentsPage() {

        CommentaireService commentaireService = new CommentaireService();

        List<Commentaire> comments = commentaireService.getCommentsByBlogIdPaginated(
                currentBlog.getId(), currentPage * COMMENTS_PER_PAGE, COMMENTS_PER_PAGE
        );
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

        for (Commentaire c : comments) {
            HBox commentBox = new HBox();

            Date timeAgo = (c.getCreatedAt());

            // Affichage sans l'heure pour le Label
            String commentText = c.getContenu();
            Label commentLabel = new Label(commentText + "\n🕒 " + timeAgo);

            Button editButton = new Button("Modifier");
            Button deleteButton = new Button("Supprimer");

            // Créer une HBox pour afficher les boutons de modification et suppression
            HBox actionsBox = new HBox(10);
            actionsBox.getChildren().addAll(editButton, deleteButton);
            actionsBox.setVisible(false);  // Par défaut, les boutons sont cachés

            // Ajouter les éléments dans la HBox pour le commentaire
            commentBox.setSpacing(10);
            commentBox.getChildren().add(commentLabel);
            commentBox.getChildren().add(actionsBox);  // Ajouter les boutons dans la HBox des commentaires

            // Ajouter la HBox dans le conteneur de commentaires
            commentContainer.getChildren().add(commentBox);

            // Action pour afficher/cacher les boutons Modifier et Supprimer lorsque l'utilisateur clique sur le commentaire
            commentLabel.setOnMouseClicked(e -> {
                // Si les boutons sont déjà visibles, les cacher
                if (actionsBox.isVisible()) {
                    actionsBox.setVisible(false);
                } else {
                    // Sinon, les afficher
                    actionsBox.setVisible(true);
                }
            });

            // Action pour modifier le commentaire
            editButton.setOnAction(e -> {
                // Créer un TextField avec le contenu du commentaire
                TextField textField = new TextField(commentText);  // Assurez-vous de n'afficher que le texte du commentaire ici.
                textField.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-size: 14;");

                // Créer un bouton "OK" pour valider la modification
                Button okButton = new Button("OK");

                // Créer un nouveau HBox pour afficher le TextField et le bouton "OK"
                HBox editBox = new HBox(10);
                editBox.getChildren().addAll(textField, okButton);

                // Ajouter ce HBox à un autre conteneur, par exemple à la fin des commentaires
                commentContainer.getChildren().add(editBox);

                // Masquer les boutons de modification et suppression pendant l'édition
                actionsBox.setVisible(false);

                // Action pour valider la modification
                okButton.setOnAction(event -> {
                    String newContent = textField.getText();
                    c.setContenu(newContent);

                    // Mise à jour de la base de données
                    try {
                        commentaireService.update(c);

                        // Mettre à jour le label du commentaire avec le texte modifié
                        commentLabel.setText(newContent + "\n🕒 " + timeAgo);  // Garder le format de date intact

                        // Retirer le HBox d'édition après la modification
                        commentContainer.getChildren().remove(editBox);

                        // Réafficher le label du commentaire avec le texte mis à jour
                        commentBox.getChildren().set(0, commentLabel);

                        // Cacher les boutons Modifier et Supprimer après la modification
                        actionsBox.setVisible(false);

                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });
            });

            // Action pour supprimer le commentaire
            deleteButton.setOnAction(e -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation de suppression");
                alert.setHeaderText(null);
                alert.setContentText("Voulez-vous vraiment supprimer ce commentaire ?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    try {
                        commentaireService.delete(c);
                        commentContainer.getChildren().remove(commentBox);
                        refreshCommentCount();

                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }

                }
            });
        }

        if (comments.size() < COMMENTS_PER_PAGE) {
            voirPlusBtn.setVisible(false);
        } else {
            voirPlusBtn.setVisible(true);
        }

        currentPage++;
    }

    @FXML
    private void handleAjouterCommentaire() throws SQLException {
        String contenu = commentField.getText().trim();

        if (!contenu.isEmpty()) {
            // Créer un nouveau commentaire avec le contenu et la date actuelle
            Commentaire commentaire = new Commentaire();
            commentaire.setContenu(contenu);
            commentaire.setBlogid(b.getId());
            commentaire.setCreatedAt(Date.valueOf(LocalDate.now()));

            // Sauvegarder dans la base de données
            commentaireService.create(commentaire);

            // Récupérer l'ID du dernier commentaire inséré
            int lastId = commentaireService.getLastInsertId();
            commentaire.setId(lastId);

            // Formatage de la date
            Date timeAgo = commentaire.getCreatedAt();

            // Maintenant on crée une vraie boîte avec boutons Modifier/Supprimer
            HBox commentBox = new HBox();
            Label commentLabel = new Label(contenu + "\n🕒 " + timeAgo);

            Button editButton = new Button("Modifier");
            Button deleteButton = new Button("Supprimer");

            HBox actionsBox = new HBox(10, editButton, deleteButton);
            actionsBox.setVisible(false);

            commentBox.setSpacing(10);
            commentBox.getChildren().addAll(commentLabel, actionsBox);

            commentContainer.getChildren().add(commentBox);

            // Clic sur le texte pour afficher les boutons
            commentLabel.setOnMouseClicked(e -> {
                actionsBox.setVisible(!actionsBox.isVisible());
            });

            // Modifier commentaire
            editButton.setOnAction(e -> {
                TextField textField = new TextField(contenu);
                textField.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-size: 14;");

                Button okButton = new Button("OK");
                HBox editBox = new HBox(10, textField, okButton);

                commentContainer.getChildren().add(editBox);
                actionsBox.setVisible(false);

                okButton.setOnAction(event -> {
                    String newContent = textField.getText();
                    commentaire.setContenu(newContent);

                    try {
                        commentaireService.update(commentaire);
                        commentLabel.setText(newContent + "\n🕒 " + timeAgo);
                        commentContainer.getChildren().remove(editBox);
                        commentBox.getChildren().set(0, commentLabel);
                        actionsBox.setVisible(false);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });
            });

            // Supprimer commentaire
            deleteButton.setOnAction(e -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation de suppression");
                alert.setHeaderText(null);
                alert.setContentText("Voulez-vous vraiment supprimer ce commentaire ?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    try {
                        commentaireService.delete(commentaire);
                        commentContainer.getChildren().remove(commentBox);
                        refreshCommentCount();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            // Rafraîchir compteur commentaires
            refreshCommentCount();

            // Nettoyer le champ texte
            commentField.clear();
        }
    }


    public void setBlogData(Blog blog) {
        this.b=blog;
        this.currentBlog = blog;
        this.currentPage = 0;
        CommentaireService cs = new CommentaireService();

        int commentCount = cs.countCommentairesByBlogId(blog.getId());

        commentCountLabel.setText(commentCount + " commentaires");
        loadCommentsPage();
        username.setText(blog.getEstAnonyme() ? "Utilisateur anonyme" : "Auteur #" + blog.getAuthorId());
        date.setText(new SimpleDateFormat("dd MMM yyyy").format(blog.getCreatedAt()));
        desc.setText(blog.getDescription());

        if (blog.getImage() != null) {
            Image image = new Image(new ByteArrayInputStream(blog.getImage()));
            imgPost.setImage(image);
        }
    }

    private void refreshCommentCount() {
        int updatedCount = commentaireService.countCommentairesByBlogId(b.getId());
        commentCountLabel.setText(updatedCount + " commentaires");
    }


}
