// ***************************************Lat*****************************
// *** BT 747 ***
// *** April 14, 2007 ***
// *** (c)2007 Mario De Weerd ***
// *** m.deweerd@ieee.org ***
// *** ********************************************************** ***
// *** Software is provided "AS IS," without a warranty of any ***
// *** kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
// *** INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS ***
// *** FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY ***
// *** EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
// *** IS ASSUMED BY THE USER. ***
// *** See the GNU General Public License Version 3 for details. ***
// *** *********************************************************** ***
package gps.log.out;

import gps.log.GPSFilter;
import gps.log.GPSRecord;
import gps.tracks.PolylineEncoder;
import gps.tracks.Track;
import gps.tracks.Trackpoint;
import bt747.Version;
import bt747.sys.I18N;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Hashtable;
import bt747.sys.interfaces.BT747Path;
import bt747.sys.interfaces.BT747Vector;

/**
 * Class to write a Google Maps HTML file.
 * 
 * @author Mario De Weerd
 */
public final class GPSGmapsV3HTMLFile extends GPSFile {
    /**
     * Local StringBuffer for output.
     */
    private final StringBuffer rec = new StringBuffer(1024); // reused
    // stringbuffer

    /**
     * When true, currently handling waypoints, otherwise trackpoints.
     */
    private boolean isWayType;
    private boolean isNewTrack = true;
    private int currentFilter;

    private final Track track = new Track();
    private final Track waypoints = new Track();

    private int trackIndex = 0; // Index for tracks

    private String trackOnClickFuncCalls = ""; // Javascript function calls
    // if
    // click.
    private final StringBuffer infoHtmls = new StringBuffer(1024);
    private final BT747Vector iconList = JavaLibBridge.getVectorInstance();
    private String trackStartInfo = "";
    private String trackDescription = "";
    
