package org.sen.webapp;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import java.io.IOException;
import java.io.PrintWriter;
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
                // try
                //     {
                        // StringBuilder builder = new StringBuilder();
                        // AccessGoogleSheetsHelper.getSpreadSheetsService().values().get( SpreadSheetId
                        // , "Sheet1" ).execute().getValues()
                        // .forEach( ( row ) -> {
                        // builder.append( row.get( 0 ).toString() + "," + row.get( 1 ).toString() + ";"
                        // );
                        // } );
                        // logger.info( AccessGoogleSheetsHelper.appendDataToSheet( SpreadSheetId ,
                        // "Sheet1" ,
                        // new JSONArray().put( "editor" ).put( "senthil" ) ) );
                        // String result = new String("Search text not found!");
                        // try{
                        // result = AccessGoogleSheetsHelper.getRangeOfTheText(SpreadSheetId, "Bounce
                        // Types Vs Times", "A", "Pager ID Rejected");
                        // } catch(SearchTextNotFound exception) {
                        // logger.warning(exception.getMessage());
                        // }
                        // response.getWriter().println(result);
                        // try
                        //     {
                                // List<List<Object>> bounceTypesVsTimesList = AccessGoogleSheetsHelper.getSheetValuesBySheetName( SpreadSheetId , "Bounce Types Vs Times" );
                                // bounceTypesVsTimesList.get(1).set(0, "senthil");
                                // List<List<Object>> bounceTypesVsTimesList1 = AccessGoogleSheetsHelper.getSheetValuesBySheetName( SpreadSheetId , "Bounce Types Vs Times" );
                                // logger.info(bounceTypesVsTimesList1.get(1).get(0).toString());
                        //     }
                        // catch ( SheetNotFoundException exception )
                        //     {
                        //         logger.warning( exception.getMessage() );
                        //     }
                //     }
                // catch ( GeneralSecurityException e )
                //     {
                //         logger.warning( e.getMessage() );
                //     }
                PrintWriter responseWriter = response.getWriter();
                List<String> customList = new CustomList<String>();
                customList.add("Start");
                customList.add("Second");
                customList.add("Done");
                customList.set(1, "Modified");
                logger.info(customList.toString());
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