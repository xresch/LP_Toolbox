package com.pengtoolbox.cfw.features.dashboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import com.google.gson.JsonObject;
import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.caching.FileDefinition;
import com.pengtoolbox.cfw.caching.FileDefinition.HandlingType;
import com.pengtoolbox.cfw.datahandling.CFWField;
import com.pengtoolbox.cfw.datahandling.CFWField.FormFieldType;
import com.pengtoolbox.cfw.datahandling.CFWObject;
import com.pengtoolbox.cfw.response.JSONResponse;

public class WidgetTable extends WidgetDefinition {

	@Override
	public String getWidgetType() {return "cfw_table";}
	
	@Override
	public CFWObject getSettings() {
		return new CFWObject()
				.addField(CFWField.newString(FormFieldType.TEXTAREA, "tableData")
						.setLabel("Table Data")
						.setDescription("Values delimited by the separator, first row will be used as header.")
						.setValue("ID     ;Firstname    ;Lastname\r\n0      ;Jane             ;Doe\r\n1      ;Testika          ;Testonia")
						)
				.addField(CFWField.newString(FormFieldType.TEXT, "separator")
						.setLabel("Separator")
						.setDescription("The separator used for the data.")
						.setValue(";")
						)
				.addField(CFWField.newBoolean(FormFieldType.BOOLEAN, "narrow")
						.setLabel("Narrow")
						.setDescription("Define if the table row height should be narrow or wide.")
						.setValue(false)
						)
				.addField(CFWField.newBoolean(FormFieldType.BOOLEAN, "filterable")
						.setLabel("Filterable")
						.setDescription("Shall a filter be added to the table or not.")
						.setValue(false)
						)
				.addField(CFWField.newBoolean(FormFieldType.BOOLEAN, "striped")
						.setLabel("striped")
						.setDescription("Define if the table should be striped.")
						.setValue(true)
						)
		;
	}

	@Override
	public void fetchData(JSONResponse response, JsonObject settings) { }

	@Override
	public ArrayList<FileDefinition> getJavascriptFiles() {
		FileDefinition js = new FileDefinition(HandlingType.JAR_RESOURCE, FeatureDashboard.RESOURCE_PACKAGE, "cfw_widget_table.js");
		ArrayList<FileDefinition> array = new ArrayList<FileDefinition>();
		array.add(js);
		return array;
	}

	@Override
	public ArrayList<FileDefinition> getCSSFiles() { return null; }

	@Override
	public HashMap<Locale, FileDefinition> getLocalizationFiles() {
		HashMap<Locale, FileDefinition> map = new HashMap<Locale, FileDefinition>();
		return map;
	}

	@Override
	public boolean hasPermission() {
		
		if(CFW.Context.Request.hasPermission(FeatureDashboard.PERMISSION_DASHBOARDING)
		|| CFW.Context.Request.hasPermission(FeatureDashboard.PERMISSION_DASHBOARD_ADMIN)) {
			return true;
		}
		
		return false;
	}



}