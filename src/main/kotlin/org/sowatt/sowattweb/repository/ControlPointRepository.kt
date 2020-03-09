package org.sowatt.sowattweb.repository

import org.sowatt.sowattweb.domain.ControlPoint
import org.springframework.data.mongodb.repository.MongoRepository

interface ControlPointRepository: MongoRepository<ControlPoint, String> {

    fun findByEnoceanId(enoceanId: String): ControlPoint?
}