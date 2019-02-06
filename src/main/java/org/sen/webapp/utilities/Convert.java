package org.sen.webapp.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONObject;
import org.sen.webapp.iot.dao.SensorData;

public class Convert
    {
        public static Object getRequestBody( InputStream inputStream , String contentType ) throws IOException
            {
                BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( inputStream ) );
                StringBuilder stringBuilder = new StringBuilder();
                String line = new String();
                while ( ( line = bufferedReader.readLine() ) != null )
                    {
                        stringBuilder.append( line );
                    }
                String resultString = stringBuilder.toString();
                if ( isNotNullOrEmpty( contentType ) )
                    {
                        if ( contentType.contains( "/" ) )
                            {
                                String type = contentType.split( "/" ) [1];
                                return convertToType( resultString , type );
                            }
                        else
                            return convertToType( resultString , contentType );
                    }
                return resultString;
            }

        private static boolean isNotNullOrEmpty( Object inpuObject )
            {
                return inpuObject != null && !inpuObject.toString().trim().isEmpty();
            }

        private static Object convertToType( String data , String type )
            {
                if ( type.toLowerCase().equals( "json" ) )
                    {
                        data = data.trim();
                        if ( data.startsWith( "{" ) )
                            return new JSONObject( data );
                        else if ( data.startsWith( "[" ) )
                            return new JSONArray( data );
                        else
                            throw new IllegalArgumentException( "JSON should start with { or [ but the input starts with " + data.charAt( 0 ) );
                    }
                else
                    return data;
            }

        public static JSONObject convertToJSONObject( Object inputObject ) throws JsonProcessingException
            {
                ObjectMapper mapper = new ObjectMapper();
                return new JSONObject( mapper.writeValueAsString( inputObject ) );
            }

        public static SensorData convertToJavaObject( JSONObject inputObject ) throws JsonParseException , IOException
            {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue( inputObject.toString() , SensorData.class );
            }
    }