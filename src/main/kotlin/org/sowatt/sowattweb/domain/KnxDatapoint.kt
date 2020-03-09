package org.sowatt.sowattweb.domain

import org.bson.types.ObjectId
import org.sowatt.sowattweb.domain.types.NoArg
import tuwien.auto.calimero.GroupAddress
import tuwien.auto.calimero.datapoint.CommandDP
import tuwien.auto.calimero.datapoint.Datapoint
import java.util.*

abstract  class KnxDatapoint(
        open var id: ObjectId?,
        open var name: String="",
        open var dptID: String="",
        open var groupAddress: Int=0) {

    fun toCommandDP(): Datapoint {
        return CommandDP(
                GroupAddress(groupAddress),
                name,
                0,
                dptID
        )
    }
}