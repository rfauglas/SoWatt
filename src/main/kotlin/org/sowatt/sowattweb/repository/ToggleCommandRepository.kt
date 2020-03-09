package org.sowatt.sowattweb.repository

import org.sowatt.sowattweb.domain.Button
import org.sowatt.sowattweb.domain.ToggleCommand
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.repository.Repository

interface ToggleCommandRepository: MongoRepository<ToggleCommand, Long> {
    fun findByButton(button: Button): ToggleCommand?
}