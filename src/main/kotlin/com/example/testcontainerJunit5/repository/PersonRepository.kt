package com.example.testcontainerJunit5.repository

import com.example.testcontainerJunit5.model.Person
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PersonRepository: JpaRepository<Person, Long> {

    fun findByPreferredName(preferredName: String): List<Person>

}