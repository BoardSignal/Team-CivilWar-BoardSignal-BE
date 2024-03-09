package com.civilwar.boardsignal.support;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.transaction.annotation.Transactional;

@TestComponent
public class DatabaseCleaner {

    private final List<String> tableNames = new ArrayList<>();
    @PersistenceContext
    private EntityManager entityManager;

    @PostConstruct
    @SuppressWarnings("unchecked")
    private void initTableNames() {
        List<Object> tableInfoList = entityManager.createNativeQuery("SHOW TABLES").getResultList();
        tableInfoList.forEach(tableInfo -> {
            String tableName = String.valueOf(tableInfo);
            tableNames.add(tableName);
        });
    }

    @Transactional
    public void clear() {
        entityManager.clear();
        truncate();
    }

    private void truncate() {
        tableNames.forEach(tableName ->
            entityManager
                .createNativeQuery(String.format("TRUNCATE TABLE %s", tableName))
                .executeUpdate()
        );
    }
}