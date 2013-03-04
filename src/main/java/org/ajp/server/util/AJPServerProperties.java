package org.ajp.server.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AJPServerProperties {
	private static AJPServerProperties AJPServerProps = null;
	private Properties props;

	/**
	 * Load the properties file AJPServer.properties
	 * 
	 * @return
	 * @throws IOException
	 */
	private AJPServerProperties() {
		if (props == null) {
			props = new Properties();
			InputStream propFileStream = this.getClass().getClassLoader()
					.getResourceAsStream("AJPServer.properties");
			try {
				props.load(propFileStream);
				// Close the streams
				propFileStream.close();
			} catch (IOException e) {
				System.out
						.println("Error occurred while loading AJPServer.properties");
			}
		}
	}

	/**
	 * Gets the singleton instance
	 * 
	 * @return AJPServerProperties static object
	 */
	public static AJPServerProperties getInstance() {
		if (AJPServerProps == null) {
			AJPServerProps = new AJPServerProperties();
		}
		return AJPServerProps;
	}

	public String getProperty(String key) {
		return this.props.getProperty(key);
	}
}
