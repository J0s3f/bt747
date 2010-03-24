// only if the google-api is proper called in the header of the html-page, this works!
try {
    if (typeof google != 'undefined')
    {
	    google.load("maps", "2.x");
    }
} catch (err) {
	addMessage(err.message);
}

/** myLieuUser
 * 
 * An Class describing any important things about a user.
 * 
 * Methods:
 *   initialize
 *   createUserIcon
 *   createMarker
 *   changePosition
 *   getLastPosition
 *   setStatus
 * 
 */
var myLieuUser = Class.create();

myLieuUser.prototype=
{
	initialize:function()
	{
		this.myUserMarker = null;
		this.myPosition = null;
		this.myAltitude = 0;
		this.myLastSpeed = 0;
		this.myNumSatellites = 0;
		this.myDirection = 0.0;
		this.myLastSatelliteInfo = 0;
		this.myLastTime = null;
		this.myTimeStampCounter = 0;
		this.myStatus = false;
		this.myUserName = "";
	},
	setUser:function(user) {
	   this.myUserName = user;
	},
	// creates an icon for this user. Depends on the username, where the icon lies
	createUserIcon:function()
	{
		// for working with google you may use a shadow-icon
		return aMapEngine.createIcon("usericon.gif", 40, 44, "shadow.png", 83, 44);
	},
	// Creates a marker on the last known position
	createMarker:function(aPosition) 
	{
		return aMapEngine.createMarker(aPosition, this.createUserIcon());
	},
	changePosition:function(aPosition)
	{
       return aMapEngine.changeMarker(this.myUserMarker, aPosition);
	},
	/* trys to fetch the latest known position of this user
	 */
	getLastPosition:function() 
	{
		var myDate = new Date();
		
		var myXMLFile = "geodata.xml?" + myDate.getTime();

		var thisInstance = this;
		
		var myAjax = new Ajax.Request(myXMLFile,
		{
			method: 'get',
			onSuccess: function(request) 
			{
				if (request.readyState == 4) 
				{
					var xmlDoc = request.responseXML;
	
					if (!xmlDoc || !xmlDoc.documentElement)
					{
						// no doc, no elements, no fun!
						return;
					}
	
					// read the elements from the xml-file
					var markers = xmlDoc.documentElement.getElementsByTagName("marker");
	
					if (!markers || (markers.length < 1))
					{
						// no markers, no fun!
						return;
					}
					
					// Last element by default
					var selectedElement = markers.length-1;
	
					if(thisInstance.myUserName!=null && thisInstance.myUserName!="") {
					  // Unless following specific user
					  selectedElement = -1;
					}
					
					for ( var i = markers.length-1; i >=0; i--) {
					    var m = markers[i];
					    if(m.getAttribute('user')==thisInstance.myUserName) {
					       selectedElement = i;
					       i = -1;
					    }  
					}
	
					if(selectedElement<0) {
					   return;
					}
					// get the last position in longitude and latitude
					thisInstance.myPosition = aMapEngine.createLatLong(parseFloat(markers[selectedElement].getAttribute("lat")), parseFloat(markers[selectedElement].getAttribute("lng")));
					
					// you may show that in the message-window
					// addMessage("lat: " + parseFloat(markers[selectedElement].getAttribute("lat")) + " lng: " + parseFloat(markers[selectedElement].getAttribute("lng")) );
	
					// get the speed
					thisInstance.myLastSpeed = markers[selectedElement].getAttribute("speed");
					
					// get the number of satellites
					thisInstance.myNumSatellites = markers[selectedElement].getAttribute("numsat");
					
					// the altitude
					thisInstance.myAltitude  = markers[selectedElement].getAttribute("alt");
					
					// the direction
					thisInstance.myDirection = markers[selectedElement].getAttribute("dir");
					
					// and the time
					thisInstance.myTime = markers[selectedElement].getAttribute("time");
					
					// check 5 times if we got a newer time stamp (another time stamp string)
					if (thisInstance.myLastTime == thisInstance.myTime)
					{
						// no, we got not
						thisInstance.myTimeStampCounter++;
					}
					else
					{
						// reset the timestampcounter
						thisInstance.myLastTime = thisInstance.myTime;
						thisInstance.myTimeStampCounter = 0;
					}
					
					// if we got 5 times the same time in the geodata.xml
					// we must asume, that no new data comes in what means
					// that connection is broken or the driver is dead.
					if (thisInstance.myTimeStampCounter > 4)
					{
						// do not let the timestampcounter run into endless
						thisInstance.myTimeStampCounter = 4;
						thisInstance.setStatus(false);
					}
					else
					{
						thisInstance.setStatus(true);
					}
	
					// check, if we got an icon for this user
					if (!thisInstance.myUserMarker)
					{
						thisInstance.myUserMarker = thisInstance.createMarker(thisInstance.myPosition);
						aMapEngine.addMarker(thisInstance.myUserMarker);
					}
					else
					{
						thisInstance.changePosition(thisInstance.myPosition);
					}		
	
	
					// then my speed will be displayed
					// addMessage("Speed: " + thisInstance.myLastSpeed);
					// addMessage("Alt: " + thisInstance.myAltitude);
	
					if (aMapEngine)
					{
						aMapEngine.map().panTo(thisInstance.myPosition);
					}
				}
			}
		});
	},
	setStatus:function(bStatus)
	{
		// only do something, if status is changing
		if (this.myStatus != bStatus)
		{
			var myStatusArea = $("status_area");
			var myStatusString = "";
			
			if (bStatus)
			{
				// condition green
				myStatusString = '<img src="green.jpg" width="30" border="0">';
			}
			else
			{
				// condition red
				myStatusString = '<img src="red.jpg" width="30" border="0">';
			}
			myStatusArea.update(myStatusString);
			
			// remember the new status
			this.myStatus = bStatus;
		}
	}
};
/** End myLieuUser Class definition */


