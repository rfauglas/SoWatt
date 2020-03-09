package org.sowatt.sowattweb.domain

import org.sowatt.sowattweb.domain.types.Switch2RockerButtonPosition
import org.springframework.data.annotation.Id
import uk.co._4ng.enocean.devices.DeviceManager
import uk.co._4ng.enocean.devices.EnOceanDevice
import uk.co._4ng.enocean.eep.EEPIdentifier
import java.time.LocalDateTime

data class ControlPoint(@Id var id: Long, var enoceanId: String) {
    lateinit var buttons: Set<Button>


    fun toEnoceanDevice():EnOceanDevice {
        return DeviceManager.createDevice(
                EnOceanDevice.parseAddress(enoceanId),
                null,
                EEPIdentifier.parse("F60201"))
    }
}

data class Button(@Id var id: Long, var controlPoint: ControlPoint, var buttonPosition: Switch2RockerButtonPosition, var isPressed:Boolean=false, var datePressed:LocalDateTime?=null
)