    // See https://google-maps-utility-library-v3.googlecode.com/svn/tags/markermanager/1.2/
    private final String markerManager=
    		"function MarkerManager(c,b){var a=this;a.map_=c;a.mapZoom_=c.getZoom();a.projectionHelper_=new ProjectionHelperOverlay(c);google.maps.event.addListener(a.projectionHelper_,'ready',function(){a.projection_=this.getProjection();a.initialize(c,b)})}MarkerManager.prototype.initialize=function(d,b){var f=this;b=b||{};f.tileSize_=MarkerManager.DEFAULT_TILE_SIZE_;var h=d.mapTypes;var i=1;for(var c in h){if(h.hasOwnProperty(c)&&h.get(c)&&h.get(c).maxZoom==='number'){var g=d.mapTypes.get(c).maxZoom;if(g>i){i=g}}}f.maxZoom_=b.maxZoom||19;f.trackMarkers_=b.trackMarkers;f.show_=b.show||true;var e;if(typeof b.borderPadding==='number'){e=b.borderPadding}else{e=MarkerManager.DEFAULT_BORDER_PADDING_}f.swPadding_=new google.maps.Size(-e,e);f.nePadding_=new google.maps.Size(e,-e);f.borderPadding_=e;f.gridWidth_={};f.grid_={};f.grid_[f.maxZoom_]={};f.numMarkers_={};f.numMarkers_[f.maxZoom_]=0;google.maps.event.addListener(d,'dragend',function(){f.onMapMoveEnd_()});google.maps.event.addListener(d,'idle',function(){f.onMapMoveEnd_()});google.maps.event.addListener(d,'zoom_changed',function(){f.onMapMoveEnd_()});f.removeOverlay_=function(a){a.setMap(null);f.shownMarkers_--};f.addOverlay_=function(a){if(f.show_){a.setMap(f.map_);f.shownMarkers_++}};f.resetManager_();f.shownMarkers_=0;f.shownBounds_=f.getMapGridBounds_();google.maps.event.trigger(f,'loaded')};MarkerManager.DEFAULT_TILE_SIZE_=1024;MarkerManager.DEFAULT_BORDER_PADDING_=100;MarkerManager.MERCATOR_ZOOM_LEVEL_ZERO_RANGE=256;MarkerManager.prototype.resetManager_=function(){var a=MarkerManager.MERCATOR_ZOOM_LEVEL_ZERO_RANGE;for(var b=0;b<=this.maxZoom_;++b){this.grid_[b]={};this.numMarkers_[b]=0;this.gridWidth_[b]=Math.ceil(a/this.tileSize_);a<<=1}};MarkerManager.prototype.clearMarkers=function(){this.processAll_(this.shownBounds_,this.removeOverlay_);this.resetManager_()};MarkerManager.prototype.getTilePoint_=function(a,c,b){var d=this.projectionHelper_.LatLngToPixel(a,c);var e=new google.maps.Point(Math.floor((d.x+b.width)/this.tileSize_),Math.floor((d.y+b.height)/this.tileSize_));return e};MarkerManager.prototype.addMarkerBatch_=function(i,d,j){var f=this;var e=i.getPosition();i.MarkerManager_minZoom=d;if(this.trackMarkers_){google.maps.event.addListener(i,'changed',function(a,b,c){f.onMarkerMoved_(a,b,c)})}var h=this.getTilePoint_(e,j,new google.maps.Size(0,0,0,0));for(var g=j;g>=d;g--){var k=this.getGridCellCreate_(h.x,h.y,g);k.push(i);h.x=h.x>>1;h.y=h.y>>1}};MarkerManager.prototype.isGridPointVisible_=function(d){var b=this.shownBounds_.minY<=d.y&&d.y<=this.shownBounds_.maxY;var e=this.shownBounds_.minX;var c=e<=d.x&&d.x<=this.shownBounds_.maxX;if(!c&&e<0){var a=this.gridWidth_[this.shownBounds_.z];c=e+a<=d.x&&d.x<=a-1}return b&&c};MarkerManager.prototype.onMarkerMoved_=function(f,b,g){var c=this.maxZoom_;var a=false;var d=this.getTilePoint_(b,c,new google.maps.Size(0,0,0,0));var e=this.getTilePoint_(g,c,new google.maps.Size(0,0,0,0));while(c>=0&&(d.x!==e.x||d.y!==e.y)){var h=this.getGridCellNoCreate_(d.x,d.y,c);if(h){if(this.removeFromArray_(h,f)){this.getGridCellCreate_(e.x,e.y,c).push(f)}}if(c===this.mapZoom_){if(this.isGridPointVisible_(d)){if(!this.isGridPointVisible_(e)){this.removeOverlay_(f);a=true}}else{if(this.isGridPointVisible_(e)){this.addOverlay_(f);a=true}}}d.x=d.x>>1;d.y=d.y>>1;e.x=e.x>>1;e.y=e.y>>1;--c}if(a){this.notifyListeners_()}};MarkerManager.prototype.removeMarker=function(d){var b=this.maxZoom_;var a=false;var e=d.getPosition();var c=this.getTilePoint_(e,b,new google.maps.Size(0,0,0,0));while(b>=0){var f=this.getGridCellNoCreate_(c.x,c.y,b);if(f){this.removeFromArray_(f,d)}if(b===this.mapZoom_){if(this.isGridPointVisible_(c)){this.removeOverlay_(d);a=true}}c.x=c.x>>1;c.y=c.y>>1;--b}if(a){this.notifyListeners_()}this.numMarkers_[d.MarkerManager_minZoom]--};MarkerManager.prototype.addMarkers=function(b,a,c){var d=this.getOptMaxZoom_(c);for(var i=b.length-1;i>=0;i--){this.addMarkerBatch_(b[i],a,d)}this.numMarkers_[a]+=b.length};MarkerManager.prototype.getOptMaxZoom_=function(a){return a||this.maxZoom_};MarkerManager.prototype.getMarkerCount=function(a){var b=0;for(var z=0;z<=a;z++){b+=this.numMarkers_[z]}return b};MarkerManager.prototype.getMarker=function(c,e,d){var b=new google.maps.LatLng(c,e);var f=this.getTilePoint_(b,d,new google.maps.Size(0,0,0,0));var g=new google.maps.Marker({position:b});var a=this.getGridCellNoCreate_(f.x,f.y,d);if(a!==undefined){for(var i=0;i<a.length;i++){if(c===a[i].getPosition().lat()&&e===a[i].getPosition().lng()){g=a[i]}}}return g};MarkerManager.prototype.addMarker=function(d,a,b){var e=this.getOptMaxZoom_(b);this.addMarkerBatch_(d,a,e);var c=this.getTilePoint_(d.getPosition(),this.mapZoom_,new google.maps.Size(0,0,0,0));if(this.isGridPointVisible_(c)&&a<=this.shownBounds_.z&&this.shownBounds_.z<=e){this.addOverlay_(d);this.notifyListeners_()}this.numMarkers_[a]++};function GridBounds(a){this.minX=Math.min(a[0].x,a[1].x);this.maxX=Math.max(a[0].x,a[1].x);this.minY=Math.min(a[0].y,a[1].y);this.maxY=Math.max(a[0].y,a[1].y)}GridBounds.prototype.equals=function(a){if(this.maxX===a.maxX&&this.maxY===a.maxY&&this.minX===a.minX&&this.minY===a.minY){return true}else{return false}};GridBounds.prototype.containsPoint=function(a){var b=this;return(b.minX<=a.x&&b.maxX>=a.x&&b.minY<=a.y&&b.maxY>=a.y)};MarkerManager.prototype.getGridCellCreate_=function(x,y,z){var b=this.grid_[z];if(x<0){x+=this.gridWidth_[z]}var c=b[x];if(!c){c=b[x]=[];return(c[y]=[])}var a=c[y];if(!a){return(c[y]=[])}return a};MarkerManager.prototype.getGridCellNoCreate_=function(x,y,z){var a=this.grid_[z];if(x<0){x+=this.gridWidth_[z]}var b=a[x];return b?b[y]:undefined};MarkerManager.prototype.getGridBounds_=function(j,b,c,e){b=Math.min(b,this.maxZoom_);var i=j.getSouthWest();var f=j.getNorthEast();var d=this.getTilePoint_(i,b,c);var g=this.getTilePoint_(f,b,e);var a=this.gridWidth_[b];if(f.lng()<i.lng()||g.x<d.x){d.x-=a}if(g.x-d.x+1>=a){d.x=0;g.x=a-1}var h=new GridBounds([d,g]);h.z=b;return h};MarkerManager.prototype.getMapGridBounds_=function(){return this.getGridBounds_(this.map_.getBounds(),this.mapZoom_,this.swPadding_,this.nePadding_)};MarkerManager.prototype.onMapMoveEnd_=function(){this.objectSetTimeout_(this,this.updateMarkers_,0)};MarkerManager.prototype.objectSetTimeout_=function(b,a,c){return window.setTimeout(function(){a.call(b)},c)};MarkerManager.prototype.visible=function(){return this.show_?true:false};MarkerManager.prototype.isHidden=function(){return!this.show_};MarkerManager.prototype.show=function(){this.show_=true;this.refresh()};MarkerManager.prototype.hide=function(){this.show_=false;this.refresh()};MarkerManager.prototype.toggle=function(){this.show_=!this.show_;this.refresh()};MarkerManager.prototype.refresh=function(){if(this.shownMarkers_>0){this.processAll_(this.shownBounds_,this.removeOverlay_)}if(this.show_){this.processAll_(this.shownBounds_,this.addOverlay_)}this.notifyListeners_()};MarkerManager.prototype.updateMarkers_=function(){this.mapZoom_=this.map_.getZoom();var a=this.getMapGridBounds_();if(a.equals(this.shownBounds_)&&a.z===this.shownBounds_.z){return}if(a.z!==this.shownBounds_.z){this.processAll_(this.shownBounds_,this.removeOverlay_);if(this.show_){this.processAll_(a,this.addOverlay_)}}else{this.rectangleDiff_(this.shownBounds_,a,this.removeCellMarkers_);if(this.show_){this.rectangleDiff_(a,this.shownBounds_,this.addCellMarkers_)}}this.shownBounds_=a;this.notifyListeners_()};MarkerManager.prototype.notifyListeners_=function(){google.maps.event.trigger(this,'changed',this.shownBounds_,this.shownMarkers_)};MarkerManager.prototype.processAll_=function(b,a){for(var x=b.minX;x<=b.maxX;x++){for(var y=b.minY;y<=b.maxY;y++){this.processCellMarkers_(x,y,b.z,a)}}};MarkerManager.prototype.processCellMarkers_=function(x,y,z,a){var b=this.getGridCellNoCreate_(x,y,z);if(b){for(var i=b.length-1;i>=0;i--){a(b[i])}}};MarkerManager.prototype.removeCellMarkers_=function(x,y,z){this.processCellMarkers_(x,y,z,this.removeOverlay_)};MarkerManager.prototype.addCellMarkers_=function(x,y,z){this.processCellMarkers_(x,y,z,this.addOverlay_)};MarkerManager.prototype.rectangleDiff_=function(c,d,a){var b=this;b.rectangleDiffCoords_(c,d,function(x,y){a.apply(b,[x,y,c.z])})};MarkerManager.prototype.rectangleDiffCoords_=function(j,k,b){var f=j.minX;var a=j.minY;var d=j.maxX;var h=j.maxY;var g=k.minX;var c=k.minY;var e=k.maxX;var i=k.maxY;var x,y;for(x=f;x<=d;x++){for(y=a;y<=h&&y<c;y++){b(x,y)}for(y=Math.max(i+1,a);y<=h;y++){b(x,y)}}for(y=Math.max(a,c);y<=Math.min(h,i);y++){for(x=Math.min(d+1,g)-1;x>=f;x--){b(x,y)}for(x=Math.max(f,e+1);x<=d;x++){b(x,y)}}};MarkerManager.prototype.removeFromArray_=function(a,c,b){var d=0;for(var i=0;i<a.length;++i){if(a[i]===c||(b&&a[i]===c)){a.splice(i--,1);d++}}return d};function ProjectionHelperOverlay(b){this.setMap(b);var d=8;var c=1<<d;var a=7;this._map=b;this._zoom=-1;this._X0=this._Y0=this._X1=this._Y1=-1}ProjectionHelperOverlay.prototype=new google.maps.OverlayView();ProjectionHelperOverlay.prototype.LngToX_=function(a){return(1+a/180)};ProjectionHelperOverlay.prototype.LatToY_=function(b){var a=Math.sin(b*Math.PI/180);return(1-0.5/Math.PI*Math.log((1+a)/(1-a)))};ProjectionHelperOverlay.prototype.LatLngToPixel=function(a,d){var c=this._map;var b=this.getProjection().fromLatLngToDivPixel(a);var e={x:~~(0.5+this.LngToX_(a.lng())*(2<<(d+6))),y:~~(0.5+this.LatToY_(a.lat())*(2<<(d+6)))};return e};ProjectionHelperOverlay.prototype.draw=function(){if(!this.ready){this.ready=true;google.maps.event.trigger(this,'ready')}};";

