package org.sowatt.sowattweb.domain

import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.sowatt.sowattweb.config.persistFixture
import org.sowatt.sowattweb.repository.KnxDatapointRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional

@DataJpaTest
class KnxDatapointTest @Autowired constructor(val entityManager: TestEntityManager,val knxDatapointRepository: KnxDatapointRepository) {

    @Test
    fun canFindByGroupAddress() {
        val switchDPID = entityManager.persistFixture(switchDP)
        Assert.assertEquals(switchDP, knxDatapointRepository.findByGroupAddress(9))
    }

    companion object {//our data sets...
        val switchDP:SwitchDP=SwitchDP(0, "name", 9, "dptID")
        fun initSwitchDP(): SwitchDP {
            return switchDP.copy();
        }
    }
}