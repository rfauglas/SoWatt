package org.sowatt.sowattweb.web.rest;

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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.*;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
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

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/knx")
public class StateDataPointsResource {

    @Autowired
    private KNXProcessCommunicationWrapper knxProcessCommunicationWrapper;

    private static final String APPLICATION_NAME = "SoWatt GoogleSpreadSheet data";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    Map<Integer, Datapoint> knxMap = new HashMap<>();

    @PostConstruct
    private void initData() throws GeneralSecurityException, IOException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String spreadsheetId = "18R2--HkLRexIaAEIpupE0yf0cq-kqAYQ-EARl2lUmpg";
        final String range = "KNX!A2:C35";
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, this.getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        for (List<Object> row : response.getValues()) {
            GroupAddress groupAddress = new GroupAddress(
                    Integer.parseInt((String) row.get(1))
            );
            Integer addressId = groupAddress.getRawAddress();
            knxMap.put(groupAddress.getRawAddress(), new CommandDP(groupAddress,
                    (String) row.get(0), 0,
                    (String) row.get(2))
            );
        }
    }

    @GetMapping(path = "/statedatapoints")
    public List<StateDataPoint> getDataPoints() {
        return new ArrayList<StateDataPoint>();
    }

    @GetMapping(path = "/statedatapoints/{address}")
    public StateDataPoint getStateDatapointById(@PathVariable() int address) throws KNXException, InterruptedException {
        Datapoint commandDP = this.knxMap.get(address);
        StateDataPoint stateDataPoint = new StateDataPoint()
                .setDatapoint(commandDP)
                .setValue(knxProcessCommunicationWrapper.read(commandDP));
        return stateDataPoint;

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
