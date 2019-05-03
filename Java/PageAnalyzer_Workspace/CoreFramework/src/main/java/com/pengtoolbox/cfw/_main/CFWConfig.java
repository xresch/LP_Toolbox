package com.pengtoolbox.cfw._main;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;

public class CFWConfig {

	public static final Properties configProperties = new Properties();
	
	/** Enables or disables the HTTP connector. (Property=cfw_http_enabled, Default=true) */
	public static boolean HTTP_ENABLED = true;
	
	/** The port for the HTTP connector. (Property=cfw_http_port, Default=80) */
	public static int HTTP_PORT = 80;
	
	/** Enables or disables the HTTPS connector. (Property=cfw_https_enabled, Default=true) */
	public static boolean HTTPS_ENABLED = true;
	
	/**  (Property=cfw_https_port, Default=443) */
	public static int HTTPS_PORT = 443;
	
	/**  (Property=cfw_https_keystore_path, Default=./config/keystore.jks) */
	public static String HTTPS_KEYSTORE_PATH = "./config/keystore.jks";
	
	/**  (Property=cfw_https_keystore_password, Default="") */
	public static String HTTPS_KEYSTORE_PASSWORD  = "";
	
	/**  (Property=cfw_https_keymanager_password, Default="") */
	public static String HTTPS_KEYMANAGER_PASSWORD = "";
	
	/** the base url of the application. */
	public static String BASE_URL = "/";
	
	/** Enables or disables the Authentication. (Property=cfw_caching_file_enabled, Default="true") */
	public static boolean CACHING_FILE_ENABLED = true;
	
	/** Time in seconds to cache resources. (Property=cfw_browser_resource_maxage, Default="36000") */
	public static int BROWSER_RESOURCE_MAXAGE = 36000;
	
	/** Enables or disables the Authentication. (Property=cfw_authentication_enabled, Default="false") */
	public static boolean AUTHENTICATION_ENABLED = false;
	
	/** The authentication method which should be used. (Property=authentication_method, Default="csv") */
	public static String AUTHENTICATION_METHOD = "csv";
	
	/** The path to the csv file with credentials. (Property=authentication_csv_file, Default="./config/credentials.csv") */
	public static String AUTHENTICATION_CSV_FILE = "./config/credentials.csv"; 
	
	/** The URL used for LDAP authentication. (Property=authentication_ldap_url, Default="") */
	public static String LDAP_URL = "";
	
	/** The User used for LDAP authentication. (Property=authentication_ldap_user, Default="") */
	public static String LDAP_USER = "";
	
	/** The URL used for LDAP authentication. (Property=authentication_ldap_password, Default="") */
	public static String LDAP_PASSWORD = "";
	
	/** The URL used for LDAP authentication. (Property=authentication_ldap_searchbase, Default="") */
	public static String LDAP_SEARCHBASE = "";
	
	/** The URL used for LDAP authentication. (Property=authentication_ldap_user_attribute, Default="") */
	public static String LDAP_USER_ATTRIBUTE = "";
	
	/** The directory where the database should be stored. (Property=cfw_h2_path, Default="false") */
	public static String DB_STORE_PATH = "./datastore";
	
	/** The name of the database. (Property=cfw_h2_db_name, Default="h2database") */
	public static String DB_NAME = "h2database";
	
	/** The port of the database. (Property=cfw_h2_port, Default="8081") */
	public static int DB_PORT = 8081;

	/** The username for the database. (Property=cfw_h2_username, Default="sa") */
	public static String DB_USERNAME = "sa";
	
