package com.ekzameno.ekzameno.proxies;

import java.sql.SQLException;
import java.util.UUID;

import com.ekzameno.ekzameno.mappers.QuestionSubmissionMapper;
import com.ekzameno.ekzameno.models.QuestionSubmission;

/**
 * Proxy list for QuestionSubmissions owned by ExamSubmissions.
 */
public class QuestionSubmissionExamSubmissionProxyList
        extends ProxyList<QuestionSubmission> {
    /**
     * Create a QuestionSubmissionExamSubmissionProxyList.
     *
     * @param examSubmissionId ID of the exam submissions the question
     *                         submissions belong to
     */
    public QuestionSubmissionExamSubmissionProxyList(UUID examSubmissionId) {
        super(examSubmissionId);
    }

    @Override
    protected void init() throws SQLException {
        if (models == null) {
            models = new QuestionSubmissionMapper()
                    .findAllForExamSubmission(id);
        }
    }
}
