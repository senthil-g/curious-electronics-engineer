package org.sen.webapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

@SuppressWarnings( "serial" )
@WebServlet( name = "SengridBounceAndBlockWebHookHandler" , urlPatterns =
    { "/sengridBounceAndBlockWebHookHandler" } )
public class SengridBounceAndBlockWebHookHandler extends HttpServlet
    {
        private static Logger logger = Logger.getLogger( SengridBounceAndBlockWebHookHandler.class.getName() );

        static final ResourceBundle resourceBundle = ResourceBundle.getBundle( "Resources" );

        static final String SpreadSheetId = resourceBundle.getString( "sendgrid.report.googlesheet.id" );


        private static Map <String , Integer> sendGridWebhookPropertyMap = new HashMap <String , Integer>()
            {
                    {
                        put( "email" , 0 );
                        put( "event" , 1 );
                        put( "ip" , 2 );
                        put( "reason" , 3 );
                        put( "sg_event_id" , 4 );
                        put( "sg_message_id" , 5 );
                        put( "smtp-id" , 6 );
                        put( "status" , 7 );
                        put( "timestamp" , 8 );
                        put( "tls" , 9 );
                        put( "type" , 10 );
                    }
            };

        @Override
        protected void doPost( HttpServletRequest req , HttpServletResponse resp ) throws ServletException , IOException
            {
                Object requestData = getRequestBody( req.getInputStream() , req.getContentType() );
                try
                    {
                        AccessGoogleSheetsHelper.writeDataToSheet( SpreadSheetId , "Sheet1" ,
                                convertToListRows( requestData ) );
                    }
                catch ( Exception exception )
                    {
                        logger.warning( exception.getMessage() );
                    }
            }

        public static Object getRequestBody( InputStream inputStream , String contentType ) throws IOException
            {
                BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( inputStream ) );
                StringBuilder stringBuilder = new StringBuilder();
                String line = new String();
                while ( ( line = bufferedReader.readLine() ) != null )
                    {
                        stringBuilder.append( line );
                    }
                String resultString = stringBuilder.toString();
                if ( isNotNullOrEmpty( contentType ) )
                    {
                        if ( contentType.contains( "/" ) )
                            {
                                String type = contentType.split( "/" ) [1];
                                return convertToType( resultString , type );
                            }
                        else
                            return convertToType( resultString , contentType );
                    }
                return resultString;
            }

        private static boolean isNotNullOrEmpty( Object inpuObject )
            {
                return inpuObject != null && !inpuObject.toString().trim().isEmpty();
            }

        private static Object convertToType( String data , String type )
            {
                if ( type.toLowerCase().equals( "json" ) )
                    {
                        data = data.trim();
                        if ( data.startsWith( "{" ) )
                            return new JSONObject( data );
                        else if ( data.startsWith( "[" ) )
                            return new JSONArray( data );
                        else
                            throw new IllegalArgumentException( "JSON should start with { or [ but the input starts with " + data.charAt( 0 ) );
                    }
                else
                    return data;
            }

        private static List <List <Object>> convertToListRows( Object object )
            {
                JSONArray jsonArray = (JSONArray) object;
                List <List <Object>> finalList = new ArrayList <>();
                jsonArray.forEach( row -> {
                    List <Object> orderedList = Stream.generate( () -> new String() ).limit( 11 ).collect( Collectors.toList() );
                    JSONObject rowObject = (JSONObject) row;
                    rowObject.keys().forEachRemaining( key -> {
                        orderedList.set( sendGridWebhookPropertyMap.get( key ) , rowObject.get( key ) );
                    } );
                    finalList.add( orderedList );
                } );
                return finalList;
            }
    }