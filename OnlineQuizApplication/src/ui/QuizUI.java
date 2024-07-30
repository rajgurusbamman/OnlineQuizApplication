package ui;

import quiz.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuizUI extends Application {
    private int currentQuestionIndex = 0;
    private int score = 0;
    private Quiz currentQuiz;
    private List<Question> questions;
    private Label questionLabel;
    private List<RadioButton> options = new ArrayList<>();
    private ToggleGroup optionsGroup;

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox();
        root.setPadding(new Insets(10));
        root.setSpacing(10);

        Label title = new Label("Online Quiz Application");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        ComboBox<Quiz> quizSelector = new ComboBox<>();
        quizSelector.setPromptText("Select Quiz");

        Button startQuizButton = new Button("Start Quiz");
        startQuizButton.setDisable(true);

        quizSelector.setOnAction(event -> startQuizButton.setDisable(false));

        startQuizButton.setOnAction(event -> {
            currentQuiz = quizSelector.getSelectionModel().getSelectedItem();
            questions = currentQuiz.getQuestions();
            currentQuestionIndex = 0;
            score = 0;
            displayQuestion();
        });

        try {
            List<Quiz> quizzes = QuizManager.getAllQuizzes();
            quizSelector.getItems().addAll(quizzes);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        questionLabel = new Label();
        optionsGroup = new ToggleGroup();
        GridPane optionsPane = new GridPane();
        optionsPane.setVgap(10);
        optionsPane.setHgap(10);

        for (int i = 0; i < 4; i++) {
            RadioButton option = new RadioButton();
            option.setToggleGroup(optionsGroup);
            options.add(option);
            optionsPane.add(option, 0, i);
        }

        Button submitButton = new Button("Submit Answer");
        submitButton.setOnAction(event -> handleSubmit());

        root.getChildren().addAll(title, quizSelector, startQuizButton, questionLabel, optionsPane, submitButton);

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("Online Quiz Application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void displayQuestion() {
        if (currentQuestionIndex < questions.size()) {
            Question question = questions.get(currentQuestionIndex);
            questionLabel.setText(question.getTitle());
            for (int i = 0; i < options.size(); i++) {
                options.get(i).setText(question.getOptions().get(i));
            }
        } else {
            showResult();
        }
    }

    private void handleSubmit() {
        Question question = questions.get(currentQuestionIndex);
        int selectedOptionIndex = options.indexOf(optionsGroup.getSelectedToggle());

        if (selectedOptionIndex == question.getCorrectAnswerIndex()) {
            score++;
        }

        currentQuestionIndex++;
        displayQuestion();
    }

    private void showResult() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Quiz Result");
        alert.setHeaderText(null);
        alert.setContentText("Your score: " + score + "/" + questions.size());
        alert.showAndWait();

        // Reset quiz
        currentQuestionIndex = 0;
        score = 0;
        displayQuestion();
    }

    public static void main(String[] args) {
        Application.launch(QuizUI.class, args);
    }
}

