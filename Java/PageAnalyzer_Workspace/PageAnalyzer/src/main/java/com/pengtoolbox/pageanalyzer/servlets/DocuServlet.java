package com.pengtoolbox.pageanalyzer.servlets;


import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pengtoolbox.cfw._main.CFWConfig;
import com.pengtoolbox.cfw.caching.FileAssembly.HandlingType;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.TemplateHTMLDefault;
import com.pengtoolbox.cfw.utils.FileUtils;

/*************************************************************************
 * 
 * @author Reto Scheiwiller, 2018
 * 
 * Distributed under the MIT license
 *************************************************************************/
@WebServlet("/docu")
@MultipartConfig(maxFileSize=1024*1024*100, maxRequestSize=1024*1024*100)
public class DocuServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LogManager.getLogManager().getLogger(DocuServlet.class.getName());
    
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
		content.append(FileUtils.getFileContent(request, "./resources/html/docu.html"));
		
		String supportDetails = CFWConfig.config("pa_support_details", "");
		if(supportDetails != null) {
			content.append("<h1>Support Contact</h1>");
	
			content.append("<ul>");
			String[] supportDetailsArray = supportDetails.split(";");
			for(String detail : supportDetailsArray) {
				content.append("<li>"+detail+"</li>");
			}
			content.append("</ul>");
		}
		
		html.getJavascript().append("<script>CFW.table.toc(\"#tocContent\", \"#toc\");</script>");
		
       response.setContentType("text/html");
       response.setStatus(HttpServletResponse.SC_OK);
       
   }

}