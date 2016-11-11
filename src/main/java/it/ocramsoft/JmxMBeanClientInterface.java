package it.ocramsoft;

import java.io.IOException;

// TODO: Auto-generated Javadoc
/**
 * The Interface JmxMBeanClientInterface.
 */
public interface JmxMBeanClientInterface{


	/**
	 * Retrieve an MBean attribute value.
	 *
	 * @param queryObject the query object
	 * @param queryAttribute the query attribute
	 * @param isComposite the is composite
	 * @param queryCompositeKey the query composite key
	 * @return the object
	 * @throws Exception the exception
	 */
	public Object retrieveAttributeValue(String queryObject,String queryAttribute, boolean isComposite,String queryCompositeKey) throws Exception;
	
	/**
	 * Retrieve an MBean attribute value of a given type
	 *
	 * @param <T> the generic type
	 * @param queryObject the query object
	 * @param queryAttribute the query attribute
	 * @param isComposite the is composite
	 * @param queryCompositeKey the query composite key
	 * @param type the type
	 * @return the t
	 * @throws Exception the exception
	 */
	public <T> T retrieveAttributeValue(String queryObject,String queryAttribute, boolean isComposite,String queryCompositeKey ,Class<T> type) throws Exception;

	
	/**
	 * Open connection.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void openConnection() throws IOException;
	
	/**
	 * Close connection.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void closeConnection() throws IOException;

}