    /**
     * 
     */
    public GPSGmapsV3HTMLFile() {
        super();
        numberOfPasses = 2;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.GPSFile#InitialiseFile(java.lang.String, java.lang.String)
     */
    public final void initialiseFile(final BT747Path basename, final String ext,
            final int oneFilePerDay) {
        super.initialiseFile(basename, ext, oneFilePerDay);
        currentFilter = GPSFilter.WAYPT;
        isWayType = true;

        trackDescription = "";
        resetTrack();
    }

    private final void resetTrack() {
        track.removeAll();
    }

    public final boolean nextPass() {
        super.nextPass();
        if (nbrOfPassesToGo > 0) {
            // if (m_multipleFiles) {
            // closeFile();
            // }
            trackDescription = "";
            nbrOfPassesToGo--;
            previousDate = 0;
            isWayType = false;
            currentFilter = GPSFilter.TRKPT;
            // if (!m_multipleFiles) {
            // writeDataHeader();
            // }
            return true;
        } else {
            return false;
        }
    }

    private final String keyCode() {
        final String googleKeyCode = getParamObject().getStringParam(
                GPSConversionParameters.GOOGLEMAPKEY_STRING);
        if ((googleKeyCode != null) && (googleKeyCode.length() != 0)) {
            return "&key=" + googleKeyCode;
        }
        return ""; // default
    }

    protected final void writeFileHeader(final String trackName) {
        final StringBuffer l_header = new StringBuffer(1700);
        l_header.setLength(0);
        l_header
                .append("<!DOCTYPE html>\n"
                        + "<html xmlns=\"http://www.w3.org/1999/xhtml\""
                        + " xmlns:v=VML>\n" + "<head>\n"
                        + "<meta name=\"viewport\" content=\"initial-scale=1.0, user-scalable=no\" />"
                        + "<title>");
        l_header.append(trackName);

        
        l_header
                .append("</title>\n"
                        + "<meta http-equiv=\"Content-Type\" "
                        + "content=\"text/html; charset=utf-8\" />\n"
                        + "<meta name=\"description\" "
                        + "content=\"Tracks - Generated with BT747 V"
                        + Version.VERSION_NUMBER
                        + " http://www.bt747.org - powered by Google Maps\" />\n"
                        + "\n"
                        + "<script src=\"http://maps.googleapis.com/maps/api/js?v=3.16&sensor=false&libraries=geometry");
        l_header.append(keyCode());
        l_header
                .append("\" type=\"text/javascript\">\n"
                        + "</script>\n"
                        + "<style type=\"text/css\">\n"
                        + " html, body, #map\n"
                        + "  {\n"
                        + "   width: 100%;\n"
                        + "   height: 100%;\n"
                        + "  }\n"
                        + " body {\n"
                        + "  margin-top: 0px;\n"
                        + "  margin-right: 0px;\n"
                        + "  margin-left: 0px;\n"
                        + "  margin-bottom: 0px;\n"
                        + " }\n"
                        + "\n"
                        + "</style>\n"
                        + "</head>\n"
                        + "\n"
                        + "<body "
                        + " onresize=\"setFooter()\">\n"
                        + "<div id=\"map\"> </div>\n"
                        + "<div id=\"footer\">\n"
                        + "\n"
                        + "<form style=\"display:inline\" action=\"javascript:gotoAddr()\">\n"
                        + "<input type=\"submit\" value=\"");
        l_header.append(I18N.i18n("Go To"));
        l_header
                .append("\" />\n"
                        // Next line allows confirmation of input with enter
                        + "<input type=\"submit\" value=\"\" style=\"display:none\" />\n"
                        + "<input size=\"30\" type=\"text\" name=\"adr\" id=\"adr\" />\n"
                        + "</form>\n" + "<input type=\"submit\" value=\"");
        l_header.append(I18N.i18n("Init"));
        l_header
                .append("\" onclick=\"initial()\"/>" + "<span id=\"latlon\">");
        l_header.append(I18N.i18n("Click on map to get position."));
        l_header.append("  </span>");
        l_header.append(I18N.i18n("Tracks"));
        l_header.append(": \n");
        l_header.append("<script type=\"text/javascript\">");
        l_header.append(markerManager);
        l_header.append("</script>\n");
        l_header.append("<script type=\"text/javascript\">\n");
        l_header.append(
                // call the info window opener for the given index
                 " geocoder = new google.maps.Geocoder();\n"
                + " preva = \"none\";\n"
                + "function getBoundsZoomLevel(bounds, mapDim) {\n"
				+ "  var WORLD_DIM = { height: 256, width: 256 };\n"
				+ "  var ZOOM_MAX = 21;\n"

				+ "  function latRad(lat) {\n"
				+ "     var sin = Math.sin(lat * Math.PI / 180);\n"
				+ "    var radX2 = Math.log((1 + sin) / (1 - sin)) / 2;\n"
				+ "    return Math.max(Math.min(radX2, Math.PI), -Math.PI) / 2;\n"
				+ "  }\n"

				+ "  function zoom(mapPx, worldPx, fraction) {\n"
				+ "    return Math.floor(Math.log(mapPx / worldPx / fraction) / Math.LN2);\n"
				+ "  }\n"

				+ "  var ne = bounds.getNorthEast();\n"
				+ "  var sw = bounds.getSouthWest();\n"

				+ "  var latFraction = (latRad(ne.lat()) - latRad(sw.lat())) / Math.PI;\n"

				+ "  var lngDiff = ne.lng() - sw.lng();\n"
				+ "  var lngFraction = ((lngDiff < 0) ? (lngDiff + 360) : lngDiff) / 360;\n"

				+ "  var latZoom = zoom(mapDim.height, WORLD_DIM.height, latFraction);\n"
				+ "  var lngZoom = zoom(mapDim.width, WORLD_DIM.width, lngFraction);\n"

				+ "  return Math.min(latZoom, lngZoom, ZOOM_MAX);\n"
				+ "}\n"
                + " function openInfoWindow(map,latlng,txt){\n"
                + "   var infowindow = new google.maps.InfoWindow({content: txt});\n"
                + "   var marker = new google.maps.Marker({\n"
                + "   position: latlng,map: map});"
                + "   infowindow.open(map,marker);"
                + "}\n"
                + " function showaddr(latlng) {\n"
                + "  if (latlng) {\n"
                + "   geocoder.geocode({'latLng':latlng}, function(result,status) {\n"
                + "    if(status===google.maps.GeocoderStatus.OK) {\n"
                + "     adr=result[0];\n"
                + "     var txt=adr.formatted_address;\n"
                + "     if(txt==preva) {\n"
                + "      openInfoWindow(map,latlng, txt);\n"
                + "     } else {\n"
                + "      preva = txt;\n" + "     }\n"
                + "    }\n"
                + "   });\n"
                + "  }\n"
                + " }\n"
                + "function latlonTxt(latlon) {\n"
                + " if(latlon) {\n"
                + "  var s=\'<b>");
        l_header.append(I18N.i18n("Last click"));
        l_header.append(": \'+ latlon.toUrlValue()+\' </b>\';\n"
                + "  showaddr(latlon);"
                + "  document.getElementById(\"latlon\").innerHTML = s;\n"
                + " }\n"
                + "}\n"
                + "function latlonFunc() {\n"
                + " return function(overlay,latlon) {latlonTxt(latlon.latLng);};\n"
                + "}\n"
                + "function gotoPt(result,status) {\n"
                + " if(status!==google.maps.GeocoderStatus.OK){\n"
                + "  document.getElementById(\"latlon\").innerHTML=\"");
        l_header.append(I18N.i18n("Location not found"));
        l_header.append(
        		"\";\n"
	            + " } else {\n"
	            + "  map.setCenter(result[0].geometry.location);latlonTxt(result[0].geometry.location);\n"
	            + " }\n"
	            + "}\n"
	            + "\n"
	            + "function gotoAddr(){\n"
	            + " var ad=document.getElementById(\"adr\").value;\n"
	            + " new google.maps.Geocoder().geocode({'address':ad},gotoPt);\n"
	            + "}\n"
	            + "function makeOpenerCaller(i) {"
	            + " return function() { showMarkerInfo(i); };"
	            + "}\n" // open an info window
	            + "function showMarkerInfo(i) {\n"
                + "   var infowindow = new google.maps.InfoWindow({content: infoHtmls[i]});\n"
	            + "   infowindow.open(map,markers[i]);\n"
	            + "}\n" // create the map
	            // Code from http://www.pompage.net/pompe/pieds/
	            + "function getWindowHeight() {\n"
	            + " var windowHeight=0;\n"
	            + " if (typeof(window.innerHeight)==\'number\') {\n"
	            + "  windowHeight=window.innerHeight;\n"
	            + " } else {\n"
	            + "  if (document.documentElement&&"
	            + "document.documentElement.clientHeight) {\n"
	            + "  windowHeight = "
	            + "document.documentElement.clientHeight;\n"
	            + "  } else {\n"
	            + "   if (document.body&&document.body.clientHeight) {\n"
	            + "    windowHeight=document.body.clientHeight;\n"
	            + "   }\n"
	            + "  }\n"
	            + " }\n"
	            + " return windowHeight;\n"
	            + "}\n"
	            + "function setFooter() {\n"
	            + " if (document.getElementById) {\n"
	            + "  var windowHeight=getWindowHeight();\n"
	            + "  var footerElement=document.getElementById(\'footer\');\n"
	            + "  var footerHeight=footerElement.offsetHeight;\n"
	            // + " if (windowHeight-footerHeight>400) {\n"
	            + "   document.getElementById(\'map\').style.height=\n"
	            + "    (windowHeight-footerHeight)+\'px\';\n"
	            // + " } else {\n"
	            // + "
	            // document.getElementById(\'map\').style.height=400;\n"
	            // + " footerElement.style.position=\'static\';\n"
	            // + " }\n" // else
	            + " }\n"
	            + "}\n" // Function
	            + "function trackClick(trk,val) {\n"
	            + " if (val == 1) {\n"
	            + "  trk.setMap(map);\n"
	            + " } else {\n"
	            + "  trk.setMap(null);\n"
	            + " } }\n"
	            + "function makeLatLonInfo(h) {\n"
	            + " return function(latlng) {\n"
	            + "  latlonTxt(latlng.latLng);\n"
	            + "  openInfoWindow(map,latlng.latLng, h);\n"
	            + " };\n"
	            + "}"
	            + " var clickStr; clickStr=\"\";"
	            + " function clickString() {\n"
	            + "  document.write(clickStr);\n"
	            + " }\n"
	
	            // + "var blueIcon = new GIcon(G_DEFAULT_ICON);"
	            // + "blueIcon.image =
	            // \"http://www.google.com/intl/en_us/mapfiles/ms/micons/blue-dot.png\";"
	            // + "markerOptions = { icon:blueIcon };" + // Set up
	            // our GMarkerOptions object
	            + " var mapTypeIds=["
	            + "'OSM',"
	            + "google.maps.MapTypeId.ROADMAP,"
	            + "google.maps.MapTypeId.SATELLITE,"
	            + "google.maps.MapTypeId.HYBRID,"
	            + "google.maps.MapTypeId.TERRAIN];\n"
	            + " var map=new google.maps.Map("
	            + "   document.getElementById(\"map\"),"
	            + "  {\n"
	            + "    center:new google.maps.LatLng(0,0)\n"
	            + "    ,mapTypeControl: true\n"
	            + "    ,mapTypeControlOptions: {mapTypeIds:mapTypeIds}"
	            + "    ,scaleControl:true\n"
	            + "    ,overviewMapControl:true\n"
	            + "    ,rotateControl:true\n"
	            + "    ,panControl:true\n"
	            + "    ,streetViewControl:true\n"
	            + "    ,zoomControl:true\n"
	            + "    ,mapTypeIds:mapTypeIds"
	            + "    ,mapTypeId:google.maps.MapTypeId.ROADMAP"
	            + "  });\n"
	            + "map.mapTypes.set('OSM', new google.maps.ImageMapType({\n"
	            + "    getTileUrl: function(coord, zoom) {\n"
	            + "        return 'http://tile.openstreetmap.org/' + zoom + '/' + coord.x + '/' + coord.y + '.png';\n"
	            + "    },\n"
	            + "    tileSize: new google.maps.Size(256, 256),\n"
	            + "    name: 'OpenStreetMap',\n"
	            + "    maxZoom: 19\n"
	            + "}));\n"
	            /*	            
 *           //Define OSM map type pointing at the OpenStreetMap tile server
	            + "var OSM = new GMapType(\n"
	            + "[ new GTileLayer(null,1,18,\n"
	            + "{ tileUrlTemplate: 'http://tile.openstreetmap.org/{Z}/{X}/{Y}.png',\n"
	            + " isPng: true, opacity: 1.0 })],\n"
	            + "new GMercatorProjection(19),\n"
	            + "'OSM',\n"
	            + "{ errorMessage:\"More OSM coming soon\"}\n"
	            + ");\n"
	            + "var OSMcycle = new GMapType(\n"
	            + "[ new GTileLayer(null,1,15,\n"
	            + "{ tileUrlTemplate: \'http://www.thunderflames.org/tiles/cycle/{Z}/{X}/{Y}.png\',\n"
	            + " isPng: true, opacity: 1.0 })],\n"
	            + "new GMercatorProjection(19),\n"
	            + "\'Cycle\',\n"
	            + "{ errorMessage:\"More OSM coming soon\"}\n"
	            + ");\n"
	            + "var Osmarender = new GMapType(\n"
	            + "[ new GTileLayer(null,1,18,\n"
	            + "{ tileUrlTemplate: \'http://tah.openstreetmap.org/Tiles/tile/{Z}/{X}/{Y}.png\',\n"
	            + " isPng: true, opacity: 1.0 })],\n"
	            + "new GMercatorProjection(19),\n" + "\'Osmardr\',\n"
	            + "{ errorMessage:\"More OSM coming soon\"}\n"
	            + ");\n" + "map.addMapType(OSM);\n"
	            + "map.addMapType(OSMcycle);\n"
	            + "map.addMapType(Osmarender);\n"
*/
	            );
        // "points.push(new GPoint(3.11492833333333,45.75697))";
        // map.addOverlay(new GPolyline(points,"#960000",2,.75));
        writeTxt(l_header.toString());
    }

    private double minlat;
    private double maxlat;
    private double minlon;
    private double maxlon;

    protected final void writeDataHeader() {
        if (isWayType) {
        } else {
            isNewTrack = true;
            minlat = 90;
            maxlat = -90;
            minlon = 180;
            maxlon = -180;
        }
    }

    protected final void endTrack(final String hexColor) {
        final PolylineEncoder a = new PolylineEncoder();
        BT747Hashtable res;
        if (false) {
            res = a.createEncodings(track, 17, 4);
        } else {
            res = a.dpEncode(track);
        }
        String tmp;
        tmp = (String) res.get("encodedPoints");

        if (tmp.length() >= 2) {
            rec.setLength(0);

            // Track assignment
            trackOnClickFuncCalls += "trackClick(track" + trackIndex
                    + ",this.checked);";
            rec.append("var track");
            rec.append(trackIndex);
            rec.append("=new google.maps.Polyline({strokeColor:\"#");
            rec.append(hexColor);
            rec.append("\",\n" + "  strokeWeight: 4,\n"+ "  strokeOpacity: 0.8,geodesic:true"
            		+ ",path:google.maps.geometry.encoding.decodePath(\"");
            rec.append(tmp);
            rec.append("\")});\n");
            rec.append("track");
            rec.append(trackIndex);
            rec.append(".setMap(map);\n");
//            rec.append(";\nmap.addOverlay(");
//            rec.append("track");
//            rec.append(trackIndex);
            //rec.append(res.get("encodedLevels"));
//
  //          rec.append("\",\n" + "  zoomFactor: 2,\n" + "  numLevels: 18\n"
  //                  + "}));\n");

            rec.append("google.maps.event.addListener(track");
            rec.append(trackIndex);
            rec.append(",'click',makeLatLonInfo(\"<b>Track span</b><br/>"
                    + trackStartInfo + "<br/>");
            rec.append("<b>#" + previousRec + "# </b>"
                    + CommonOut.getDateTimeStr(previousTime));
            rec.append("\"));\n");
            // writeTxt(PolylineEncoder.replace(rec.toString(),"\\", "\\\\"));
            writeTxt(rec.toString());
            rec.setLength(0);
        }
        trackIndex++;
        resetTrack();
    }

    private final BT747Hashtable icons = JavaLibBridge
            .getHashtableInstance(10);

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFile#writeDataFooter()
     */
    protected final void writeDataFooter() {
        if (isWayType) {
            if (waypoints.size() != 0) {
                rec.setLength(0);
                rec.append("\nvar baseIcon = new google.maps.Marker();"
                		// + "baseIcon.iconSize = new GSize(32, 32);\n"
                        );
                final BT747Hashtable iter = icons.iterator();
                while (iter.hasNext()) {
                    final Object key = iter.nextKey();
                    rec.append("\nvar ICON");
                    rec.append((String) key);
                    rec.append("={");
                    rec.append("image:'");
                    rec.append((String) iter.get(key));
                    rec.append("'");
                    rec.append("};");
                }

                rec.append("\nvar markers;\nmarkers=[");
                for (int i = 0; i < waypoints.size(); i++) {
                	rec.append("\nnew google.maps.Marker({");
                    if (((String) iconList.elementAt(i)).length() != 0) {
                        rec.append("icon:");
                        rec.append(iconList.elementAt(i));
                        rec.append(".image,");
                    }
                    rec.append("map:map,position:new google.maps.LatLng(");
                    rec.append(JavaLibBridge.toString(waypoints.get(i)
                            .getLatDouble(), 7));
                    rec.append(',');
                    rec.append(JavaLibBridge.toString(waypoints.get(i)
                            .getLonDouble(), 7));
                    rec.append(')');
                    
                	rec.append("}),"); // Last char must be ',' to erase it
                    // below
                }
                rec.setCharAt(rec.length() - 1, ']'); // Delete last ','
                rec.append(";\n infoHtmls=[\n");
                rec.append(infoHtmls.toString());
                rec.append("];\n");
                rec.append("for (var i=0; i<markers.length; ++i) {\n");
                rec.append("google.maps.event.addListener(markers[i],\'click\',makeOpenerCaller(i));\n");
                rec.append("}\n");
                /* TODO: make code functional - currently issues with marker initialisation.
	            rec.append()"var mgr = new MarkerManager(map);\n"):
				rec.append("        google.maps.event.addListener(mgr, 'loaded', function () {\n"
						+ "                mgr.addMarkers(markers);\n"
						+ "            mgr.refresh();\n" + "        });\n");
				*/
                rec.append("\n");
                // Small popup:
                writeTxt(rec.toString());
                rec.setLength(0);
                waypoints.removeAll();
                infoHtmls.setLength(0);
                iconList.removeAllElements();
            }
            resetTrack();
        } else {
            endTrack(goodTrackColor);
            splitOrEndTrack();
        }
    }

    private final void splitOrEndTrack() {
        final StringBuffer lrec = new StringBuffer();
        if (!isWayType && (trackOnClickFuncCalls.length() != 0)) {
            lrec.setLength(0);
            // Vm.debug("Do:"trackDescription);

            lrec.append("clickStr+= \"" + trackDescription
                    + "<input type=\\\"checkbox\\\"" + "onClick=\\\""
                    + trackOnClickFuncCalls + "\\\" checked/>\";\n");
            writeTxt(lrec.toString());
            trackOnClickFuncCalls = "";
            trackDescription = "";
        }
    }

    /**
     * UTC time of previous record.
     */
    private int previousTime = 0;
    /**
     * Record number of previous record
     */
    private int previousRec = 0;

    /*
     * (non-Javadoc)
     * 
     * @see gps.GPSFile#WriteRecord()
     */
    public final void writeRecord(final GPSRecord r) {
        super.writeRecord(r);


            if (!ptFilters[currentFilter].doFilter(r)) {
                // The track is interrupted by a removed log item.
                // Break the track in the output file
                if (!isWayType && !isNewTrack && !firstRecord
                        && !ignoreBadPoints) {
                    isNewTrack = true;
                    if (track.size() != 0) {
                        final Trackpoint tp = track.get(track.size() - 1);
                        endTrack(goodTrackColor);
                        track.addTrackpoint(tp);
                        trackStartInfo = "<b>#" + previousRec + "# </b>"
                                + CommonOut.getDateTimeStr(previousTime);
                    }
                    // "points.push(new GPoint(3.11492833333333,45.75697))";
                    // map.addOverlay(new GPolyline(points,"#960000",2,.75));
                }
                if (!isWayType && cachedRecordIsNeeded(r)) {
                    // Update map boundaries
                    if (r.getLatitude() < minlat) {
                        minlat = r.getLatitude();
                    }
                    if (r.getLatitude() > maxlat) {
                        maxlat = r.getLatitude();
                    }
                    if (r.getLongitude() < minlon) {
                        minlon = r.getLongitude();
                    }
                    if (r.getLongitude() > maxlon) {
                        maxlon = r.getLongitude();
                    }
                }
            } else {
                // This log item is to be transcribed in the output file.

                if (!isWayType) {
                    rec.setLength(0);
                    if (isNewTrack || needsToSplitTrack) {
                        isNewTrack = false;
                        if ((r.hasLatitude())
                                && (r.hasLongitude())) {
                            if (!needsToSplitTrack) {
                                track.addTrackpoint(new Trackpoint(
                                        r.getLatitude(), r.getLongitude()));
                                if ((r.hasUtc())) {
                                    previousTime = r.getUtc();
                                    previousRec = r.recCount;
                                }
                                endTrack(badTrackColor);
                            } else {
                                // points quite separated -
                                // No line, but separate track
                                // bt747.sys.Vm.debug(""+(s.utc-previousTime)+":"+isTimeSPlit);
                                endTrack(goodTrackColor);
                                splitOrEndTrack();
                            }
                        }
                        resetTrack();
                        // Vm.debug("Pos:"+getTimeStr(s));
                        trackStartInfo = "<b>#" + r.recCount + "# </b>"
                                + CommonOut.getDateTimeStr(r, t);
                        if (trackDescription.length() == 0) {
                            trackDescription = CommonOut.getDateTimeStr(
                                    r, t);
                            // bt747.sys.Vm.debug(trackDescription);
                        }
                    }
                }

                if ((r.hasUtc())) {
                    previousTime = r.getUtc();
                    previousRec = r.getRecCount();
                }
                // " <wpt lat=\"39.921055008\" lon=\"3.054223107\">"+
                // " <ele>12.863281</ele>"+
                // " <time>2005-05-16T11:49:06Z</time>"+
                // " <name>Cala Sant Vicen� - Mallorca</name>"+
                // " <sym>City</sym>"+
                // " </wpt>"+

                // lat="latitudeType [1] ?"
                // lon="longitudeType [1] ?">
                // if (m_isWayType) {
                // rec.append("<wpt ");
                // } else {
                // rec.append("<trkpt ");
                // }
                if ((r.hasLatitude())
                        && (r.hasLongitude())) {
                    // rec.append("points.push(new google.maps.LatLng(");
                    // rec.append(JavaLibBridge.toString(s.latitude,6));
                    // rec.append(',');
                    // rec.append(JavaLibBridge.toString(s.longitude,6));
                    // rec.append("));");
                    final Trackpoint tp = new Trackpoint(r.getLatitude(),
                            r.getLongitude());
                    //                    
                    track.addTrackpoint(tp);

                    // Update map boundaries
                    if (tp.getLatDouble() < minlat) {
                        minlat = tp.getLatDouble();
                    }
                    if (tp.getLatDouble() > maxlat) {
                        maxlat = tp.getLatDouble();
                    }
                    if (tp.getLonDouble() < minlon) {
                        minlon = tp.getLonDouble();
                    }
                    if (tp.getLonDouble() > maxlon) {
                        maxlon = tp.getLonDouble();
                    }
                }

                if (isWayType && (r.hasLatitude())
                        && (r.hasLongitude())) {
                    waypoints.addTrackpoint(new Trackpoint(r.getLatitude(),
                            r.getLongitude()));
                    infoHtmls.append("\"");
                    final String rcrStr = CommonOut.getRCRstr(r);
                    String icon = "";
                    if (icons.get(rcrStr) == null) {
                        WayPointStyle style;
                        if ((rcrStr.length() > 0) && (rcrStr.charAt(0) == 'X')) {
                            style = CommonOut.getWayPointStyles().get(
                                    rcrStr.substring(1));
                        } else if (rcrStr.length() > 1) {
                            style = CommonOut.getWayPointStyles().get("M");
                        } else {
                            style = CommonOut.getWayPointStyles().get(rcrStr);
                        }
                        if (style != null) {
                            final String url = style.getIconUrl();
                            icons.put(rcrStr, url);
                            icon = "ICON" + rcrStr;
                        }
                    } else {
                        icon = "ICON" + rcrStr;
                    }
                    iconList.addElement(icon);
                    CommonOut.getHtml(infoHtmls, r,
                            selectedFileFields, t, recordNbrInLogs, imperial);
                    infoHtmls.append("\",\n");
                }

                writeTxt(rec.toString());
                rec.setLength(0);

            }
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.GPSFile#FinaliseFile()
     */
    public final void finaliseFile() {
        if (isOpen()) {
            String footer;
            writeDataFooter();
            footer = "clickString();\n"
                    + "google.maps.event.addListener(map,\'click\',latlonFunc());\n"
                    + "function initial() {"
                    + " map.setCenter(new google.maps.LatLng("
                    + JavaLibBridge.toString((maxlat + minlat) / 2)
                    + ","
                    + JavaLibBridge.toString((maxlon + minlon) / 2)
                    + "));\n"
                    + "var mapDim = document.getElementById(\'map\').getBoundingClientRect();\n"
                    + " map.setZoom(\n"
                    + "getBoundsZoomLevel(new google.maps.LatLngBounds(new google.maps.LatLng("
                    + minlat + ',' + minlon
                    + "),new google.maps.LatLng("
                    + maxlat
                    + ','
                    + maxlon
                    + "))\n"
                    + ",mapDim)\n"
                    + ");"
                    + "\n"
                    + "};\n"
                    + " setFooter();\n"
                    + " initial();\n"
                    //+ " map.enableContinuousZoom();\n"
                    //+ " map.enableDoubleClickZoom();\n"
                    + "\n"
                    + "//]]>\n"
                    + "</script>\n"
                    + " </div>\n"
                    + "</body>\n" + "</html>";
            writeTxt(footer);
        }
        super.finaliseFile();

    }
}
