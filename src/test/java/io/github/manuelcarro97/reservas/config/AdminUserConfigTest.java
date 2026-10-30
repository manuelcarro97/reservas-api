package io.github.manuelcarro97.reservas.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.security.core.userdetails.UserDetailsService;

class AdminUserConfigTest {

	private final ApplicationContextRunner runner = new ApplicationContextRunner()
		.withUserConfiguration(AdminUserConfig.class);

	@Test
	void appFailsToStartWithoutAdminCredentials() {
		runner.withPropertyValues("ADMIN_USERNAME=", "ADMIN_PASSWORD=")
			.run(context -> assertThat(context).hasFailed());
	}

	@Test
	void adminUserIsCreatedFromEnvironment() {
		runner.withPropertyValues("ADMIN_USERNAME=admin", "ADMIN_PASSWORD=secret")
			.run(context -> assertThat(context.getBean(UserDetailsService.class).loadUserByUsername("admin"))
				.isNotNull());
	}

}
