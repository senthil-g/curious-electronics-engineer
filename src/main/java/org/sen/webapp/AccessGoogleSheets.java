package org.sen.webapp;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import java.io.IOException;
import java.security.GeneralSecurityException;

@SuppressWarnings( "serial" )
@WebServlet( name = "AccessGoogleSheets" , urlPatterns = "/accessGoogleSheets" )
public class AccessGoogleSheets extends HttpServlet
    {

        private static final Logger logger = Logger.getLogger( AccessGoogleSheets.class.getName() );

        static final ResourceBundle resourceBundle = ResourceBundle.getBundle( "Resources" );

        static final String SpreadSheetId = resourceBundle.getString( "sendgrid.report.googlesheet.id" );

        @Override
        protected void doGet( HttpServletRequest request , HttpServletResponse response ) throws ServletException , IOException
            {
                try
                    {
                        StringBuilder builder = new StringBuilder();
                        AccessGoogleSheetsHelper.getSpreadSheetsService().values().get( SpreadSheetId , "Sheet1!A1:B2" ).execute().getValues()
                                .forEach( ( row ) -> {
                                    builder.append( row.get( 0 ).toString() + "," + row.get( 1 ).toString() + ";" );
                                } );
                        logger.info( AccessGoogleSheetsHelper.writeDataToSheet( SpreadSheetId , "Sheet1" ,
                                new JSONArray().put( "editor" ).put( "senthil" ) ) );
                    }
                catch ( GeneralSecurityException e )
                    {
                        logger.info( e.getMessage() );
                    }

            }

        @Override
        public void init() throws ServletException
            {
                logger.info( "Servlet " + this.getServletName() + " has started" );
            }

        @Override
        public void destroy()
            {
                logger.info( "Servlet " + this.getServletName() + " has stopped" );
            }
    }