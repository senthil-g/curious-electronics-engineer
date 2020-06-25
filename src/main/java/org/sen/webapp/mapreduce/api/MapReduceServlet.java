package org.sen.webapp.mapreduce.api;

import static java.lang.Integer.parseInt;
import com.google.appengine.tools.pipeline.PipelineService;
import com.google.appengine.tools.pipeline.PipelineServiceFactory;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sen.webapp.mapreduce.CreateEntitiesMapJob;

import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import java.io.IOException;

@SuppressWarnings("serial")
@WebServlet(name = "MapReduceServlet", urlPatterns = "/api/startMapReduce")
public class MapReduceServlet extends HttpServlet {

    private static final String DATASTORE_TYPE = "Senthil_Demo_Kind";

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void init() throws ServletException {
        logger.info("Servlet " + this.getServletName() + " has started");
    }

    private String getPipelineStatusUrl(String pipelineId) {
        return "https://ah-builtin-python-bundle-dot-testing-sb.appspot.com/_ah/mapreduce/detail?mapreduce_id=" + pipelineId;
    }

    private void redirectToPipelineStatus(HttpServletResponse resp,
                                          String pipelineId) throws IOException {
        String destinationUrl = getPipelineStatusUrl(pipelineId);
        logger.info("Redirecting to " + destinationUrl);
        resp.sendRedirect(destinationUrl);
    }

    private void writeResponse(HttpServletResponse resp) throws IOException {
        try (PrintWriter pw = new PrintWriter(resp.getOutputStream())) {
            pw.println("<html><body>"
                    + "<br><form method='post'>"
                    + "Runs three MapReduces: <br /> <ul> <li> Creates random MapReduceTest "
                    + "entities of the type:  " + DATASTORE_TYPE + ".</li> "
                    + "<li> Counts the number of each character in these entities.</li>"
                    + "<li> Deletes all entities of the type: " + DATASTORE_TYPE + ".</li> </ul> <div> <br />"
                    + "Entities to create: <input name='entities' value='10000'> <br />"
                    + "Entity payload size: <input name='payloadBytesPerEntity' value='1000'> <br />"
                    + "ShardCount: <input name='shardCount' value='10'> <br />"
                    + "GCS bucket: <input name='gcs_bucket'> (Leave empty to use the app's default bucket)"
                    + "<br /> <input type='submit' value='Create, Count, and Delete'>"
                    + "</div> </form> </body></html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        writeResponse(response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int entities = parseInt(request.getParameter("entities"));
        int bytesPerEntity = parseInt(request.getParameter("payloadBytesPerEntity"));
        int shardCount = parseInt(request.getParameter("shardCount"));
        PipelineService service = PipelineServiceFactory.newPipelineService();
        String pipelineId = service.startNewPipeline(new CreateEntitiesMapJob(DATASTORE_TYPE, shardCount, entities, bytesPerEntity));
        redirectToPipelineStatus(response, pipelineId);
    }

    @Override
    public void destroy() {
        logger.info("Servlet " + this.getServletName() + " has stopped");
    }
}