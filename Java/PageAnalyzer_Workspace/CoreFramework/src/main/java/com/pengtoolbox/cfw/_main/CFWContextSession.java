package com.pengtoolbox.cfw._main;

import java.util.Collection;

import com.pengtoolbox.cfw.datahandling.CFWForm;

public class CFWContextSession {
	
	public static SessionData getSessionData(){
		return CFW.Context.Request.getSessionData();
	}
	
	public static void addForm(CFWForm form){
		CFW.Context.Request.getSessionData().addForm(form);
	}
	
	public static CFWForm getForm(String formID) {
		return CFW.Context.Request.getSessionData().getForm(formID);
	}
	
	public static Collection<CFWForm> getForms() {
		return CFW.Context.Request.getSessionData().getForms();
	}

}
