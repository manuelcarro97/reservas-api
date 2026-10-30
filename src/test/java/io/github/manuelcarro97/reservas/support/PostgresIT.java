package io.github.manuelcarro97.reservas.support;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
public abstract class PostgresIT {

	@Autowired
	protected JdbcClient jdbc;

	@BeforeEach
	void cleanDatabase() {
		List<String> tables = jdbc.sql("""
				SELECT quote_ident(tablename) FROM pg_tables
				WHERE schemaname = 'public' AND tablename <> 'flyway_schema_history'
				""").query(String.class).list();
		if (!tables.isEmpty()) {
			jdbc.sql("TRUNCATE " + String.join(", ", tables) + " RESTART IDENTITY CASCADE").update();
		}
	}

}
