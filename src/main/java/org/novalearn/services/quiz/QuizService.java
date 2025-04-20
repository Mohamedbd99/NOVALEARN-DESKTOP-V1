// File: src/main/java/org/novalearn/services/quiz/QuizService.java
package org.novalearn.services.quiz;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.novalearn.MainApp;
import org.novalearn.controllers.Quiz.QuizzesController;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class QuizService {
    private final String userId;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public QuizService(String userId) {
        this.userId = userId;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<QuizzesController.QuizDTO> loadLocalQuizzes(String subject) throws SQLException {
        String sql = "SELECT quiz_id, difficulty FROM novalearn.quiz WHERE user_id = ? AND matiere = ?";
        List<QuizzesController.QuizDTO> list = new ArrayList<>();
        Connection conn = MainApp.getDbConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, Long.parseLong(userId));
            ps.setString(2, subject);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    QuizzesController.QuizDTO q = new QuizzesController.QuizDTO();
                    q.setQuizId(rs.getString("quiz_id"));
                    q.setDifficulty(rs.getString("difficulty"));
                    list.add(q);
                }
            }
        }
        return list;
    }

    public List<QuizzesController.QuestionDTO> loadQuestions(String quizId) throws SQLException {
        String sql = "SELECT question_id, question, correction, a, b, c, audio FROM novalearn.question WHERE quiz_id = ?";
        List<QuizzesController.QuestionDTO> list = new ArrayList<>();
        Connection conn = MainApp.getDbConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    QuizzesController.QuestionDTO q = new QuizzesController.QuestionDTO();
                    q.setQuestionId(rs.getString("question_id"));
                    q.setQuestion(rs.getString("question"));
                    q.setCorrection(rs.getString("correction"));
                    q.setA(rs.getString("a"));
                    q.setB(rs.getString("b"));
                    q.setC(rs.getString("c"));
                    q.setAudio(rs.getString("audio"));
                    list.add(q);
                }
            }
        }
        return list;
    }

    public boolean hasSubmission(String quizId) {
        String sql = "SELECT 1 FROM novalearn.quiz_submission WHERE quiz_id = ? LIMIT 1";
        try (PreparedStatement ps = MainApp.getDbConnection().prepareStatement(sql)) {
            ps.setString(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int fetchScore(String quizId) {
        String sql = "SELECT score FROM novalearn.quiz_submission WHERE quiz_id = ?";
        try (PreparedStatement ps = MainApp.getDbConnection().prepareStatement(sql)) {
            ps.setString(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("score");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean isAudioReady(String quizId) {
        String sql = "SELECT COUNT(*) AS cnt FROM novalearn.question WHERE quiz_id = ? AND (audio IS NULL OR audio = '')";
        try (PreparedStatement ps = MainApp.getDbConnection().prepareStatement(sql)) {
            ps.setString(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("cnt") == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<QuizzesController.QuizDTO> fetchRemoteAndPersist(String subject) throws Exception {
        String url = "http://localhost:8001/generate/" + subject;
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

        QuizzesController.ResponseWrapper wrapper = objectMapper.readValue(
                resp.body(), QuizzesController.ResponseWrapper.class);
        List<QuizzesController.QuizDTO> quizzes = wrapper.getGeneratedJson().getQuizzes();

        // persist quizzes
        String insertQuiz = "INSERT INTO novalearn.quiz(quiz_id,difficulty,matiere,user_id) VALUES(?,?,?,?)";
        String insertQ = "INSERT INTO novalearn.question(question_id,quiz_id,question,correction,a,b,c) VALUES(?,?,?,?,?,?,?)";
        Connection conn = MainApp.getDbConnection();
        for (QuizzesController.QuizDTO quiz : quizzes) {
            String newQuizId = UUID.randomUUID().toString();
            try (PreparedStatement ps = conn.prepareStatement(insertQuiz)) {
                ps.setString(1, newQuizId);
                ps.setString(2, quiz.getDifficulty());
                ps.setString(3, subject);
                ps.setLong(4, Long.parseLong(userId));
                ps.executeUpdate();
            }
            quiz.setQuizId(newQuizId);

            for (QuizzesController.QuestionDTO q : quiz.getQuestions()) {
                String newQId = UUID.randomUUID().toString();
                try (PreparedStatement ps = conn.prepareStatement(insertQ)) {
                    ps.setString(1, newQId);
                    ps.setString(2, newQuizId);
                    ps.setString(3, q.getQuestion());
                    ps.setString(4, q.getCorrection());
                    ps.setString(5, q.getA());
                    ps.setString(6, q.getB());
                    ps.setString(7, q.getC());
                    ps.executeUpdate();
                }
                q.setQuestionId(newQId);
            }
        }

        return quizzes;
    }

    public void generateAudioFiles(String quizId) throws Exception {
        List<QuizzesController.QuestionDTO> questions = loadQuestions(quizId);
        for (QuizzesController.QuestionDTO q : questions) {
            String text = q.getQuestion() + " Option A: " + q.getA() + ". Option B: " + q.getB() + ". Option C: " + q.getC();
            String payload = objectMapper.writeValueAsString(Map.of("text", text, "lang", "en"));

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8002/tts"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            String filePath = objectMapper.readTree(resp.body()).get("file_path").asText();

            String updateSql = "UPDATE novalearn.question SET audio = ? WHERE question_id = ?";
            try (PreparedStatement ps = MainApp.getDbConnection().prepareStatement(updateSql)) {
                ps.setString(1, filePath);
                ps.setString(2, q.getQuestionId());
                ps.executeUpdate();
            }
        }
    }
}
