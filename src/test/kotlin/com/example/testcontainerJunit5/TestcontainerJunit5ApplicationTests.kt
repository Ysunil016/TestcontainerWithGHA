package com.example.testcontainerJunit5

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
class TestcontainerJunit5ApplicationTests {

	companion object {

		@Container
		private val postgreSQLContainer = PostgreSQLContainer("postgres:latest")
			.withExposedPorts(5432)

		@DynamicPropertySource
		@JvmStatic
		fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
			registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl)
			registry.add("spring.datasource.username", postgreSQLContainer::getUsername)
			registry.add("spring.datasource.password", postgreSQLContainer::getPassword)
		}

	}

	@Test
	fun contextLoads() {
	}

}
