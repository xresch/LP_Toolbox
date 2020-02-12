
/**************************************************************************************************************
 * CFW.js
 * ======
 * Main library for the core framwork.
 * 
 * @author Reto Scheiwiller, © 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/

/******************************************************************
 * Class to wrap a date for easier formatting.
 * 
 ******************************************************************/
class CFWDate{
	
	 constructor(dateArgument){
		 if(dateArgument != null){
			 this.date = new Date(dateArgument);
		 }else{
			 this.date = new Date();
		 }
		 
	 }
	
	 /********************************************
	  * Returns a String in the format YYYY-MM-DD
	  ********************************************/
	 getDateForInput(){
		 var datestring = this.fillDigits(this.date.getFullYear(), 4)+"-"
		 				+ this.fillDigits(this.date.getMonth()+1, 2)+"-"
		 				+ this.fillDigits(this.date.getDate(), 2);
		 
		 return datestring;
	 }
	 
	 /********************************************
	  * Returns a String in the format HH:MM:ss.SSS
	  ********************************************/
	 getTimeForInput(){
		 var datestring = this.fillDigits(this.date.getHours(), 2)+":"
		 				+ this.fillDigits(this.date.getMinutes(), 2);
		 				/* +":"
		 				+ this.fillDigits(this.date.getSeconds(), 2)+"."
		 				+ this.f illDigits(this.date.getMilliseconds(), 3);*/
		 
		 return datestring;
	 }
	 
	 /********************************************
	  * Fill the digits to the needed amount
	  ********************************************/
	 fillDigits(value, digits){
		
		var stringValue = ''+value;
		var length = stringValue.length;
		
		for(var i = stringValue.length; i < digits;i++){
			stringValue = '0'+stringValue;
		}
		
		return stringValue;
		
	}
}


/******************************************************************
 * Creates a CFWTable.
 * 
 ******************************************************************/
 class CFWTable{
	
	 constructor(){
		 this.table = $('<table class="table">');
		 
		 this.thead = $('<thead>');
		 this.table.append(this.thead);
		 
		 this.tbody = $('<tbody>');
		 this.table.append(this.tbody);
		 
		 this.tableFilter = true;
		 this.isResponsive = true;
		 this.isHover = true;
		 this.isStriped = true;
		 this.isNarrow = false;
		 this.isSticky = false;
	 }
	
	 /********************************************
	  * Toggle the table filter, default is true.
	  ********************************************/
	 filter(isFilter){
		 this.tableFilter = isFilter;
	 }
	 /********************************************
	  * Toggle the table filter, default is true.
	  ********************************************/
	 responsive(isResponsive){
		 this.isResponsive = isResponsive;
	 }
	 
	 /********************************************
	  * Adds a header using a string.
	  ********************************************/
	 addHeader(label){
		 this.thead.append('<th>'+label+'</th>');
	 }
	 
	 /********************************************
	  * Adds headers using a string array.
	  ********************************************/
	 addHeaders(stringArray){
		 
		 var htmlString = "";
		 for(var i = 0; i < stringArray.length; i++){
			 htmlString += '<th>'+stringArray[i]+'</th>';
		 }
		 this.thead.append(htmlString);
	 }
	 
	 /********************************************
	  * Adds a row using a html string or a 
	  * jquery object .
	  ********************************************/
	 addRow(htmlOrJQueryObject){
		 this.tbody.append(htmlOrJQueryObject);
	 }
	 
	 /********************************************
	  * Adds rows using a html string or a 
	  * jquery object .
	  ********************************************/
	 addRows(htmlOrJQueryObject){
		 this.tbody.append(htmlOrJQueryObject);
	 }
	 
	 /********************************************
	  * Append the table to the jquery object.
	  * @param parent JQuery object
	  ********************************************/
	 appendTo(parent){
		  
		 if(this.isStriped){		this.table.addClass('table-striped'); }
		 if(this.isHover){			this.table.addClass('table-hover'); }
		 if(this.isNarrow){			this.table.addClass('table-sm'); }
		 
		 if(this.tableFilter){
			 var filter = $('<input type="text" class="form-control" onkeyup="cfw_filterTable(this)" placeholder="Filter Table...">');
			 parent.append(filter);
			 //jqueryObject.append('<span style="font-size: xx-small;"><strong>Hint:</strong> The filter searches through the innerHTML of the table rows. Use &quot;&gt;&quot; and &quot;&lt;&quot; to search for the beginning and end of a cell content(e.g. &quot;&gt;Test&lt;&quot; )</span>');
			 filter.data("table", this.table);
		 }
		 
		 if(this.isSticky){
			 this.thead.find("th").addClass("cfw-sticky-th bg-dark text-light");
			 this.isResponsive = false;
			 this.table.css("width", "100%");
		 }
		 
		 if(this.isResponsive){
			var responsiveDiv = $('<div class="table-responsive">');
			responsiveDiv.append(this.table);
			
			parent.append(responsiveDiv);
		 }else{
			 parent.append(this.table);
		 }
	 }
}

function cfw_createTable(){
	return new CFWTable();
}

