package org.sen.webapp.iot.api;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.cmd.Query;

import static com.googlecode.objectify.ObjectifyService.factory;
import static com.googlecode.objectify.ObjectifyService.ofy;

import org.json.JSONArray;
import org.json.JSONObject;
import org.sen.webapp.iot.dao.SensorData;
import org.sen.webapp.utilities.Convert;

@SuppressWarnings( "serial" )
@WebServlet( name = "GetData" , urlPatterns =
    { "/getData" } )
public class GetData extends HttpServlet
    {
        private static Logger logger = Logger.getLogger( GetData.class.getName() );

        @Override
        protected void doGet( HttpServletRequest req , HttpServletResponse resp ) throws ServletException , IOException
            {
                factory().register( SensorData.class );
                JSONArray sensorDataList = new JSONArray();
                JSONObject responseObject = new JSONObject();
                Query<SensorData> query = ofy().load().type( SensorData.class ).order("timestamp").limit( 10 );
                String cursorStr = req.getParameter("cursorString");
                if (cursorStr != null)
                    query = query.endAt(Cursor.fromWebSafeString(cursorStr));
                QueryResultIterator<SensorData> iterator = query.iterator();
                Cursor cursor = iterator.getCursor();
                responseObject.put("cursorString", cursor.toWebSafeString());
                while (iterator.hasNext()) {
                    SensorData sensorData = iterator.next();
                    try
                        {
                            sensorDataList.put( Convert.convertToJSONObject( sensorData ) );
                        }
                    catch ( Exception e )
                        {
                            logger.warning( e.getMessage() );
                        }
                }
                responseObject.put("data", sensorDataList);
                resp.setContentType("application/json");
                resp.getWriter().println( responseObject );
            }
    }