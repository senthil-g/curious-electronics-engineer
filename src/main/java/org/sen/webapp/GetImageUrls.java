package org.sen.webapp;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import java.io.IOException;

import com.google.appengine.api.appidentity.AppIdentityService;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;

import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.google.appengine.tools.cloudstorage.ListOptions;
import com.google.appengine.tools.cloudstorage.ListResult;

@SuppressWarnings("serial")
@WebServlet(name = "GetImageUrls", urlPatterns = "/getImageUrls")
public class GetImageUrls extends HttpServlet {

    private static final Logger logger = Logger.getLogger(GetHostAddress.class.getName());
    AppIdentityService appIdentity = AppIdentityServiceFactory.getAppIdentityService();
    private final GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
            .initialRetryDelayMillis(10).retryMaxAttempts(10).totalRetryPeriodMillis(15000).build());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JSONObject responseJson = new JSONObject();
        responseJson.put("isSuccess", false);
        try {
            ListOptions.Builder builder = new ListOptions.Builder();
            builder.setRecursive(true);
            builder.setPrefix("Dam");
            ListResult result = gcsService.list(appIdentity.getDefaultGcsBucketName(), builder.build());
            logger.info(appIdentity.getDefaultGcsBucketName());
            List<String> resultList = new ArrayList<>();
            while(result.hasNext())
                resultList.add(result.next().getName());
            responseJson.put("image_urls", resultList);
            responseJson.put("isSuccess", true);
        } catch (Exception e) {
            logger.warning(e.getMessage());
            e.printStackTrace();
        }
        response.setContentType("application/json");
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