package org.sen.webapp;

import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.Sheets.Spreadsheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

import org.json.JSONArray;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

class AccessGoogleSheetsHelper
    {
        private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

        private static final String APP_NAME = "Google Calendar Data API Sample Web Client";

        private static final Logger logger = Logger.getLogger( AccessGoogleSheetsHelper.class.getName() );

        private static final String ValueInputOption = "USER_ENTERED";

        public static Sheets getSpreadSheets() throws IOException , GeneralSecurityException
            {
                Set <String> scope = Collections.singleton( SheetsScopes.SPREADSHEETS );
                Credential credential = convertGoogleCredentialsToCredential( getGoogleCredentials( scope ) );
                final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
                return new Sheets.Builder( HTTP_TRANSPORT , JSON_FACTORY , credential ).setApplicationName( APP_NAME ).build();
            }

        public static Spreadsheets getSpreadSheetsService() throws IOException , GeneralSecurityException
            {
                Sheets sheets = getSpreadSheets();
                return sheets.spreadsheets();
            }

        private static GoogleCredentials getGoogleCredentials( Set <String> scopes ) throws IOException
            {
                GoogleCredentials credentials = getServiceAccountCredentials().createScoped( scopes );
                credentials.refreshIfExpired();
                return credentials;
            }

        private static GoogleCredentials getServiceAccountCredentials() throws IOException
            {
                return GoogleCredentials.fromStream( AccessGoogleSheetsHelper.class.getResourceAsStream( "/access-sheets-java.json" ) );
            }

        private static Credential convertGoogleCredentialsToCredential( GoogleCredentials googleCredentials )
            {
                AccessToken accessToken = googleCredentials.getAccessToken();
                return new Credential( BearerToken.authorizationHeaderAccessMethod() ).setAccessToken( accessToken.getTokenValue() );
            }

        public static String writeDataToSheet( String spreadsheetId , String sheetName , List <List <Object>> data ) throws IOException ,
                                                                                                                     GeneralSecurityException
            {
                String range = sheetName;
                ValueRange valueRange = new ValueRange();
                valueRange.setValues( data );
                logger.info( "data to be written to sheets" + data.toString() );
                Spreadsheets spreadsheets = getSpreadSheetsService();
                return spreadsheets.values().append( spreadsheetId , range , valueRange ).setValueInputOption( ValueInputOption ).execute()
                        .getTableRange();
            }

        public static String writeDataToSheet( String spreadsheetId , String sheetName , JSONArray data ) throws IOException ,
                                                                                                          GeneralSecurityException
            {
                List <List <Object>> dataList = convertJsonArrayToList( data );
                return writeDataToSheet( spreadsheetId , sheetName , dataList );
            }

        private static List <List <Object>> convertJsonArrayToList( JSONArray jsonArray )
            {
                List <List <Object>> rowList = new ArrayList <List <Object>>();
                if(jsonArray.get(0) instanceof JSONArray)
                    jsonArray.forEach( object -> {
                        List <Object> columnList = ( (JSONArray) object ).toList();
                        rowList.add( columnList );
                    } );
                else rowList.add(jsonArray.toList());
                return rowList;
            }
    }