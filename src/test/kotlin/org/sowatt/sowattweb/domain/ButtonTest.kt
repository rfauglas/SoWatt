package org.sowatt.sowattweb.domain

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.sowatt.sowattweb.config.persistFixture
import org.sowatt.sowattweb.domain.types.Switch2RockerButtonPosition
import org.sowatt.sowattweb.repository.ButtonRepository
import org.sowatt.sowattweb.repository.ControlPointRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest()
class ButtonTest @Autowired constructor(val entityManager: TestEntityManager, val buttonRepository: ButtonRepository, val controlPointRepository: ControlPointRepository) {
    lateinit var testControlPoint: ControlPoint

    companion object {
        private val controlPoint = ControlPoint(0, "enoceanId")
        private val button = Button(0, controlPoint, Switch2RockerButtonPosition.UP_LEFT)
        private val button2 = Button(0, controlPoint, Switch2RockerButtonPosition.DOWN_LEFT)

        fun initControlPoint(): ControlPoint {
            val newControlPoint = controlPoint.copy()
            //we must copy all aggregate relation controlPoint <-> button
            newControlPoint.buttons = setOf(button.copy(), button2.copy())
            newControlPoint.buttons.forEach { button: Button -> button.controlPoint = newControlPoint }
            return newControlPoint;
        }

        fun initButton2(): Button {
            return button2.copy()
        }

        fun initButton(): Button {
            return button.copy()
        }

    }

    @BeforeEach
    fun prepareDatabase() {
        testControlPoint = initControlPoint()
        entityManager.persistFixture(testControlPoint)
        testControlPoint.buttons.forEach { button: Button ->
            entityManager.persistFixture(button)
        }
        entityManager.flush()
    }

    @Test
    fun ` get button`() {
        Assertions.assertEquals(testControlPoint, controlPointRepository.findByEnoceanId(testControlPoint.enoceanId))
        Assertions.assertEquals(testControlPoint.buttons.elementAt(0), buttonRepository.getButton(testControlPoint.enoceanId, Switch2RockerButtonPosition.UP_LEFT))
    }

}