package org.sowatt.sowattweb.domain

import org.springframework.data.annotation.Id
import java.time.Duration

data class ToggleCommand(@Id  val id: Long, var button: Button, var switchList: MutableList<SwitchDP> = mutableListOf<SwitchDP>()) {
}