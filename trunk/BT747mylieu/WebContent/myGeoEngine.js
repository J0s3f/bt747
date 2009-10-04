var myGeoEngine = Class.create();

try {
if (GBrowserIsCompatible()) {
	 geocoder = new GClientGeocoder();
	 preva = "none";
	 function showaddr(latlng) {
	  if (latlng) {
	   geocoder.getLocations(latlng, function(adrs) {
	    if(adrs.Status.code == 200) {
	     adr=adrs.Placemark[0];
	     var txt=adr.address;
	     if(txt==preva) {
	      map.openInfoWindow(latlng, txt);
	     } else {
	      preva = txt;
	     }
	    }
	   });
	  }
	 }

function latlonTxt(latlon) {
	//addMessage("latlon");
	if(latlon) {
	     var s='<b>Last click: '+ latlon.toUrlValue()+' </b>';
	  showaddr(latlon);
	  try {
	     document.getElementById("latlon").innerHTML = s;
	  } catch (err) {
		  // latlon does not exist probably
	  }
	 }
	}
function latlonFunc() {
	 return function(overlay,latlon) {latlonTxt(latlon);}
	}

function gotoPt(map, pt) {
	 if(!pt){
	  document.getElementById("latlon").innerHTML="Location not found";
	 } else {
	  map.setCenter(pt);latlonTxt(pt);
	 }
	}

function gotoAddr(){
	 var ad=document.getElementById("adr").value;
	 new GClientGeocoder().getLatLng(ad,gotoPt);
	}
}
} catch(err) { }

