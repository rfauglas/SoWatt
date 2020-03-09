package org.sowatt.sowattweb.repository

import org.sowatt.sowattweb.domain.Button
import org.sowatt.sowattweb.domain.ControlPoint
import org.sowatt.sowattweb.domain.types.Switch2RockerButtonPosition
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface ButtonRepository: MongoRepository<Button, Long> {
    fun findByControlPoint(controlPoint: ControlPoint): Iterable<Button>

    @Query("select b from Button b where b.controlPoint.enoceanId =:enoceanId and b.buttonPosition =  :buttonPosition")
    fun getButton( enoceanId: String, buttonPosition: Switch2RockerButtonPosition): Button?
}

