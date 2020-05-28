package com.pengtoolbox.cfw.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Strings;
import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.bootstrap.AlertMessage.MessageType;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, � 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class CFWHttp {
	
	private static Logger logger = CFWLog.getLogger(CFWHttp.class.getName());
	
	private static String proxyPAC = null;
	
	private static CFWHttp instance = new CFWHttp();
	
	public static String encode(String toEncode) {
		
		try {
			return URLEncoder.encode(toEncode, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			new CFWLog(logger)
				.method("encode")
				.severe("Exception while encoding: "+e.getMessage(), e);	
		}
		
		return toEncode;
	}
	
	/******************************************************************************************************
	 * Returns an encoded parameter string with leading '&'.
	 ******************************************************************************************************/
	public static String  encode(String paramName, String paramValue) {
		
		return "&" + encode(paramName) + "=" + encode(paramValue);
	}
	
	/******************************************************************************************************
	 * Redirects to the referer of the request.
	 * @throws IOException 
	 ******************************************************************************************************/
	public static void redirectToReferer( HttpServletRequest request, HttpServletResponse response ) throws IOException {
		response.sendRedirect(response.encodeRedirectURL(request.getHeader("referer")));
	}
	
	/******************************************************************************************************
	 * Redirects to the specified url.
	 * @throws IOException 
	 ******************************************************************************************************/
	public static void redirectToURL(HttpServletResponse response, String url ) throws IOException {
		response.sendRedirect(response.encodeRedirectURL(url));
	}
	
	/******************************************************************************************************
	 * Return a URL or null in case of exceptions.
	 * 
	 ******************************************************************************************************/
	public static HttpURLConnection createProxiedURLConnection(String url) {
		
		try {
			if(CFW.Properties.PROXY_ENABLED == false) {
				return (HttpURLConnection)new URL(url).openConnection();
			}else {
				HttpURLConnection proxiedConnection = null;
				for(CFWProxy cfwProxy : getProxies(url)) {

					if(cfwProxy.type.trim().toUpperCase().equals("DIRECT")) {
						proxiedConnection = (HttpURLConnection)new URL(url).openConnection();
					}else {
						InetSocketAddress address = new InetSocketAddress(cfwProxy.host, cfwProxy.port);
						Proxy proxy = new Proxy(Proxy.Type.HTTP, address);

						proxiedConnection = (HttpURLConnection)new URL(url).openConnection(proxy);
					}
					return proxiedConnection;
				}
				
			}
		} catch (MalformedURLException e) {
			new CFWLog(logger)
				.method("createProxiedURLConnection")
				.severe("The URL is malformed.", e);
			
		} catch (IOException e) {
			new CFWLog(logger)
				.method("createProxiedURLConnection")
				.severe("An IO error occured.", e);
		}
		
		return null;
		
	}
	
	/******************************************************************************************************
	 * Send a HTTP GET request and returns the result or null in case of error.
	 * @param url used for the request.
	 * @return String response
	 ******************************************************************************************************/
	public static CFWHttpResponse sendGETRequest(String url) {
		
		try {			
			HttpURLConnection connection = createProxiedURLConnection(url);
			if(connection != null) {
				connection.setRequestMethod("GET");
				connection.connect();
				
				return instance.new CFWHttpResponse(connection);
			}
	    
		} catch (Exception e) {
			new CFWLog(logger)
				.method("sendGETRequest")
				.severe("Exception occured.", e);
		} 
		
		return null;
	    	
	}
	
	/******************************************************************************************************
	 * Send a HTTP POST request sending JSON with a Content-Type header "application/json; charset=UTF-8".
	 * Returns the result or null in case of error.
	 * 
	 * @param url used for the request.
	 * @param body the content of the POST body
	 * @return String response
	 ******************************************************************************************************/
	public static CFWHttpResponse sendPOSTRequestJSON(String url, String body) {
		return sendPOSTRequest(url, "application/json; charset=UTF-8", body);
	}
	
	/******************************************************************************************************
	 * Send a HTTP POST request and returns the result or null in case of error.
	 * @param url used for the request.
	 * @param contentType the value for the Content-Type header, e.g. " "application/json; charset=UTF-8", or null
	 * @param body the content of the POST body
	 * @return String response
	 ******************************************************************************************************/
	public static CFWHttpResponse sendPOSTRequest(String url, String contentType, String body) {
		
		try {
			HttpURLConnection connection = createProxiedURLConnection(url);
			if(connection != null) {
				connection.setRequestMethod("POST");
				if(!Strings.isNullOrEmpty(contentType)) {
					connection.setRequestProperty("Content-Type", contentType);
				}
				connection.setDoOutput(true);
				connection.connect();
				try(OutputStream outStream = connection.getOutputStream()) {
				    byte[] input = body.getBytes("utf-8");
				    outStream.write(input, 0, input.length);           
				}
				return instance.new CFWHttpResponse(connection);
			}
	    
		} catch (Exception e) {
			new CFWLog(logger)
				.method("sendPOSTRequest")
				.severe("Exception occured.", e);
		} 
		
		return null;
	    	
	}
	
	/******************************************************************************************************
	 * 
	 ******************************************************************************************************/
	private static void loadPacFile() {
		
		if(CFW.Properties.PROXY_ENABLED && proxyPAC == null) {
		
			if(CFW.Properties.PROXY_PAC.toLowerCase().startsWith("http")) {
				//------------------------------
				// Get PAC from URL
				CFWHttpResponse response = CFW.HTTP.sendGETRequest(CFW.Properties.PROXY_PAC);
				if(response.getStatus() <= 299) {
					proxyPAC = response.getResponseBody();
					
					if(proxyPAC == null || !proxyPAC.contains("FindProxyForURL")) {
						CFW.Context.Request.addAlertMessage(MessageType.ERROR, "The Proxy .pac-File seems not be in the expected format.");
						proxyPAC = null;
					}
				}else {
					CFW.Context.Request.addAlertMessage(MessageType.ERROR, "Error occured while retrieving .pac-File from URL. (HTTP Code: "+response.getStatus()+")");
				}
			}else {
				//------------------------------
				// Load from Disk
				proxyPAC = CFW.Files.getFileContent(CFW.Context.Request.getRequest(), CFW.Properties.PROXY_PAC);
				
				if(proxyPAC == null || !proxyPAC.contains("FindProxyForURL")) {
					CFW.Context.Request.addAlertMessage(MessageType.ERROR, "The Proxy .pac-File seems not be in the expected format.");
					proxyPAC = null;
				}
			}
		}
	}

	/******************************************************************************************************
	 * Returns a String Array with URLs used as proxies. If the string is "DIRECT", it means the URL should
	 * be called without a proxy. This method will only return "DIRECT" when "cfw_proxy_enabled" is set to 
	 * false.
	 * 
	 * @param urlToCall used for the request.
	 * @return ArrayList<String> with proxy URLs
	 ******************************************************************************************************/
	public static ArrayList<CFWProxy> getProxies(String urlToCall) {
		
		//------------------------------
		// Get Proxy PAC
		//------------------------------
		loadPacFile();
		
		//------------------------------
		// Get Proxy PAC
		//------------------------------
		ArrayList<CFWProxy> proxies = new ArrayList<CFWProxy>();
		
		if(CFW.Properties.PROXY_ENABLED && proxyPAC != null) {
			 URL tempURL;
			try {
				tempURL = new URL(urlToCall);
				String hostname = tempURL.getHost();
				Object result = CFW.Scripting.executeJavascript(proxyPAC, CFWHttpPacScriptMethods.class, "FindProxyForURL", urlToCall, hostname);
				if(result != null) {
					String[] proxyArray = result.toString()
						.split(";");
					
					for(String proxyDef : proxyArray) {
						String[] splitted = proxyDef.split(" ");
						CFWProxy cfwProxy = instance.new CFWProxy();
						cfwProxy.type = splitted[0];
						if(splitted.length > 1) {
							String hostport = splitted[1];
							cfwProxy.host = hostport.substring(0, hostport.indexOf(":"));
							String port = hostport.substring(hostport.indexOf(":")+1);
							try {
								cfwProxy.port = Integer.parseInt(port);
							}catch(Throwable e) {
								new CFWLog(logger)
									.method("getProxies")
									.silent(true)
									.severe("Error parsing port to integer.", e);
							}
							proxies.add(cfwProxy);
						}
					}
					
					
				}
				
			} catch (MalformedURLException e) {
				new CFWLog(logger)
					.method("getProxies")
					.severe("Resolving URL failed as it is malformed.", e);
			}
		}
		if (proxies.size() == 0){
			CFWProxy direct = instance.new CFWProxy();
			direct.type = "DIRECT";
			proxies.add(direct);
		}
		return proxies;
	}
		
	/******************************************************************************************************
	 * Creates a map of all cookies in a request.
	 * @param request
	 * @return HashMap containing the key value pairs of the cookies in the response
	 ******************************************************************************************************/
	public static HashMap<String,String> getCookiesAsMap(HttpServletRequest request) {
		
		HashMap<String,String> cookieMap = new HashMap<String,String>();
		
		for(Cookie cookie : request.getCookies()) {
			cookieMap.put(cookie.getName(), cookie.getValue());
		}
		
		return cookieMap;
	}
	
	/******************************************************************************************************
	 * Get the cookie
	 * @param url used for the request.
	 * @return String response
	 ******************************************************************************************************/
	public static Cookie getRequestCookie(HttpServletRequest request, String cookieKey) {
		
		if(request.getCookies() != null){
			for(Cookie cookie : request.getCookies()){
				if(cookie.getName().equals(cookieKey)){
					return cookie;
				}
			}
		}
		
		return null;
		
	}
	
	/******************************************************************************************************
	 * Get the body content of the request.
	 * @param url used for the request.
	 * @return String response
	 ******************************************************************************************************/
	public static String getRequestBody(HttpServletRequest request) throws IOException {

	    String body = null;
	    StringBuilder stringBuilder = new StringBuilder();
	    BufferedReader bufferedReader = null;

	    try {
	        InputStream inputStream = request.getInputStream();
	        if (inputStream != null) {
	            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	            char[] charBuffer = new char[128];
	            int bytesRead = -1;
	            while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
	                stringBuilder.append(charBuffer, 0, bytesRead);
	            }
	        } else {
	            stringBuilder.append("");
	        }
	    } catch (IOException ex) {
	        throw ex;
	    } finally {
	        if (bufferedReader != null) {
	            try {
	                bufferedReader.close();
	            } catch (IOException ex) {
	                throw ex;
	            }
	        }
	    }

	    body = stringBuilder.toString();
	    return body;
	}
	
	/******************************************************************************************************
	 * Inner Class for HTTP Response
	 ******************************************************************************************************/
	public class CFWHttpResponse {
		
		private Logger responseLogger = CFWLog.getLogger(CFWHttpResponse.class.getName());
		
		private String body;
		private int status = 500;
		private Map<String, List<String>> headers;
		
		public CFWHttpResponse(HttpURLConnection conn) {
			
			BufferedReader in = null;
			StringBuffer buffer = new StringBuffer();
			
			try{
				
				//------------------------------
				// connect if not already done
				conn.connect();
				
				//------------------------------
				// Read Response Body
				status = conn.getResponseCode();
				headers = conn.getHeaderFields();

				//------------------------------
				// Get Response Stream
				if (conn.getResponseCode() <= 299) {
				    in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				} else {
				    in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
				}
				
				//------------------------------
				// Read Response Body
		        String inputLine;
		
		        while ((inputLine = in.readLine()) != null) {
		        	buffer.append(inputLine);
		        	buffer.append("\n");
		        }
		        
		        body = buffer.toString();
		        
		        
			}catch(Exception e) {
				new CFWLog(responseLogger)
					.method("<init>")
					.severe("Exception occured while accessing URL: "+e.getMessage(), e);
			}finally {
				if(in != null) {
					try {
						in.close();
					} catch (IOException e) {
						new CFWLog(responseLogger)
							.method("<init>")
							.severe("Exception occured while closing http stream.", e);
					}
				}
			}
		}

		public String getResponseBody() {
			return body;
		}

		public int getStatus() {
			return status;
		}

		public Map<String, List<String>> getHeaders() {
			return headers;
		}
		
	}
	
	protected class CFWProxy {
		public String type;
		public String host;
		public int port = 80;
		
	}
}