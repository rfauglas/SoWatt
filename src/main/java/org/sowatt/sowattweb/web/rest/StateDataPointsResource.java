package org.sowatt.sowattweb.web.rest;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

import org.sowatt.sowattweb.domain.DataPointState;
import org.sowatt.sowattweb.integration.KNXProcessCommunicationWrapper;
import org.sowatt.sowattweb.repository.GoogleSpreadSheetDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tuwien.auto.calimero.KNXException;
import tuwien.auto.calimero.datapoint.Datapoint;

import javax.annotation.PostConstruct;
import java.util.List;

@RestController
@RequestMapping("/api/knx")
public class StateDataPointsResource {

    @Autowired
    private KNXProcessCommunicationWrapper knxProcessCommunicationWrapper;

    @Autowired
    private GoogleSpreadSheetDatabase googleSpreadSheetRepository;


    @PostConstruct
    private void initData() throws GeneralSecurityException, IOException {

    }

    @GetMapping(path = "/statedatapoints")
    public List<DataPointState> getDataPoints() {
        return new ArrayList<DataPointState>();
    }

    @GetMapping(path = "/statedatapoints/{address}")
    public DataPointState getStateDatapointById(@PathVariable() int address) throws KNXException, InterruptedException {
        Datapoint commandDP = this.googleSpreadSheetRepository.getDataPointById(address);
        DataPointState stateDataPoint = new DataPointState()
                .setDatapoint(commandDP)
                .setValue(knxProcessCommunicationWrapper.read(commandDP));
        return stateDataPoint;
    }

}
