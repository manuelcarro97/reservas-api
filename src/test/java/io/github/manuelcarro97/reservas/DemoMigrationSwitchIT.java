package io.github.manuelcarro97.reservas;

import static org.assertj.core.api.Assertions.assertThatCode;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.github.manuelcarro97.reservas.support.PostgresIT;

class DemoMigrationSwitchIT extends PostgresIT {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private Flyway flyway;

	@Test
	void databaseThatUsedDemoProfileStillValidatesWithoutIt() {
		Flyway.configure()
			.dataSource(dataSource)
			.locations("classpath:db/migration", "classpath:db/demo")
			.load()
			.migrate();

		assertThatCode(flyway::validate).doesNotThrowAnyException();
	}

}
