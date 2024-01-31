package com.civilwar.boardsignal.support;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
public class DatabaseCleaner {

    private final List<String> tableNames = new ArrayList<>();
    @PersistenceContext
    private EntityManager entityManager;

    @PostConstruct
    @SuppressWarnings("unchecked")
    private void initTableNames() {
        List<Object[]> tableInfoList = entityManager.createNativeQuery("SHOW TABLES").getResultList();
        tableInfoList.forEach(tableInfo -> {
            String tableName = (String) tableInfo[0];
            tableNames.add(tableName);
        });
    }

    public void clear() {
        entityManager.clear();
        truncate();
    }

    private void truncate() {
        entityManager
            .createNativeQuery(String.format("SET FOREIGN_KEY_CHECKS %d", 0))
            .executeUpdate();

        tableNames.forEach(tableName ->
            entityManager
                .createNativeQuery(String.format("TRUNCATE TABLE %s", tableName))
                .executeUpdate()
        );

        entityManager
            .createNativeQuery(String.format("SET FOREIGN_KEY_CHECKS %d", 1))
            .executeUpdate();
    }
}