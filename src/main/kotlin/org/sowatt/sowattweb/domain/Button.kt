package org.sowatt.sowattweb.domain

import org.sowatt.sowattweb.domain.types.Switch2RockerButtonPosition
import uk.co._4ng.enocean.devices.DeviceManager
import uk.co._4ng.enocean.devices.EnOceanDevice
import uk.co._4ng.enocean.eep.EEPIdentifier
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class ControlPoint(@Id @GeneratedValue  var id: Long, var enoceanId: String, @OneToMany var buttons: Set<Button>) {


    fun toEnoceanDevice():EnOceanDevice {
        return DeviceManager.createDevice(
                EnOceanDevice.parseAddress(enoceanId),
                null,
                EEPIdentifier.parse("F60201"))
    }
}

@Entity
class Button(@Id @GeneratedValue  var id: Long, @ManyToOne var controlPoint: ControlPoint, var buttonPosition: Switch2RockerButtonPosition, var isPressed:Boolean=false, var datePressed:LocalDateTime?=null
)