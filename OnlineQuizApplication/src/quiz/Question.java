package quiz;

import java.util.List;

public class Question {
    private String title;
    private List<String> options;
    private int correctAnswerIndex;

    public Question(String title, List<String> options, int correctAnswerIndex) {
        this.title = title;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }
}
