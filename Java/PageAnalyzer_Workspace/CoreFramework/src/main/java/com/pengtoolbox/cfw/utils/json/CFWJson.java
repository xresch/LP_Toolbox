package com.pengtoolbox.cfw.utils.json;

import java.sql.Date;
import java.sql.Timestamp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.datahandling.CFWField;
import com.pengtoolbox.cfw.datahandling.CFWObject;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, � 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class CFWJson {
	
	public static Gson gsonInstance;
	
	static{
		//Type cfwobjectListType = new TypeToken<LinkedHashMap<CFWObject>>() {}.getType();
		
		gsonInstance = new GsonBuilder().registerTypeHierarchyAdapter(CFWObject.class, new SerializerCFWObject()).serializeNulls().create();
	}
			
	
	public static Gson exposedOnlyInstance = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
			.serializeNulls().create();

	/*************************************************************************************
	 * 
	 *************************************************************************************/
	private static final String escapes[][] = new String[][]{
	        {"\\", "\\\\"},
	        {"\"", "\\\""},
	        {"\n", "\\n"},
	        {"\r", "\\r"},
	        {"\b", "\\b"},
	        {"\f", "\\f"},
	        {"\t", "\\t"}
	};
	
	/*************************************************************************************
	 * 
	 *************************************************************************************/
	public static Gson getGsonInstance() {
		return gsonInstance;
	}
	
	/*************************************************************************************
	 * 
	 *************************************************************************************/
	public static String toJSON(Object object) {
		return gsonInstance.toJson(object);
	}
	
	/*************************************************************************************
	 * 
	 *************************************************************************************/
	public static String toJSONExposedOnly(Object object) {
		return exposedOnlyInstance.toJson(object);
	}
	
	/*************************************************************************************
	 * 
	 *************************************************************************************/
	public static String escapeString(String string) {

		if(string != null) {
	        for (String[] esc : escapes) {
	            string = string.replace(esc[0], esc[1]);
	        }
		}
        return string;
    }
	
	/*************************************************************************************
	 * 
	 *************************************************************************************/
	public static JsonArray arrayToJsonArray(Object[] array) {
		JsonArray jsonArray = new JsonArray();
		for(Object o : array) {
			if(o instanceof String) 			{	jsonArray.add((String)o); }
			else if(o instanceof Number) 		{	jsonArray.add((Number)o); }
			else if(o instanceof Boolean) 		{	jsonArray.add((Boolean)o); }
			else if(o instanceof Character) 	{	jsonArray.add((Character)o); }
			else if(o instanceof JsonElement) 	{	jsonArray.add((JsonElement)o); }
			else {	
				jsonArray.add(gsonInstance.toJsonTree(o)); 
			}
		}
		
		return jsonArray;
	}
	
	/*************************************************************************************
	 * 
	 *************************************************************************************/
	public static JsonElement objectToJsonElement(Object o) {
		return gsonInstance.toJsonTree(o);
	}
	
	/*************************************************************************************
	 * 
	 *************************************************************************************/
	public static JsonElement jsonStringToJsonElement(String jsonString) {
		return new JsonParser().parse(jsonString);
	}
	
	/*************************************************************************************
	 * 
	 *************************************************************************************/
	public static void addObject(JsonObject target, String propertyName, Object object) {
		if(object instanceof String) 			{	target.addProperty(propertyName, (String)object); }
		else if(object instanceof Number) 		{	target.addProperty(propertyName, (Number)object); }
		else if(object instanceof Boolean) 		{	target.addProperty(propertyName, (Boolean)object); }
		else if(object instanceof Character) 	{	target.addProperty(propertyName, (Character)object); }
		else if(object instanceof Date) 		{	target.addProperty(propertyName, ((Date)object).getTime()); }
		else if(object instanceof Timestamp) 	{	target.addProperty(propertyName, ((Timestamp)object).getTime()); }
		else if(object instanceof JsonElement) 	{	target.add(propertyName, (JsonElement)object); }
		else if(object instanceof Object[]) 	{	target.add(propertyName, CFW.JSON.arrayToJsonArray((Object[])object)); }
		else {	
			target.add(propertyName, gsonInstance.toJsonTree(object)); 
		}
	}
	
	/*************************************************************************************
	 * 
	 *************************************************************************************/
	public static void addFieldAsProperty(JsonObject target, CFWField field) {
		
		String name = field.getName();
		Object value = field.getValue();
		
		if(name.startsWith("JSON")) {
			JsonElement asElement = CFW.JSON.jsonStringToJsonElement(value.toString());
			target.add(name, asElement);
		}else {
			CFW.JSON.addObject(target, name, value);
		}
		
	}
	
	
	
	
	

}