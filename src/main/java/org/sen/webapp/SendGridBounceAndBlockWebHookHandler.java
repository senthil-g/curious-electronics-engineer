package org.sen.webapp;

import java.io.IOException;
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

import org.sen.webapp.utilities.Convert;

@SuppressWarnings( "serial" )
@WebServlet( name = "SengridBounceAndBlockWebHookHandler" , urlPatterns =
    { "/sengridBounceAndBlockWebHookHandler" } )
public class SendGridBounceAndBlockWebHookHandler extends HttpServlet
    {
        private static Logger logger = Logger.getLogger( SendGridBounceAndBlockWebHookHandler.class.getName() );

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
                JSONArray requestData = (JSONArray) Convert.getRequestBody( req.getInputStream() , req.getContentType() );
                performActionsOnSendGridData( requestData );
            }
            
        private static List <Object> converJSONObjectToList( Object object )
            {
                List <Object> orderedList = Stream.generate( () -> new String() ).limit( 11 ).collect( Collectors.toList() );
                JSONObject rowObject = (JSONObject) object;
                rowObject.keys().forEachRemaining( key -> {
                    orderedList.set( sendGridWebhookPropertyMap.get( key ) , rowObject.get( key ) );
                } );
                return orderedList;
            }

        private static void performActionsOnSendGridData( JSONArray requestData )
            {
                try
                    {
                        JSONObject firstEntry = requestData.getJSONObject( 0 );
                        List <List <Object>> dataToBeUpdated = new ArrayList <List <Object>>();
                        if ( firstEntry.getString( "type" ).equals( "bounce" ) )
                            {
                                for ( int index = 0 ; index < requestData.length() ; index++ )
                                    {
                                        List <Object> newRow = converJSONObjectToList( requestData.getJSONObject( index ) );
                                        dataToBeUpdated.add( newRow );
                                    }
                                AccessGoogleSheetsHelper.appendDataToSheet( SpreadSheetId , "Original Data from SendGrid for Bounces" ,
                                        dataToBeUpdated );
                                
                            }
                    }
                catch ( Exception exception )
                    {
                        logger.warning( exception.getMessage() );
                        exception.printStackTrace();
                    }
            }

        private static String getBounceType( String reason ) throws BounceTypeUnkownException
            {
                if ( reason.contains( "Recipient Not Found" ) || reason.contains( "User unknown" ) || reason.contains( "No such user here" )
                        || reason.contains( "recipient does not exist here." )
                        || reason.contains( "The email account that you tried to reach is disabled" )
                        || reason.contains( "not found by SMTP address lookup" ) || reason.contains( "does not exist" )
                        || reason.toLowerCase().contains( "recipient rejected" ) || reason.contains( "Addressee unknown" )
                        || reason.contains( "Not our Customer" ) || reason.contains( "Recipient address rejected" )
                        || reason.contains( "Invalid recipient" ) || reason.contains( "Email address could not be found" )
                        || reason.contains( "RESOLVER.ADR.RecipNotFound" ) || reason.contains( "invalid recipient specified" )
                        || reason.contains( "recipient in non-accepted domain" ) || reason.contains( "no mailbox by that name" ) )
                    {
                        return "Recipient Not Found";
                    }
                else if ( reason.contains( "Pager ID" ) )
                    {
                        return "Pager ID Rejected";
                    }
                else
                    throw new BounceTypeUnkownException( "Bounce Type unknown for reason : " + reason );
            }

        private static String getBlockType( String reason ) throws BlockTypeUnkownException
            {
                if ( reason.contains( "appears to be unsolicited" ) || reason.contains( "UnsolicitedMessageError" ) )
                    return "Email appears to be unsolicited";
                else if ( reason.contains( "contact is no longer employed" ) || reason.contains( "account that you tried to reach is disabled" ) )
                    return "User removed";
                else if ( reason.contains( "Error dialing remote address" ) )
                    return "Error dialing remote address";
                else if ( reason.contains( "too many hops" ) || reason.contains( "Hop count exceeded" ) )
                    return "Too many hops";
                else if ( reason.contains( "Quota exceeded" ) || reason.contains( "overquota" ) || reason.contains( "QuotaExceededException" ) )
                    return "Quota exceeded";
                else if ( reason.contains( "Mail loop detected" ) )
                    return "Mail loop detected";
                else if ( reason.contains( "High probability of spam" ) )
                    return "High probability of spam";
                else if ( reason.contains( "SenderNotAuthenticatedForGroup" ) )
                    return "Sender Not Authenticated For Group";
                else if ( reason.contains( "invalid recipient specified" ) || reason.contains( "RecipientNotFound" )
                        || reason.contains( "the group you tried to contact (gogaemergency) may not exist" ) )
                    return "Invalid recipient specified";
                else
                    throw new BlockTypeUnkownException( "Bounce Type unknown for reason : " + reason );
            }
    }