package org.sowatt.sowattweb.domain

import org.bson.types.ObjectId
import org.sowatt.sowattweb.domain.types.ShutterState
import org.sowatt.sowattweb.domain.types.SwitchState
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Field

data class SwitchDP(@Id override var id: ObjectId?, @Field("overridenName") override var name: String, @Field("overridenGroupAddress") override var groupAddress: Int, @Field("overridenDptID") override var dptID: String, var switchState: SwitchState? = null) :  KnxDatapoint(id,name, dptID, groupAddress)

data class ShutterDP(@Id  override var id: ObjectId?, @Field("overridenName") override var name: String, @Field("overridenGroupAddress")override var groupAddress: Int, @Field("overridenDptID") override var dptID: String, var shutterState: ShutterState? = null) :  KnxDatapoint(id, name, dptID, groupAddress)

enum class DatapointType {
    SWITCH,
    SHUTTER
}
