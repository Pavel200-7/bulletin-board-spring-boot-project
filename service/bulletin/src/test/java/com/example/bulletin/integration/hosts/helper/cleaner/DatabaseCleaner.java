package com.example.bulletin.integration.hosts.helper.cleaner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DatabaseCleaner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public void cleanAll() {
        List<String> tables = jdbcTemplate.queryForList(
                "SELECT table_name FROM information_schema.tables " +
                        "WHERE table_schema = 'public' " +
                        "AND table_type = 'BASE TABLE' " +
                        "AND table_name NOT IN ('flyway_schema_history', 'databasechangelog', 'databasechangeloglock')",
                String.class
        );

        for (String table : tables) {
            jdbcTemplate.execute("TRUNCATE TABLE " + table + " CASCADE");
        }
    }

}