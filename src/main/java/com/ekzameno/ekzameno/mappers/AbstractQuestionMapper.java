package com.ekzameno.ekzameno.mappers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.ekzameno.ekzameno.models.Question;
import com.ekzameno.ekzameno.shared.DBConnection;
import com.ekzameno.ekzameno.shared.IdentityMap;

/**
 * Abstract Data Mapper for Questions.
 *
 * @param <T> type of the questions
 */
public abstract class AbstractQuestionMapper<T extends Question>
        extends Mapper<T> {
    private static final String tableName = "questions";

    @Override
    public void insert(T question) throws SQLException {
        String query = "INSERT INTO " + tableName +
            " (id, question, marks, type) VALUES (?,?,?,?)";

        Connection connection = DBConnection.getInstance().getConnection();

        try (
            PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setObject(1, question.getId());
            statement.setString(2, question.getQuestion());
            statement.setInt(3, question.getMarks());
            statement.setString(4, getType());
            statement.executeUpdate();
            IdentityMap.getInstance().put(question.getId(), question);
        }
    }

    @Override
    public void update(T question) throws SQLException {
        String query = "UPDATE " + tableName +
            " SET question = ?, marks = ? WHERE id = ?";

        Connection connection = DBConnection.getInstance().getConnection();

        try (
            PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setString(1, question.getQuestion());
            statement.setInt(2, question.getMarks());
            statement.setObject(3, question.getId());
            statement.executeUpdate();
        }
    }

    @Override
    protected String getTableName() {
        return tableName;
    }

    protected abstract String getType();
}
