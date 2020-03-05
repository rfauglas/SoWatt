package org.sowatt.sowattweb.domain

import java.time.Duration
import javax.persistence.*

@Entity
data class ToggleCommand(@Id @GeneratedValue(strategy = GenerationType.AUTO) val id: Long, @OneToOne  var button: Button, @ManyToMany() var switchList: MutableList<SwitchDP> = mutableListOf<SwitchDP>()) {



}