package org.sowatt.sowattweb.domain

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.sowatt.sowattweb.domain.types.Switch2RockerButtonPosition
import org.sowatt.sowattweb.repository.ButtonRepository
import org.sowatt.sowattweb.repository.ControlPointRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class ButtonTest  @Autowired constructor(val entityManager: TestEntityManager, val buttonRepository: ButtonRepository, val controlPointRepository: ControlPointRepository) {

    companion object {
        val controlPoint= ControlPoint(0,"enoceanId", mutableSetOf())
        val button= Button(0, controlPoint,Switch2RockerButtonPosition.UP_LEFT)
        val button2= Button(0, controlPoint,Switch2RockerButtonPosition.DOWN_LEFT)

        init {
            controlPoint.buttons.plus(listOf(button, button2))
        }
    }

    @BeforeEach
    fun prepareDatabase() {
        entityManager.persistAndGetId(controlPoint)
        entityManager.persistAndGetId(button)
        entityManager.persistAndGetId(button2)

    }
    @Test
    fun ` get button`(){
        Assertions.assertEquals(controlPoint, controlPointRepository.findByEnoceanId(controlPoint.enoceanId))
        Assertions.assertEquals(button, buttonRepository.getButton(controlPoint.enoceanId,Switch2RockerButtonPosition.UP_LEFT))
    }



}