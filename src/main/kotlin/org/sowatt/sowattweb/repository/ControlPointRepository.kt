package org.sowatt.sowattweb.repository

import org.sowatt.sowattweb.domain.ControlPoint
import org.springframework.data.repository.Repository

interface ControlPointRepository: Repository<ControlPoint, String> {

    fun findAll(): Iterable<ControlPoint>
    fun findByEnoceanId(enoceanId: String): ControlPoint?
}