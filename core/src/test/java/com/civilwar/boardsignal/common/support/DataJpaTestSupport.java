package com.civilwar.boardsignal.common.support;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import com.civilwar.boardsignal.common.config.TestAuditingConfig;
import com.civilwar.boardsignal.support.DatabaseCleaner;
import com.civilwar.boardsignal.support.DatabaseCleanerExtension;
import com.civilwar.boardsignal.support.TestContainerSupport;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Repository.class))
@AutoConfigureTestDatabase(replace = NONE)
@Import({TestAuditingConfig.class, DatabaseCleaner.class})
@ExtendWith(DatabaseCleanerExtension.class)
public abstract class DataJpaTestSupport extends TestContainerSupport {

}
