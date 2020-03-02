package org.sowatt.sowattweb.domain

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.sowatt.sowattweb.repository.ToggleCommandRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class ToggleCommandTest @Autowired constructor(val entityManager: TestEntityManager, val toggleCommandRepository: ToggleCommandRepository) {
    companion object {
        val toggleCommand: ToggleCommand = ToggleCommand(0, ButtonTest.button, mutableListOf(KnxDatapointTest.switchDP))
        val toggleCommand2: ToggleCommand = ToggleCommand(0, ButtonTest.button2, mutableListOf(KnxDatapointTest.switchDP))

    }

    @BeforeEach
    fun prepareDatabase() {
        entityManager.persistAndGetId(ButtonTest.controlPoint)
        entityManager.persistAndGetId(ButtonTest.button)
        entityManager.persistAndGetId(ButtonTest.button2)
        entityManager.persistAndGetId(KnxDatapointTest.switchDP)
        entityManager.persistAndGetId(toggleCommand)
        entityManager.persistAndGetId(toggleCommand2)
    }

    @Test
    fun `find by Button`() {
        Assertions.assertEquals(toggleCommand, toggleCommandRepository.findByButton(ButtonTest.button))
    }
}