myGeoEngine.prototype = {
	initialize : function() {
		this.myMap = null;
		// default engine is openstreetmap
		// this.myEngine = "OSM";
		this.myEngine = "google";
		this.layerVectors = null;
	},
	init : function() {
		var lat = 50.3;
		var lon = 8.5;
		var zoom = 13;
		try {
			// If problem with google, use OSM.
			if (this.myEngine = "google") {
				if (typeof google == 'undefined') {
					this.myEngine = "OSM";
				}
			}
		} catch (err) {
			this.myEngine = "OSM";
		}

		switch (this.myEngine) {
		case "google":
			// only if the google-api is proper called in the header of the html-page, this works!
			if (typeof google != 'undefined') {
				// create a good google map for the div "map"
				this.myMap = new google.maps.Map2($("map"));
				this.myMap.setCenter(new GLatLng(lat, lon));
				this.myMap.setZoom(zoom);
				this.myMap.enableScrollWheelZoom();
				this.myMap.addControl(new GLargeMapControl());
				this.myMap.addControl(new GMapTypeControl());
				this.myMap.addControl(new GLargeMapControl());
				this.myMap.addControl(new GMapTypeControl());
				this.myMap.addControl(new GScaleControl());
				this.myMap.addControl(new GOverviewMapControl());
				this.myMap.enableContinuousZoom();
				this.myMap.enableDoubleClickZoom();
				try {
					// Does not seem to work yet here.
				    GEvent.addListener(this.myMap,'click',latlonFunc());
				} catch(err) {}
		       	
				var OSM = new GMapType(
						[ new GTileLayer(
								null,
								1,
								18,
								{
									tileUrlTemplate : 'http://tile.openstreetmap.org/{Z}/{X}/{Y}.png',
									isPng : true,
									opacity : 1.0
								}) ], new GMercatorProjection(19), 'OSM', {
							errorMessage : "More OSM coming soon"
						});
				var OSMcycle = new GMapType(
						[ new GTileLayer(
								null,
								1,
								15,
								{
									tileUrlTemplate : 'http://www.thunderflames.org/tiles/cycle/{Z}/{X}/{Y}.png',
									isPng : true,
									opacity : 1.0
								}) ], new GMercatorProjection(19), 'Cycle', {
							errorMessage : "More OSM coming soon"
						});
				var Osmarender = new GMapType(
						[ new GTileLayer(
								null,
								1,
								18,
								{
									tileUrlTemplate : 'http://tah.openstreetmap.org/Tiles/tile/{Z}/{X}/{Y}.png',
									isPng : true,
									opacity : 1.0
								}) ], new GMercatorProjection(19), 'Osmardr', {
							errorMessage : "More OSM coming soon"
						});
				this.myMap.addMapType(OSM);
				this.myMap.addMapType(OSMcycle);
				this.myMap.addMapType(Osmarender);
			}
			break;

		case "OSM":
			this.myMap = new OpenLayers.Map("map", {
				controls : [ new OpenLayers.Control.Navigation(),
						new OpenLayers.Control.PanZoomBar(),
						new OpenLayers.Control.Attribution() ],
				maxExtent : new OpenLayers.Bounds(-20037508.34, -20037508.34,
						20037508.34, 20037508.34),
				maxResolution : 156543.0399,
				numZoomLevels : 19,
				units : 'm',
				projection : new OpenLayers.Projection("EPSG:900913"),
				displayProjection : new OpenLayers.Projection("EPSG:4326")
			});

			// Define the map layer
			// Note that we use a predefined layer that will be
			// kept up to date with URL changes
			// Here we define just one layer, but providing a choice
			// of several layers is also quite simple
			// Other defined layers are OpenLayers.Layer.OSM.Mapnik,
			// OpenLayers.Layer.OSM.Maplint and OpenLayers.Layer.OSM.CycleMap
			layerTilesAtHome = new OpenLayers.Layer.OSM.Osmarender("Osmarender");
			this.myMap.addLayer(layerTilesAtHome);

			var lonLat = new OpenLayers.LonLat(lon, lat).transform(
					new OpenLayers.Projection("EPSG:4326"), this.myMap
							.getProjectionObject());

			this.myMap.setCenter(lonLat, zoom);

			// add a layer for markers
			this.layerMarkers = new OpenLayers.Layer.Markers("Markers");
			// add a layer for polylines
			this.layerVectors = new OpenLayers.Layer.Vector("Vectors");

			this.myMap.addLayers( [ this.layerMarkers, this.layerVectors ]);
			break;
		default:
			break;
		}
	},
	unload : function() {
		switch (this.myEngine) {
		case "google":
			GUnload();
			break;
		default:
			break;
		}
	},
	// Here you may switch the engine from "google" to "OSM" ( OpenStreetMap)
	setEngine : function(anEngine) {
		this.myEngine = anEngine;
		this.init();
	},
	// return the engine-object directly
	map : function() {
		return this.myMap;
	},
	// creates a marker, engine-depending
	createMarker : function(aPosition, aIcon) {
		switch (this.myEngine) {
		case "google":
			// aIcon = our marker
			// return new GMarker(aPosition, aIcon);
			// Prefer default marker
			return new GMarker(aPosition);
			break;
		case "OSM":
			return new OpenLayers.Marker(aPosition, aIcon);
			break;
		default:
			return null;
		}
	},
	/** creates an icon, engine-depending
	 * @parameter string
	 */
	createIcon : function(anIconName, anIcon_X, anIcon_Y, anShadowIconName,
			anShadowIcon_X, anShadowIcon_Y) {
		var anIcon;

		switch (this.myEngine) {
		case "google":
			// a new Icon for a user
			anIcon = new GIcon();
			anIcon.image = anIconName;
			anIcon.shadow = anShadowIconName;
			anIcon.iconSize = new GSize(anIcon_X, anIcon_Y);
			anIcon.shadowSize = new GSize(anShadowIcon_X, anShadowIcon_Y);
			// set the anchor to the middle of the icon
			anIcon.iconAnchor = new GPoint(20, 22);
			anIcon.infoWindowAnchor = new GPoint(20, 20);
			break;

		case "OSM":
			var size = new OpenLayers.Size(anIcon_X, anIcon_Y);
			var offset = new OpenLayers.Pixel(-(size.w / 2), -size.h);
			anIcon = new OpenLayers.Icon(anIconName, size, offset);
			break;

		default:
			break;
		}
		return anIcon;
	},
	addMarker : function(aMarker) {
		switch (this.myEngine) {
		case "google":
			this.myMap.addOverlay(aMarker);
			break;

		case "OSM":
			this.layerMarkers.addMarker(aMarker);
			break;

		default:
			break;
		}
	},
	changeMarker : function(aMarker, aPosition) {
		switch (this.myEngine) {
		case "google":
			aMarker.setPoint(aPosition);
			break;

		case "OSM":
			aMarker.lonlat = aPosition;
			this.layerMarkers.redraw();
			break;

		default:
			break;
		}
	},
	createLatLong : function(aLatitude, aLongitude) {
		switch (this.myEngine) {
		case "google":
			return new GLatLng(aLatitude, aLongitude);
			break;

		case "OSM":
			return new OpenLayers.LonLat(aLongitude, aLatitude).transform(
					new OpenLayers.Projection("EPSG:4326"), this.myMap
							.getProjectionObject());
			break;

		default:
			return null;
			break;
		}
	},
	// adding a polyline out of an array of points
	addPolyLine : function(pointsArray) {
		switch (this.myEngine) {
		case "google":
			this.myMap.clearOverlays();
			var myPolyLine = new GPolyline(pointsArray, "#0000ff", 3);
			this.myMap.addOverlay(myPolyLine);

			// center and zoom to the bounding box
			this.myMap.setCenter(myPolyLine.getBounds().getCenter(), this.myMap
					.getBoundsZoomLevel(myPolyLine.getBounds()));
			break;

		case "OSM":
			this.layerVectors.destroyFeatures();
			// add this point-array as an layer to the map
			// but convert it before into the OSM-geometry-points
			var points = [];
			for ( var i = 0; i < pointsArray.length; i++) {
				point = new OpenLayers.Geometry.Point(pointsArray[i].lon,
						pointsArray[i].lat);
				points.push(point);
			}
			var geometry = new OpenLayers.Geometry.LineString(points);
			var feature = new OpenLayers.Feature.Vector(geometry, null, {
				strokeColor : "#0033ff",
				strokeOpacity : 0.7,
				strokeWidth : 3
			});
			this.layerVectors.addFeatures( [ feature ]);

			// center and zoom to the bounding box
			this.myMap.setCenter(geometry.getBounds().getCenterLonLat(),
					this.myMap.zoomToExtent(geometry.getBounds()));
			break;

		default:
			return null;
			break;
		}
	}
}
