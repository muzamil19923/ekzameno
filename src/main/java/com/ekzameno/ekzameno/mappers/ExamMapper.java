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

import javax.ws.rs.NotFoundException;

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
     * Find an exam for a given slug.
     *
     * @param slug      ID of the model to find
     * @param forUpdate whether the row should be locked
     * @return model with the given ID
     * @throws SQLException if unable to retrieve the model
     */
    public Exam findBySlug(
        String slug,
        boolean forUpdate
    ) throws NotFoundException, SQLException {
        return findByProp("slug", slug, forUpdate);
    }

    /**
     * Find an exam for a given slug.
     *
     * @param slug ID of the model to find
     * @return model with the given ID
     * @throws SQLException if unable to retrieve the model
     */
    public Exam findBySlug(String slug) throws SQLException {
        return findBySlug(slug, false);
    }

    /**
     * Retrieve all exams for a given subject ID.
     *
     * @param id        ID of the subject to retrieve exams for
     * @param forUpdate whether the rows should be locked
     * @return exams for the given subject
     * @throws SQLException if unable to retrieve the exams
     */
    public List<Exam> findAllForSubject(
        UUID id,
        boolean forUpdate
    ) throws SQLException {
        String query = "SELECT * FROM " + tableName + " WHERE subject_id = ?" +
            (forUpdate ? " FOR UPDATE" : "");

        Connection connection = DBConnection.getCurrent().getConnection();

        try (
            PreparedStatement statement = connection.prepareStatement(query);
        ) {
            List<Exam> exams = new ArrayList<>();

            statement.setObject(1, id);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Exam exam = load(rs);
                IdentityMap.getCurrent().put(exam.getId(), exam);
                exams.add(exam);
            }

            return exams;
        }
    }

    /**
     * Retrieve all exams for a given subject ID.
     *
     * @param id ID of the subject to retrieve exams for
     * @return exams for the given subject
     * @throws SQLException if unable to retrieve the exams
     */
    public List<Exam> findAllForSubject(UUID id) throws SQLException {
        return findAllForSubject(id, false);
    }

    /**
     * Retrieve all published exams for a given subject ID.
     *
     * @param id        ID of the subject to retrieve exams for
     * @param forUpdate whether the rows should be locked
     * @return exams for the given subject
     * @throws SQLException if unable to retrieve the exams
     */
    public List<Exam> findAllPublishedExams(
        UUID id,
        boolean forUpdate
    ) throws SQLException {
        String query = "SELECT * FROM " + tableName + " WHERE subject_id = ? " +
            "AND start_time < NOW()" + (forUpdate ? " FOR UPDATE" : "");

        Connection connection = DBConnection.getCurrent().getConnection();

        try (
            PreparedStatement statement = connection.prepareStatement(query);
        ) {
            List<Exam> exams = new ArrayList<>();

            statement.setObject(1, id);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Exam exam = load(rs);
                IdentityMap.getCurrent().put(exam.getId(), exam);
                exams.add(exam);
            }

            return exams;
        }
    }

    /**
     * Retrieve all published exams for a given subject ID.
     *
     * @param id ID of the subject to retrieve exams for
     * @return exams for the given subject
     * @throws SQLException if unable to retrieve the exams
     */
    public List<Exam> findAllPublishedExams(UUID id) throws SQLException {
        return findAllPublishedExams(id, false);
    }

    @Override
    public void insert(Exam exam) throws SQLException {
        String query = "INSERT INTO " + tableName +
            " (id, slug, name, description, start_time, finish_time, " +
            "subject_id) VALUES (?,?,?,?,?,?,?)";

        Connection connection = DBConnection.getCurrent().getConnection();

        try (
            PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setObject(1, exam.getId());
            statement.setString(2, exam.getSlug());
            statement.setString(3, exam.getName());
            statement.setObject(4,exam.getDescription());
            statement.setTimestamp(
                5,
                new Timestamp(exam.getStartTime().getTime())
            );
            statement.setTimestamp(
                6,
                new Timestamp(exam.getFinishTime().getTime())
            );
            statement.setObject(7, exam.getSubjectId());
            statement.executeUpdate();
        }
    }

    @Override
    public void update(Exam exam) throws SQLException {
        String query = "UPDATE " + tableName +
            " SET name = ?, description = ?, start_time = ?, " +
            "finish_time = ?, subject_id = ? WHERE id = ?";

        Connection connection = DBConnection.getCurrent().getConnection();

        try (
            PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setString(1, exam.getName());
            statement.setString(2, exam.getDescription());
            statement.setTimestamp(
                3,
                new Timestamp(exam.getStartTime().getTime())
            );
            statement.setTimestamp(
                4,
                new Timestamp(exam.getFinishTime().getTime())
            );
            statement.setObject(5, exam.getSubjectId());
            statement.setObject(6, exam.getId());
            statement.executeUpdate();
        }
    }

    @Override
    protected Exam load(ResultSet rs) throws SQLException {
        UUID id = rs.getObject("id", java.util.UUID.class);
        Exam exam = (Exam) IdentityMap.getCurrent().get(id);
        if (exam != null) {
            return exam;
        }
        String name = rs.getString("name");
        String description = rs.getString("description");
        String slug = rs.getString("slug");
        Date startTime = rs.getTimestamp("start_time");
        Date finishTime = rs.getTimestamp("finish_time");
        DateRange dateRange = new DateRange(startTime, finishTime);
        UUID subjectId = rs.getObject("subject_id", java.util.UUID.class);
        return new Exam(id, name, description, dateRange, subjectId, slug);
    }

    @Override
    protected String getTableName() {
        return tableName;
    }
}
