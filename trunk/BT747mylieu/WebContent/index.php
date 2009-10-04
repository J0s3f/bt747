<?php
include "defaults.php";
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:v="urn:schemas-microsoft-com:vml" >
<head>

<!-- http://www.lindhorst.cc/tag/iphone-ipod-touch -->
<meta name="apple-mobile-web-app-capable" content="yes" />
<meta http-equiv="content-type" content="text/html; charset=utf-8" />

<?php
print "<title>".$MYLIEU_SITE_TITLE_WHERE_IS_X.$MYLIEU_WHO."</title>";
?>

<script src="prototype.js" type="text/javascript"></script>
<script src="main.js" type="text/javascript"></script>
<?php GmapMgmt::gmapScript(); ?>
<script src="http://www.openlayers.org/api/OpenLayers.js"
	type="text/javascript"></script>
<script src="http://www.openstreetmap.org/openlayers/OpenStreetMap.js"
	type="text/javascript"></script>
<link href="myLieu.css" rel="stylesheet" type="text/css" />
<script src="myGeoEngine.js" type="text/javascript"></script>
<script src="myLieu.js" type="text/javascript"></script>
</head>
<body onload="onLoad(1)" onresize="onResize()" onunload="onUnLoad()">
<div id="map"></div>
<div id="message"><textarea id="message_text" name="message_text"
	rows="10" cols="30" readonly="readonly">.:myLieu:.</textarea>
	</div>
<div id="status_area">
<img src="red.jpg" width="30" alt="red" />
</div>
</body>
</html>
