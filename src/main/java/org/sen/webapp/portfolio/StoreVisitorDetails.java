package org.sen.webapp.portfolio;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Entity;

import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query.SortDirection;

import java.util.logging.Logger;

import javax.servlet.ServletException;

import java.io.BufferedReader;
import java.io.IOException;

import org.json.JSONObject;
import org.joda.time.DateTime;

import org.sen.webapp.utilities.Convert;

@SuppressWarnings( "serial" )
@WebServlet( name = "StoreVisitorDetails" , urlPatterns = "/api/StoreVisitorDetails" )
public class StoreVisitorDetails extends HttpServlet
    {

        private static final Logger logger = Logger.getLogger( StoreVisitorDetails.class.getName() );

        @Override
        protected void doPost( HttpServletRequest request , HttpServletResponse response ) throws ServletException , IOException
            {
                try
                    {
                        JSONObject rObject = (JSONObject) Convert.getRequestBody( request.getInputStream() , request.getContentType() );
                        logger.info( "Received object : " + rObject );
                        Entity entry = new Entity( "VisitorDetails" ); // create a new entity
                        String firstName = rObject.getString( "firstName" );
                        String lastName = rObject.getString( "lastName" );
                        String email = rObject.getString( "email" );
                        String phoneNumber = rObject.getString( "phoneNumber" );
                        entry.setProperty( "firstName" , firstName );
                        entry.setProperty( "lastName" , lastName );
                        entry.setProperty( "email" , email );
                        entry.setProperty( "phoneNumber" , phoneNumber );
                        entry.setProperty( "timestamp" , new DateTime().getMillis() );
                        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
                        datastore.put( entry ); // store the entity
                    }
                catch ( Exception e )
                    {
                        logger.warning( e.getMessage() );
                        e.printStackTrace();
                    }
                response.setContentType( "application/json" );
                response.setCharacterEncoding( "UTF-8" );
                JSONObject responseJson = new JSONObject();
                responseJson.put( "isSuccess" , true );
                response.getWriter().println( responseJson );
            }

        @Override
        public void init() throws ServletException
            {
                logger.info( "Servlet " + this.getServletName() + " has started" );
            }

        @Override
        protected void doOptions( HttpServletRequest req , HttpServletResponse resp ) throws ServletException , IOException
            {
                super.doOptions( req , resp );
            }

        @Override
        public void destroy()
            {
                logger.info( "Servlet " + this.getServletName() + " has stopped" );
            }
    }