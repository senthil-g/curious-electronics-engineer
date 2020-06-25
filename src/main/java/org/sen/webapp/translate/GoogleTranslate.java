package org.sen.webapp.translate;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import org.json.JSONObject;
import org.sen.webapp.utilities.Convert;

@SuppressWarnings( "serial" )
@WebServlet( name = "GoogleTranslate" , urlPatterns =
    { "/googleTranslate" } )
public class GoogleTranslate extends HttpServlet
    {
        ResourceBundle resourceBundle = ResourceBundle.getBundle( "Resources" );

        Logger logger = Logger.getLogger(GoogleTranslate.class.getName());

        @Override
        protected void doPost( HttpServletRequest req , HttpServletResponse resp ) throws ServletException , IOException
            {

                TranslateOptions translateOptions = TranslateOptions.newBuilder().setProjectId( "thecuriouselectronicsengineer" )
                        .setApiKey( resourceBundle.getString( "google.services.translate.apikey" ) ).build();
                // Instantiates a client
                Translate translate = translateOptions.getService();

                // The text to translate
                JSONObject inputToBeTranslated = (JSONObject) Convert.getRequestBody( req.getInputStream() , req.getContentType() );

                logger.info(inputToBeTranslated.toString());

                List<String> inputList = new ArrayList<String>();
                
                for(String key : inputToBeTranslated.keySet()){
                    inputList.add(inputToBeTranslated.getString(key));
                }

                // Translates some text into Russian
                List<Translation> translatedList = translate.translate( inputList , TranslateOption.sourceLanguage( "en" ) ,
                        TranslateOption.targetLanguage( "ta" ) );

                resp.setCharacterEncoding( "UTF-8" );
                resp.setContentType( "application/json" );
                resp.setHeader( "Access-Control-Allow-Origin" , "*" );
                int index = 0;
                for(String key : inputToBeTranslated.keySet()){
                    inputToBeTranslated.put(key, translatedList.get(index++).getTranslatedText());
                }
                resp.getWriter().print(inputToBeTranslated);
            }
    }