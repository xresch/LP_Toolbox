package com.pengtoolbox.pageanalyzer.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.TemplateHTMLDefault;
import com.pengtoolbox.cfw.utils.FileUtils;
import com.pengtoolbox.pageanalyzer.db.PageAnalyzerDB;
import com.pengtoolbox.pageanalyzer.yslow.YSlow;

//@MultipartConfig(maxFileSize=1024*1024*100, maxRequestSize=1024*1024*100)
public class HARUploadServlet extends HttpServlet
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = CFWLog.getLogger(HARUploadServlet.class.getName());

	/*****************************************************************
	 *
	 ******************************************************************/
	@Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
		CFWLog log = new CFWLog(logger, request).method("doGet");
		log.info(request.getRequestURL().toString());
			
		TemplateHTMLDefault html = new TemplateHTMLDefault(request, "Analyze");
		StringBuffer content = html.getContent();
		content.append(FileUtils.getFileContent(request, "./resources/html/harupload.html"));
		
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        
    }
	
	/*****************************************************************
	 *
	 ******************************************************************/
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		CFWLog log = new CFWLog(logger, request).method("doPost");
		log.info(request.getRequestURL().toString());
			
		TemplateHTMLDefault html = new TemplateHTMLDefault(request, "Analyze");
		StringBuffer content = html.getContent();
		content.append(FileUtils.getFileContent(request, "./resources/html/harupload.html"));
		
		content.append("<h1>Results</h1>");
		content.append("<p>Use the links in the menu to change the view. </p>");
		
		//--------------------------------------
		// Get Save Results Checkbox
		Part saveResults = request.getPart("saveResults");
		String saveResultsString = "off";
		
		if(saveResults != null) {
			saveResultsString =	FileUtils.readContentsFromInputStream(saveResults.getInputStream());
		}


		//--------------------------------------
		// Get HAR File
		Part harFile = request.getPart("harFile");

		if (harFile == null) {
			html.addAlert(CFW.ALERT_ERROR, "HAR File could not be loaded.");
		}else {

			log.start().method("doPost()-StreamHarFile");
				String harContents = FileUtils.readContentsFromInputStream(harFile.getInputStream());
			log.end();
						
			String results = YSlow.instance().analyzeHarString(harContents);
			
			//--------------------------------------
			// Save Results to DB
			if(saveResultsString.trim().toLowerCase().equals("on")) {
				PageAnalyzerDB.saveResults(request, results, harContents);
			}
			
			//--------------------------------------
			// Prepare Response
			content.append("<div id=\"results\"></div>");
			
			StringBuffer javascript = html.getJavascript();
			javascript.append("<script defer>");
			javascript.append("		YSLOW_RESULT = "+results+";\n");
			javascript.append("		HAR_DATA = "+harContents.replaceAll("</script>", "&lt;/script>")+";\n");
			javascript.append("		initialize();");
			javascript.append("		prepareYSlowResults(YSLOW_RESULT);");
			javascript.append("		prepareGanttData(HAR_DATA);");
			javascript.append("		RULES = CFW.array.sortArrayByValueOfObject(RULES, \"score\");");
			javascript.append("		$(\".result-view-tabs\").css(\"visibility\", \"visible\");");
			javascript.append("		draw({data: 'yslowresult', info: 'overview', view: ''})");
			javascript.append("</script>");
				
		}
	}
}