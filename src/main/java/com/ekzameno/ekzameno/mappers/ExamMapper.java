package com.ekzameno.ekzameno.mappers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.ekzameno.ekzameno.models.DateRange;
import com.ekzameno.ekzameno.models.Exam;
import com.ekzameno.ekzameno.shared.DBConnection;
import com.ekzameno.ekzameno.shared.IdentityMap;

/**
 * Data Mapper for Exams.
 */
public class ExamMapper extends Mapper<Exam> {
    private static final String tableName = "exams";

    /**
     * Retrieve all exams for a given subject ID.
     *
     * @param id ID of the subject to retrieve exams for
     * @return exams for the given subject
     * @throws SQLException if unable to retrieve the exams
     */
    public List<Exam> findAllForSubject(UUID id) throws SQLException {
        String query = "SELECT * FROM exams WHERE subject_id = ?";

        Connection connection = DBConnection.getInstance().getConnection();

        try (
            PreparedStatement statement = connection.prepareStatement(query);
        ) {
            List<Exam> exams = new ArrayList<>();

            statement.setObject(1, id);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Exam exam = load(rs);
                IdentityMap.getInstance().put(exam.getId(), exam);
                exams.add(exam);
            }

            return exams;
        }
    }

    @Override
    public void insert(Exam exam) throws SQLException {
        String query = "INSERT INTO " + tableName +
            " (id, name, publish_date, close_date, subject_id) " +
            "VALUES (?,?,?,?,?)";

        Connection connection = DBConnection.getInstance().getConnection();

        try (
            PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setObject(1, exam.getId());
            statement.setString(2, exam.getName());
            statement.setTimestamp(
                3,
                new Timestamp(exam.getPublishDate().getTime())
            );
            statement.setTimestamp(
                4,
                new Timestamp(exam.getCloseDate().getTime())
            );
            statement.setObject(5, exam.getSubjectId());
            statement.executeUpdate();
            IdentityMap.getInstance().put(exam.getId(), exam);
        }
    }

    @Override
    public void update(Exam exam) throws SQLException {
        String query = "UPDATE " + tableName +
            " SET name = ?, publish_date = ?, close_date = ?, subject_id = ? " +
            "WHERE id = ?";

        Connection connection = DBConnection.getInstance().getConnection();

        try (
            PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setString(1, exam.getName());
            statement.setTimestamp(
                2,
                new Timestamp(exam.getPublishDate().getTime())
            );
            statement.setTimestamp(
                3,
                new Timestamp(exam.getCloseDate().getTime())
            );
            statement.setObject(4, exam.getSubjectId());
            statement.setObject(5, exam.getId());
            statement.executeUpdate();
        }
    }

    @Override
    protected Exam load(ResultSet rs) throws SQLException {
        UUID id = rs.getObject("id", java.util.UUID.class);
        String name = rs.getString("name");
        Date publishDate = rs.getTimestamp("publish_date");
        Date closeDate = rs.getTimestamp("close_date");
        DateRange dateRange = new DateRange(publishDate, closeDate);
        UUID subjectId = rs.getObject("subject_id", java.util.UUID.class);
        return new Exam(id, name, dateRange, subjectId);
    }

    @Override
    protected String getTableName() {
        return tableName;
    }
}