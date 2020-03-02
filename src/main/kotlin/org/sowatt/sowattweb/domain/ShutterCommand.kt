package org.sowatt.sowattweb.domain

import javax.persistence.*

@Entity
data class ShutterCommand(@Id @GeneratedValue var id: Long=0, @OneToOne val openButton: Button, @OneToOne val closeButton: Button, @OneToOne var shutter: ShutterDP) {
}