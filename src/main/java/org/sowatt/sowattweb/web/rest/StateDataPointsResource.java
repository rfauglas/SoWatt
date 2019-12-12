package org.sowatt.sowattweb.web.rest;

import org.sowatt.sowattweb.domain.StateDataPoint;
import org.sowatt.sowattweb.integration.KNXProcessCommunicationWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.KNXException;
import tuwien.auto.calimero.KNXFormatException;
import tuwien.auto.calimero.datapoint.CommandDP;
import tuwien.auto.calimero.datapoint.Datapoint;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/knx")
public class StateDataPointsResource {

    @Autowired
    private KNXProcessCommunicationWrapper knxProcessCommunicationWrapper;

    @GetMapping(path = "/statedatapoints")
    public List<StateDataPoint> getDataPoints() {
        return new ArrayList<StateDataPoint>();
    }

    @GetMapping(path = "/statedatapoints/{address}")
    public StateDataPoint getStateDatapointById(@PathVariable() int address) throws KNXException, InterruptedException {
        GroupAddress groupAddress = new GroupAddress(
                address
        );
        Datapoint commandDP= new CommandDP(groupAddress,"chambre isaoronan",0, "1.001");
        StateDataPoint stateDataPoint = new StateDataPoint()
                .setDatapoint(commandDP)
                .setValue(knxProcessCommunicationWrapper.read(commandDP));
        return stateDataPoint;

    }
}
