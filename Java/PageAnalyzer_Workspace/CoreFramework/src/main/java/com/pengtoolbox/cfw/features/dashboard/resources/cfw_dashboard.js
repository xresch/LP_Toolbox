
var CFW_DASHBOARD_EDIT_MODE = false;
var CFW_DASHBOARD_FULLSCREEN_MODE = false;
var CFW_DASHBOARD_REFRESH_INTERVAL_ID = null;

var CFW_DASHBOARD_WIDGET_REGISTRY = {};

//saved with guid
var CFW_DASHBOARD_WIDGET_DATA = {};
var CFW_DASHBOARD_WIDGET_GUID = 0;

var CFW_DASHBOARDVIEW_URL = "/app/dashboard/view";

var CFW_DASHBOARDVIEW_PARAMS = CFW.http.getURLParamsDecoded();

/************************************************************************************************
 * 
 ************************************************************************************************/
function cfw_dashboard_registerWidget(widgetUniqueType, widgetObject){
	
	CFW_DASHBOARD_WIDGET_REGISTRY[widgetUniqueType] = widgetObject;
	
	var category =  widgetObject.category;
	var menulabel =  widgetObject.menulabel;
	var menuicon = widgetObject.menuicon;
	
	var categorySubmenu = $('ul[data-submenuof="'+category+'"]');
	
	var menuitemHTML = 
		'<li><a class="dropdown-item" onclick="cfw_dashboard_addWidget(\''+widgetUniqueType+'\')" >'
			+'<div class="cfw-fa-box"><i class="'+menuicon+'"></i></div>'
			+'<span class="cfw-menuitem-label">'+menulabel+'</span>'
		+'</a></li>';
	
	categorySubmenu.append(menuitemHTML);
	
}

/************************************************************************************************
 * 
 ************************************************************************************************/
function cfw_dashboard_getWidgetDefinition(widgetUniqueType){
	
	return CFW_DASHBOARD_WIDGET_REGISTRY[widgetUniqueType];
}

/************************************************************************************************
 * 
 * @param faiconClasses
 * @param the name of the category, used to reference the category
 * @param the label of the category, used for localization
 ************************************************************************************************/
function cfw_dashboard_registerCategory(faiconClasses, categoryName, categoryLabel){
	
	if(categoryLabel == null){
		categoryLabel = categoryName;
	}
	
	//----------------------------
	// Check if exists
	var categorySubmenu = $('ul[data-submenuof="'+categoryName+'"]');
	if(categorySubmenu.length > 0){
		return;
	}
	
	//----------------------------
	// Add Category
	var categoryHTML = 
		'<li class="dropdown dropdown-submenu show">'
			+'<a href="#" class="dropdown-item dropdown-toggle" id="cfwMenuDropdown" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true"><div class="cfw-fa-box"><i class="'+faiconClasses+'"></i></div><span class="cfw-menuitem-label">'+categoryLabel+'</span><span class="caret"></span></a>'
			+'<ul class="dropdown-menu dropdown-submenu" aria-labelledby="cfwMenuDropdown" data-submenuof="'+categoryName+'">'
			+'</ul>'
		+'</li>'
		
	$('#addWidgetDropdown').append(categoryHTML);
}

function cfw_dashboard_createFormField(label, infotext, fieldHTML){
	
	var html = 	
	  '<div class="form-group row ml-1">'
		+'<label class="col-sm-3 col-form-label" for="3">'+label+':</label>'
		+'<div class="col-sm-9">'
			+'<span class="badge badge-info cfw-decorator" data-toggle="tooltip" data-placement="top" data-delay="500" title=""'
				+'data-original-title="'+infotext+'"><i class="fa fa-sm fa-info"></i></span>'
				+ fieldHTML
		+'</div>'
	+'</div>';
	
	return html;
}

/************************************************************************************************
 * 
 ************************************************************************************************/
