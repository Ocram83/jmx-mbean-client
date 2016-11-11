package it.ocramsoft;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

// TODO: Auto-generated Javadoc
/**
 * The Class JmxMBeanClient.
 */
public class JmxMBeanClient extends AbstractJmxMBeanClient {

	/** The jmxc connector object. */
	JMXConnector jmxc;

	/** The remote host. */
	protected String remoteHost;

	/** The remote port. */
	protected int remotePort;

	/** The username. */
	protected String username;

	/** The password. */
	protected String password;

	/**
	 * Instantiates a new jmx M bean client.
	 *
	 * @param remoteHost
	 *            the remote host
	 * @param remotePort
	 *            the remote port
	 * @param queryObject
	 *            the query object
	 * @param queryAttribute
	 *            the query attribute
	 * @param isComposite
	 *            the is composite
	 * @param queryCompositeKey
	 *            the query composite key
	 */
	public JmxMBeanClient(String remoteHost, int remotePort) {
		super();
		this.remoteHost = remoteHost;
		this.remotePort = remotePort;
	}

	/**
	 * Instantiates a new jmx M bean client.
	 *
	 * @param remoteHost
	 *            the remote host
	 * @param remotePort
	 *            the remote port
	 * @param queryObject
	 *            the query object
	 * @param queryAttribute
	 *            the query attribute
	 * @param isComposite
	 *            the is composite
	 * @param queryCompositeKey
	 *            the query composite key
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 */
	public JmxMBeanClient(String remoteHost, int remotePort, String username, String password) {
		super();
		this.remoteHost = remoteHost;
		this.remotePort = remotePort;
		this.username = username;
		this.password = password;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.ocramsoft.AbstractJmxMBeanClient#doRetrieveAttributeValue(java.lang.
	 * Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected <T> T doRetrieveAttributeValue(String queryObject, String queryAttribute, boolean isComposite,
			String queryCompositeKey, Class<T> type) throws AttributeNotFoundException, InstanceNotFoundException,
			MalformedObjectNameException, MBeanException, ReflectionException, IOException {
		Object o = jmxc.getMBeanServerConnection().getAttribute(new ObjectName(queryObject), queryAttribute);

		if (isComposite) {
			CompositeData cd = (CompositeData) o;
			return (T) cd.get(queryCompositeKey);
		} else
			return (T) o;
	}

	@Override
	public Object doRetrieveAttributeValue(String queryObject, String queryAttribute, boolean isComposite,
			String queryCompositeKey) throws Exception {

		Object o = jmxc.getMBeanServerConnection().getAttribute(new ObjectName(queryObject), queryAttribute);

		if (isComposite) {
			CompositeData cd = (CompositeData) o;
			return cd.get(queryCompositeKey);
		} else
			return o;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.ocramsoft.AbstractJmxMBeanClient#doCloseConnection()
	 */
	@Override
	public void doCloseConnection() throws IOException {
		jmxc.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.ocramsoft.AbstractJmxMBeanClient#doOpenConnection()
	 */
	@Override
	public void doOpenConnection() throws IOException {
		JMXServiceURL url = new JMXServiceURL(
				"service:jmx:rmi:///jndi/rmi://" + remoteHost + ":" + remotePort + "/jmxrmi");

		// TODO: This part is not tested!
		if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
			String[] creds = { username, password };
			Map<String, Object> env = new HashMap<String, Object>();
			env.put(JMXConnector.CREDENTIALS, creds);

			jmxc = JMXConnectorFactory.connect(url, env);
		} else
			jmxc = JMXConnectorFactory.connect(url, null);

	}
}