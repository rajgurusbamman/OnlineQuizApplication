package quiz;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizManager {
    private static final String DB_URL = "jdbc:sqlite:quiz.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static List<Quiz> getAllQuizzes() throws SQLException {
        List<Quiz> quizzes = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM quizzes")) {
            while (rs.next()) {
                String name = rs.getString("name");
                int quizId = rs.getInt("id");
                List<Question> questions = getQuestionsForQuiz(quizId);
                quizzes.add(new Quiz(name, questions));
            }
        }
        return quizzes;
    }

    private static List<Question> getQuestionsForQuiz(int quizId) throws SQLException {
        List<Question> questions = new ArrayList<>();
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM questions WHERE quiz_id = ?")) {
            pstmt.setInt(1, quizId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String title = rs.getString("title");
                    String[] options = rs.getString("options").split(";");
                    int correctAnswerIndex = rs.getInt("correct_answer");
                    questions.add(new Question(title, List.of(options), correctAnswerIndex));
                }
            }
        }
        return questions;
    }

    public static void addQuiz(Quiz quiz) throws SQLException {
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO quizzes (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, quiz.getName());
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int quizId = generatedKeys.getInt(1);
                    for (Question question : quiz.getQuestions()) {
                        addQuestion(quizId, question);
                    }
                }
            }
        }
    }

    private static void addQuestion(int quizId, Question question) throws SQLException {
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO questions (quiz_id, title, options, correct_answer) VALUES (?, ?, ?, ?)")) {
            pstmt.setInt(1, quizId);
            pstmt.setString(2, question.getTitle());
            pstmt.setString(3, String.join(";", question.getOptions()));
            pstmt.setInt(4, question.getCorrectAnswerIndex());
            pstmt.executeUpdate();
        }
    }
}
