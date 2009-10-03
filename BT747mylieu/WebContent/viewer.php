<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" 
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"  xmlns:v="urn:schemas-microsoft-com:vml">
<head>

<!-- http://www.lindhorst.cc/tag/iphone-ipod-touch -->
	<meta name="apple-mobile-web-app-capable" content="yes"/>
	
	<meta http-equiv="content-type" content="text/html; charset=utf-8"/>
	<title>Wi ist -flo-</title>
	<script src="prototype.js" type="text/javascript"></script>
	<script src="main.js" type="text/javascript"></script>
	<script src="http://maps.google.com/jsapi?key=ABQIAAAADFJoSTzQCLixbHnZMg9AvxR6YUi2YfTiQhP3zYSLu6ra0IkJEBRLpEmSREA4fB4w333HzzpyN8brDA" type="text/javascript"></script>
	<script src="http://www.openlayers.org/api/OpenLayers.js"></script>
	<script src="http://www.openstreetmap.org/openlayers/OpenStreetMap.js"></script>
	<link href="myLieu.css" rel="stylesheet" type="text/css" />
	<script src="myGeoEngine.js" type="text/javascript"></script>
	<script src="myLieu.js" type="text/javascript"></script>
</head>
<body onLoad="onViewLoad()">
<div id="map"></div>
<div id="downloadLink"></div>
<div id="routeChoser">Your tracks:<br/>
<?php

// check actual directory
$dir = ".";
$dh  = opendir($dir);

// store all found files into an array
while (false !== ($filename = readdir($dh))) 
{
	$files[] = $filename;
}

rsort($files);

echo "<select  name=\"routeChoserCombo\" onChange=\"routeChosing(this.options[this.selectedIndex].value)\">\n";
echo "<option value=\"0\">choose track</option>\n";
foreach($files as $f)
{
	if(strpos($f, '_geodata.xml') !== false)
	{
		echo "<option value=\"".substr($f,0,strlen($f))."\">".substr($f,0,strlen($f)-12)."</option>\n";
	}
}
?>
</div>
</body>
</html>
