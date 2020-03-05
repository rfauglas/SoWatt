package org.sowatt.sowattweb.domain

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.sowatt.sowattweb.config.persistFixture
import org.sowatt.sowattweb.repository.ToggleCommandRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.annotation.DirtiesContext
import org.springframework.transaction.annotation.Transactional

@DataJpaTest()
class ToggleCommandTest @Autowired constructor(val entityManager: TestEntityManager, val toggleCommandRepository: ToggleCommandRepository) {

    lateinit var testToggleCommand: ToggleCommand
    lateinit var testToggleCommand2: ToggleCommand
    lateinit var testSwitchDP : SwitchDP

    companion object {
        val toggleCommand: ToggleCommand = ToggleCommand(0, ButtonTest.initButton(), mutableListOf())
        val toggleCommand2: ToggleCommand = ToggleCommand(0, ButtonTest.initButton2(), mutableListOf())

        fun initToggleCommand(toggleCommand: ToggleCommand): ToggleCommand {
            val newToggleCommand = toggleCommand.copy()
            return newToggleCommand
        }
    }

    @BeforeEach()
    fun prepareDatabase() {
        testToggleCommand = initToggleCommand(toggleCommand)
        testToggleCommand2= initToggleCommand(toggleCommand2)
        testSwitchDP = KnxDatapointTest.initSwitchDP()

        entityManager.persist(testSwitchDP)
        persistToggleCommandAgregate(testToggleCommand)
        persistToggleCommandAgregate(testToggleCommand2)
        entityManager.flush()
    }

    private fun persistToggleCommandAgregate(toggleCommand: ToggleCommand) {
        testToggleCommand.switchList.add(testSwitchDP)
        entityManager.persistFixture(testToggleCommand.button)
        entityManager.persistFixture(testToggleCommand.button.controlPoint)
        entityManager.persistFixture(testToggleCommand)
    }

    @Test
    fun `find by Button`() {
        Assertions.assertEquals(testToggleCommand, toggleCommandRepository.findByButton(testToggleCommand.button))
    }
}