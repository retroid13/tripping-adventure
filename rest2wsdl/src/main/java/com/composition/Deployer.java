package com.composition;

import com.composition.model.Operation;
import com.composition.proxy.ProxyServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Main entry point of the rest2wsdl application
 */
public final class Deployer {
    /**
     * The port on which Tomcat is listening, default 9090
     */
    private static int port = 9090;
    /**
     * The web context name, default is rest2wsdl
     */
    public static String context = "rest2wsdl";
    public static List<Operation> OPERATIONS;

    public static void main(String[] args) throws  InterruptedException {
        port = Integer.parseInt(args[0]);
        context = args[1];
        final Properties propFile = new Properties();
        try {
            final InputStream input = new FileInputStream(args[2]);
            propFile.load(input);
            final int count = Integer.parseInt(propFile.getProperty("operations"));
            OPERATIONS = new ArrayList<Operation>();
            System.out.println("Loading operations...");
            for(int i = 1; i <= count; ++i){
                final Operation o = new Operation();
                o.setBaseUrl(propFile.getProperty("operation" + i + ".baseUrl"));
                o.setLocation(propFile.getProperty("operation" + i + ".location"));
                o.setHttpMethod(propFile.getProperty("operation" + i + ".httpMethod"));
                o.setRequestContentType(propFile.getProperty("operation" + i + ".request.contentType"));
                o.setResponseContentType(propFile.getProperty("operation" + i + ".response.contentType"));
                final List<String> params = new ArrayList<String>();
                params.add(propFile.getProperty("operation" + i + ".params"));
                o.setParams(params);
                OPERATIONS.add(o);
                System.out.println(o);
            }
            final Server server = new Server(port);
            final ServletHandler handler = new ServletHandler();
            server.setHandler(handler);
            handler.addServletWithMapping(ProxyServlet.class, "/" + context + "/*");
            server.start();
            server.join();
        } catch (IOException e) {
            System.out.println("I/O error.");
        } catch (Exception e) {
            System.out.println("Error.");
        }
    }
}