// the google-panel
var aMapEngine = null;

// create an object which has an Icon and a position
var aUser = new myLieuUser();

function checkPHPversion()
{
	var myDate = new Date();
	
	var myCheckFile = "checker.php";
	
	var myParameter = "action=phpversion";

	var myAjax = new Ajax.Request(myCheckFile,
	{
		method: 'get',
		parameters: myParameter,
		onSuccess: function(request) 
		{
			if (request.responseText < '5.0.0')
			{
				alert("php version on the server is too low: " + request.responseText);
			}
			else
			{
				addMessage("php-version: " + request.responseText);
				// go on with the next check
				onCheck(2);
			}
		}
	});
}

/**
 * Checks, if the class DomDocument is available on the php-server
 */
function checkDomDocument()
{
	var myDate = new Date();
	
	var myCheckFile = "checker.php";
	
	var myParameter = "action=isDomDocument";

	var myAjax = new Ajax.Request(myCheckFile,
	{
		method: 'get',
		parameters: myParameter,
		onSuccess: function(request) 
		{
			if (request.responseText == '0')
			{
				addMessage("DomDocument is NOT available");
				// go on with the alternate check
				onCheck(3);
			}
			else
			{
				addMessage("DomDocument is available");
				// go on with the next check
				onCheck(4);
			}
		}
	});
}

 /**
  * Checks, if the class DomDocument is available on the php-server
  */
 function checkDomXmlOpenFile()
 {
 	var myDate = new Date();
 	
 	var myCheckFile = "checker.php";
 	
 	var myParameter = "action=domxml_open_file";

 	var myAjax = new Ajax.Request(myCheckFile,
 	{
 		method: 'get',
 		parameters: myParameter,
 		onSuccess: function(request) 
 		{
 			if (request.responseText == '0')
 			{
 				alert("Function domxml_open_file is not available");
 			}
 			else
 			{
 				addMessage("Function domxml_open_file is available");
 				// go on with the next check
 				onCheck(4);
 			}
 		}
 	});
 }


/**
 * Checks, if the class DomDocument is available on the php-server
 */
function checkFileCreatePermission()
{
	var myDate = new Date();
	
	var myCheckFile = "checker.php";
	
	var myParameter = "action=filecreatepermission";

	var myAjax = new Ajax.Request(myCheckFile,
	{
		method: 'get',
		parameters: myParameter,
		onSuccess: function(request) 
		{
			if (request.responseText == '0')
			{
				alert("File creation is not possible");
			}
			else
			{
				addMessage("File creation is possible");
				// go on with the next check
				onCheck(99);
			}
		}
	});
}

/**
 * This one is called instead of onLoad at the beginning
 * of this page.
 * We will check here some preconditions for myLieu:
 * - php-Version
 * - Ajax-support
 * - file-writing on the server
 * 
 */
