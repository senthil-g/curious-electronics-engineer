package org.sen.webapp;

import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.Sheets.Spreadsheets;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.Sheet;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class AccessGoogleSheetsHelper
    {
        private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

        private static final String APP_NAME = "Google Calendar Data API Sample Web Client";

        private static final Logger logger = Logger.getLogger( AccessGoogleSheetsHelper.class.getName() );

        private static final String ValueInputOption = "USER_ENTERED";

        private static final Map <String , Map <String , CustomList <CustomList <Object>>>> sheetValuesMap = new HashMap <String , Map <String , CustomList <CustomList <Object>>>>();

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

        public static String appendDataToSheet( String spreadsheetId , String sheetName , List <List <Object>> data ) throws IOException ,
                                                                                                                      GeneralSecurityException
            {
                String range = sheetName;
                ValueRange valueRange = new ValueRange();
                valueRange.setValues( data );
                Spreadsheets spreadsheets = getSpreadSheetsService();
                return spreadsheets.values().append( spreadsheetId , range , valueRange ).setValueInputOption( ValueInputOption ).execute()
                        .getTableRange();
            }

        private static Map <String , CustomList <CustomList <Object>>> getAllSheetValues( String spreadsheetId ) throws IOException , GeneralSecurityException
            {
                if ( sheetValuesMap.containsKey( spreadsheetId ) )
                    {
                        return sheetValuesMap.get( spreadsheetId );
                    }
                else
                    {
                        List <Sheet> sheetsList = getSpreadSheetsService().get( spreadsheetId ).execute().getSheets();
                        Iterator <Sheet> sheetIterator = sheetsList.iterator();
                        List <String> ranges = new ArrayList <String>();
                        while ( sheetIterator.hasNext() )
                            {
                                Sheet currentSheet = sheetIterator.next();
                                ranges.add( currentSheet.getProperties().getTitle() );
                            }
                        BatchGetValuesResponse batchGetValuesResponse = getSpreadSheetsService().values().batchGet( spreadsheetId )
                                .setRanges( ranges ).execute();
                        Map <String , CustomList <CustomList <Object>>> singleSheetValues = new HashMap <String , CustomList <CustomList <Object>>>();
                        List <ValueRange> valueRangeList = batchGetValuesResponse.getValueRanges();
                        // need to fix this
                        // for ( int index = 0 ; index < valueRangeList.size() ; index++ )
                        //     singleSheetValues.put( ranges.get( index ) , (CustomList<CustomList<Object>>) valueRangeList.get( index ).getValues() );
                        sheetValuesMap.put( spreadsheetId , singleSheetValues );
                        return singleSheetValues;
                    }
            }

        public static CustomList <CustomList <Object>> getSheetValuesBySheetName( String spreadsheetId , String sheetName ) throws IOException ,
                                                                                                                 GeneralSecurityException ,
                                                                                                                 SheetNotFoundException
            {
                Map <String , CustomList <CustomList <Object>>> allSheetValues = getAllSheetValues( spreadsheetId );
                if ( allSheetValues.containsKey( sheetName ) )
                    return allSheetValues.get( sheetName );
                else
                    throw new SheetNotFoundException( "No sheet found with the name : " + sheetName );
            }

        public static String appendDataToSheet( String spreadsheetId , String sheetName , JSONArray data ) throws IOException ,
                                                                                                           GeneralSecurityException
            {
                List <List <Object>> dataList = convertJsonArrayToList( data );
                return appendDataToSheet( spreadsheetId , sheetName , dataList );
            }

        private static List <List <Object>> convertJsonArrayToList( JSONArray jsonArray )
            {
                List <List <Object>> rowList = new ArrayList <List <Object>>();
                if ( jsonArray.get( 0 ) instanceof JSONArray )
                    jsonArray.forEach( object -> {
                        List <Object> columnList = ( (JSONArray) object ).toList();
                        rowList.add( columnList );
                    } );
                else
                    rowList.add( jsonArray.toList() );
                return rowList;
            }

        private static String getColumnNameFromIndex( int columnIndex )
            {
                String alphabets = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                int lengthOfColumnName = ( ( columnIndex + 1 ) / 26 ) + 1;
                StringBuilder columnName = new StringBuilder();
                for ( int i = 0 ; i < lengthOfColumnName ; i++ )
                    columnName.append( alphabets.charAt( columnIndex - ( alphabets.length() * ( lengthOfColumnName - 1 ) ) ) );
                return columnName.toString();
            }

        public static String getRangeOfTheText( String spreadsheetId , String sheetName , int columnIndex ,
                                                String searchText ) throws IOException , GeneralSecurityException , SearchTextNotFound
            {
                String columnName = getColumnNameFromIndex( columnIndex );
                List <List <Object>> columnNames = getSpreadSheetsService().values()
                        .get( spreadsheetId , "'" + sheetName + "'!" + columnName + ":" + columnName ).execute().getValues();
                String rangeOfValue = null;
                for ( List <Object> rowValue : columnNames )
                    {
                        for ( Object columnValue : rowValue )
                            {
                                if ( columnValue.equals( searchText ) )
                                    {
                                        int rowIndex = columnNames.indexOf( rowValue );
                                        rangeOfValue = "'" + sheetName + "'!" + columnName + rowIndex + ":" + columnName + rowIndex;
                                    }
                            }
                    }
                if ( rangeOfValue == null )
                    throw new SearchTextNotFound(
                            "The search text : " + searchText + " was not found in the sheet " + sheetName + " on the column name " + columnName );
                else
                    return rangeOfValue;
            }

        public static String rangeToSheetName( String range )
            {
                return range.substring( 1 , range.lastIndexOf( "'" ) );
            }

        public static int rangeToStartColumnIndex( String range )
            {
                String rowAndColumnIndexString = range.substring( range.lastIndexOf( "'" ) ).substring( 2 );
                String startIndexes = rowAndColumnIndexString.split( ":" ) [0];
                Pattern regex = Pattern.compile( "[0-9]*+$" );
                Matcher matcher = regex.matcher( startIndexes );
                matcher.find();
                return Integer.valueOf( matcher.group( 0 ) );
            }

        public static int rangeToEndColumnIndex( String range )
            {
                String rowAndColumnIndexString = range.substring( range.lastIndexOf( "'" ) ).substring( 2 );
                String startIndexes = rowAndColumnIndexString.split( ":" ) [1];
                Pattern regex = Pattern.compile( "[0-9]*+$" );
                Matcher matcher = regex.matcher( startIndexes );
                matcher.find();
                return Integer.valueOf( matcher.group( 0 ) );
            }

        public static String rangeToStartRowIndex( String range )
            {
                String rowAndColumnIndexString = range.substring( range.lastIndexOf( "'" ) ).substring( 2 );
                String startIndexes = rowAndColumnIndexString.split( ":" ) [0];
                Pattern regex = Pattern.compile( "^+[a-zA-Z]*" );
                Matcher matcher = regex.matcher( startIndexes );
                matcher.find();
                return matcher.group( 0 );
            }

        public static String rangeToEndRowIndex( String range )
            {
                String rowAndColumnIndexString = range.substring( range.lastIndexOf( "'" ) ).substring( 2 );
                String startIndexes = rowAndColumnIndexString.split( ":" ) [1];
                Pattern regex = Pattern.compile( "^+[a-zA-Z]*" );
                Matcher matcher = regex.matcher( startIndexes );
                matcher.find();
                return matcher.group( 0 );
            }
    }