function cfw_dashboard_editWidget(widgetGUID){
	var widgetInstance = $('#'+widgetGUID);
	var widgetObject = widgetInstance.data("widgetObject");
	var widgetDef = CFW.dashboard.getWidgetDefinition(widgetObject.TYPE);
	
	//##################################################
	// Create Widget Specific Form
	//##################################################
	var customForm = $(widgetDef.getEditForm(widgetObject));
	var buttons = customForm.find('input[type="button"]');
	if(buttons.length > 0){
		buttons.remove();
	}
	var customFormButton = '<input type="button" onclick="cfw_dashboard_saveCustomSettings(this, \''+widgetGUID+'\')" class="form-control btn-primary" value="'+CFWL('cfw_core_save', 'Save')+'">';
	
	customForm.append(customFormButton);
		
	//##################################################
	// Show Form for Default values
	//##################################################
	var defaultForm = 
		'<h2>Widget Default Settings</h2><form id="form-edit-'+widgetGUID+'">';
	
	//------------------------------
	// Title
	defaultForm += new CFWFormField({ 
			type: "text", 
			name: "title", 
			label: CFWL('cfw_core_title', 'Title'), 
			value: widgetObject.TITLE, 
			description: CFWL('cfw_widget_title_desc', 'The title of the widget.')
		}
	).createHTML();
	
	//------------------------------
	// Title Fontsize
	defaultForm += new CFWFormField({ 
			type: "number", 
			name: "titlefontsize", 
			label: CFWL('cfw_core_fontsize', 'Font Size') + ' ' + CFWL('cfw_core_title', 'Title'), 
			value: widgetObject.TITLE_FONTSIZE, 
			description: CFWL('cfw_widget_titlefontsize_desc', 'The font size of the title in pixel.') 
		}
	).createHTML();
	
	//------------------------------
	// Content Fontsize
	defaultForm += new CFWFormField({ 
			type: "number", 
			name: "contentfontsize", 
			label: CFWL('cfw_core_fontsize', 'Font Size') + ' ' + CFWL('cfw_core_content', 'Content'), 
			value: widgetObject.CONTENT_FONTSIZE, 
			description: CFWL('cfw_widget_contentfontsize_desc', 'The font size used for the widget content. Some widgets might ignore or override this value.') 
		}
	).createHTML();
	//------------------------------
	// Footer
	defaultForm += new CFWFormField({ 
			type: "textarea", 
			name: "footer", 
			label: CFWL('cfw_core_footer', 'Footer'), 
			value: widgetObject.FOOTER, 
			description: CFWL('cfw_widget_footer_desc', 'The contents of the footer of the widget.') 
		}
	).createHTML();
	
	//defaultForm += cfw_dashboard_createFormField("Footer", 'The footer of the widget.', '<textarea class="form-control" rows="10" name="footer" placeholder="Footer Contents">'+widgetObject.footer+'</textarea>');
	
	//------------------------------
	// Color Selectors
	var selectOptions = {
			"Default": "", 
			"Primary": "primary", 
			"Secondary": "secondary",
			"Info": "info", 
			"Success": "success", 
			"Warning": "warning", 
			"Danger": "danger", 
			"Dark": "dark", 
			"Light": "light"
	};
	
	defaultForm += new CFWFormField({ 
		type: "select", 
		name: "BGCOLOR", 
		label: CFWL('cfw_core_bgcolor', 'Background Color'), 
		value: widgetObject.BGCOLOR, 
		options: selectOptions,
		description: CFWL('cfw_core_bgcolor_desc', 'Define the color used for the background.') 
	}).createHTML();
	
	defaultForm += new CFWFormField({ 
		type: "select", 
		name: "FGCOLOR", 
		label: CFWL('cfw_core_fgcolor', 'Foreground Color'), 
		value: widgetObject.FGCOLOR, 
		options: selectOptions,
		description: CFWL('cfw_core_fgcolor_desc', 'Define the color used for the foreground, like text and borders.')
	}).createHTML();
	
	//------------------------------
	// Save Button
	defaultForm += '<input type="button" onclick="cfw_dashboard_saveDefaultSettings(\''+widgetGUID+'\')" class="form-control btn-primary" value="'+CFWL('cfw_core_save', 'Save')+'">';
	


	//##################################################
	// Create and show Modal
	//##################################################
	var compositeDiv = $('<div id="editWidgetComposite">');
	compositeDiv.append('<p>'+widgetDef.description+'</p>');
	compositeDiv.append(customForm);
	compositeDiv.append(defaultForm);

	
	CFW.ui.showModal(CFWL('cfw_core_settings', 'Settings'), compositeDiv, "CFW.cache.clearCache();");
	
	//-----------------------------------
	// Initialize Forms

	$('#editWidgetComposite [data-toggle="tooltip"]').tooltip();
	
	formID = $(customForm).attr("id");
	// workaround, force evaluation
	eval($(customForm).find("script").text());
	eval("intializeForm_"+formID+"();");
				
}

/************************************************************************************************
 * 
 ************************************************************************************************/