function onCheck(stage)
{
	switch (stage)
	{
		case 1:
			checkPHPversion();
			break;
			
		case 2:
			checkDomDocument();
			break;
			
		case 3:
			checkDomXmlOpenFile();
			break;
			
		case 4:
			checkFileCreatePermission();
			break;
			
		// last thing we do: load myLieu finally
		case 99:
			onLoad();
			break;
			
		case 0:
		default:
			break;
	}
}

/**
 * This one is called when the html-page is loaded.
 * @param string anUser
 * the name of the user you want to focus after loading
 */
function onLoad(anUser)
{	
	aUser.setUser(anUser);
	// resize the map-div to screen of browser
	onResize();

	aMapEngine = new myGeoEngine();
	aMapEngine.init();
	
	addMessage("User: "+anUser);

	// start the periodical updater
	onUpdate();
}
 
/* Function to be called when page is unloaded. */
function onUnLoad() {
	aMapEngine.unload();
}

/**
 * This one is a special loader for the viewing page
 */
function onViewLoad()
{
	aMapEngine = new myGeoEngine();
	aMapEngine.init();
	
	// set the map static to 640x480 with some space on the left
	// for the controls
	var myContentArea = $("map");
	
	if (myContentArea != null)
	{
		myContentArea.style.height = "480px";
		myContentArea.style.width = "600px";
		myContentArea.style.left = "200px";
	}
}

/**
 * resizing some div-element to the size of the browser-client-window
 */
function resizeDiv(myDiv)
{
	var myContentArea = $(myDiv);
	
	if (myContentArea != null)
	{
		var myNewHeight = getWindowHeight() + "px";
		myContentArea.style.height = myNewHeight;
		var myNewWidth = getWindowWidth() + "px";
		myContentArea.style.width = myNewWidth;
		addMessage("Size "+myNewHeight+" "+myNewWidth);
	} else {
		addMessage("Map not found in resizeDiv");
	}
}


/**
 * resize the inner map to the browser-windowsize
 */
function onResize()
{
	resizeDiv("map");
}

/**
 * does a refresh of the screen and start again the timer
 * every 2 seconds or whatever the value ist
 */
function onUpdate()
{
	// ask for the last position of the user
	var aPosition = aUser.getLastPosition();

	// repeat this every 2 seconds
	window.setTimeout("onUpdate()", 2000);	
}


/**
 * This shows an Text in the message-div and deletes
 * the previous
 */
function showMessage(aText)
{
	var myTextArea = $("message_text");
	myTextArea.value = aText;
}

/**
 * This adds an Text in the message-div 
 */
function addMessage(aText)
{
	var myTextArea = $("message_text");
	myTextArea.value = aText+"\n"+myTextArea.value;
}


/**
 *
 */
function routeChosing(aRouteFileName)
{
	if (aRouteFileName == "0")
	{
		return;
	}
	
	// the user surely wants to download the gpx-file, which has
	// the same name except the suffix.
	var aRouteFileNameGPX = aRouteFileName.replace(".xml", ".gpx");
	showDownloadLink(aRouteFileNameGPX);
	
	var myAjax = new Ajax.Request(aRouteFileName,
	{
		method: 'get',
		onSuccess: function(request) 
		{
			if (request.readyState == 4) 
			{
				var xmlDoc = request.responseXML;

				if (!xmlDoc || !xmlDoc.documentElement)
				{
					// no doc, no elements, no fun!
					return;
				}

				// read the elements from the xml-file
				var markers = xmlDoc.documentElement.getElementsByTagName("marker");

				if (!markers || (markers.length < 1))
				{
					// no markers, no fun!
					alert("no markers");
					return;
				}
				
				var points = [];
				
				for (var i = 0; i < markers.length; i++) 
				{
					// get each marker and store the latitude and longitude as a pointset into an array
					point = aMapEngine.createLatLong(parseFloat(markers[i].getAttribute("lat")), parseFloat(markers[i].getAttribute("lng")));
					points.push(point);
				}
				aMapEngine.addPolyLine(points);				
			}
		}
	});
}

/**
 * This one is responsible for showing a link to download
 * the gpx-file
 * @param aFileName
 */
function showDownloadLink(aFileName)
{
	var myLinkArea = $("downloadLink");
	myLinkArea.update("<a href=\"" + aFileName + "\">GPX</a>");
}

// Get URL parameter
function gup( name )
{
  name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
  var regexS = "[\\?&]"+name+"=([^&#]*)";
  var regex = new RegExp( regexS );
  var results = regex.exec( window.location.href );
  if( results == null )
    return "";
  else
    return results[1];
}