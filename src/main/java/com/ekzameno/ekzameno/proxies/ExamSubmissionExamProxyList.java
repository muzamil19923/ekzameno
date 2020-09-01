package com.ekzameno.ekzameno.proxies;

import java.sql.SQLException;
import java.util.UUID;

import com.ekzameno.ekzameno.mappers.ExamSubmissionMapper;
import com.ekzameno.ekzameno.models.ExamSubmission;

public class ExamSubmissionExamProxyList extends ProxyList<ExamSubmission> {
    public ExamSubmissionExamProxyList(UUID id) {
        super(id);
    }

    @Override
    protected void init() throws SQLException {
        if (models == null) {
            new ExamSubmissionMapper().findAllForExam(id);
        }
    }
}
