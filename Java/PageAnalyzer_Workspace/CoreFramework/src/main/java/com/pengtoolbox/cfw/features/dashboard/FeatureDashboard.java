package com.pengtoolbox.cfw.features.dashboard;

import java.util.Locale;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFWAppFeature;
import com.pengtoolbox.cfw._main.CFWApplicationExecutor;
import com.pengtoolbox.cfw.caching.FileDefinition;
import com.pengtoolbox.cfw.caching.FileDefinition.HandlingType;
import com.pengtoolbox.cfw.features.usermgmt.Permission;
import com.pengtoolbox.cfw.response.bootstrap.MenuItem;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, � 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class FeatureDashboard extends CFWAppFeature {

	public static final String PERMISSION_DASHBOARDING = "Dashboarding";

	public static final String RESOURCE_PACKAGE = "com.pengtoolbox.cfw.features.dashboard.resources";
	
	@Override
	public void register() {
		//----------------------------------
		// Register Package
		CFW.Files.addAllowedPackage(RESOURCE_PACKAGE);
		
		//----------------------------------
		// Register Languages
		CFW.Localization.registerLocaleFile(Locale.ENGLISH, "/app/dashboard", new FileDefinition(HandlingType.JAR_RESOURCE, RESOURCE_PACKAGE, "lang_en_dashboard.properties"));
		CFW.Localization.registerLocaleFile(Locale.GERMAN, "/app/dashboard", new FileDefinition(HandlingType.JAR_RESOURCE, RESOURCE_PACKAGE, "lang_de_dashboard.properties"));
		
    	//----------------------------------
    	// Register Objects
		CFW.Registry.Objects.addCFWObject(Dashboard.class);
		CFW.Registry.Objects.addCFWObject(DashboardWidget.class);
		
    	//----------------------------------
    	// Register Regular Menu
		CFW.Registry.Components.addRegularMenuItem(
				(MenuItem)new MenuItem("Dashboards")
					.faicon("fas fa-tachometer-alt")
					.addPermission(PERMISSION_DASHBOARDING)
				, null);
		
		CFW.Registry.Components.addRegularMenuItem(
				(MenuItem)new MenuItem("Dashboard Test View")
					.faicon("fas fa-flask")
					.addPermission(PERMISSION_DASHBOARDING)
					.href("/app/dashboard/view")
				, "Dashboards");
		
		CFW.Registry.Components.addRegularMenuItem(
				(MenuItem)new MenuItem("Dashboard List")
					.faicon("fas fa-images")
					.addPermission(PERMISSION_DASHBOARDING)
					.href("/app/dashboard/list")
				, "Dashboards");
		
	}

	@Override
	public void initializeDB() {
		//-----------------------------------
		// 
		CFW.DB.Permissions.oneTimeCreate(
				new Permission(PERMISSION_DASHBOARDING, "user")
					.description("Create and view dashboards"),
					true,
					false
				);	
	}

	@Override
	public void addFeature(CFWApplicationExecutor app) {	
    	app.addAppServlet(ServletDashboardList.class,  "/dashboard/list");
    	app.addAppServlet(ServletDashboardView.class,  "/dashboard/view");
	}

	@Override
	public void startTasks() {

	}

	@Override
	public void stopFeature() {
		// TODO Auto-generated method stub
		
	}

}
