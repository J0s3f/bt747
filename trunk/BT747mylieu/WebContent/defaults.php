<?php

// Singleton to keep GoogleMap key and generate initialisation code for Gmaps.
class GmapMgmt {
  public static $gmapKey = "DUMMYKEY";
  // Set the GoogleMap key
  public static function setKey($key) {
    GmapMgmt::$gmapKey=$key;
  }
  // Generate the Gmap script initialisation
  public static function gmapScript() {
     print '<script src="http://maps.google.com/jsapi?key='.GmapMgmt::$gmapKey.'" type="text/javascript"></script>';
  }  
}


$MYLIEU_SITE_TITLE_WHERE_IS_X="BT747 WHERE IS ";
$MYLIEU_SITE_TITLE_WHERE_WAS_X="BT747 WHERE WAS ";
$MYLIEU_WHO="X";
$GMAP_KEY="GOOGLEMAPKEY";
$MYLIEU_LOGDIRECTORY="./";
$MYLIEU_DEBUG=0;
$MYLIEU_DEBUGLOG="debug.log";


// Get USER specific settings
try {
if(file_exists("config.php")) {
 include "config.php"; }
} catch  (Exception $e) {
}

// Some of these settigns must be propagated.
GmapMgmt::setKey($GMAP_KEY);

?>
