package org.sowatt.sowattweb.repository

import org.sowatt.sowattweb.domain.KnxDatapoint
import org.sowatt.sowattweb.domain.SwitchDP
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.repository.Repository

interface KnxDatapointRepository: MongoRepository<KnxDatapoint, Long> {
    fun findByGroupAddress(groupAddress: Int): KnxDatapoint
}

