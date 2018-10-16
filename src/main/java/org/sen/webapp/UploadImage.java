package org.sen.webapp;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

@SuppressWarnings("serial")
@WebServlet(name = "UploadImage", urlPatterns = "/uploadImage")
public class UploadImage extends HttpServlet {

    private static final Logger logger = Logger.getLogger(GetHostAddress.class.getName());
    private final GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
            .initialRetryDelayMillis(10).retryMaxAttempts(10).totalRetryPeriodMillis(15000).build());
    GcsOutputChannel outputChannel = null;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("Received request for file upload!");
        long startTime = System.currentTimeMillis();
        ServletFileUpload upload = new ServletFileUpload();
        FileItemIterator iter;
        String fileName = "";
        String fileNaming = "";
        JSONObject responseJson = new JSONObject();
        responseJson.put("isSuccess", false);
        try {
            iter = upload.getItemIterator(request);
            while (iter.hasNext()) {
                FileItemStream item = iter.next();
                fileName = item.getName();
                String extension = "";
                Pattern regex = Pattern.compile("\\.+[a-z]+$");
                Matcher matcher = regex.matcher(fileName);
                if (matcher.find()) {
                    logger.info("File extension :: " + matcher.group(0));
                    extension = matcher.group(0);
                    fileNaming = "Image_" + request.getRemoteAddr() + "_" + System.currentTimeMillis() + extension;
                    GcsFilename filename = null;
                    GcsFileOptions.Builder builder = new GcsFileOptions.Builder();
                    builder.mimeType("image/"+extension.replaceFirst(".", ""));
                    GcsFileOptions options = builder.build();
                    logger.info("fileName ::: " + fileName);
                    filename = new GcsFilename("thecuriouselectronicsengineer.appspot.com/Dam", fileNaming);
                    outputChannel = gcsService.createOrReplace(filename, options);
                    OutputStream outputStream = Channels.newOutputStream(outputChannel);
                    InputStream is = item.openStream();
                    byte[] b = new byte[1024 * 1024];
                    int readBytes = is.read(b, 0, 1024 * 1024);
                    while (readBytes != -1) {
                        outputStream.write(b, 0, readBytes);
                        readBytes = is.read(b, 0, readBytes);
                    }
                    is.close();
                    outputStream.close();
                }
            }
            responseJson.put("isSuccess", true);
        } catch (Exception e) {
            logger.warning(e.getMessage());
            e.printStackTrace();
        } finally {
            logger.info("Uploading file complete! " + (System.currentTimeMillis() - startTime));
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