package com.example.testcontainerJunit5.repository

import com.example.testcontainerJunit5.model.Person
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
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

}