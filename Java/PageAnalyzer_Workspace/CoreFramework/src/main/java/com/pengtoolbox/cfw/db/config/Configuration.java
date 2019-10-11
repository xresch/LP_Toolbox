package com.pengtoolbox.cfw.db.config;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.datahandling.CFWField;
import com.pengtoolbox.cfw.datahandling.CFWField.FormFieldType;
import com.pengtoolbox.cfw.datahandling.CFWObject;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.validation.LengthValidator;

public class Configuration extends CFWObject {
	
	public static final String TABLE_NAME = "CFW_CONFIG";
	
	public static final String FILE_CACHING = "Cache Files";

	public static final String THEME = "Theme";
	
	public enum ConfigFields{
		PK_ID,
		CATEGORY,
		NAME,
		DESCRIPTION,
		TYPE,
		VALUE,
		OPTIONS
	}

	private static Logger logger = CFWLog.getLogger(Configuration.class.getName());
	
	private CFWField<Integer> id = CFWField.newInteger(FormFieldType.HIDDEN, ConfigFields.PK_ID.toString())
									.setPrimaryKeyAutoIncrement(this)
									.setValue(-999);
	
	private CFWField<String> category = CFWField.newString(FormFieldType.NONE, ConfigFields.CATEGORY.toString())
									.setColumnDefinition("VARCHAR(255)")
									.addValidator(new LengthValidator(1, 255))
									;
	private CFWField<String> name = CFWField.newString(FormFieldType.NONE, ConfigFields.NAME.toString())
									.setColumnDefinition("VARCHAR(255) UNIQUE")
									.addValidator(new LengthValidator(1, 255))
									;
	
	private CFWField<String> description = CFWField.newString(FormFieldType.TEXTAREA, ConfigFields.DESCRIPTION.toString())
											.setColumnDefinition("VARCHAR(4096)")
											.addValidator(new LengthValidator(-1, 4096));
	
	private CFWField<String> type = CFWField.newString(FormFieldType.NONE, ConfigFields.TYPE.toString())
			.setColumnDefinition("VARCHAR(32)")
			.addValidator(new LengthValidator(1, 32));
	
	private CFWField<String> value = CFWField.newString(FormFieldType.NONE, ConfigFields.VALUE.toString())
			.setColumnDefinition("VARCHAR(1024)")
			.addValidator(new LengthValidator(1, 1024))
			;
	
	private CFWField<Object[]> options = CFWField.newArray(FormFieldType.NONE, ConfigFields.OPTIONS.toString())
			.setColumnDefinition("ARRAY");
	
	public Configuration() {
		initializeFields();
	}
	
	public Configuration(String category, String name) {
		initializeFields();
		this.category.setValue(category);
		this.name.setValue(name);
	}
	
	public Configuration(ResultSet result) throws SQLException {
		initializeFields();
		this.mapResultSet(result);	
	}
	
	private void initializeFields() {
		this.setTableName(TABLE_NAME);
		this.addFields(id, name, description, type, value, options, category);
	}
	
	public void initDBSecond() {
		//-----------------------------------------
		// 
		//-----------------------------------------
		if(!CFW.DB.Config.checkConfigExists(Configuration.FILE_CACHING)) {
			CFW.DB.Config.create(
				new Configuration("Core Framework", Configuration.FILE_CACHING)
					.description("Enables the caching of files read from the disk.")
					.type(FormFieldType.BOOLEAN)
					.value("true")
			);
		}
		
		//-----------------------------------------
		// 
		//-----------------------------------------
		if(!CFW.DB.Config.checkConfigExists(Configuration.THEME)) {
			CFW.DB.Config.create(
				new Configuration("Core Framework", Configuration.THEME)
					.description("Set the application look and feel. 'Slate' is the default and recommended theme, all others are not 100% tested.")
					.type(FormFieldType.SELECT)
					.options(new String[]{"darkblue", "flatly", "lumen", "materia", "minty", "pulse", "sandstone", "simplex", "sketchy", "slate", "spacelab", "superhero", "united"})
					.value("slate")
			);
		}
				
				
		CFW.DB.Config.updateCache();
	}
	
	public int id() {
		return id.getValue();
	}
	
	public Configuration id(int id) {
		this.id.setValue(id);
		return this;
	}
	
	public String category() {
		return category.getValue();
	}
	
	public Configuration category(String category) {
		this.category.setValue(category);
		return this;
	}
	
	public String name() {
		return name.getValue();
	}
	
	public Configuration name(String name) {
		this.name.setValue(name);
		return this;
	}
	
	public String description() {
		return description.getValue();
	}

	public Configuration description(String description) {
		this.description.setValue(description);
		return this;
	}

	public String type() {
		return type.getValue();
	}

	public Configuration type(FormFieldType type) {
		this.type.setValue(type.toString());
		return this;
	}

	public String value() {
		return value.getValue();
	}

	public Configuration value(String value) {
		this.value.setValue(value);
		return this;
	}

	public Object[] options() {
		return options.getValue();
	}

	public Configuration options(Object[] options) {
		this.options.setValue(options);
		return this;
	}
	
	



	
	
}