function cfw_dashboard_duplicateWidget(widgetGUID) {
	var widgetInstance = $('#'+widgetGUID);
	var widgetObject = widgetInstance.data("widgetObject");
	var widgetDef = CFW.dashboard.getWidgetDefinition(widgetObject.TYPE);
	
	CFW.http.postJSON(CFW_DASHBOARDVIEW_URL, {action: 'create', item: 'widget', type: widgetObject.TYPE, dashboardid: CFW_DASHBOARDVIEW_PARAMS.id }, function(data){
			var newWidgetObject = data.payload;
			if(newWidgetObject != null){
				var deepCopyWidgetObject = JSON.parse(JSON.stringify(widgetObject));
				deepCopyWidgetObject.PK_ID = newWidgetObject.PK_ID;
				delete deepCopyWidgetObject.guid;
				cfw_dashboard_createWidgetInstance(deepCopyWidgetObject, true);
			}
		}
	);
}

/************************************************************************************************
 * 
 ************************************************************************************************/
function cfw_dashboard_saveDefaultSettings(widgetGUID){
	var widget = $('#'+widgetGUID);
	var widgetObject = widget.data("widgetObject");
	var settingsForm = $('#form-edit-'+widgetGUID);
			
	widgetObject.TITLE = settingsForm.find('input[name="title"]').val();
	widgetObject.TITLE_FONTSIZE = settingsForm.find('input[name="titlefontsize"]').val();
	widgetObject.CONTENT_FONTSIZE = settingsForm.find('input[name="contentfontsize"]').val();
	widgetObject.FOOTER = settingsForm.find('textarea[name="footer"]').val();
	widgetObject.BGCOLOR = settingsForm.find('select[name="BGCOLOR"]').val();
	widgetObject.FGCOLOR = settingsForm.find('select[name="FGCOLOR"]').val();
	
	cfw_dashboard_rerenderWidget(widgetGUID);
	
}

/************************************************************************************************
 * 
 ************************************************************************************************/
function cfw_dashboard_saveCustomSettings(formButton, widgetGUID){
	var widget = $('#'+widgetGUID);
	var widgetObject = widget.data("widgetObject");

	var widgetDef = CFW.dashboard.getWidgetDefinition(widgetObject.TYPE);
	
	var success = widgetDef.onSave($(formButton).closest('form'), widgetObject);

	if(success){
		cfw_dashboard_rerenderWidget(widgetGUID);
	}
	
}
/************************************************************************************************
 * 
 ************************************************************************************************/
function cfw_dashboard_removeWidgetConfirmed(widgetGUID){
	CFW.ui.confirmExecute('Do you really want to remove this widget?', 'Remove', "cfw_dashboard_removeWidget('"+widgetGUID+"')" );
}

/************************************************************************************************
 * 
 ************************************************************************************************/
function cfw_dashboard_removeWidget(widgetGUID) {
	var widget = $('#'+widgetGUID);
	var widgetObject = widget.data('widgetObject');
	CFW.http.postJSON(CFW_DASHBOARDVIEW_URL, {action: 'delete', item: 'widget', widgetid: widgetObject.PK_ID, dashboardid: CFW_DASHBOARDVIEW_PARAMS.id }, function(data){

			if(data.success){
				cfw_dashboard_removeWidgetFromGrid(widget);
			}
		}
	);
};


/************************************************************************************************
 * 
 ************************************************************************************************/
function cfw_dashboard_removeWidgetFromGrid(widgetElement) {
	var grid = $('.grid-stack').data('gridstack');
	grid.removeWidget(widgetElement);
};


/************************************************************************************************
 * 
 ************************************************************************************************/
