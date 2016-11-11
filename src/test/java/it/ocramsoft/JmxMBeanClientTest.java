package it.ocramsoft;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.management.remote.JMXServiceURL;

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.jmx.ConnectorServer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import it.ocramsoft.JmxMBeanClient;

/**
 * Unit test for simple App.
 */
public class JmxMBeanClientTest {
	
	static JmxMBeanClient client;
	
    private static ConnectorServer connectorServer; 
    private static int remotePort = 1099; 
    private static int httpPort = 8282; 
    
	protected static String remoteHost = "localhost";
	protected static String queryObject = "java.lang:type=Memory";
	protected static String queryAttribute = "HeapMemoryUsage";
	protected static String queryCompositeKey = "used";
    
    @BeforeClass 
    public static void setUp() throws MalformedURLException, Exception 
    { 
        connectorServer = new ConnectorServer(new JMXServiceURL("rmi",null,remotePort,"/jndi/rmi://localhost:" + remotePort + "/jmxrmi"), 
                        "org.eclipse.jetty:name=rmiconnectorserver"); 
        connectorServer.doStart(); 
        
		client = new JmxMBeanClient(remoteHost,remotePort);
    } 

    
	@Test
	public void testGet() throws Exception {

		// Create Server, no web application needed
		Server server = new Server(httpPort);
		ServletContextHandler context = new ServletContextHandler();
		ServletHolder defaultServ = new ServletHolder("default", DefaultServlet.class);
		defaultServ.setInitParameter("resourceBase", System.getProperty("user.dir"));
		defaultServ.setInitParameter("dirAllowed", "true");
		context.addServlet(defaultServ, "/");
		server.setHandler(context);

		// Start Server
		server.start();

		// Test GET
		HttpURLConnection http = (HttpURLConnection) new URL("http://localhost:"+httpPort+"/").openConnection();
		http.connect();

		//Server up and running, this is a precondition
		Assert.assertEquals(http.getResponseCode(), HttpStatus.OK_200);

		client.openConnection();
		
		//An exception would be thrown if something fails while retrieving the attribute
		Long val = client.retrieveAttributeValue(queryObject, queryAttribute,true,queryCompositeKey,Long.class);
		
		System.out.println("Memory usage is:"+val);
		
		client.closeConnection();
		
		// Stop Server
		server.stop();
	}
}