	/** The password for the database. (Property=cfw_h2_password, Default="sa") */
	public static String DB_PASSWORD = "sa";


	
	/******************************************************************************
	 * Initialize the configuration with the given properties file.
	 * @param key
	 * @return
	 *******************************************************************************/
	public static void loadConfiguration(String configFilePath) throws IOException {
		
		CFWConfig.configProperties.load(new FileReader(new File(configFilePath)));
		printConfiguration();
		
		BASE_URL 						= "/"+CFWConfig.config("cfw_application_name", BASE_URL);
		
		HTTP_ENABLED 					= CFWConfig.configAsBoolean("cfw_http_enabled", HTTP_ENABLED);
		HTTP_PORT 						= CFWConfig.configAsInt("cfw_http_port", HTTP_PORT);

		HTTPS_ENABLED 					= CFWConfig.configAsBoolean("cfw_https_enabled", HTTPS_ENABLED);
		HTTPS_PORT 						= CFWConfig.configAsInt("cfw_https_port", HTTPS_PORT);
		
		HTTPS_KEYSTORE_PATH 			= CFWConfig.config("cfw_https_keystore_path", HTTPS_KEYSTORE_PATH);
		HTTPS_KEYSTORE_PASSWORD			= CFWConfig.config("cfw_https_keystore_password", HTTPS_KEYSTORE_PASSWORD);
		HTTPS_KEYMANAGER_PASSWORD		= CFWConfig.config("cfw_https_keymanager_password", HTTPS_KEYMANAGER_PASSWORD);
		
		CACHING_FILE_ENABLED 	  		= CFWConfig.configAsBoolean("cfw_caching_file_enabled", CACHING_FILE_ENABLED);
		BROWSER_RESOURCE_MAXAGE 		= CFWConfig.configAsInt("cfw_browser_resource_maxage", BROWSER_RESOURCE_MAXAGE);
		
		AUTHENTICATION_METHOD 			= CFWConfig.config("authentication_method", AUTHENTICATION_METHOD);
		AUTHENTICATION_ENABLED 			= CFWConfig.configAsBoolean("cfw_authentication_enabled", AUTHENTICATION_ENABLED);
		AUTHENTICATION_CSV_FILE			= CFWConfig.config("authentication_csv_file", AUTHENTICATION_CSV_FILE);
		
		LDAP_URL 						= CFWConfig.config("authentication_ldap_url", LDAP_URL);
		LDAP_USER 						= CFWConfig.config("authentication_ldap_user", LDAP_USER);
		LDAP_PASSWORD 					= CFWConfig.config("authentication_ldap_password", LDAP_PASSWORD);
		LDAP_SEARCHBASE 				= CFWConfig.config("authentication_ldap_searchbase", LDAP_SEARCHBASE);
		LDAP_USER_ATTRIBUTE 			= CFWConfig.config("authentication_ldap_user_attribute", LDAP_USER_ATTRIBUTE);

		DB_STORE_PATH				= CFWConfig.config("cfw_h2_path", DB_STORE_PATH);
		DB_NAME						= CFWConfig.config("cfw_h2_db_name", DB_NAME);
		DB_PORT						= CFWConfig.configAsInt("cfw_h2_port", DB_PORT);
		DB_USERNAME					= CFWConfig.config("cfw_h2_username", DB_USERNAME);
		DB_PASSWORD					= CFWConfig.config("cfw_h2_password", DB_PASSWORD);
	}
	
	
	/******************************************************************************
	 * Initialize the configuration with the given properties file.
	 * @param key
	 * @return
	 *******************************************************************************/
	public static void printConfiguration() {
		
		System.out.println("################################################");
		System.out.println("##            LOADED CONFIGURATION            ##");
		System.out.println("################################################");
		for (Entry<Object,Object> e : configProperties.entrySet()) {
			System.out.println(e.getKey()+"='"+e.getValue()+"'");
		}
		System.out.println("################################################");
	}

	
	/******************************************************************************
	 * Retrieve
	 * @param key
	 * @return
	 *******************************************************************************/
	public static boolean configAsBoolean(String key, boolean defaultValue){
		
		return configProperties.getOrDefault(key, defaultValue).toString().toLowerCase().equals("true") ? true : false;
	}

	public static int configAsInt(String key, int defaultValue){
		
		return (int)Integer.parseInt((String)configProperties.getOrDefault(key, ""+defaultValue));
	}

	public static String config(String key, String defaultValue){
		
		return (String)configProperties.getOrDefault(key, defaultValue);
	}


}
