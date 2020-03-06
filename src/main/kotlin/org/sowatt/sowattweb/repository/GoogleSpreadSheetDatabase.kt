package org.sowatt.sowattweb.repository

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.sowatt.sowattweb.domain.*
import org.sowatt.sowattweb.domain.types.Switch2RockerButtonPosition
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import tuwien.auto.calimero.datapoint.Datapoint
import uk.co._4ng.enocean.devices.EnOceanDevice
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.security.GeneralSecurityException
import java.util.*
import javax.annotation.PostConstruct
import javax.persistence.EntityManager

@Service
@Transactional(propagation = Propagation.REQUIRED)
class GoogleSpreadSheetDatabase(private val entityManager: EntityManager, private val transactionManager: PlatformTransactionManager, private val switchRepository: KnxDatapointRepository, private val controlPointRepository: ControlPointRepository, private val buttonRepository: ButtonRepository, private val toggleCommandRepository: ToggleCommandRepository, private val knxDatapointRepository: KnxDatapointRepository) {
    private val logger = LoggerFactory.getLogger(GoogleSpreadSheetDatabase::class.java) as Logger

    internal enum class GsEnOceanColumn {
        DEVICE_ID, BUTTON_POSITION, DATAPOINT_NAME, KNX_ADDRESS
    }

    public fun init() {
        val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
        // Build a new authorized API client service.
        val values = Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build().spreadsheets().values()

        val spreadsheetId = "18R2--HkLRexIaAEIpupE0yf0cq-kqAYQ-EARl2lUmpg"

        val transactionTemplate: TransactionTemplate = TransactionTemplate(transactionManager);
        transactionTemplate.execute {
            val KNX_RANGE = "KNX!A2:D35"
            for (row in values[spreadsheetId, KNX_RANGE].execute().getValues())
                when (DatapointType.valueOf(row[3] as String)) {
                    DatapointType.SWITCH -> this.entityManager.persist(
                            SwitchDP(0, row[0] as String, (row[1] as String).toInt(), row[2] as String))
                    DatapointType.SHUTTER -> this.entityManager.persist(
                            ShutterDP(0, row[0] as String, (row[1] as String).toInt(), row[2] as String))
                }

            val ENOCEAN_SWITCH_ROCKER_RANGE = "Enocean-switch!A2:D100"
            for (row in values[spreadsheetId, ENOCEAN_SWITCH_ROCKER_RANGE]
                    .execute().getValues()) {
                row as List<String>
                val enOceanId = row[GsEnOceanColumn.DEVICE_ID.ordinal]
                val groupAddressID = row[GsEnOceanColumn.KNX_ADDRESS.ordinal].toInt()

                //Stop uppon empty line
                if ("" == enOceanId) break
                val controlPoint = controlPointRepository.findByEnoceanId(enOceanId)
                        ?: entityManager.merge(ControlPoint(0, enOceanId))

                val buttonPosition: Switch2RockerButtonPosition = Switch2RockerButtonPosition.valueOf(row[GsEnOceanColumn.BUTTON_POSITION.ordinal])
                val button: Button = buttonRepository.getButton(controlPoint.enoceanId, buttonPosition)
                        ?: entityManager.merge(Button(0, controlPoint, buttonPosition))

                val toggleCommand: ToggleCommand=toggleCommandRepository.findByButton(button)?:
                        ToggleCommand(0,button)
                val switchDP = knxDatapointRepository.findByGroupAddress(groupAddressID) as SwitchDP
                toggleCommand.switchList.add(switchDP)
                entityManager.persist(button.controlPoint)
                entityManager.persist(toggleCommand)
            }

        }

    }

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws java.io.IOException If the credentials.json file cannot be found.
     */
    @Throws(IOException::class)
    private fun getCredentials(HTTP_TRANSPORT: NetHttpTransport): Credential { // Load client secrets.
        val `in` = GoogleSpreadSheetDatabase::class.java.getResourceAsStream(CREDENTIALS_FILE_PATH)
                ?: throw FileNotFoundException("Resource not found: $CREDENTIALS_FILE_PATH")
        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(`in`))
        // Build flow and trigger user authorization request.
        val flow = GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build()
        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }

    companion object {
        private const val APPLICATION_NAME = "SoWatt GoogleSpreadSheet data"
        private val JSON_FACTORY: JsonFactory = JacksonFactory.getDefaultInstance()
        private const val TOKENS_DIRECTORY_PATH = "tokens"
        /**
         * Global instance of the scopes required by this quickstart.
         * If modifying these scopes, delete your previously saved tokens/ folder.
         */
        private val SCOPES = listOf(SheetsScopes.SPREADSHEETS_READONLY)
        private const val CREDENTIALS_FILE_PATH = "/credentials.json"
    }
}
