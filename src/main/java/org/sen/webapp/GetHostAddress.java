package org.sen.webapp;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query.SortDirection;

import org.json.JSONObject;
import org.joda.time.DateTime;

import java.util.logging.Logger;

import javax.servlet.ServletException;
import java.io.IOException;



@SuppressWarnings("serial")
@WebServlet(name = "GetHostAddress", urlPatterns = "/GetHostAddress")
public class GetHostAddress extends HttpServlet {

    private static final Logger logger = Logger.getLogger(GetHostAddress.class.getName());

    private static DatastoreService datastore;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JSONObject responseJson = new JSONObject();
        responseJson.put("isSuccess", false);
        try {
            Query result = new Query("IpAddress").addSort("timestamp", SortDirection.DESCENDING);
            PreparedQuery pq = datastore.prepare(result);
            String ipAddress = "";
            String timestamp = "";
            if(pq.asIterable().iterator().hasNext()){
                Entity entry = pq.asIterable().iterator().next();
                ipAddress = entry.getProperty("ip_address").toString();
                timestamp = entry.getProperty("timestamp").toString();
            }
            long time = new DateTime().getMillis();
            responseJson.put("isSuccess", true);
            responseJson.put("isConnected", ipAddress != "");
            responseJson.put("ip_address", ipAddress);
            responseJson.put("timestamp", timestamp);
            responseJson.put("time_in_milliseconds", time);
        } catch (Exception e){
            logger.warning(e.getMessage());
            e.printStackTrace();
        }
        response.setContentType("application/json");
        response.getWriter().println(responseJson);
    }

    @Override
    public void init() throws ServletException {
        logger.info("Servlet " + this.getServletName() + " has started");
        datastore = DatastoreServiceFactory.getDatastoreService();
    }

    @Override
    public void destroy() {
        logger.info("Servlet " + this.getServletName() + " has stopped");
    }
}