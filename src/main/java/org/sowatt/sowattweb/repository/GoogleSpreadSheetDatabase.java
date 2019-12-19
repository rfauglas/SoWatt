package org.sowatt.sowattweb.repository;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sowatt.sowattweb.domain.ButtonState;
import org.sowatt.sowattweb.domain.Switch2RockerButtonPosition;
import org.sowatt.sowattweb.web.rest.StateDataPointsResource;
import org.springframework.stereotype.Service;
import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.datapoint.CommandDP;
import tuwien.auto.calimero.datapoint.Datapoint;
import uk.co._4ng.enocean.devices.DeviceManager;
import uk.co._4ng.enocean.devices.EnOceanDevice;
import uk.co._4ng.enocean.eep.EEPIdentifier;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.*;

@Service
public class GoogleSpreadSheetDatabase {

    private static final String APPLICATION_NAME = "SoWatt GoogleSpreadSheet data";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private Logger logger = (Logger) LoggerFactory.getLogger(GoogleSpreadSheetDatabase.class);

    enum GsEnOceanColumn {
        DEVICE_ID,
        BUTTON_POSITION,
        DATAPOINT_NAME,
        KNX_ADDRESS,
    }


    private Map<Integer, Datapoint> knxMap = new HashMap<>();
    //Since EnOceanDevice does not implement equals/hashCode properly, we use a tree map.
    private Map<EnOceanDevice, Map<Switch2RockerButtonPosition, ButtonState>> enOceanDeviceRockerMap = new TreeMap<>(new Comparator<EnOceanDevice>() {
        @Override
        public int compare(EnOceanDevice left, EnOceanDevice right) {
            return left.getAddressHex().compareTo(right.getAddressHex());
        }
    });

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    @PostConstruct()
    private void init() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String spreadsheetId = "18R2--HkLRexIaAEIpupE0yf0cq-kqAYQ-EARl2lUmpg";
        final String KNX_RANGE = "KNX!A2:C35";
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, this.getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        ValueRange knxRange = service.spreadsheets().values()
                .get(spreadsheetId, KNX_RANGE)
                .execute();
        // Build a new authorized API client service.

        for (List<Object> row : knxRange.getValues()) {
            GroupAddress groupAddress = new GroupAddress(
                    Integer.parseInt((String) row.get(1))
            );
            Integer addressId = groupAddress.getRawAddress();
            knxMap.put(groupAddress.getRawAddress(), new CommandDP(groupAddress,
                    (String) row.get(0), 0,
                    (String) row.get(2))
            );
        }
        String ENOCEAN_SWITCH_ROCKER_RANGE = "Enocean-switch!A2:D100";
        ValueRange enOceanRockerRange = service.spreadsheets().values()
                .get(spreadsheetId, ENOCEAN_SWITCH_ROCKER_RANGE)
                .execute();
        for (List<Object> enOceanLine : enOceanRockerRange.getValues()) {
            String enOceanId = (String) enOceanLine.get(GsEnOceanColumn.DEVICE_ID.ordinal());
            //Stop uppon empty line
            if (enOceanId == null
                    || "".equals(enOceanId))
                break;
            EnOceanDevice enOceanDevice = DeviceManager.createDevice(
                    EnOceanDevice.parseAddress(enOceanId),
                    null,
                    EEPIdentifier.parse("F60201"));
            Map<Switch2RockerButtonPosition, ButtonState> switch2RockerButtonsPositionDatapointMap = enOceanDeviceRockerMap.get(enOceanDevice);
            if (switch2RockerButtonsPositionDatapointMap == null) {
                switch2RockerButtonsPositionDatapointMap = new HashMap<>();
                enOceanDeviceRockerMap.put(enOceanDevice, switch2RockerButtonsPositionDatapointMap);
            }

            Switch2RockerButtonPosition switchPosition = Switch2RockerButtonPosition.valueOf((String) enOceanLine.get(GsEnOceanColumn.BUTTON_POSITION.ordinal())
            );
            ButtonState buttonState = switch2RockerButtonsPositionDatapointMap.get(switchPosition);
            if (buttonState == null) {
                buttonState = new ButtonState();
                switch2RockerButtonsPositionDatapointMap.put(switchPosition, buttonState);
            }

            Datapoint datapoint = knxMap.get(Integer.parseInt(
                    (String)enOceanLine.get(GsEnOceanColumn.KNX_ADDRESS.ordinal()))
            );
            buttonState
                    .getDatapoints()
                    .add(datapoint);
            logger.info("Inserted knx address {} to Enocean 2 rocker switch {}-{}", datapoint.getMainAddress(), enOceanId, switchPosition.name());
        }
    }

    public Datapoint getDataPointById(int address) {
        return knxMap.get(address);
    }

    public ButtonState findButtonStateBy(EnOceanDevice enOceanDevice, Switch2RockerButtonPosition position) {
        return this.enOceanDeviceRockerMap
                .get(enOceanDevice)
                .get(position);
    }

    public Set<EnOceanDevice> getAllRockerSwitch() {
        return enOceanDeviceRockerMap.keySet();
    }

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws java.io.IOException If the credentials.json file cannot be found.
     */
    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = StateDataPointsResource.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }


}