function cfw_dashboard_createWidgetElement(widgetObject){
	
	//---------------------------------------
	// Merge Data
	CFW_DASHBOARD_WIDGET_GUID++;
	var defaultOptions = {
			guid: 'widget-'+CFW_DASHBOARD_WIDGET_GUID,
			TITLE: "",
			TITLE_FONTSIZE: 16,
			CONTENT_FONTSIZE: 16,
			FOOTER: "",
			BGCOLOR: "",
			FGCOLOR: "",
			JSON_SETTINGS: {}
	}
	
	var merged = Object.assign({}, defaultOptions, widgetObject);
	
	//---------------------------------------
	// Resolve Classes
	var FGCOLORClass = '';
	var borderClass = '';
	if(merged.FGCOLOR != null && merged.FGCOLOR.trim().length > 0){
		FGCOLORClass = 'text-'+merged.FGCOLOR;
		borderClass = 'border-'+merged.FGCOLOR;
	}
	
	var BGCOLORClass = '';
	if(merged.BGCOLOR != null && merged.BGCOLOR.trim().length > 0){
		BGCOLORClass = 'bg-'+merged.BGCOLOR;
	}
	
	var settingsDisplayClass = '';
	if(!CFW_DASHBOARD_EDIT_MODE){
		settingsDisplayClass = 'd-none';
	}
	
	var htmlString =
		'    <div class="grid-stack-item-content card d-flex '+BGCOLORClass+' '+FGCOLORClass+'">'
		+'		<a role="button" class="cfw-dashboard-widget-settings '+settingsDisplayClass+'" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">'
		+'			<i class="fas fa-cog"></i>'
		+'		</a>'
		+'		<div class="dropdown-menu">'
		+'			<a class="dropdown-item" onclick="cfw_dashboard_editWidget(\''+merged.guid+'\')"><i class="fas fa-pen"></i>&nbsp;'+CFWL('cfw_core_edit', 'Edit')+'</a>'
		+'			<a class="dropdown-item" onclick="cfw_dashboard_duplicateWidget(\''+merged.guid+'\')"><i class="fas fa-clone"></i>&nbsp;'+CFWL('cfw_core_duplicate', 'Duplicate')+'</a>'
		+'			<div class="dropdown-divider"></div>'
		+'				<a class="dropdown-item" onclick="cfw_dashboard_removeWidgetConfirmed(\''+merged.guid+'\')"><i class="fas fa-trash"></i>&nbsp;'+CFWL('cfw_core_remove', 'Remove')+'</a>'
		+'			</div>'

	if(merged.TITLE != null && merged.TITLE != ''){
		htmlString += 
		 '     	  <div class="cfw-dashboard-widget-title border-bottom '+borderClass+'" style="font-size: '+merged.TITLE_FONTSIZE+'px;">'
		+'		  	<span>'+merged.TITLE+'</span>'
		+'		  </div>'
	}
	
	if(merged.content != null && merged.content != ''
	|| merged.FOOTER != null && merged.FOOTER != ''){
		htmlString += 
			'<div class="cfw-dashboard-widget-body d-flex flex-grow-1 align-items-start" style="font-size: '+merged.CONTENT_FONTSIZE+'px;">';
				if(merged.FOOTER != null && merged.FOOTER != ''){
					htmlString +=
					'		 <div class="cfw-dashboard-widget-footer border-top '+borderClass+'">'
					+			merged.FOOTER
					+'		  </div>'
				}
		htmlString += '</div>';
	}
	htmlString += '</div>';
	
	var widgetItem = $('<div id="'+merged.guid+'" data-id="'+merged.widgetID+'"  class="grid-stack-item">');
	widgetItem.append(htmlString);
	widgetItem.data("widgetObject", merged)
	
	if(merged.content != null && merged.content != ''){
		widgetItem.find('.cfw-dashboard-widget-body').append(merged.content);
	}

	return widgetItem;
}

/************************************************************************************************
 * 
 ************************************************************************************************/
function cfw_dashboard_addWidget(type) {

	CFW.http.postJSON(CFW_DASHBOARDVIEW_URL, {action: 'create', item: 'widget', type: type, dashboardid: CFW_DASHBOARDVIEW_PARAMS.id }, function(data){
			var widgetObject = data.payload;
			if(widgetObject != null){
				var widgetDefinition = CFW.dashboard.getWidgetDefinition(type);
				widgetObject.TYPE = type;
				widgetObject.TITLE = widgetDefinition.menulabel;
				var merged = Object.assign({}, widgetDefinition.defaultValues, widgetObject);
				
				cfw_dashboard_createWidgetInstance(merged, true);
			}
		}
	);
}

/************************************************************************************************
 * 
 ************************************************************************************************/
function cfw_dashboard_getSettingsForm(widgetObject) {
	
	var formHTML = "";
	
	var params = Object.assign({action: 'fetch', item: 'settingsform'}, widgetObject); 
	
	delete params.content;
	delete params.guid;
	delete params.JSON_SETTINGS;
	
	params.JSON_SETTINGS = JSON.stringify(widgetObject.JSON_SETTINGS);
	
	$.ajaxSetup({async: false});
		CFW.http.postJSON(CFW_DASHBOARDVIEW_URL, params, function(data){
				formHTML = data.payload.html;
			}
		);
	$.ajaxSetup({async: true});
	
	return formHTML;
}

/************************************************************************************************
 * 
 ************************************************************************************************/
