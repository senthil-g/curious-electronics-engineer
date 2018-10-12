package org.sen.webapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONArray;

import com.google.appengine.repackaged.org.joda.time.DateTime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;

import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;


@SpringBootApplication
@RestController
public class HandlerServlet {

    public static void main(String[] args) {
        SpringApplication.run(HandlerServlet.class, args);
    }

    @GetMapping("/")
    public String hello() {
        return "hello world!";
    }

    @GetMapping("/time")
    public String getTime() {
        return DateTime.now().toString();
    }

    @PostMapping("/getData")
    public JSONObject getData() {
        JSONObject json = new JSONObject("{'name':'senthil'}");
        return json;
    }

    @PostMapping("/store")
    public String store(@RequestBody String logContentArray) {
        String sessionId = RequestContextHolder.getRequestAttributes().getSessionId();
        List<Entity> logEntries = new ArrayList<Entity>();
        JSONArray logList = new JSONArray(logContentArray);
        logList.forEach((entry) -> {
            Entity log = new Entity("LogEntry"); // create a new entity
            log.setProperty("session_id", sessionId);
            log.setUnindexedProperty("entry", new Text((String) entry));
            // log.setProperty("timestamp", new Date().getTime());
            logEntries.add(log);
        });
        String confirmation = "success";
        try {
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            datastore.put(logEntries); // store the entity
        } catch (DatastoreFailureException e) {
            confirmation = "error";
        }
        return confirmation;
    }
}