/******************************************************************
 * Creates a CFWPanel 
 * 
//	<div class="card">
//	  <div class="card-header">
//	    Featured
//	  </div>
//	  <div class="card-body">
//	    <h5 class="card-title">Special title treatment</h5>
//	    <p class="card-text">With supporting text below as a natural lead-in to additional content.</p>
//	    <a href="#" class="btn btn-primary">Go somewhere</a>
//	  </div>
//	</div>
 * 
 ******************************************************************/
CFW_GLOBAL_PANEL_COUNTER = 0;

class CFWPanel{
	
	 constructor(style){
		 
		 //----------------------------
	     // to be set by user
		 this.title = "Default Title, use something like CFWPanel.title = $('<span>your title</span>'); to change. ";
		 this.body = "Default Title, use something like CFWPanel.body = $('<span>your title</span>'); to change. ";
		 
		//----------------------------
	     // to be set by user
		 this.title = "";
		 
		 this.panel = $(document.createElement("div"));
		 this.panel.addClass("card border-"+style);
		 
		 this.counter = CFW_GLOBAL_PANEL_COUNTER++;
		
		//----------------------------
		// Create Header
		this.panelHeader = $(document.createElement("div"));
		this.panelHeader.addClass("card-header text-light bg-"+style);
		this.panelHeader.attr("id", "panelHead"+this.counter);
		this.panelHeader.attr("role", "button");
		this.panelHeader.attr("data-toggle", "collapse");		
		this.panelHeader.attr("data-target", "#collapse"+this.counter);			
	 }
		 
	 /********************************************
	  * Append the table to the jquery object.
	  * @param parent JQuery object
	  ********************************************/
	 appendTo(parent){
		  
		//----------------------------
		// Populate Header
		this.panelHeader.html("");
		this.panelHeader.append(this.title); 
			
		this.panel.append(this.panelHeader);

		//----------------------------
		// Create Collapse Container
		var collapseContainer = $(document.createElement("div"));
		collapseContainer.addClass("collapse");
		collapseContainer.attr("id", "collapse"+this.counter);
		//collapseContainer.attr("role", "tabpanel");
		collapseContainer.attr("aria-labelledby", "panelHead"+this.counter);
		
		this.panel.append(collapseContainer);
		
		//----------------------------
		// Create Body
		var panelBody = $(document.createElement("div"));
		panelBody.addClass("card-body");
		collapseContainer.append(panelBody);
		panelBody.append(this.body);
		
		
		 parent.append(this.panel);
		 
		 
		 
	 }
 }

/******************************************************************
 * Print the list of results found in the database.
 * 
 * @param parent JQuery object
 * @param data object containing the list of results.
 * 
 ******************************************************************/
class CFWToogleButton{
	
	constructor(url, params, isEnabled){
		this.url = url;
		this.params = params;
		this.isLocked = false;
		this.button = $('<button class="btn btn-sm">');
		this.button.data('instance', this);
		this.button.attr('onclick', 'cfw_toggleTheToggleButton(this)');
		this.button.html($('<i class="fa"></i>'));
		
		if(isEnabled){
			this.setEnabled();
		}else{
			this.setDisabled();
		}
	}
	
	/********************************************
	 * Change the display of the button to locked.
	 ********************************************/
	setLocked(){
		this.isLocked = true;
		this.button
		.prop('disabled', this.isLocked)
		.attr('title', "Cannot be changed")
		.find('i')
			.removeClass('fa-check')
			.removeClass('fa-ban')
			.addClass('fa-lock');
	}
	/********************************************
	 * Change the display of the button to enabled.
	 ********************************************/
	setEnabled(){
		this.isEnabled = true;
		this.button.addClass("btn-success")
			.removeClass("btn-danger")
			.attr('title', "Click to Disable")
			.find('i')
				.addClass('fa-check')
				.removeClass('fa-ban');
	}
	
	/********************************************
	 * Change the display of the button to locked.
	 ********************************************/
	setDisabled(){
		this.isEnabled = false;
		this.button.removeClass("btn-success")
			.addClass("btn-danger")
			.attr('title', "Click to Enable")
			.find('i')
				.removeClass('fa-check')
				.addClass('fa-ban');
	}

	/********************************************
	 * toggle the Button
	 ********************************************/
	toggleButton(){
		if(this.isEnabled){
			this.setDisabled();
		}else{
			this.setEnabled();
		}
	}
	
	/********************************************
	 * Send the request and toggle the button if
	 * successful.
	 ********************************************/
	onClick(){
		var button = this.button;

		CFW.http.getJSON(this.url, this.params, 
			function(data){
				if(data.success){
					var instance = $(button).data('instance');
					instance.toggleButton();
					CFW.ui.addToast("Saved!", null, "success", CFW.config.toastDelay);
				}
			}
		);
	}
	
	/********************************************
	 * Toggle the table filter, default is true.
	 ********************************************/
	appendTo(parent){
		parent.append(this.button);
	}
}

function cfw_createToggleButton(url, params, isEnabled){
	return new CFWToogleButton(url, params, isEnabled);
}

function cfw_toggleTheToggleButton(button){
	var cfwToogleButton = $(button).data('instance');
	cfwToogleButton.onClick();
}
