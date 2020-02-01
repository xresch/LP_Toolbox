package com.pengtoolbox.cfw._main;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;
import javax.servlet.SessionTrackingMode;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.ShutdownHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.server.session.DefaultSessionCache;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.eclipse.jetty.server.session.NullSessionDataStore;
import org.eclipse.jetty.server.session.SessionCache;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;

import com.pengtoolbox.cfw.features.api.ServletAPILogin;
import com.pengtoolbox.cfw.handlers.AuthenticationHandler;
import com.pengtoolbox.cfw.handlers.HTTPSRedirectHandler;
import com.pengtoolbox.cfw.handlers.RedirectDefaultPageHandler;
import com.pengtoolbox.cfw.handlers.RequestHandler;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.login.LoginServlet;
import com.pengtoolbox.cfw.servlets.AssemblyServlet;
import com.pengtoolbox.cfw.servlets.AutocompleteServlet;
import com.pengtoolbox.cfw.servlets.FormServlet;
import com.pengtoolbox.cfw.servlets.JARResourceServlet;
import com.pengtoolbox.cfw.servlets.LogoutServlet;
import com.pengtoolbox.cfw.servlets.userprofile.ChangePasswordServlet;
import com.pengtoolbox.cfw.utils.HandlerChainBuilder;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, � 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class CFWApplication {
	
	private Server server;
	private MultipartConfigElement globalMultipartConfig;
	
	// HashMap contains Context Path and Context
	private static LinkedHashMap<String, ContextHandler> unsecureContextArray = new LinkedHashMap<String, ContextHandler>();
	private static LinkedHashMap<String, ContextHandler> secureContextArray = new LinkedHashMap<String, ContextHandler>();
	
	private static LinkedHashMap<String, ServletContextHandler> servletContextArray = new LinkedHashMap<String, ServletContextHandler>();	
	
	ServletContextHandler servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
	
	private String defaultURL = "/";
	static DefaultSessionIdManager idmanager;
	private SessionHandler sessionHandler;	
	
	public static Logger logger = CFWLog.getLogger(CFW.class.getName());
	
	public WebAppContext applicationContext;
	
	public CFWApplication(String[] args) throws Exception {
		    	       
		sessionHandler = CFWApplication.createSessionHandler("/");
		
    	//---------------------------------------
    	// Create Server 
        server = CFWApplication.createServer();
        applicationContext = new WebAppContext();
        applicationContext.setContextPath("/");
        applicationContext.setServer(server);
        applicationContext.setSessionHandler(sessionHandler);
        applicationContext.setErrorHandler(CFWApplication.createErrorHandler());

    	//---------------------------------------
    	// Default Multipart Config
        int maxSize = 1024*1024*CFW.Properties.APPLICATION_MAX_UPLOADSIZE;
        globalMultipartConfig = new MultipartConfigElement(null, maxSize, maxSize, maxSize);
         
	}
	
//	/**************************************************************************************************
//	 * Returns a ServletContextHandler that can be accesses without a user login.
//	 * Adds several handlers like gzipHandler, SessionHandler and RequestHandler.
//	 * 
//	 * @param the relative path of the context, CFWConfig.BASE_URL will be prepended.
//	 **************************************************************************************************/
//	public ServletContextHandler getUnsecureContext(String relativePath){
//		//-------------------------------
//        // Check if exists
//        //-------------------------------
//		if(servletContextArray.containsKey(relativePath)) {
//			return servletContextArray.get(relativePath);
//		}
//		
//        //----------------------------------
//        // Build Handler Chain
//        ContextHandler unsecureContextHandler = new ContextHandler(CFWProperties.BASE_URL+""+relativePath);	
//        ServletContextHandler servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS);   
//        servletContext.setSessionHandler(createSessionHandler(relativePath));
//        
//        new HandlerChainBuilder(unsecureContextHandler)
//	        .chain(new GzipHandler())
//	    	.chain(new RequestHandler())
//	        .chain(new AuthenticationHandler())
//	        .chain(servletContext);
//        
//        unsecureContextArray.put(relativePath, unsecureContextHandler);
//        servletContextArray.put(relativePath, servletContext);
//        return servletContext;
//	}
	
	/**************************************************************************************************
	 * Adds a servlet to the secure application context that needs authentication to access.
	 * The resulting path will be CFW.Properties.BASE_URL + "/app" + path.
	 * @param the relative path of the context,
	 **************************************************************************************************/
	public ServletHolder addAppServlet(Class<? extends Servlet> clazz, String path){
		return this.servletContext.addServlet(clazz, "/app"+path);
	}
	
	/**************************************************************************************************
	 * Adds a servlet to the secure application context that needs authentication to access.
	 * The resulting path will be CFW.Properties.BASE_URL + "/app" + path.
	 * 
	 * @param the relative path of the context, CFWConfig.BASE_URL will be prepended.
	 **************************************************************************************************/
	public void addAppServlet(ServletHolder holder, String path){
		this.servletContext.addServlet(holder, "/app"+path);
	}
	
	/**************************************************************************************************
	 * Adds a servlet to the secure application context that needs authentication to access.
	 * The resulting path will be CFW.Properties.BASE_URL + path.
	 * 
	 * @param the relative path of the context, CFWConfig.BASE_URL will be prepended.
	 **************************************************************************************************/
	public ServletHolder addUnsecureServlet(Class<? extends Servlet> clazz, String path){
		return this.servletContext.addServlet(clazz, path);
	}
	
	/**************************************************************************************************
	 * Adds a servlet to the secure application context that needs authentication to access.
	 * The resulting path will be CFW.Properties.BASE_URL + path.
	 * 
	 * @param the relative path of the context, CFWConfig.BASE_URL will be prepended.
	 **************************************************************************************************/
	public void addUnsecureServlet(ServletHolder holder, String path){
		this.servletContext.addServlet(holder, path);
	}
	
	/**************************************************************************************************
	 * Adds a servlet to the secure application context that needs authentication to access.
	 * The resulting path will be CFW.Properties.BASE_URL + path.
	 * 
	 * @param the relative path of the context, CFWConfig.BASE_URL will be prepended.
	 * @return 
	 **************************************************************************************************/
	public HttpSession createNewSession(HttpServletRequest request){
		
		HttpSession session = servletContext.getSessionHandler().getSession(request.getSession().getId());
		if(session == null) {
			session = servletContext.getSessionHandler().newHttpSession(request);
		}
		
		return session;
	}
	
//	/**************************************************************************************************
//	 * Returns a ServletContextHandler that can be accesses with a prior user login.
//	 * Adds several handlers like gzipHandler, SessionHandler, AuthenticationHandler and RequestHandler.
//	 * 
//	 * @param the relative path of the context, CFWConfig.BASE_URL will be prepended.
//	 **************************************************************************************************/
//	private ServletContextHandler getSecureContext(String relativePath){
//        
//		//-------------------------------
//        // Check if exists
//        //-------------------------------
//		if(servletContextArray.containsKey(relativePath)) {
//			return servletContextArray.get(relativePath);
//		}
//		
//        //-------------------------------
//        // Create HandlerChain
//        //-------------------------------
//        ContextHandler secureContext = new ContextHandler(CFWProperties.BASE_URL+""+relativePath);
//        ServletContextHandler servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
//        servletContext.setSessionHandler(createSessionHandler(relativePath));
//        
//
//        new HandlerChainBuilder(secureContext)
//        	.chain(new GzipHandler())
//	        .chain(new RequestHandler())
//	        .chain(servletContext);
//       
//        secureContextArray.put(relativePath, secureContext);
//        servletContextArray.put(relativePath, servletContext);
//        
//        //Login, Logout and Resource Servlets
//        addCFWServlets(servletContext);
//        
//        return servletContext;
//	}
	
	public Server getServer() {
		return server;
	}

	public MultipartConfigElement getGlobalMultipartConfig() {
		return globalMultipartConfig;
	}

	public String getDefaultURL() {
		return defaultURL;
	}

	/**************************************************************************************************
	 * Set the default URL for redirects, e.g after login.
	 * @param defaultURL 
	 * @param isAppServlet prepends "/app" if true
	 **************************************************************************************************/
	public void setDefaultURL(String defaultURL, boolean isAppServlet) {
		if(isAppServlet) {
			this.defaultURL = "/app"+defaultURL;
		}else {
			this.defaultURL = defaultURL;
		}
	}

	/***********************************************************************
	 * Add the servlets provided by CFW to the given context.
	 *  LoginServlet on /login
	 *  LogoutServlet on /logout
	 ***********************************************************************/
	public void addCFWServlets() {
		
		//-----------------------------------------
		// Authentication Servlets
	    if(CFWProperties.AUTHENTICATION_ENABLED) {
	        this.addAppServlet(LoginServlet.class, "/login");
	        this.addAppServlet(LogoutServlet.class,  "/logout");
	    }
	  
		//-----------------------------------------
		// User Profile Servlets
	    this.addAppServlet(ChangePasswordServlet.class,  "/changepassword");
	    
	    //-----------------------------------------
	    // CFW Servlets
	    servletContext.addServlet(ServletAPILogin.class,  "/cfw/apilogin");
	    servletContext.addServlet(FormServlet.class,  "/cfw/formhandler");
	    servletContext.addServlet(AutocompleteServlet.class,  "/cfw/autocomplete");
		//-----------------------------------------
		// Resource Servlets
	    servletContext.addServlet(AssemblyServlet.class, "/cfw/assembly"); 
	    servletContext.addServlet(JARResourceServlet.class, "/cfw/jarresource");

	    	    
	}
	
	/**************************************************************************************************
	 * Create an error handler.
	 * @throws Exception
	 **************************************************************************************************/
	public static ErrorHandler createErrorHandler() throws Exception {
//	    ErrorPageErrorHandler errorHandler = new ErrorPageErrorHandler();
//	    errorHandler.addErrorPage(404, "/missing.html");
//	    context.setErrorHandler(errorHandler);
	    
		// Extend ErrorHandler and overwrite methods to create custom error page
	    ErrorHandler handler = new ErrorHandler();
	    return handler;
	}

	/***********************************************************************
	 * Setup and returns a SessionHandler
	 * @param string 
	 ***********************************************************************/
	public static SessionHandler createSessionHandler(String string) {
	
	    SessionHandler sessionHandler = new SessionHandler();
	    
	    
	    sessionHandler.setSessionIdManager(CFWApplication.idmanager);
	    // workaround maxInactiveInterval=-1 issue
	    // set inactive interval in RequestHandler
	    sessionHandler.setMaxInactiveInterval(CFW.Properties.SESSION_TIMEOUT);
	    sessionHandler.setHttpOnly(false);
	    sessionHandler.setUsingCookies(true);
	    sessionHandler.setSecureRequestOnly(false);
	    
	    HashSet<SessionTrackingMode> trackingModes = new HashSet<SessionTrackingMode>();
	    trackingModes.add(SessionTrackingMode.COOKIE);
	    sessionHandler.setSessionTrackingModes(trackingModes);
	    sessionHandler.getSessionCookieConfig().setPath("/");
	    
	    //prevent URL rewrite
	    sessionHandler.setSessionIdPathParameterName("none");
	    
	    // Explicitly set Session Cache and null Datastore.
	    // This is normally done by default,
	    // but is done explicitly here for demonstration.
	    // If more than one context is to be deployed, it is
	    // simpler to use SessionCacheFactory and/or
	    // SessionDataStoreFactory instances set as beans on 
	    // the server.
	    SessionCache cache = new DefaultSessionCache(sessionHandler);
	    cache.setSessionDataStore(new NullSessionDataStore());
	    sessionHandler.setSessionCache(cache);
	
	    return sessionHandler;
	}

	/***********************************************************************
	 * Setup and returns a ResourceHandler
	 ***********************************************************************/
	public static ContextHandler createResourceHandler() {
	
	    ResourceHandler resourceHandler = new ResourceHandler();
	    // Configure the ResourceHandler. Setting the resource base indicates where the files should be served out of.
	    // In this example it is the current directory but it can be configured to anything that the jvm has access to.
	    resourceHandler.setDirectoriesListed(false);
	    //resource_handler.setWelcomeFiles(new String[]{ "/"+PA.config("pa_application_name")+"/harupload" });
	    resourceHandler.setResourceBase("./resources");
	
	    // Add the ResourceHandler to the server.
	    ContextHandler resourceContextHandler = new ContextHandler();
	    resourceContextHandler.setContextPath("/resources");
	    
	    GzipHandler resourceGzipHandler = new GzipHandler();
	    
	    resourceContextHandler.setHandler(resourceGzipHandler);
	    resourceGzipHandler.setHandler(resourceHandler);
	    
	    return resourceContextHandler;
	}

	/***********************************************************************
	 * Create a Server with the defined HTTP and HTTPs settings in the 
	 * cfw.properties.
	 * @return Server instance
	 ***********************************************************************/
	private static Server createServer() {
		Server server = new Server();
		ArrayList<Connector> connectorArray = new ArrayList<Connector>();
		
		CFWApplication.idmanager = new DefaultSessionIdManager(server);
	    server.setSessionIdManager(CFWApplication.idmanager);
	    
		
		if(CFWProperties.HTTP_ENABLED) {
			HttpConfiguration httpConf = new HttpConfiguration();
			httpConf.setSecurePort(CFWProperties.HTTPS_PORT);
			httpConf.setSecureScheme("https");
			
			ServerConnector httpConnector = new ServerConnector(server, new HttpConnectionFactory(httpConf));
			httpConnector.setName("unsecured");
			httpConnector.setHost("127.0.0.1");
			httpConnector.setPort(CFWProperties.HTTP_PORT);
			connectorArray.add(httpConnector);
		}
		
		if(CFWProperties.HTTPS_ENABLED) {
			HttpConfiguration httpsConf = new HttpConfiguration();
			httpsConf.addCustomizer(new SecureRequestCustomizer());
			httpsConf.setSecurePort(CFWProperties.HTTPS_PORT);
			httpsConf.setSecureScheme("https");
			
			SslContextFactory sslContextFactory = new SslContextFactory();
			sslContextFactory.setKeyStorePath(CFWProperties.HTTPS_KEYSTORE_PATH);
			sslContextFactory.setKeyStorePassword(CFWProperties.HTTPS_KEYSTORE_PASSWORD);
			sslContextFactory.setKeyManagerPassword(CFWProperties.HTTPS_KEYMANAGER_PASSWORD);
			
			ServerConnector httpsConnector = new ServerConnector(server,
					new SslConnectionFactory(sslContextFactory, "http/1.1"),
					new HttpConnectionFactory(httpsConf));
			httpsConnector.setName("secured");
			httpsConnector.setHost("127.0.0.1");
			httpsConnector.setPort(CFWProperties.HTTPS_PORT);
			
			connectorArray.add(httpsConnector);
		}
		
		server.setConnectors(connectorArray.toArray(new Connector[] {}));
		
		return server;
	}
		
	/**************************************************************************************************
	 * @throws Exception
	 **************************************************************************************************/
	public void start() throws Exception {
		
        //###################################################################
        // Create Handler chain
        //###################################################################
        //----------------------------------
        // Build Handler Chain
        ContextHandler contextHandler = new ContextHandler("/");	 
        servletContext.setSessionHandler(createSessionHandler("/"));
        
        
        new HandlerChainBuilder(contextHandler)
	        .chain(new GzipHandler())
	    	.chain(new RequestHandler())
	        .chain(new AuthenticationHandler("/app", defaultURL))
	        .chain(servletContext);
        
        //###################################################################
        // Add CFW Servlets
        //###################################################################
        addCFWServlets();
        // Debug
        System.out.println(servletContext.dump());
        
        //###################################################################
        // Create Handler Collection
        //###################################################################
        
        //Connect all relevant Handlers
        ArrayList<Handler> handlerArray = new ArrayList<Handler>();
        handlerArray.add(new ShutdownHandler(CFW.Properties.APPLICATION_ID, true, true));
        handlerArray.add(new HTTPSRedirectHandler());
        handlerArray.add(new RedirectDefaultPageHandler(defaultURL));
        handlerArray.add(CFWApplication.createResourceHandler());
        handlerArray.add(contextHandler);
        
        HandlerCollection handlerCollection = new HandlerCollection();
        handlerCollection.setHandlers(handlerArray.toArray(new Handler[] {}));
        server.setHandler(handlerCollection);
        
        //###################################################################
        // Startup
        //###################################################################
        CFW.Context.App.setApp(this);
        server.start();
        server.join();
	}

		
	/**************************************************************************************************
	 * 
	 **************************************************************************************************/
	public static void stop() {
		
		System.out.println("Try to stop running application instance.");
		
		//----------------------------------
		// Resolve Port to use
		String protocol = "http";
		int port = CFW.Properties.HTTP_PORT;
		if(!CFW.Properties.HTTP_ENABLED && CFW.Properties.HTTPS_ENABLED) {
			protocol = "https";
			port = CFW.Properties.HTTPS_PORT;
		}
		
		//----------------------------------
		// Try Stop 
        try {
        	URL url = new URL(protocol, "localhost", port, "/shutdown?token="+CFW.Properties.APPLICATION_ID);
        	 HttpURLConnection connection = (HttpURLConnection)url.openConnection();
             connection.setRequestMethod("POST");

             if(connection.getResponseCode() == 200) {
            	 System.out.println("Shutdown successful.");
             }else {
            	 System.err.println("Jetty returned response code HTTP "+connection.getResponseCode());
             }
             
        } catch (IOException ex) {
            System.err.println("Stop Jetty failed: " + ex.getMessage());
        }
	}


}