function cfw_dashboard_fetchWidgetData(widgetObject, callback) {
	
	var formHTML = "";
	
	var params = Object.assign({action: 'fetch', item: 'widgetdata'}, widgetObject); 
	
	delete params.content;
	delete params.guid;
	delete params.JSON_SETTINGS;
	
	params.JSON_SETTINGS = JSON.stringify(widgetObject.JSON_SETTINGS);
	
		CFW.http.postJSON(CFW_DASHBOARDVIEW_URL, params, function(data){
			callback(data);
		});

	
	return formHTML;
}

/************************************************************************************************
 * 
 ************************************************************************************************/
function cfw_dashboard_saveWidgetState(widgetObject, forceSave) {
	if(forceSave || ( JSDATA.canEdit == true && CFW_DASHBOARD_EDIT_MODE) ){
		var params = Object.assign({action: 'update', item: 'widget'}, widgetObject); 
		
		delete params.content;
		delete params.guid;
		delete params.JSON_SETTINGS;
		
		params.JSON_SETTINGS = JSON.stringify(widgetObject.JSON_SETTINGS);
		
		CFW.http.postJSON(CFW_DASHBOARDVIEW_URL, params, function(data){});
	}
}
/************************************************************************************************
 * 
 ************************************************************************************************/
function cfw_dashboard_rerenderWidget(widgetGUID) {
	var widget = $('#'+widgetGUID);
	var widgetObject = widget.data("widgetObject");
	
	cfw_dashboard_removeWidgetFromGrid(widget);
	cfw_dashboard_createWidgetInstance(widgetObject, false);
	
}
/************************************************************************************************
 * 
 ************************************************************************************************/
function cfw_dashboard_createWidgetInstance(widgetObject, doAutoposition) {
	var widgetDefinition = CFW.dashboard.getWidgetDefinition(widgetObject.TYPE);	
	
	if(widgetDefinition != null){
		try{
		var widgetInstance = widgetDefinition.createWidgetInstance(widgetObject, 
			function(widgetObject, widgetContent){
				
				widgetObject.content = widgetContent;
				var widgetInstance = CFW.dashboard.createWidget(widgetObject);

				var grid = $('.grid-stack').data('gridstack');

			    grid.addWidget($(widgetInstance),
			    		widgetObject.X, 
			    		widgetObject.Y, 
			    		widgetObject.WIDTH, 
			    		widgetObject.HEIGHT, 
			    		doAutoposition);
			   
			    //----------------------------
			    // Disable widget
			    var widgetObject = $(widgetInstance).data('widgetObject');
			    
			    if(!CFW_DASHBOARD_EDIT_MODE){
			    	grid.movable('#'+widgetObject.guid, false);
			    	grid.resizable('#'+widgetObject.guid, false);
			    }
			    //----------------------------
			    // Update Data
			    widgetObject.WIDTH	= widgetInstance.attr("data-gs-width");
			    widgetObject.HEIGHT	= widgetInstance.attr("data-gs-height");
			    widgetObject.X		= widgetInstance.attr("data-gs-x");
			    widgetObject.Y		= widgetInstance.attr("data-gs-y");

			    cfw_dashboard_saveWidgetState(widgetObject);
			    
			}
		);
		}catch(err){
			CFW.ui.addToastDanger('An error occured while creating a widget instance: '+err.message);
		}
	}
	
}

/******************************************************************
 * 
 ******************************************************************/
CFW.dashboard = {
		registerWidget: 		cfw_dashboard_registerWidget,
		getWidgetDefinition: 	cfw_dashboard_getWidgetDefinition,
		registerCategory: 		cfw_dashboard_registerCategory,
		createWidget:   		cfw_dashboard_createWidgetElement,
		getSettingsForm:		cfw_dashboard_getSettingsForm,
		fetchWidgetData: 		cfw_dashboard_fetchWidgetData,
};

/******************************************************************
 * 
 ******************************************************************/
function cfw_dashboard_toggleFullscreenMode(){
	var grid = $('.grid-stack').data('gridstack');
	
	if(CFW_DASHBOARD_FULLSCREEN_MODE){
		CFW_DASHBOARD_FULLSCREEN_MODE = false;

		$('.hideOnFullScreen').css('display', '');
		$('.navbar').css('display', '');
		$('#cfw-dashboard-control-panel').css('padding', '');
		
		$('#fullscreenButton')
			.removeClass('fullscreened-button')
			.addClass('fullscreenButton');
		
		$('#fullscreenButtonIcon')
			.removeClass('fa-compress')
			.addClass('fa-expand');
		
	}else{
		CFW_DASHBOARD_FULLSCREEN_MODE = true;
		$('.hideOnFullScreen').css('display', 'none');
		$('.navbar').css('display', 'none');
		$('#cfw-dashboard-control-panel').css('padding', '0px');
		
		$('#fullscreenButton')
			.removeClass('fullscreenButton')
			.addClass('fullscreened-button');
		
		$('#fullscreenButtonIcon')
			.removeClass('fa-expand')
			.addClass('fa-compress');
	}
}

