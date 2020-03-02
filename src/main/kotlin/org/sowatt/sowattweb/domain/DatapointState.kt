package org.sowatt.sowattweb.domain

import org.sowatt.sowattweb.domain.types.ShutterState
import org.sowatt.sowattweb.domain.types.SwitchState
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class SwitchDP(@Id @GeneratedValue(strategy = GenerationType.AUTO)  override var id: Long, override var name: String, override var groupAddress: Int, override var dptID: String, var switchState: SwitchState? = null) :  KnxDatapoint(id,name, dptID, groupAddress)

@Entity
data class ShutterDP(@Id @GeneratedValue(strategy = GenerationType.AUTO)  override var id: Long, override var name: String, override var groupAddress: Int, override var dptID: String, var shutterState: ShutterState? = null) :  KnxDatapoint(id, name, dptID, groupAddress)

enum class DatapointType {
    SWITCH,
    SHUTTER
}
