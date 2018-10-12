package org.sen.webapp;

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

@SuppressWarnings("serial")
@WebServlet(name = "StoreHostAddress", urlPatterns = "/StoreHostAddress")
public class StoreHostAddress extends HttpServlet {

    private static final Logger logger = Logger.getLogger(StoreHostAddress.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String data = new String();
        try{
            BufferedReader reader = new BufferedReader(request.getReader());
            String line;
            while((line = reader.readLine()) != null){
                data += line;
            }
        } catch (Exception e){
            logger.warning(e.getMessage());
            e.printStackTrace();
        }
        logger.info(data);
        try{
            JSONObject rObject = new JSONObject(data);
            Entity entry = new Entity("IpAddress"); // create a new entity
            entry.setProperty("ip_address", rObject.get("ip_address"));
            entry.setProperty("timestamp", new DateTime().getMillis());
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            Query result = new Query("IpAddress").addSort("timestamp", SortDirection.DESCENDING);
            PreparedQuery pq = datastore.prepare(result);
            for(Entity entity : pq.asIterable())
                datastore.delete(entity.getKey());
            datastore.put(entry); // store the entity
        } catch (Exception e){
            logger.warning(e.getMessage());
            e.printStackTrace();
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSONObject responseJson = new JSONObject();
        responseJson.put("isSuccess", true);
        response.getWriter().println(responseJson);
    }

    @Override
    public void init() throws ServletException {
        logger.info("Servlet " + this.getServletName() + " has started");
    }

    @Override
    public void destroy() {
        logger.info("Servlet " + this.getServletName() + " has stopped");
    }
}