/******************************************************************
 * 
 ******************************************************************/
function cfw_dashboard_toggleEditMode(){
	var grid = $('.grid-stack').data('gridstack');
	if(CFW_DASHBOARD_EDIT_MODE){
		CFW_DASHBOARD_EDIT_MODE = false;
		$('.cfw-dashboard-widget-settings').addClass('d-none');
		$('#addWidget').addClass('d-none');
		$('#doneButton').addClass('d-none');
		$('#editButton').removeClass('d-none');
		grid.disable();
		
	}else{
		CFW_DASHBOARD_EDIT_MODE = true;
		$('.cfw-dashboard-widget-settings').removeClass('d-none');
		$('#addWidget').removeClass('d-none');
		$('#doneButton').removeClass('d-none');
		$('#editButton').addClass('d-none');
		grid.enable();
	}
}

/**************************************************************************************
 * Change duration between refreshes
 * @param selector selected value of #refreshSelector
 * @see storeLocalValue()
 * @see refreshTimer()
 *************************************************************************************/
function setReloadInterval(selector) {
	
	
	var refreshInterval = $(selector).val();
	//------------------------
	// Disable Old Interval
	if(refreshInterval == 'stop' && CFW_DASHBOARD_REFRESH_INTERVAL_ID != null){
		clearInterval(CFW_DASHBOARD_REFRESH_INTERVAL_ID);
		return;
	}
	
	//------------------------
	// Prevent user set lower interval
//	if(refreshInterval < 300000){
//		refreshInterval = 300000;
//	}

	//------------------------
	// Disable Old Interval
	if(CFW_DASHBOARD_REFRESH_INTERVAL_ID != null){
		clearInterval(CFW_DASHBOARD_REFRESH_INTERVAL_ID);
	}
	
	CFW_DASHBOARD_REFRESH_INTERVAL_ID = setInterval(function(){
    	if(!CFW_DASHBOARD_EDIT_MODE){
    		$('.grid-stack').html('');
	    	cfw_dashboard_draw();
	    };
    }, refreshInterval);
    	
}

/******************************************************************
 * Main method for building the view.
 * 
 ******************************************************************/
function cfw_dashboard_initialize(gridStackElementSelector){
	
	
	$('#dashboardName').text(JSDATA.dashboardName);
	
	//-----------------------------
	// Set options 
	if(JSDATA.canEdit){
		$('#editButton').removeClass('d-none');
	}
	
	
	//-----------------------------
	// Set options 
	$(gridStackElementSelector).gridstack({
		alwaysShowResizeHandle: /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent),
		resizable: {
		    handles: 'e, se, s, sw, w'
		  },
		column: 24, 
		cellHeight: 40,
		animate: true,
		float: true,
		verticalMargin: 10
	});
	
	//-----------------------------
	// Set update on dragstop 
	$(gridStackElementSelector).on('change', function(event, items) {
		  var grid = this;
		  var i = 0;
		  for(key in items){
			  var currentItem = items[key].el;

			  var widgetInstance = $(currentItem);
			  var widgetObject 	 = widgetInstance.data("widgetObject");
			  
			  widgetObject.X			= widgetInstance.attr("data-gs-x");
			  widgetObject.Y		 	= widgetInstance.attr("data-gs-y");
			  widgetObject.WIDTH	= widgetInstance.attr("data-gs-width");
			  widgetObject.HEIGHT	= widgetInstance.attr("data-gs-height");
			  
			  cfw_dashboard_saveWidgetState(widgetObject);
		  }
	});
	
}

