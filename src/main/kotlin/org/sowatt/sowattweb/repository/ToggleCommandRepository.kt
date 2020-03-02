package org.sowatt.sowattweb.repository

import org.sowatt.sowattweb.domain.Button
import org.sowatt.sowattweb.domain.ToggleCommand
import org.springframework.data.repository.Repository

interface ToggleCommandRepository: Repository<ToggleCommand, Long> {
    fun findByButton(button: Button): ToggleCommand?

    fun findAll(): Iterable<ToggleCommand>
}