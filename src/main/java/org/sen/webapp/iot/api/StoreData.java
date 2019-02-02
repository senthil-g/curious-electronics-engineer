package org.sen.webapp.iot.api;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static com.googlecode.objectify.ObjectifyService.factory;

import org.json.JSONObject;
import org.sen.webapp.iot.dao.SensorData;
import org.sen.webapp.utilities.Convert;

@SuppressWarnings( "serial" )
@WebServlet( name = "StoreData" , urlPatterns =
    { "/storeData" } )
public class StoreData extends HttpServlet
    {
        Logger logger = Logger.getLogger( StoreData.class.getName() );

        @Override
        protected void doPost( HttpServletRequest req , HttpServletResponse resp ) throws ServletException , IOException
            {
                logger.info( "Request received!" );
                JSONObject sensorData = (JSONObject) Convert.getRequestBody( req.getInputStream() , req.getContentType() );
                factory().register( SensorData.class );
                ofy().save().entities( Convert.convertToJavaObject( sensorData ) ).now();
            }
    }