function addTestdata(){
	
	var rendererTestdata = {
		 	idfield: 'id',
		 	bgstylefield: 'bgstyle',
		 	textstylefield: 'textstyle',
		 	titlefields: ['firstname', 'lastname'],
		 	titledelimiter: ' ',
		 	visiblefields: ['id', 'firstname', 'lastname', 'postal_code', 'status'],
		 	labels: {
		 		id: 'ID'
		 	},
		 	customizers: {
		 		status: function(record, value) { return (value == 'active') ? '<div class="badge badge-success">'+value+'</div>' : '<div class="badge badge-danger">'+value+'</div>' }
		 	},
			actions: [ 
				function (record, id){ return '<button class="btn btn-sm btn-primary" onclick="alert(\'Edit record '+id+'\')"><i class="fas fa-pen"></i></button>'},
				function (record, id){ return '<button class="btn btn-sm btn-danger" onclick="alert(\'Delete record '+id+'\')"><i class="fas fa-trash"></i></button>'},
			],
			bulkActions: {
				"Edit": function (elements, records, values){ alert('Edit records '+values.join(',')+'!'); },
				"Delete": function (elements, records, values){ $(elements).remove(); },
			},
			bulkActionsPos: "both",
			data: [
				{id: 0, firstname: "Jane", lastname: "Doe", city: "Nirwana", postal_code: 8008, status: 'active'},
				{id: 1, firstname: "Testika", lastname: "Testonia", city: "Manhattan", postal_code: 9000, status: 'active', bgstyle: 'success'},
				{id: 2, firstname: "Theus", lastname: "De Natore", city: "Termi-Nation", postal_code: 666, status: 'blocked', bgstyle: 'danger'},
				{id: 3, firstname: "Jane", lastname: "De Natore", city: "Termi-Nation", postal_code: 666, status: 'blocked', bgstyle: 'info', textstyle: 'white'},
			],
			rendererSettings: {
				table: {narrow: true, filterable: true}
			},
		};

		var rendererTestdataMinimal = {
				data: [
					{id: 0, firstname: "Jane", lastname: "Doe", city: "Nirwana", postal_code: 8008, status: 'active'},
					{id: 1, firstname: "Testika", lastname: "Testonia", city: "Manhattan", postal_code: 9000, status: 'active', bgstyle: 'success', textstyle: 'dark'},
					{id: 2, firstname: "Theus", lastname: "De Nator", city: "Termi-Nation", postal_code: 666, status: 'blocked', bgstyle: 'danger', textstyle: 'dark'},
				],
			};
	
		
//	cfw_dashboard_createWidgetInstance({TYPE:'cfw_table', X:0, Y:0, HEIGHT: 5, WIDTH: 5, TITLE: "Table Test Maximal",
//		JSON_SETTINGS: {
//			tableData: rendererTestdata
//		}
//	});
	
//	cfw_dashboard_createWidgetInstance({TYPE:'cfw_table', X:6, Y:0, HEIGHT: 5, WIDTH: 7, TITLE: "Table Test Lot of Data", 
//		JSON_SETTINGS: {
//			delimiter: ';',
//			narrow: true,
//			striped: true,
//			filter: true,
//			tableData: "PK_ID;TIME;FK_ID_SIGNATURE;FK_ID_PARENT;COUNT;MIN;AVG;MAX;GRANULARITY\n2943;2020-02-01 15:51:21.606;1;null;540;180;180;180;15\n2944;2020-02-01 15:51:21.606;2;null;540;180;180;180;15\n2945;2020-02-01 15:51:21.606;3;2;540;180;180;180;15\n2946;2020-02-01 15:51:21.606;4;3;540;180;180;180;15\n2947;2020-02-01 15:51:21.606;4;53;540;180;180;180;15\n2948;2020-02-01 15:51:21.606;4;74;2430;690;810;1020;15\n2949;2020-02-01 15:51:21.606;5;4;3510;1050;1170;1380;15\n2950;2020-02-01 15:51:21.606;5;67;1080;340;360;380;15\n2951;2020-02-01 15:51:21.606;6;null;540;180;180;180;15\n2952;2020-02-01 15:51:21.606;7;6;540;180;180;180;15\n2953;2020-02-01 15:51:21.606;7;12;540;180;180;180;15\n2954;2020-02-01 15:51:21.606;8;7;1080;360;360;360;15\n2955;2020-02-01 15:51:21.606;9;8;1080;360;360;360;15\n2956;2020-02-01 15:51:21.606;10;9;1080;360;360;360;15\n2957;2020-02-01 15:51:21.606;11;10;540;180;180;180;15\n2958;2020-02-01 15:51:21.606;12;11;540;180;180;180;15\n2959;2020-02-01 15:51:21.606;13;10;540;180;180;180;15"
//		}
//	});
	
//	cfw_dashboard_createWidgetInstance({TYPE:'cfw_iframe', X:6, Y:0, HEIGHT: 4, WIDTH: 7, TITLE: "", JSON_SETTINGS: { url: "/app/cpusampling" } } );
//	
//	cfw_dashboard_createWidgetInstance({TYPE:'cfw_table', X:0, Y:0, HEIGHT: 4, WIDTH: 5, TITLE: "Table Test Minimal", 
//		JSON_SETTINGS: {
//			tableData: rendererTestdataMinimal 
//		}
//	});
//	
//	cfw_dashboard_createWidgetInstance({TYPE: 'cfw_image', X:6, Y:0, HEIGHT: 4, WIDTH: 7, TITLE: "", JSON_SETTINGS: { url: "/resources/images/login_background.jpg" } } );
//	
//	cfw_dashboard_createWidgetInstance({TYPE:'cfw_text', X:0, Y:0, HEIGHT: 2, WIDTH: 2, TITLE: "Test Success", BGCOLOR: "success", FGCOLOR: "light" , JSON_SETTINGS: { } });
//	cfw_dashboard_createWidgetInstance({TYPE:'cfw_text', X:11, Y:0, HEIGHT: 5, WIDTH: 2, TITLE: "Test Danger", BGCOLOR: "danger", FGCOLOR: "light"});
//	cfw_dashboard_createWidgetInstance({TYPE:'cfw_text', X:8, Y:0, HEIGHT: 3, WIDTH: 2, TITLE: "Test Primary and Object", BGCOLOR: "primary", FGCOLOR: "light", data: {firstname: "Jane", lastname: "Doe", street: "Fantasyroad 22", city: "Nirwana", postal_code: "8008" }, JSON_SETTINGS: { }});
//	cfw_dashboard_createWidgetInstance({TYPE:'cfw_text', X:7, Y:0, HEIGHT: 5, WIDTH: 3, TITLE: "Test Light and Array", BGCOLOR: "light", FGCOLOR: "secondary", data: ["Test", "Foo", "Bar", 3, 2, 1], JSON_SETTINGS: { }});
//	cfw_dashboard_createWidgetInstance({TYPE:'cfw_text', X:2, Y:0, HEIGHT: 2, WIDTH: 4, TITLE: "Test Matrix", BGCOLOR: "dark", FGCOLOR: "success", data: "Mister ÄÄÄÄÄÄÄÄÄÄÄnderson.", JSON_SETTINGS: { }});
//	cfw_dashboard_createWidgetInstance({TYPE:'cfw_text', X:9, Y:0, HEIGHT: 2, WIDTH: 4, TITLE: "Test Warning", BGCOLOR: "warning", FGCOLOR: "dark", JSON_SETTINGS: { }});
//	cfw_dashboard_createWidgetInstance({TYPE:'cfw_text', X:3, Y:0, HEIGHT: 4, WIDTH: 5, JSON_SETTINGS: { }});
//	cfw_dashboard_createWidgetInstance({TYPE:'cfw_text', X:0, Y:0, HEIGHT: 3, WIDTH: 3, JSON_SETTINGS: { }});
//	cfw_dashboard_createWidgetInstance({TYPE:'cfw_text', JSON_SETTINGS: { }});
	
}

