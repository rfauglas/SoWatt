package org.sowatt.sowattweb.integration

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import tuwien.auto.calimero.GroupAddress
import tuwien.auto.calimero.KNXException
import tuwien.auto.calimero.KNXTimeoutException
import tuwien.auto.calimero.Priority
import tuwien.auto.calimero.datapoint.Datapoint
import tuwien.auto.calimero.dptxlator.DPTXlator
import tuwien.auto.calimero.link.KNXLinkClosedException
import tuwien.auto.calimero.link.KNXNetworkLink
import tuwien.auto.calimero.link.KNXNetworkLinkIP
import tuwien.auto.calimero.link.medium.TPSettings
import tuwien.auto.calimero.process.ProcessCommunicator
import tuwien.auto.calimero.process.ProcessCommunicatorImpl
import tuwien.auto.calimero.process.ProcessListener
import java.net.InetSocketAddress
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Component
class KNXProcessCommunicationWrapper : ProcessCommunicator {
    @Value("\${knx.remote.host}")
    private val remoteHost: String? = null
    @Value("\${knx.local.host}")
    private val localHost: String? = null
    //    @Autowired
//    private Logger logger;
    private val logger = LoggerFactory.getLogger(KNXProcessCommunicationWrapper::class.java)
    private var processCommunicator: ProcessCommunicator? = null
//    @PostConstruct
    @Throws(KNXException::class, InterruptedException::class)
    private fun initProcessCommmunicator() {
        val remoteEP = InetSocketAddress(remoteHost, 3671)
        val localEP = InetSocketAddress(localHost, 3671)
        val knxLink: KNXNetworkLink = KNXNetworkLinkIP.newTunnelingLink(localEP, remoteEP, false, TPSettings.TP1)
        processCommunicator = ProcessCommunicatorImpl(knxLink)
    }

    @PreDestroy
    private fun detachKNXLink() {
        processCommunicator!!.detach()
                .close()
    }

    override fun setResponseTimeout(i: Int) {
        processCommunicator!!.responseTimeout = i
    }

    override fun getResponseTimeout(): Int {
        return processCommunicator!!.responseTimeout
    }

    @Throws(KNXException::class, InterruptedException::class)
    override fun readBool(groupAddress: GroupAddress?): Boolean {
        return processCommunicator!!.readBool(groupAddress)
    }

    @Throws(KNXException::class, InterruptedException::class)
    override fun readUnsigned(groupAddress: GroupAddress?, s: String?): Int {
        return processCommunicator!!.readUnsigned(groupAddress, s)
    }

    @Throws(KNXException::class, InterruptedException::class)
    override fun readControl(groupAddress: GroupAddress?): Int {
        return processCommunicator!!.readControl(groupAddress)
    }

    @Deprecated("")
    @Throws(KNXException::class, InterruptedException::class)
    override fun readFloat(groupAddress: GroupAddress?, b: Boolean): Double {
        return processCommunicator!!.readFloat(groupAddress, b)
    }

    @Throws(KNXException::class, InterruptedException::class)
    override fun readFloat(groupAddress: GroupAddress?): Double {
        return processCommunicator!!.readFloat(groupAddress)
    }

    @Throws(KNXException::class, InterruptedException::class)
    override fun readString(groupAddress: GroupAddress?): String {
        return processCommunicator!!.readString(groupAddress)
    }

    @Throws(KNXException::class, InterruptedException::class)
    override fun read(datapoint: Datapoint?): String {
        return processCommunicator!!.read(datapoint)
    }

    @Throws(KNXException::class, InterruptedException::class)
    override fun readNumeric(datapoint: Datapoint?): Double {
        return processCommunicator!!.readNumeric(datapoint)
    }

    override fun detach(): KNXNetworkLink {
        return processCommunicator!!.detach()
    }

    override fun setPriority(priority: Priority?) {
        processCommunicator!!.priority = priority
    }

    override fun getPriority(): Priority {
        return processCommunicator!!.priority
    }

    override fun addProcessListener(processListener: ProcessListener?) {
        processCommunicator!!.addProcessListener(processListener)
    }

    override fun removeProcessListener(processListener: ProcessListener?) {
        processCommunicator!!.removeProcessListener(processListener)
    }

    @Throws(KNXTimeoutException::class, KNXLinkClosedException::class)
    override fun write(groupAddress: GroupAddress?, b: Boolean) {
        processCommunicator!!.write(groupAddress, b)
    }

    @Throws(KNXException::class)
    override fun write(groupAddress: GroupAddress?, i: Int, s: String?) {
        processCommunicator!!.write(groupAddress, i, s)
    }

    @Throws(KNXException::class)
    override fun write(groupAddress: GroupAddress?, b: Boolean, i: Int) {
        processCommunicator!!.write(groupAddress, b, i)
    }

    @Throws(KNXException::class)
    override fun write(groupAddress: GroupAddress?, v: Double, b: Boolean) {
        processCommunicator!!.write(groupAddress, v, b)
    }

    @Throws(KNXException::class)
    override fun write(groupAddress: GroupAddress?, s: String?) {
        processCommunicator!!.write(groupAddress, s)
    }

    @Throws(KNXException::class)
    override fun write(groupAddress: GroupAddress?, dptXlator: DPTXlator?) {
        processCommunicator!!.write(groupAddress, dptXlator)
    }

    @Throws(KNXException::class)
    override fun write(datapoint: Datapoint?, s: String?) {
        processCommunicator!!.write(datapoint, s)
    }

    override fun close() {
        processCommunicator!!.close()
    }
}
