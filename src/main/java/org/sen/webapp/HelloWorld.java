package org.sen.webapp;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

@SuppressWarnings("serial")
@WebServlet(name = "HelloWorld", urlPatterns = "/HelloWorld")
public class HelloWorld extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest reqest, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().println("Hello World!");
    }

    @Override
    public void init() throws ServletException {
        System.out.println("Servlet " + this.getServletName() + " has started");
    }

    @Override
    public void destroy() {
        System.out.println("Servlet " + this.getServletName() + " has stopped");
    }
}