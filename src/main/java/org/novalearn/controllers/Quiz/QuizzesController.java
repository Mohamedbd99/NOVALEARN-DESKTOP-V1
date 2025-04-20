package org.novalearn.controllers.Quiz;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.novalearn.MainApp;

public class QuizzesController {
    @FXML private Button btnMath, btnHistory, btnScience, btnGeo;
    @FXML private HBox difficultyBox;
    @FXML private VBox questionsContainer;
    @FXML private Button btnSubmit;
    @FXML private Rectangle overlay;
    @FXML private ProgressIndicator loader;
    @FXML private StackPane rootPane;

    private final String userId = "10";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private List<QuizDTO> currentQuizzes = new ArrayList<>();
    private String currentQuizId;
    private void clearScoreOverlay() {
        overlay.setVisible(false);
        rootPane.getChildren().removeIf(node ->
                node.getStyleClass().contains("score-label")
        );
    }

    @FXML
    public void initialize() {
        System.out.println("[DEBUG] initialize()");
        questionsContainer.getChildren().clear();
        btnSubmit.setVisible(false);
        difficultyBox.getChildren().clear();
        difficultyBox.setVisible(false);
        hideLoader();
    }

    @FXML public void onMath(ActionEvent e)    { loadQuizzes("math"); }
    @FXML public void onHistory(ActionEvent e) { loadQuizzes("history"); }
    @FXML public void onScience(ActionEvent e) { loadQuizzes("science"); }
    @FXML public void onGeo(ActionEvent e)     { loadQuizzes("geo"); }

    private void showLoader() {
        Platform.runLater(() -> {
            overlay.setVisible(true);
            loader.setVisible(true);
        });
    }

    private void hideLoader() {
        Platform.runLater(() -> {
            loader.setVisible(false);
            overlay.setVisible(false);
        });
    }

