package org.sowatt.sowattweb.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.KNXException;
import tuwien.auto.calimero.KNXTimeoutException;
import tuwien.auto.calimero.Priority;
import tuwien.auto.calimero.datapoint.Datapoint;
import tuwien.auto.calimero.dptxlator.DPTXlator;
import tuwien.auto.calimero.link.KNXLinkClosedException;
import tuwien.auto.calimero.link.KNXNetworkLink;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.medium.TPSettings;
import tuwien.auto.calimero.process.ProcessCommunicator;
import tuwien.auto.calimero.process.ProcessCommunicatorImpl;
import tuwien.auto.calimero.process.ProcessListener;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

@Service
public class KNXProcessCommunicationWrapper implements  ProcessCommunicator {
    @Value("${knx.remote.host}")
    private String remoteHost;

    @Value("${knx.local.host}")
    private String localHost;


//    @Autowired
//    private Logger logger;
    private final Logger logger = LoggerFactory.getLogger(KNXProcessCommunicationWrapper.class);

    private ProcessCommunicator processCommunicator;

    @PostConstruct
    private void initProcessCommmunicator() throws KNXException, InterruptedException {
        final InetSocketAddress remoteEP = new InetSocketAddress(this.remoteHost, 3671);
        final InetSocketAddress localEP = new InetSocketAddress(this.localHost, 3671);

        KNXNetworkLink knxLink = KNXNetworkLinkIP.newTunnelingLink(localEP, remoteEP, false, TPSettings.TP1);
        this.processCommunicator = new ProcessCommunicatorImpl(knxLink);
    }

    @PreDestroy
    private void detachKNXLink() {
        this.processCommunicator.detach()
                .close();
    }

    @Override
    public void setResponseTimeout(int i) {
        processCommunicator.setResponseTimeout(i);
    }

    @Override
    public int getResponseTimeout() {
        return processCommunicator.getResponseTimeout();
    }

    @Override
    public boolean readBool(GroupAddress groupAddress) throws KNXException, InterruptedException {
        return processCommunicator.readBool(groupAddress);
    }

    @Override
    public int readUnsigned(GroupAddress groupAddress, String s) throws KNXException, InterruptedException {
        return processCommunicator.readUnsigned(groupAddress, s);
    }

    @Override
    public int readControl(GroupAddress groupAddress) throws KNXException, InterruptedException {
        return processCommunicator.readControl(groupAddress);
    }

    @Override
    @Deprecated
    public double readFloat(GroupAddress groupAddress, boolean b) throws KNXException, InterruptedException {
        return processCommunicator.readFloat(groupAddress, b);
    }

    @Override
    public double readFloat(GroupAddress groupAddress) throws KNXException, InterruptedException {
        return processCommunicator.readFloat(groupAddress);
    }

    @Override
    public String readString(GroupAddress groupAddress) throws KNXException, InterruptedException {
        return processCommunicator.readString(groupAddress);
    }

    @Override
    public String read(Datapoint datapoint) throws KNXException, InterruptedException {
        return processCommunicator.read(datapoint);
    }

    @Override
    public double readNumeric(Datapoint datapoint) throws KNXException, InterruptedException {
        return processCommunicator.readNumeric(datapoint);
    }

    @Override
    public KNXNetworkLink detach() {
        return processCommunicator.detach();
    }

    @Override
    public void setPriority(Priority priority) {
        processCommunicator.setPriority(priority);
    }

    @Override
    public Priority getPriority() {
        return processCommunicator.getPriority();
    }

    @Override
    public void addProcessListener(ProcessListener processListener) {
        processCommunicator.addProcessListener(processListener);
    }

    @Override
    public void removeProcessListener(ProcessListener processListener) {
        processCommunicator.removeProcessListener(processListener);
    }

    @Override
    public void write(GroupAddress groupAddress, boolean b) throws KNXTimeoutException, KNXLinkClosedException {
        processCommunicator.write(groupAddress, b);
    }

    @Override
    public void write(GroupAddress groupAddress, int i, String s) throws KNXException {
        processCommunicator.write(groupAddress, i, s);
    }

    @Override
    public void write(GroupAddress groupAddress, boolean b, int i) throws KNXException {
        processCommunicator.write(groupAddress, b, i);
    }

    @Override
    public void write(GroupAddress groupAddress, double v, boolean b) throws KNXException {
        processCommunicator.write(groupAddress, v, b);
    }

    @Override
    public void write(GroupAddress groupAddress, String s) throws KNXException {
        processCommunicator.write(groupAddress, s);
    }

    @Override
    public void write(GroupAddress groupAddress, DPTXlator dptXlator) throws KNXException {
        processCommunicator.write(groupAddress, dptXlator);
    }

    @Override
    public void write(Datapoint datapoint, String s) throws KNXException {
        processCommunicator.write(datapoint, s);
    }

    @Override
    public void close() {
        processCommunicator.close();
    }
}
