<?php

// config.template.php
//
// Create 'config.php' where you can uncomment some of the lines below
// to personalize your server.
//

// Page title for current position
//$MYLIEU_SITE_TITLE_WHERE_IS_X="BT747 WHERE IS ";

// Page title for history
$MYLIEU_SITE_TITLE_WHERE_WAS_X="BT747 WHERE WAS ";

// Name/identification of person/object followed.
$MYLIEU_WHO="X";

// The site key for showing the googlemap.
// You should be able to get it on
// http://code.google.com/intl/fr/apis/maps/signup.html .
$GMAP_KEY="GOOGLEMAPKEY";

// If not 0, debug is active
$MYLIEU_DEBUG=0;

// Subdirectory for the logs (for instance 'log/')
// Must include the '/', otherwise it is a prefix.
$MYLIEU_LOGDIRECTORY="./";

// Filename for the debug log.
$MYLIEU_DEBUGLOG=$MYLIEU_LOGDIRECTORY."debug.log";

// Timezone as allowed in date_default_timezone_set
// Used for date format
$MYLIEU_TIMEZONE=date_default_timezone_get();
//$MYLIEU_TIMEZONE="UTC";


?>