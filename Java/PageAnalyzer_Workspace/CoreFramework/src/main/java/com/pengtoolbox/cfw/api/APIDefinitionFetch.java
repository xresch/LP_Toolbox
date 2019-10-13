package com.pengtoolbox.cfw.api;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;

import com.google.gson.JsonObject;
import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.datahandling.CFWField;
import com.pengtoolbox.cfw.datahandling.CFWObject;
import com.pengtoolbox.cfw.datahandling.CFWStatement;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.JSONResponse;
import com.pengtoolbox.cfw.response.PlaintextResponse;

public class APIDefinitionFetch extends APIDefinition{
	
	public enum ReturnFormat{
		JSON, CSV, XML
	}
	
	private ReturnFormat format;
	
	public APIDefinitionFetch(Class<? extends CFWObject> clazz,
							  String apiName, 
						      String actionName, 
						      String[] inputFieldnames,
						      String[] outputFieldnames,
						      ReturnFormat format) {

		super(clazz, apiName, actionName, inputFieldnames, outputFieldnames);
		this.format = format;
		
		this.setRequestHandler(new APIRequestHandler() {
			
			@Override
			public void handleRequest(HttpServletRequest request, HttpServletResponse response, APIDefinition definition) {
				
				JSONResponse json = new JSONResponse();
				
				//----------------------------------
				// Resolve Fields
				Enumeration<String> params = request.getParameterNames();
				
				CFWObject object;
				try {
					object = clazz.newInstance();
				} catch (Exception e) {
					new CFWLog(logger)
						.method("handleRequest")
						.severe("Could not create instance for '"+clazz.getSimpleName()+"'. Check if you have a constructor without parameters.", e);
				
					json.setSuccess(false);
					return;
				}
				
				ArrayList<CFWField> affectedFields = new ArrayList<CFWField>();
				ArrayList<String> fieldnames = new ArrayList<String>();
				boolean success = true;
				
				// iterate parameters
				while(params.hasMoreElements()) {
					String current = params.nextElement();
					String currentValue = request.getParameter(current);
					
					CFWField field = object.getFieldIgnoreCase(current);

					if(field != null) {
						if(currentValue != null && !currentValue.isEmpty()) {
							field.setValueValidated(request.getParameter(current));
							affectedFields.add(field);
							fieldnames.add(field.getName());
						}
						
					}
				}
				
				//----------------------------------
				// Create Response
				if(success) {
					
					CFWStatement statement = object.select(definition.getOutputFieldnames());
					
					for(int i = 0; i < affectedFields.size(); i++) {
						CFWField<?> currentField = affectedFields.get(i);
						if(i == 0) {
							statement.where(currentField.getName(), currentField.getValue(), false);
						}else {
							statement.and(currentField.getName(), currentField.getValue(), false);
						}
					}
					 
					if(format.equals(ReturnFormat.JSON)) {
						json.getContent().append(statement.getAsJSON());
					}else if(format.equals(ReturnFormat.CSV)){		
						PlaintextResponse plaintext = new PlaintextResponse();
						
						plaintext.getContent().append(statement.getAsCSV());
					}else if(format.equals(ReturnFormat.XML)){		
						PlaintextResponse plaintext = new PlaintextResponse();
						
						plaintext.getContent().append(statement.getAsXML());
					}
					
				}else {
					response.setStatus(HttpStatus.BAD_REQUEST_400);
				}
				
				json.setSuccess(success);

			}
		});		
	}

}
