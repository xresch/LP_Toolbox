package com.pengtoolbox.pageanalyzer._main;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jetty.servlet.ServletHolder;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFWAppInterface;
import com.pengtoolbox.cfw._main.CFWApplicationExecutor;
import com.pengtoolbox.cfw.caching.FileDefinition.HandlingType;
import com.pengtoolbox.cfw.features.manual.FeatureManual;
import com.pengtoolbox.cfw.features.manual.ManualPage;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.bootstrap.MenuItem;
import com.pengtoolbox.pageanalyzer.db.PAPermissions;
import com.pengtoolbox.pageanalyzer.db.Result;
import com.pengtoolbox.pageanalyzer.response.PageAnalyzerFooter;
import com.pengtoolbox.pageanalyzer.servlets.AnalyzeURLServlet;
import com.pengtoolbox.pageanalyzer.servlets.CompareServlet;
import com.pengtoolbox.pageanalyzer.servlets.CustomContentServlet;
import com.pengtoolbox.pageanalyzer.servlets.DataServlet;
import com.pengtoolbox.pageanalyzer.servlets.DeleteResultServlet;
import com.pengtoolbox.pageanalyzer.servlets.GanttChartServlet;
import com.pengtoolbox.pageanalyzer.servlets.HARUploadServlet;
import com.pengtoolbox.pageanalyzer.servlets.ManageResultsServlet;
import com.pengtoolbox.pageanalyzer.servlets.RestAPIServlet;
import com.pengtoolbox.pageanalyzer.servlets.ResultListServlet;
import com.pengtoolbox.pageanalyzer.servlets.ResultViewServlet;
import com.pengtoolbox.pageanalyzer.yslow.YSlow;
import com.pengtoolbox.pageanalyzer.yslow.YSlowExecutorJavaFX;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, � 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class Main extends Application implements CFWAppInterface {
	
	public static final String RESOURCE_PACKAGE = "com.pengtoolbox.pageanalyzer.resources";
	
	public static Logger logger = CFWLog.getLogger(Main.class.getName());
	protected static CFWLog log = new CFWLog(logger);
	
    public static void main( String[] args ) throws Exception
    {
    	
    	CFW.initializeApp(new Main(), args);

    }
    
	@Override
	public void settings() {
		
	}
	
	@Override
	public void register() {
    	
		//----------------------------------
		// Register Package
		CFW.Files.addAllowedPackage(RESOURCE_PACKAGE);
				
		//----------------------------------
		// Register Objects
    	CFW.Registry.Objects.addCFWObject(Result.class);
    	
    	//----------------------------------
    	// Register Regular Menu
		CFW.Registry.Components.addRegularMenuItem(
				(MenuItem)new MenuItem("HAR Upload")
					.faicon("fas fa-upload")
					.addPermission(PAPermissions.ANALYZE_HAR)
					.href("./harupload")	
				, null);
		
		CFW.Registry.Components.addRegularMenuItem(
				(MenuItem)new MenuItem("Analyze URL")
					.faicon("fas fa-flask")
					.addPermission(PAPermissions.ANALYZE_URL)
					.href("./analyzeurl")	
				, null);
		
		CFW.Registry.Components.addRegularMenuItem(
				(MenuItem)new MenuItem("History")
					.faicon("fas fa-history")
					.addPermission(PAPermissions.VIEW_HISTORY)
					.href("./resultlist")
				, null);
			
//		CFW.Registry.Components.addRegularMenuItem(
//				(MenuItem)new MenuItem("Docu")
//					.faicon("fas fa-book")
//					.addPermission(PAPermissions.VIEW_DOCU)
//					.href("./docu")
//				, null);
		
		CFW.Registry.Components.addRegularMenuItem(
				(MenuItem)new MenuItem("Summary")
					.faicon("fas fa-calculator")
					.addPermission(PAPermissions.VIEW_DOCU)
					.addCssClass("result-view-tabs")
					.onclick("draw({data: 'yslowresult', info: 'overview', view: ''})")
				, null);
		
		CFW.Registry.Components.addRegularMenuItem(
				(MenuItem)new MenuItem("Grade")
					.faicon("fas fa-thermometer-half")
					.addPermission(PAPermissions.VIEW_DOCU)
					.addCssClass("result-view-tabs")
					.addChild(new MenuItem("Panels").faicon("fas fa-columns")		.onclick("draw({data: 'yslowresult', info: 'grade', view: 'panels'})"))
					.addChild(new MenuItem("Table").faicon("fas fa-table")			.onclick("draw({data: 'yslowresult', info: 'grade', view: 'table'})"))
					.addChild(new MenuItem("Plain Text").faicon("fas fa-file-alt")	.onclick("draw({data: 'yslowresult', info: 'grade', view: 'plaintext'})"))
					.addChild(new MenuItem("JIRA Ticket").faicon("fab fa-jira")		.onclick("draw({data: 'yslowresult', info: 'grade', view: 'jira'})"))
					.addChild(new MenuItem("CSV").faicon("fas fa-file-csv")			.onclick("draw({data: 'yslowresult', info: 'grade', view: 'csv'})"))
					.addChild(new MenuItem("JSON").faicon("fab fa-js")				.onclick("draw({data: 'yslowresult', info: 'grade', view: 'json'})"))
				, null);

		CFW.Registry.Components.addRegularMenuItem(
				(MenuItem)new MenuItem("Statistics")
					.faicon("fas fa-signal")
					.addPermission(PAPermissions.VIEW_DOCU)
					.addCssClass("result-view-tabs")
					.addChild(new MenuItem("Gantt Chart").faicon("fas fa-signal fa-rotate-90")						.onclick("openGanttChartForResult()"))
					.addChild(new MenuItem("Table: Statistics by Type").faicon("fas fa-table")						.onclick("draw({data: 'yslowresult', info: 'stats', view: 'table', stats: 'type'})"))
					.addChild(new MenuItem("Table: Statistics by Type with primed Cache").faicon("fas fa-table")	.onclick("draw({data: 'yslowresult', info: 'stats', view: 'table', stats: 'type_cached'})"))
					.addChild(new MenuItem("Table: Components").faicon("fas fa-table")								.onclick("draw({data: 'yslowresult', info: 'stats', view: 'table', stats: 'components'})"))
				, null);
				
    	//----------------------------------
    	// Register Admin Menu
		CFW.Registry.Components.addAdminMenuItem(
				(MenuItem)new MenuItem("Manage Results")
					.faicon("fas fa-poll")
					.addPermission(PAPermissions.MANAGE_RESULTS)
					.href("./manageresults")	
				, null);
		
		//----------------------------------
		// Register Footer
    	CFW.Registry.Components.setDefaultFooter(PageAnalyzerFooter.class);
    	
    	
		//----------------------------------
		// Register Manual Pages
		ManualPage pageAnalyzer = new ManualPage("Page Analyzer").faicon("fas fa-search")
				.addPermission(FeatureManual.PERMISSION_MANUAL);
		
		CFW.Registry.Manual.addManualPage(null, pageAnalyzer);
		
			pageAnalyzer.addChild(
				new ManualPage("Introduction")
					.faicon("fas fa-star")
					.addPermission(FeatureManual.PERMISSION_MANUAL)
					.content(HandlingType.JAR_RESOURCE, RESOURCE_PACKAGE, "z_manual_intro.html")
			);
			
			pageAnalyzer.addChild(
				new ManualPage("Analyze a Har File")
					.faicon("fas fa-cogs")
					.addPermission(FeatureManual.PERMISSION_MANUAL)
					.content(HandlingType.JAR_RESOURCE, RESOURCE_PACKAGE, "z_manual_analyzehar.html")
			);
			
			ManualPage views = new ManualPage("Views").faicon("fas fa-binoculars")
					.addPermission(FeatureManual.PERMISSION_MANUAL);
			pageAnalyzer.addChild(views);
			
				views.addChild(
					new ManualPage("Result View")
						.faicon("fas fa-traffic-light")
						.addPermission(FeatureManual.PERMISSION_MANUAL)
						.content(HandlingType.JAR_RESOURCE, RESOURCE_PACKAGE, "z_manual_views_result.html")
				);
				
				views.addChild(
						new ManualPage("History View")
							.faicon("fas fa-history")
							.addPermission(FeatureManual.PERMISSION_MANUAL)
							.content(HandlingType.JAR_RESOURCE, RESOURCE_PACKAGE, "z_manual_views_history.html")
					);
    			
				views.addChild(
						new ManualPage("Gantt View")
							.faicon("fas fa-signal fa-rotate-90")
							.addPermission(FeatureManual.PERMISSION_MANUAL)
							.content(HandlingType.JAR_RESOURCE, RESOURCE_PACKAGE, "z_manual_views_gantt.html")
					);
				
				views.addChild(
						new ManualPage("Comparison View")
							.faicon("fas fa-not-equal")
							.addPermission(FeatureManual.PERMISSION_MANUAL)
							.content(HandlingType.JAR_RESOURCE, RESOURCE_PACKAGE, "z_manual_views_comparison.html")
					);
				
			pageAnalyzer.addChild(
					new ManualPage("See Also")
						.faicon("fas fa-eye")
						.addPermission(FeatureManual.PERMISSION_MANUAL)
						.content(HandlingType.JAR_RESOURCE, RESOURCE_PACKAGE, "z_manual_seealso.html")
				);
	}

	@Override
	public void initializeDB() {
		//------------------------------------
		// Initialize Database
    	//PageAnalyzerDB.initialize();
    	PAPermissions.initializePermissions();
		
	}
	
	@Override
	public void stopApp() {
		Platform.exit();
	}

	@Override
	public void startApp(CFWApplicationExecutor app) {
			//------------------------------------
			// Initialize YSlow Singleton
			// prevents error on first analysis request.
			YSlow.instance();
			YSlowExecutorJavaFX.instance();
			
	    	
	    	// For Testing only
	    	//CFW.DB.createTestData();
	    	
	        //###################################################################
	        // Create API ServletContext, no login needed
	        //################################################################### 
	    	ServletHolder apiHolder = new ServletHolder(new RestAPIServlet());
	        apiHolder.getRegistration().setMultipartConfig(app.getGlobalMultipartConfig());
	        
	        app.addUnsecureServlet(apiHolder, "/analyzehar");
	        
	        //###################################################################
	        // Create authenticatedServletContext
	        //###################################################################    	
	    	
	        ServletHolder uploadHolder = new ServletHolder(new HARUploadServlet());
	        uploadHolder.getRegistration().setMultipartConfig(app.getGlobalMultipartConfig());
	        app.addAppServlet(uploadHolder, "/");
	        app.addAppServlet(uploadHolder, "/harupload");
	        app.addAppServlet(DataServlet.class, "/data");
	        
	        app.addAppServlet(AnalyzeURLServlet.class, "/analyzeurl");
	        app.addAppServlet(ResultViewServlet.class, "/resultview");
	        app.addAppServlet(CompareServlet.class, "/compare");
	        app.addAppServlet(DeleteResultServlet.class, "/delete");
	        app.addAppServlet(ResultListServlet.class, "/resultlist");
	        app.addAppServlet(GanttChartServlet.class, "/ganttchart");
	        app.addAppServlet(ManageResultsServlet.class, "/manageresults");
	        //app.addAppServlet(DocuServlet.class, "/docu");
	        app.addAppServlet(CustomContentServlet.class, "/custom");
	        	        
	        //###################################################################
	        // Startup
	        //###################################################################
	        app.setDefaultURL("/harupload", true);
	        try {
				app.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}

    //Method from JavaFX Application startup
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	/********************************************************
	 * Workaround for classloading issue
	 ********************************************************/
	public static void javafxLogWorkaround(Level level, String message, String method){
		
		log.method(method).log(level, message, null);
	}
	
	/********************************************************
	 * Workaround for classloading issue
	 ********************************************************/
	public static void javafxLogWorkaround(Level level, String message, Throwable e, String method){
		
		log.method(method).log(level, message, e);
	}

	@Override
	public void startTasks() {
		// TODO Auto-generated method stub
		
	}

}
