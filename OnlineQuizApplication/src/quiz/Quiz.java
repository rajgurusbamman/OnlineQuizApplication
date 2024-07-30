package quiz;

import java.util.List;

public class Quiz {
    private String name;
    private List<Question> questions;

    public Quiz(String name, List<Question> questions) {
        this.name = name;
        this.questions = questions;
    }

    public String getName() {
        return name;
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