    private void loadQuizzes(String subject) {
        clearScoreOverlay();

        System.out.println("[DEBUG] loadQuizzes(subject=" + subject + ")");
        showLoader();
        try {
            List<QuizDTO> existing = loadQuizzesFromDB(subject);
            if (!existing.isEmpty()) {
                System.out.println("[DEBUG] found " + existing.size() + " quizzes in DB");
                currentQuizzes = existing;
                setupDifficultyFilters(existing);
                rootPane.getChildren().removeIf(node ->
                        node.getStyleClass().contains("score-label")
                );

                hideLoader();

            } else {
                System.out.println("[DEBUG] no existing quizzes, generating new ones");
                generateAndSave(subject);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Failed to load quizzes: " + ex.getMessage());
        } finally {
        }
    }

    private void generateAndSave(String subject) {
        showLoader();

        System.out.println("[DEBUG] generateAndSave(subject=" + subject + ")");
        String url = "http://localhost:8001/generate/" + subject;
        httpClient.sendAsync(
                        HttpRequest.newBuilder().uri(URI.create(url)).GET().build(),
                        HttpResponse.BodyHandlers.ofString()
                ).thenApply(HttpResponse::body)
                .thenAccept(body -> {
                    try {
                        System.out.println("[DEBUG] generator response: " + body);
                        ResponseWrapper w = objectMapper.readValue(body, ResponseWrapper.class);
                        persistToDatabase(w.getGeneratedJson().getQuizzes(), subject);
                        currentQuizzes = w.getGeneratedJson().getQuizzes();
                        Platform.runLater(() -> setupDifficultyFilters(currentQuizzes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Platform.runLater(() -> showError("Failed to generate quizzes: " + e.getMessage()));
                    } finally {
                    }
                })
                .exceptionally(ex -> {
                    hideLoader();

                    ex.printStackTrace();
                    Platform.runLater(() -> {
                        showError("Generator service error: " + ex.getMessage());
                    });
                    return null;
                });
        hideLoader();

    }

    private void persistToDatabase(List<QuizDTO> quizzes, String subject) {
        System.out.println("[DEBUG] persistToDatabase(" + quizzes.size() + " quizzes, subject=" + subject + ")");
        String insertQuizSql    = "INSERT INTO novalearn.quiz(quiz_id,difficulty,matiere,user_id) VALUES(?,?,?,?)";
        String insertQuestionSql= "INSERT INTO novalearn.question(question_id,quiz_id,question,correction,a,b,c) VALUES(?,?,?,?,?,?,?)";

        Connection conn = MainApp.getDbConnection();
        try {
            for (QuizDTO quizDto : quizzes) {
                String newQuizId = UUID.randomUUID().toString();
                try (PreparedStatement psQuiz = conn.prepareStatement(insertQuizSql)) {
                    psQuiz.setString(1, newQuizId);
                    psQuiz.setString(2, quizDto.getDifficulty());
                    psQuiz.setString(3, subject);
                    psQuiz.setLong(4, Long.parseLong(userId));
                    psQuiz.executeUpdate();
                }
                quizDto.setQuizId(newQuizId);
                for (QuestionDTO q : quizDto.getQuestions()) {
                    String newQuestionId = UUID.randomUUID().toString();
                    try (PreparedStatement psQ = conn.prepareStatement(insertQuestionSql)) {
                        psQ.setString(1, newQuestionId);
                        psQ.setString(2, newQuizId);
                        psQ.setString(3, q.getQuestion());
                        psQ.setString(4, q.getCorrection());
                        psQ.setString(5, q.getA());
                        psQ.setString(6, q.getB());
                        psQ.setString(7, q.getC());
                        psQ.executeUpdate();
                    }
                    q.setQuestionId(newQuestionId);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showError("Database error: " + ex.getMessage());
        }
    }

    private List<QuizDTO> loadQuizzesFromDB(String subject) throws SQLException {
        System.out.println("[DEBUG] loadQuizzesFromDB(subject=" + subject + ")");
        List<QuizDTO> list = new ArrayList<>();
        String sql = "SELECT quiz_id, difficulty FROM novalearn.quiz WHERE user_id=? AND matiere=?";
        Connection conn = MainApp.getDbConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, Long.parseLong(userId));
            ps.setString(2, subject);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    QuizDTO q = new QuizDTO();
                    q.setQuizId(rs.getString("quiz_id"));
                    q.setDifficulty(rs.getString("difficulty"));
                    list.add(q);
                }
            }
        }
        return list;
    }

    private void setupDifficultyFilters(List<QuizDTO> quizzes) {
        System.out.println("[DEBUG] setupDifficultyFilters(" + quizzes.size() + ")");
        difficultyBox.getChildren().clear();
        difficultyBox.setVisible(true);
        for (QuizDTO quiz : quizzes) {
            Button b = new Button(capitalize(quiz.getDifficulty()));
            b.setOnAction(evt -> showQuestionsForDifficulty(quiz));
            difficultyBox.getChildren().add(b);
        }
        Platform.runLater(() -> {
            if (!difficultyBox.getChildren().isEmpty()) {
                ((Button)difficultyBox.getChildren().get(0)).fire();
            }
        });
    }

    private void showQuestionsForDifficulty(QuizDTO quizDto) {
        currentQuizId = quizDto.getQuizId();
        questionsContainer.getChildren().clear();
        clearScoreOverlay();

        // --- generate audio button (unchanged) ---
        Button btnAudio = new Button("🎧 Generate audio");
        btnAudio.setDisable(isAudioGenerated(currentQuizId));
        btnAudio.setOnAction(evt -> {
            btnAudio.setDisable(true);
            generateAudio(currentQuizId);
        });
        questionsContainer.getChildren().add(btnAudio);

        // --- if already submitted, show overlay with score instead of dialog ---
        if (hasSubmitted(currentQuizId)) {
            int prev = fetchPreviousScore(currentQuizId);
            int total = 0;
            try {
                total = loadQuestionsFromDB(currentQuizId).size();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            questionsContainer.getChildren().clear();
            overlay.setVisible(false);
            // remove any nodes styled as score-label
            rootPane.getChildren().removeIf(node ->
                    node.getStyleClass().contains("score-label")
            );

            Label scoreLabel = new Label("You previously scored " + prev + " out of " + total);
            scoreLabel.getStyleClass().add("score-label");
            StackPane.setAlignment(scoreLabel, Pos.CENTER);
            rootPane.getChildren().add(scoreLabel);

            rootPane.setOnMouseClicked(ev -> {
                overlay.setVisible(false);
                rootPane.getChildren().remove(scoreLabel);
                rootPane.setOnMouseClicked(null);
            });
            return;
        }

        // --- otherwise load and render the questions ---
        List<QuestionDTO> questions;
        try {
            questions = loadQuestionsFromDB(currentQuizId);
        } catch (SQLException ex) {
            ex.printStackTrace();
            hideLoader();
            showError("Failed to load questions: " + ex.getMessage());
            return;
        }

        ToggleGroup[] groups = new ToggleGroup[questions.size()];
        for (int i = 0; i < questions.size(); i++) {
            QuestionDTO qt = questions.get(i);
            ToggleGroup tg = new ToggleGroup();
            groups[i] = tg;

            VBox v = new VBox(5);
            v.getChildren().add(new Label((i + 1) + ". " + qt.getQuestion()));

            RadioButton rA = new RadioButton("A: " + qt.getA());
            RadioButton rB = new RadioButton("B: " + qt.getB());
            RadioButton rC = new RadioButton("C: " + qt.getC());
            rA.setToggleGroup(tg);
            rB.setToggleGroup(tg);
            rC.setToggleGroup(tg);
            v.getChildren().addAll(rA, rB, rC);

            if (qt.getAudio() != null && !qt.getAudio().isEmpty()) {
                Button play = new Button("🔊");
                String uri = Paths.get("src", "main", "AudioQuestion", "en", qt.getAudio())
                        .toAbsolutePath().toUri().toString();
                MediaPlayer mp = new MediaPlayer(new Media(uri));
                play.setOnAction(e -> mp.play());
                v.getChildren().add(play);
            }

            questionsContainer.getChildren().add(v);
        }

        btnSubmit.setVisible(true);
        btnSubmit.setOnAction(e -> handleSubmit(groups, questions));
    }

    private List<QuestionDTO> loadQuestionsFromDB(String quizId) throws SQLException {
        System.out.println("[DEBUG] loadQuestionsFromDB(quizId=" + quizId + ")");
        List<QuestionDTO> list = new ArrayList<>();
        String sql = "SELECT question_id,question,correction,a,b,c,audio FROM novalearn.question WHERE quiz_id=?";
        Connection conn = MainApp.getDbConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    QuestionDTO q = new QuestionDTO();
                    q.setQuestionId(rs.getString("question_id"));
                    q.setQuestion(rs.getString("question"));
                    q.setCorrection(rs.getString("correction"));
                    q.setA(rs.getString("a")); q.setB(rs.getString("b")); q.setC(rs.getString("c"));
                    q.setAudio(rs.getString("audio"));
                    list.add(q);
                }
            }
        }
        return list;
    }

    private boolean isAudioGenerated(String quizId) {
        System.out.println("[DEBUG] isAudioGenerated(quizId=" + quizId + ")");
        String sql = "SELECT COUNT(*) AS cnt FROM novalearn.question WHERE quiz_id=? AND (audio IS NULL OR audio='')";
        Connection conn = MainApp.getDbConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("cnt")==0;
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    private void generateAudio(String quizId) {
        System.out.println("[DEBUG] generateAudio(quizId=" + quizId + ")");
        showLoader();

        CompletableFuture.runAsync(() -> {
                    List<QuestionDTO> questions;
                    try {
                        questions = loadQuestionsFromDB(quizId);
                    } catch (SQLException ex) {
                        throw new RuntimeException("Failed to load questions: " + ex.getMessage(), ex);
                    }

                    // Call TTS and update each question row in DB
                    for (QuestionDTO q : questions) {
                        try {
                            String textToSpeak = q.getQuestion()
                                    + ". Option A: " + q.getA()
                                    + ". Option B: " + q.getB()
                                    + ". Option C: " + q.getC();
                            String payload = objectMapper.writeValueAsString(Map.of(
                                    "text", textToSpeak,
                                    "lang", "en"
                            ));
                            HttpRequest req = HttpRequest.newBuilder()
                                    .uri(URI.create("http://localhost:8002/tts"))
                                    .header("Content-Type", "application/json")
                                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                                    .build();
                            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
                            String fileName = objectMapper.readTree(resp.body()).get("file_path").asText();

                            try (PreparedStatement ps = MainApp.getDbConnection()
                                    .prepareStatement("UPDATE novalearn.question SET audio=? WHERE question_id=?")) {
                                ps.setString(1, fileName);
                                ps.setString(2, q.getQuestionId());
                                ps.executeUpdate();
                            }
                        } catch (Exception e) {
                            throw new RuntimeException("TTS error for question " + q.getQuestionId() + ": " + e.getMessage(), e);
                        }
                    }
                })
                .whenComplete((__, err) -> Platform.runLater(() -> {
                    hideLoader();
                    if (err != null) {
                        err.printStackTrace();
                        showError("Audio generation failed: " + err.getCause().getMessage());
                    } else {
                        // refresh the quiz UI so that each question shows its play button
                        QuizDTO quizDto = currentQuizzes.stream()
                                .filter(q -> q.getQuizId().equals(quizId))
                                .findFirst()
                                .orElse(null);
                        if (quizDto != null) {
                            showQuestionsForDifficulty(quizDto);
                        }
                        new Alert(Alert.AlertType.INFORMATION, "Audio generated for all questions!")
                                .showAndWait();
                    }
                }));
    }
    private void handleSubmit(ToggleGroup[] groups, List<QuestionDTO> qs) {
        System.out.println("[DEBUG] handleSubmit()");
        for (ToggleGroup tg : groups)
            if (tg.getSelectedToggle()==null) {
                new Alert(Alert.AlertType.WARNING,"Please answer all questions").showAndWait();
                return;
            }

        int score=0;
        List<Map<String,String>> respList=new ArrayList<>();
        for (int i=0;i<qs.size();i++) {
            QuestionDTO q=qs.get(i);
            String txt=((RadioButton)groups[i].getSelectedToggle()).getText();
            String letter=txt.substring(0,1).toLowerCase();
            if (letter.equalsIgnoreCase(q.getCorrection())) score++;
            respList.add(Map.of("questionId",q.getQuestionId(),"answer",letter));
        }

        showLoader();
        try (PreparedStatement ps = MainApp.getDbConnection()
                .prepareStatement("INSERT INTO novalearn.quiz_submission (id,responses,score,submitted_at,quiz_id) VALUES(?,?,?,?,?)")) {
            ps.setLong(1,System.currentTimeMillis());
            ps.setString(2,objectMapper.writeValueAsString(respList));
            ps.setInt(3,score);
            ps.setTimestamp(4,java.sql.Timestamp.from(Instant.now()));
            ps.setString(5,currentQuizId);
            ps.executeUpdate();
            new Alert(Alert.AlertType.INFORMATION,"You scored "+score+" out of "+qs.size()).showAndWait();
            btnSubmit.setVisible(false);
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Submit failed: "+ex.getMessage());
        } finally {
            hideLoader();
        }
    }

    private boolean hasSubmitted(String quizId) {
        System.out.println("[DEBUG] hasSubmitted(quizId=" + quizId + ")");
        String sql = "SELECT 1 FROM novalearn.quiz_submission WHERE quiz_id=? LIMIT 1";
        try (PreparedStatement ps = MainApp.getDbConnection().prepareStatement(sql)) {
            ps.setString(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private int fetchPreviousScore(String quizId) {
        System.out.println("[DEBUG] fetchPreviousScore(quizId=" + quizId + ")");
        String sql = "SELECT score FROM novalearn.quiz_submission WHERE quiz_id=?";
        try (PreparedStatement ps = MainApp.getDbConnection().prepareStatement(sql)) {
            ps.setString(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("score");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    private void showError(String msg) {
        Platform.runLater(() ->
                new Alert(Alert.AlertType.ERROR, msg).showAndWait()
        );
    }

    private String capitalize(String s) {
        return s.isEmpty() ? s : s.substring(0,1).toUpperCase()+s.substring(1);
    }

    // ----------------------------------------------------------
    // DTO classes (unchanged)
    // ----------------------------------------------------------

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResponseWrapper {
        @JsonProperty("generated_json") private GeneratedJson generatedJson;
        public GeneratedJson getGeneratedJson() { return generatedJson; }
        public void setGeneratedJson(GeneratedJson g) { this.generatedJson = g; }
    }
    public static class GeneratedJson {
        private List<QuizDTO> quizzes;
        public List<QuizDTO> getQuizzes() { return quizzes; }
        public void setQuizzes(List<QuizDTO> q) { this.quizzes = q; }
    }
    public static class QuizDTO {
        @JsonProperty("quiz_id") private String quizId;
        private String difficulty;
        @JsonProperty("questions") private List<QuestionDTO> questions;
        public String getQuizId() { return quizId; }
        public void setQuizId(String id) { this.quizId = id; }
        public String getDifficulty() { return difficulty; }
        public void setDifficulty(String d) { this.difficulty = d; }
        public List<QuestionDTO> getQuestions() { return questions; }
        public void setQuestions(List<QuestionDTO> questions) { this.questions = questions; }
    }
    public static class QuestionDTO {
        @JsonProperty("question_id") private String questionId;
        private String question, correction, a, b, c, audio;
        public void setAudio(String audio) { this.audio = audio; }
        public String getAudio() { return audio; }
        @JsonProperty("options")
        private void unpackOptions(Map<String,String> opts) {
            this.a = opts.get("a"); this.b = opts.get("b"); this.c = opts.get("c");
        }
        public String getQuestionId() { return questionId; }
        public void setQuestionId(String id) { this.questionId = id; }
        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }
        public String getCorrection() { return correction; }
        public void setCorrection(String correction) { this.correction = correction; }
        public String getA() { return a; } public void setA(String a) { this.a = a; }
        public String getB() { return b; } public void setB(String b) { this.b = b; }
        public String getC() { return c; } public void setC(String c) { this.c = c; }
    }
}