/******************************************************************
 * Main method for building the view.
 * 
 ******************************************************************/
function cfw_dashboard_initialDraw(){
		
	cfw_dashboard_initialize('.grid-stack');
	
	addTestdata();
	
	cfw_dashboard_draw();
}
/******************************************************************
 * Main method for building the view.
 * 
 ******************************************************************/
function cfw_dashboard_draw(){
		
	CFW.ui.toogleLoader(true);
	
	window.setTimeout( 
	function(){

		CFW.http.getJSON(CFW_DASHBOARDVIEW_URL, {action: "fetch", item: "widgets", dashboardid: CFW_DASHBOARDVIEW_PARAMS.id}, function(data){
			
			var widgetArray = data.payload;
			
			for(var i = 0;i < widgetArray.length ;i++){
				cfw_dashboard_createWidgetInstance(widgetArray[i], false);
			}

			//-----------------------------
			// Disable resize & move
			$('.grid-stack').data('gridstack').disable();
		});
		
		CFW.ui.toogleLoader(false);
	}, 100);
}

/******************************************************************
 * Initialize Localization
 * has to be done before widgets are registered
 ******************************************************************/
CFW.lang.loadLocalization();

CFW.dashboard.registerCategory("fas fa-th-large", "Static Widgets", CFWL('cfw_dashboard_category_static'));
