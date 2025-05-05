// File: C:/Users/moham/IdeaProjects/untitled/src/main/java/org/novalearn/controllers/Quiz/QuizzesController.java
package org.novalearn.controllers.Quiz;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.checkout.Session;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.web.WebView;
import org.novalearn.services.quiz.QuizService;
import org.novalearn.MainApp;
import java.awt.Desktop;
import java.net.URI;

import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class QuizzesController {
    @FXML private Button btnMath, btnHistory, btnScience, btnGeo;
    @FXML private HBox difficultyBox;
    @FXML private VBox questionsContainer;
    @FXML private Button btnSubmit;
    @FXML private Rectangle overlay;
    @FXML private ProgressIndicator loader;
    @FXML private StackPane rootPane;
    @FXML private WebView checkoutWebView;

    private QuizService quizService;
    private List<QuizDTO> currentQuizzes;
    private String currentQuizId;
    private ObjectMapper objectMapper;

    @FXML
    public void initialize() {
        this.quizService = new QuizService("9");
        this.objectMapper = new ObjectMapper();
        clearState();
    }

    private void clearScoreOverlay() {
        rootPane.getChildren().removeIf(n -> n.getStyleClass().contains("score-label"));
    }

    private void clearState() {
        questionsContainer.getChildren().clear();
        btnSubmit.setVisible(false);
        difficultyBox.getChildren().clear();
        difficultyBox.setVisible(false);
        overlay.setVisible(false);
        loader.setVisible(false);
    }

    @FXML private void onMath(ActionEvent e)    { loadQuizzes("math"); }
    @FXML private void onHistory(ActionEvent e) { loadQuizzes("history"); }
    @FXML private void onScience(ActionEvent e) { loadQuizzes("science"); }
    @FXML private void onGeo(ActionEvent e)     { loadQuizzes("geo"); }

    private void showLoader() { overlay.setVisible(true); loader.setVisible(true); }
    private void hideLoader() { loader.setVisible(false); overlay.setVisible(false); }

    private void loadQuizzes(String subject) {
        clearState(); clearScoreOverlay(); showLoader();
        CompletableFuture.runAsync(() -> {
            try {
                // 4.1 if not yet bought, show buy prompt
                if (!quizService.hasPurchased(subject)) {
                    Platform.runLater(() -> showPurchasePrompt(subject));
                    return;
                }
                // 4.2 otherwise fall back to existing
                List<QuizDTO> list = quizService.loadLocalQuizzes(subject);
                currentQuizzes = list.isEmpty()
                        ? quizService.fetchRemoteAndPersist(subject)
                        : list;
                Platform.runLater(() -> {
                    setupDifficultyFilters();
                    hideLoader();
                });
            } catch (Exception ex) {
                Platform.runLater(() -> { hideLoader(); showError("Failed: " + ex.getMessage()); });
            }
        });
    }

    private void showPurchasePrompt(String subject) {
        hideLoader();
        questionsContainer.getChildren().clear();
        Label msg = new Label("You need to buy this quiz to get access:");
        Button buy = new Button("💳 Buy Quiz");
        buy.setOnAction(evt -> handlePurchase(subject));
        questionsContainer.getChildren().addAll(msg, buy);
    }
    private void handlePurchase(String subject) {
        showLoader();
        CompletableFuture.runAsync(() -> {
            try {
                Session session = quizService.createCheckoutSession(subject);
                String checkoutUrl = session.getUrl();
                String stripeSessionId = session.getId();

                Platform.runLater(() -> {
                    hideLoader();
                    // 1) show WebView
                    checkoutWebView.setVisible(true);
                    var engine = checkoutWebView.getEngine();
                    engine.load(checkoutUrl);

                    // 2) intercept navigation
                    engine.locationProperty().addListener((obs, oldLoc, newLoc) -> {
                        if (newLoc.startsWith("https://example.com/success")) {
                            // extract session_id
                            var uri = URI.create(newLoc);
                            var sid = Arrays.stream(uri.getQuery().split("&"))
                                    .filter(p -> p.startsWith("session_id="))
                                    .map(p -> p.substring(11))
                                    .findFirst().orElse(stripeSessionId);
                            // hide WebView
                            checkoutWebView.setVisible(false);
                            // confirm purchase
                            showLoader();
                            CompletableFuture.runAsync(() -> {
                                try {
                                    boolean paid = quizService.confirmPurchase(sid);

                                    // now switch back to JavaFX thread for any UI work
                                    Platform.runLater(() -> {
                                        hideLoader();
                                        if (paid) {
                                            loadQuizzes(subject);
                                        } else {
                                            showError("Payment was not confirmed.");
                                        }
                                    });

                                } catch (Exception e) {
                                    Platform.runLater(() -> {
                                        hideLoader();
                                        showError("Error confirming payment: " + e.getMessage());
                                    });
                                }
                            });
                        }
                        else if (newLoc.startsWith("https://example.com/cancel")) {
                            // user cancelled
                            checkoutWebView.setVisible(false);
                            showError("Payment cancelled.");
                        }
                    });
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    hideLoader();
                    showError("Purchase failed: " + ex.getMessage());
                });
            }
        });
    }

    private void setupDifficultyFilters() {
        difficultyBox.setVisible(true); difficultyBox.getChildren().clear();
        for (QuizDTO qt : currentQuizzes) {
            Button b = new Button(capitalize(qt.getDifficulty()));
            b.setOnAction(evt -> {
                try { showQuestionsForDifficulty(qt); }
                catch (SQLException e) { showError("Error showing quiz: " + e.getMessage()); }
            });
            difficultyBox.getChildren().add(b);
        }
        if (!difficultyBox.getChildren().isEmpty()) ((Button)difficultyBox.getChildren().get(0)).fire();
    }

    private void showQuestionsForDifficulty(QuizDTO quiz) throws SQLException {
        showLoader(); clearScoreOverlay(); questionsContainer.getChildren().clear();
        currentQuizId = quiz.getQuizId();

        // Generate audio button
        Button btnAudio = new Button("🎧 Generate audio");
        btnAudio.setDisable(quizService.isAudioReady(currentQuizId));
        btnAudio.setOnAction(evt -> {
            showLoader();
            CompletableFuture.runAsync(() -> {
                try { quizService.generateAudioFiles(currentQuizId); }
                catch (Exception e) { throw new RuntimeException(e); }
            }).whenComplete((v,err) -> Platform.runLater(() -> {
                hideLoader();
                if (err != null) showError("Audio generation failed: " + err.getMessage());
                else try { showQuestionsForDifficulty(quiz); }
                catch (SQLException e) { showError("Refresh error: " + e.getMessage()); }
            }));
        });
        questionsContainer.getChildren().add(btnAudio);

        if (quizService.hasSubmission(currentQuizId)) {
            int prev = quizService.fetchScore(currentQuizId);
            int total = quizService.loadQuestions(currentQuizId).size();
            Label lbl = new Label("Already scored " + prev + "/" + total);
            lbl.getStyleClass().add("score-label");
            StackPane.setAlignment(lbl, Pos.CENTER); rootPane.getChildren().add(lbl);
            hideLoader(); return;
        }

        List<QuestionDTO> qs = quizService.loadQuestions(currentQuizId);
        renderQuestions(qs); hideLoader();
    }

    private void renderQuestions(List<QuestionDTO> questions) {
        ToggleGroup[] groups = new ToggleGroup[questions.size()];
        for (int i=0;i<questions.size();i++){
            QuestionDTO q=questions.get(i); ToggleGroup tg=new ToggleGroup(); groups[i]=tg;
            VBox v=new VBox(5,new Label((i+1)+". "+q.getQuestion()));
            RadioButton rA=new RadioButton("A: "+q.getA()),rB=new RadioButton("B: "+q.getB()),rC=new RadioButton("C: "+q.getC());
            rA.setToggleGroup(tg);rB.setToggleGroup(tg);rC.setToggleGroup(tg); v.getChildren().addAll(rA,rB,rC);
            if(q.getAudio()!=null&&!q.getAudio().isEmpty()){ Button play=new Button("🔊");
                MediaPlayer mp=new MediaPlayer(new Media(Paths.get("src/main/AudioQuestion/en/"+q.getAudio()).toUri().toString()));
                play.setOnAction(e->mp.play()); v.getChildren().add(play);
            }
            questionsContainer.getChildren().add(v);
        }
        btnSubmit.setVisible(true);
        btnSubmit.setOnAction(e->handleSubmit(groups,questions));
    }

    private void handleSubmit(ToggleGroup[] groups,List<QuestionDTO> questions){
        for(ToggleGroup tg:groups) if(tg.getSelectedToggle()==null){ showAlert(Alert.AlertType.WARNING,"Answer all"); return; }
        int score=0; List<Map<String,String>> resp=new ArrayList<>();
        for(int i=0;i<questions.size();i++){ QuestionDTO q=questions.get(i);
            String c=((RadioButton)groups[i].getSelectedToggle()).getText().substring(0,1).toLowerCase();
            if(c.equalsIgnoreCase(q.getCorrection())) score++;
            resp.add(Map.of("questionId",q.getQuestionId(),"answer",c)); }
        try(PreparedStatement ps=MainApp.getDbConnection()
                .prepareStatement("INSERT INTO novalearn.quiz_submission(id,responses,score,submitted_at,quiz_id)VALUES(?,?,?,?,?)")){
            ps.setLong(1,Instant.now().toEpochMilli());ps.setString(2,objectMapper.writeValueAsString(resp));
            ps.setInt(3,score);ps.setTimestamp(4,java.sql.Timestamp.from(Instant.now()));ps.setString(5,currentQuizId);ps.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION,"Scored "+score+" of "+questions.size());
        }catch(Exception ex){ showError("Submit failed: "+ex.getMessage()); }
    }

    private void showError(String m){ Platform.runLater(()->new Alert(Alert.AlertType.ERROR,m).showAndWait()); }
    private void showAlert(Alert.AlertType t,String m){ Platform.runLater(()->new Alert(t,m).showAndWait()); }
    private String capitalize(String s){ return s.isEmpty()?s:s.substring(0,1).toUpperCase()+s.substring(1); }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResponseWrapper{ @JsonProperty("generated_json")private GeneratedJson gen; public GeneratedJson getGeneratedJson(){return gen;}public void setGeneratedJson(GeneratedJson g){this.gen=g;} }
    public static class GeneratedJson{private List<QuizDTO> quizzes;public List<QuizDTO> getQuizzes(){return quizzes;}public void setQuizzes(List<QuizDTO>q){this.quizzes=q;}}
    public static class QuizDTO{ @JsonProperty("quiz_id")private String quizId;private String difficulty;@JsonProperty("questions")private List<QuestionDTO>questions; public String getQuizId(){return quizId;}public void setQuizId(String id){this.quizId=id;}public String getDifficulty(){return difficulty;}public void setDifficulty(String d){this.difficulty=d;}public List<QuestionDTO>getQuestions(){return questions;}public void setQuestions(List<QuestionDTO>qs){this.questions=qs;} }
    public static class QuestionDTO{ @JsonProperty("question_id")private String questionId;private String question,correction,a,b,c,audio;public void setAudio(String x){audio=x;}public String getAudio(){return audio;}@JsonProperty("options")private void unpackOptions(Map<String,String>o){a=o.get("a");b=o.get("b");c=o.get("c");}public String getQuestionId(){return questionId;}public void setQuestionId(String id){this.questionId=id;}public String getQuestion(){return question;}public void setQuestion(String q){this.question=q;}public String getCorrection(){return correction;}public void setCorrection(String c){this.correction=c;}public String getA(){return a;}public void setA(String x){a=x;}public String getB(){return b;}public void setB(String x){b=x;}public String getC(){return c;}public void setC(String x){c=x;} }
}

