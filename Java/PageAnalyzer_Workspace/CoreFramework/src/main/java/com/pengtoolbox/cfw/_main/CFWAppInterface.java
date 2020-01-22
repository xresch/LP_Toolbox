package com.pengtoolbox.cfw._main;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, � 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public interface CFWAppInterface {

	/************************************************************************************
	 * Register components, features and objects.
	 ************************************************************************************/
	public void register();
	
	/************************************************************************************
	 * Initialize database with data.
	 ************************************************************************************/
	public void initializeDB();
	
	/************************************************************************************
	 * Add servlets and start the application.
	 * Make sure to call app.start();
	 ************************************************************************************/
	public void startApp(CFWApplication app);
	
	/************************************************************************************
	 * Actions that should be executed when the application is stopped.
	 ************************************************************************************/
	public void stopApp();
}
