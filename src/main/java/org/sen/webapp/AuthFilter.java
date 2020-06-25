package org.sen.webapp;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sen.webapp.utilities.StringUtils;

@WebFilter( "/*" )
public class AuthFilter implements Filter
    {

        private static final Logger logger = Logger.getLogger( AuthFilter.class.getName() );

        static final ResourceBundle resourceBundle = ResourceBundle.getBundle( "Resources" );

        private ServletContext context;

        public void init( FilterConfig fConfig ) throws ServletException
            {
                this.context = fConfig.getServletContext();
                logger.info( "RequestLoggingFilter initialized" );
            }

        public void doFilter( ServletRequest request , ServletResponse response , FilterChain chain ) throws IOException , ServletException
            {
                // logger.info("Request received");
                // HttpServletRequest req = (HttpServletRequest) request;
                // Enumeration<String> params = req.getParameterNames();
                // while (params.hasMoreElements()) {
                // String name = params.nextElement();
                // String value = request.getParameter(name);
                // this.context.log(req.getRemoteAddr() + "::Request Params::{" + name + "=" +
                // value + "}");
                // }

                // Cookie[] cookies = req.getCookies();
                // if (cookies != null) {
                // for (Cookie cookie : cookies) {
                // this.context
                // .log(req.getRemoteAddr() + "::Cookie::{" + cookie.getName() + "," +
                // cookie.getValue() + "}");
                // }
                // }

                HttpServletResponse httpResponse = (HttpServletResponse) response;
                if ( ( (HttpServletRequest) request ).getMethod().equals( "OPTIONS" ) )
                    {
                        String allowedDomains = resourceBundle.getString( "allowed.domains" );
                        if ( StringUtils.isNotNullOrEmpty( allowedDomains ) )
                            {
                                String requestDomain = ( (HttpServletRequest) request ).getHeader( "Origin" );
                                if ( allowedDomains.contains( "," ) )
                                    {
                                        Arrays.asList( allowedDomains.split( "," ) ).stream()
                                                .filter( domain -> domain.trim().equals( requestDomain ) ).findFirst().ifPresent( domain -> {
                                                    httpResponse.setHeader( "Access-Control-Allow-Origin" , requestDomain );
                                                } );
                                    }
                                else
                                    httpResponse.setHeader( "Access-Control-Allow-Origin" , allowedDomains );
                                httpResponse.setHeader( "Access-Control-Allow-Headers" , "content-type" );
                                httpResponse.setHeader( "Content-Type" , "application/json" );
                                httpResponse.setCharacterEncoding( "UTF-8" );
                            }
                    }
                // pass the request along the filter chain
                chain.doFilter( request , response );
            }

        public void destroy()
            {
                logger.info( "RequestLoggingFilter Destroyed" );
            }
    }