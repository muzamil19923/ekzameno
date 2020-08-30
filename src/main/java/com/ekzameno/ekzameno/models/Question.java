package com.ekzameno.ekzameno.models;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import com.ekzameno.ekzameno.mappers.QuestionSubmissionMapper;

public abstract class Question extends Model {
    private String question;
    private int marks;
    private List<QuestionSubmission> questionSubmissions = null;

    public Question(UUID id, String question, int marks) {
        super(id);
        this.question = question;
        this.marks = marks;
    }

    public Question(String question, int marks) {
        this.question = question;
        this.marks = marks;
    }

    public String getQuestion() {
        return question;
    }

    public int getMarks() {
        return marks;
    }

    public List<QuestionSubmission> getQuestionSubmissions() throws SQLException {
        if (questionSubmissions == null) {
            return new QuestionSubmissionMapper().findAllForQuestion(getId());
        } else {
            return questionSubmissions;
        }
    }
}
