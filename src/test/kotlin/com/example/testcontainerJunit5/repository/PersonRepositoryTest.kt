package com.example.testcontainerJunit5.repository

import com.example.testcontainerJunit5.model.Person
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.BindMode
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class PersonRepositoryTest {

    companion object {

        @Container
        private val postgreSQLContainer = PostgreSQLContainer("postgres:latest")
            .withExposedPorts(5432)
            .withClasspathResourceMapping(
                "application.properties", "/application.properties", BindMode.READ_ONLY
            )
            .withFileSystemBind(
                "./src/test/resources/hello.txt", "/hello.txt", BindMode.READ_ONLY
            )

        @DynamicPropertySource
        @JvmStatic
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgreSQLContainer::getUsername)
            registry.add("spring.datasource.password", postgreSQLContainer::getPassword)
        }

    }

    @Autowired
    private lateinit var personRepository: PersonRepository

    @Test
    fun `when multiple records with the same preferred name then all are found`() {
        personRepository.save(Person(name = "Tom Smith", preferredName = "Tom"))
        personRepository.save(Person(name = "Thomas Doe", preferredName = "Thomas"))
        personRepository.save(Person(name = "Tommy Jones", preferredName = "Tom"))

        val actual = personRepository.findByPreferredName("Tom")

        Assertions.assertEquals(2, actual.size)
    }

    @Test
    fun `should execute command in container`() {
        postgreSQLContainer.execInContainer("touch", "test.txt")

        val lsResult = postgreSQLContainer.execInContainer("ls", "-la")
        val stdout: String = lsResult.stdout
        val exitCode: Int = lsResult.exitCode

        println("ls Output: $stdout")

        assertThat(stdout).contains("test.txt")
        assertThat(stdout).contains("hello.txt")
        assertThat(stdout).contains("application.properties")
        assertThat(exitCode).isZero
    }

    @Test
    fun `should get the mapped port of container`() {
        val mappedPort = postgreSQLContainer.getMappedPort(5432)

        println("Mapped Port: $mappedPort")
    }

    @Test
    fun `should get logs of container`() {
        val logs = postgreSQLContainer.logs
        println("Container Logs: $logs")
    }

}