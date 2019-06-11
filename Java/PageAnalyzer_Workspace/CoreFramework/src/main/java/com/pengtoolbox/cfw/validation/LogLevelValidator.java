package com.pengtoolbox.cfw.validation;

import com.pengtoolbox.cfw.cli.ArgumentDefinition;

/**************************************************************************************
 * The LogLevelArgumentValidator will validate if the value of the ArgumentDefinition
 * is a valid log4j2 log level.
 * 
 * @author Reto Scheiwiller, 2015
 *
 **************************************************************************************/
public class LogLevelValidator extends AbstractPropertyValidator {

	
	public LogLevelValidator(IValidatable validatable) {
		super(validatable);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean validate(Object value) {
		
		if( !(value instanceof String) ) {
			this.setInvalidMessage("The type '"+value.getClass().getName()+"' is not supported for this validator.");
			return false;
		}else if( ((String)value).toUpperCase().matches("ALL|TRACE|DEBUG|INFO|WARN|ERROR|SEVERE|OFF") ) {
			return true;
		}else {
			this.setInvalidMessage("The value of the argument "+validateable.getPropertyName()+" is not a valid log4j2 log level.(value='"+value+"')");
			return false;
		}
	}
}