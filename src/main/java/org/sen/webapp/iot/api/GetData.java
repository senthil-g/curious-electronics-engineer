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
    { "/iot/getData" } )
public class GetData extends HttpServlet
    {
        private static Logger logger = Logger.getLogger( GetData.class.getName() );

        @Override
        protected void doGet( HttpServletRequest req , HttpServletResponse resp ) throws ServletException , IOException
            {
                factory().register( SensorData.class );
                JSONArray sensorDataList = new JSONArray();
                JSONObject responseObject = new JSONObject();
                String receivedTimestamp = req.getParameter("lastTimestamp");
                if(receivedTimestamp == null)
                    receivedTimestamp = String.valueOf(System.currentTimeMillis() - (60 * 1000));
                Long timestamp = Long.valueOf(receivedTimestamp);
                Query<SensorData> query = ofy().load().type( SensorData.class ).order("timestamp").filter("timestamp >", timestamp).limit( 10 );
                String newTimestamp = "";
                QueryResultIterator<SensorData> iterator = query.iterator();
                while (iterator.hasNext()) {
                    SensorData sensorData = iterator.next();
                    try
                        {
                            JSONObject jsonObject = Convert.convertToJSONObject( sensorData );
                            sensorDataList.put( jsonObject );
                            if(newTimestamp.equals("")){
                                responseObject.put("lastTimestamp", String.valueOf(jsonObject.getLong("timestamp")));
                             }
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