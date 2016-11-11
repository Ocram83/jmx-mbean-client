package it.ocramsoft;

import java.io.IOException;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractJmxMBeanClient.
 */
public abstract class AbstractJmxMBeanClient implements JmxMBeanClientInterface {

	/**
	 * The Connection status
	 */
	private enum Status {CONNECTION_OPENED,CONNECTION_CLOSED};
	
	/** The inner status of the connection */
	private Status innerStatus = Status.CONNECTION_CLOSED;
	
	/**
	 * Do the real job and retrieve attribute value.
	 *
	 * @param <T> the generic type
	 * @param type the type
	 * @return the t
	 * @throws Exception the exception
	 */
	protected abstract <T> T doRetrieveAttributeValue(String queryObject,String queryAttribute, boolean isComposite,String queryCompositeKey ,Class<T> type) throws Exception;
	
	
	/**
	 * Do retrieve attribute class.
	 *
	 * @param queryObject the query object
	 * @param queryAttribute the query attribute
	 * @param isComposite the is composite
	 * @param queryCompositeKey the query composite key
	 * @return the class
	 * @throws Exception the exception
	 */
	public abstract Object doRetrieveAttributeValue(String queryObject,String queryAttribute, boolean isComposite,String queryCompositeKey) throws Exception;

	/**
	 * Do the real job and open connection.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected abstract void doOpenConnection() throws IOException;
	
	/**
	 * Do the real job and close connection.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected abstract void doCloseConnection() throws IOException;
	
	/* (non-Javadoc)
	 * @see it.ocramsoft.JmxMBeanClientInterface#retrieveAttributeValue(java.lang.Class)
	 */
	public <T> T retrieveAttributeValue(String queryObject,String queryAttribute, boolean isComposite,String queryCompositeKey ,Class<T> type) throws Exception	{
		if( innerStatus!= Status.CONNECTION_OPENED)
			throw new RuntimeException("Connection must be opened before retrieving an MBean attribute");
		return doRetrieveAttributeValue(queryObject,queryAttribute,isComposite,queryCompositeKey,type);
	}
	
	/* (non-Javadoc)
	 * @see it.ocramsoft.JmxMBeanClientInterface#retrieveAttributeValue(java.lang.Class)
	 */
	public Object retrieveAttributeValue(String queryObject,String queryAttribute, boolean isComposite,String queryCompositeKey ) throws Exception	{
		if( innerStatus!= Status.CONNECTION_OPENED)
			throw new RuntimeException("Connection must be opened before retrieving an MBean attribute");
		return doRetrieveAttributeValue(queryObject,queryAttribute,isComposite,queryCompositeKey);
	}
	
	/* (non-Javadoc)
	 * @see it.ocramsoft.JmxMBeanClientInterface#openConnection()
	 */
	public void openConnection() throws IOException
	{
		doOpenConnection();
		innerStatus = Status.CONNECTION_OPENED;
	}
	
	/* (non-Javadoc)
	 * @see it.ocramsoft.JmxMBeanClientInterface#closeConnection()
	 */
	public void closeConnection() throws IOException
	{
		doCloseConnection();
		innerStatus = Status.CONNECTION_CLOSED;
	}
}
