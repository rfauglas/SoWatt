package org.sowatt.sowattweb.domain

import org.sowatt.sowattweb.domain.types.NoArg
import tuwien.auto.calimero.GroupAddress
import tuwien.auto.calimero.datapoint.CommandDP
import tuwien.auto.calimero.datapoint.Datapoint
import java.util.*
import javax.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn( discriminatorType = DiscriminatorType.STRING, name = "toto")
abstract  class KnxDatapoint(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    open var id: Long=0,
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