package org.sowatt.sowattweb.service;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sowatt.sowattweb.domain.ButtonState;
import org.sowatt.sowattweb.domain.Switch2RockerButtonPosition;
import org.sowatt.sowattweb.integration.KNXProcessCommunicationWrapper;
import org.sowatt.sowattweb.repository.GoogleSpreadSheetDatabase;
import org.sowatt.sowattweb.web.rest.StateDataPointsResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import tuwien.auto.calimero.KNXException;
import tuwien.auto.calimero.datapoint.Datapoint;
import uk.co._4ng.enocean.communication.Connection;
import uk.co._4ng.enocean.communication.DeviceListener;
import uk.co._4ng.enocean.communication.DeviceValueListener;
import uk.co._4ng.enocean.devices.DeviceManager;
import uk.co._4ng.enocean.devices.EnOceanDevice;
import uk.co._4ng.enocean.eep.EEP;
import uk.co._4ng.enocean.eep.EEPAttribute;
import uk.co._4ng.enocean.eep.EEPAttributeChangeJob;
import uk.co._4ng.enocean.eep.eep26.attributes.EEP26RockerSwitch2RockerAction;
import uk.co._4ng.enocean.eep.eep26.attributes.EEP26RockerSwitch2RockerButtonCount;
import uk.co._4ng.enocean.link.LinkLayer;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EnoceanDeviceListener implements DeviceListener, DeviceValueListener {

    private DeviceManager deviceManager;
    private Connection connection;
    private LinkLayer linkLayer;
    @Autowired
    private GoogleSpreadSheetDatabase googleSpreadSheetDatabase;
    @Autowired
    private StateDataPointsResource stateDataPointsResource;
    @Autowired
    private KNXProcessCommunicationWrapper knxProcessCommunicationWrapper;


    private static final Logger logger = LoggerFactory.getLogger(EnoceanDeviceListener.class);

    @PostConstruct
    private void initEnoceanDevices() {
        try {
            // create the lowest link layer
            linkLayer = new LinkLayer("ttyUSB0");
            List<String> ports = LinkLayer.getCommsPorts();

//            LinkLayer linkLayer = new LinkLayer("toto");$
            for (String port : LinkLayer.getCommsPorts()
            ) {
                logger.info("Found port: {}", port);
            }
            deviceManager = new DeviceManager();

            // create a device listener for handling device updates

            deviceManager.addDeviceListener(this);
            deviceManager.addDeviceValueListener(this);

            // register a rocker switch
            for (EnOceanDevice enOceanDevice : googleSpreadSheetDatabase.getAllRockerSwitch()
            ) {
                deviceManager.registerDevice(enOceanDevice);
            }

            // create the connection layer
            connection = new Connection(linkLayer, deviceManager);

            // connect the link
            linkLayer.connect();
        } catch (Exception e) {
            System.err.println("The given port does not exist or no device is plugged in" + e);
        }
    }

    @Override
    public void addedEnOceanDevice(EnOceanDevice device) {
        logger.info("Added device: {} ({})", device.getAddressHex(), device.getEEP().getIdentifier());
    }

    @Override
    public void modifiedEnOceanDevice(EnOceanDevice device) {
        logger.info("Modified device: {} ({})", device.getAddressHex(), device.getEEP().getIdentifier());
    }

    @Override
    public void removedEnOceanDevice(EnOceanDevice device) {
        logger.info("Removed device: {} ({})", device.getAddressHex(), device.getEEP().getIdentifier());
    }

    @Override
    public void deviceAttributeChange(EEPAttributeChangeJob eepAttributeChangeJob) {
        EnOceanDevice enOceanDevice = eepAttributeChangeJob.getDevice();

        for (EEPAttribute attr : eepAttributeChangeJob.getChangedAttributes()) {
            for (Switch2RockerButtonPosition buttonPosition : Switch2RockerButtonPosition.values()
            ) {
                ButtonState buttonState = googleSpreadSheetDatabase.findButtonStateBy(enOceanDevice, buttonPosition);
                if (attr instanceof EEP26RockerSwitch2RockerAction) {
                    Boolean buttonValue = ((EEP26RockerSwitch2RockerAction) attr).getButtonValue(buttonPosition.ordinal());
                    buttonState.setPressed(buttonValue);
                    if (buttonValue) {
                        buttonState.setDatePressed(LocalDateTime.now());
                    }
                }

                if (attr instanceof EEP26RockerSwitch2RockerButtonCount) {
                    EEP26RockerSwitch2RockerButtonCount buttonCount = (EEP26RockerSwitch2RockerButtonCount) attr;
                    if (buttonState.getPressed() && (buttonCount.getValue() == 0)) {
                        assert buttonState.getDatapoints().size() == 1;
                        Datapoint dp = buttonState.getDatapoints().get(0);
                        try {
                            Boolean previousValue = knxProcessCommunicationWrapper.readBool(dp.getMainAddress());
                            knxProcessCommunicationWrapper.write(dp.getMainAddress(), !previousValue);
                        } catch (KNXException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
            logger.info("Device: {} Channel: {} Attribute: {} Value: {}", eepAttributeChangeJob.getDevice().getAddressHex(), eepAttributeChangeJob.getChannelId(), attr.getName(), attr.getValue());
        }
    }
}