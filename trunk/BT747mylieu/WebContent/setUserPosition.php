<?php 
include "defaults.php";

/*
 * setUserPosition.php
 * 
 * Part of the project myLieu from Markus Schepp for c't.
 * 03.04.2009
 * 
 * Storing data as trackpoints.
 * Karl Linne, 05.06.2009
 * 
 * Updates by Mario De Weerd
 * Updates by Fabian Schonack to add fields and debug log.
 * 
 * This file is used to tell a webserver position data of a person.
 * You need to call this with the parameters:
 * @parameter longitude
 * must be given: longitude in dezimal degree
 * @parameter latitude
 * must be given: latitude in dezimal degree
 * @parameter speed
 * optional: km/h
 * @paramter dir
 * optional: direction in degree
 * @parameter alt
 * optional: altitude in meter
 * 
 */


// show every warning, error, info
error_reporting(E_ALL);
if($MYLIEU_DEBUG!=0) {
  debugLog();
}
  
function debugLog() {
//*************DEBUG-LOG
// write the string send to the server into an logfile

$now=getdate(date("U"));

//print("$now[weekday], $now[month] $now[mday], $now[year]");
//echo $_SERVER['QUERY_STRING'];

$fh = fopen($MYLIEU_DEBUGLOG, 'a') or die("can't open file $MYLIEU_DEBUGLOG");

$stringData = ("[$now[year]-$now[month]-$now[mday] $now[hours]:$now[minutes]:$now[seconds]] ");
 fwrite($fh, $stringData);
$stringData = $_SERVER['QUERY_STRING'];
 fwrite($fh, $stringData);
$stringData = "\n";
 fwrite($fh, $stringData);
fclose($fh);
}

//DEBUG-LOG*************/

// check, if we got a client, which can not convert into decimal format.
if (isset($_GET['longitude_raw']))
{
	$myLongitude_raw = $_GET['longitude_raw'];
	
	// check, if it is empty
	if ($myLongitude_raw != "")
	{
		$myLongitude = raw2long_dez($myLongitude_raw);
		echo $myLongitude;
	}
}


// try to get the longitude. This parameter is MUST
if (isset($_GET['longitude']))
{
	$myLongitude = $_GET['longitude'];
}

if  ($myLongitude == "")
{
	// leave it here
	exit;
}

// here we receive instead the latitude in degree, minute and second?
if (isset($_GET['latitude_raw']))
{
	$myLatitude_raw = $_GET['latitude_raw'];
	
	// check, if we got a client, which can not convert into decimal format.
	if ($myLatitude_raw != "")
	{
		$myLatitude = raw2lat_dez($myLatitude_raw);
	}
}


// try to get the latitude. This parameter is MUST
if (isset($_GET['latitude']))
{
	$myLatitude = $_GET['latitude'];
}

if ($myLatitude == "")
{
	// leave it here
	exit;
}

if (isset($_GET['time'])) {
	$myTime = $_GET['time'];
} else {
    $orgZone=date_default_timezone_get();
    date_default_timezone_set($MYLIEU_TIMEZONE);
	$myTime = date("c");
	date_default_timezone_set($orgZone);
}

if (isset($_GET['speed']))
{
	$mySpeed = $_GET['speed'];
}
else
{
	$mySpeed = "0";
}

if (isset($_GET['alt']))
{
	$myAltitude = $_GET['alt'];
}
else
{
	$myAltitude = "0";
}

if (isset($_GET['dir']))
{
	$myDirection = $_GET['dir'];
}
else
{
	$myDirection = "0.0";
}
if (isset($_GET['numsat']))
{
	$myNumsat = $_GET['numsat'];
}
else
{
	$myNumsat = "0";
}
if (isset($_GET['hdop']))
{
	$myHdop = $_GET['hdop'];
}
else
{
	$myHdop = "0.0";
}
if (isset($_GET['user']))
{
	$myUser = $_GET['user'];
}
else
{
	$myUser = "unknown";
}
if (isset($_GET['btaddr']))
{
	$myBt_addr = $_GET['btaddr'];
}
else
{
	$myBt_addr = "00:00:00:00:00";
}

// add an entry in an single-node xml-file
$myFile = $MYLIEU_LOGDIRECTORY.'geodata.xml';

$myDayString = date("Y_m_d",strtotime($myTime));
if(!($myUser === "")) {
  $myDayString.='_'.$myUser;
}

// the dayfile should be in the format 2009_03_23_geodata.xml
$myDayFile = $MYLIEU_LOGDIRECTORY.$myDayString.'_'.'geodata.xml';
$myDayFileGPX = $MYLIEU_LOGDIRECTORY.$myDayString.'_'.'geodata.gpx';

// create both file-entries
writeXMLfile(false, $myFile, $myLatitude, $myLongitude, $myTime, $mySpeed, $myAltitude, $myDirection, $myNumsat, $myHdop, $myBt_addr, $myUser);
writeGPXfile(true, $myDayFileGPX, $myLatitude, $myLongitude, $myTime, $mySpeed, $myAltitude, $myDirection, $myNumsat, $myHdop);
writeXMLfile(true, $myDayFile, $myLatitude, $myLongitude, $myTime, $mySpeed, $myAltitude, $myDirection, $myNumsat, $myHdop, $myBt_addr, $myUser);



/**
 * Writes data into an XML-file.
 * @param $bAppend
 * true: appends data into existing file
 * false: creates a new file
 * @param $aFile
 * the name of the xml-file
 * @param $aLatitude
 * a latitude-value
 * @param $aLongitude
 * a longitude-value
 * @param $aSpeed
 * a speed-value
 * @param $anAltitude
 * an altitude-value
 * @param $aDirection
 * a direction-value
 */
function writeXMLfile($bAppend, 
					  $aFile, 
					  $aLatitude, 
					  $aLongitude, 
						$aTime,
					  $aSpeed, 
					  $anAltitude, 
					  $aDirection,
					  $aNumsat,
					  $aHdop,
					  $aBt_addr,
					  $aUser)
{
	$doc = new DomDocument("1.0", "iso-8859-1");

	if (!$doc)
	{
		echo "error creating dom document";
		return;
	}
	
	if (file_exists($aFile))
	{
			$doc->load($aFile);
	if (!$bAppend)
	{
		// We want to replace existing data for the user
	    $markers = $doc->documentElement;
       if ($markers->hasChildNodes()) {
           foreach ($markers->childNodes as $m) {
              if ($m->nodeType == XML_ELEMENT_NODE) {
                 if($m->hasAttribute('user')) {
    		        $node_user = $m->getAttribute('user');
    		        if("$node_user" === "$aUser") {
    		           $markers->removeChild($m);
    		           break;
    		        }
                 }
               }
           }		
	   }
    }
    }
	// make the XML-file human-readable
	$doc->preserveWhiteSpace = false;
	$doc->formatOutput = true;

	// check, if we got a root element
	if (!$doc->documentElement)
	{
		$root = $doc->createElement("markers");
		if (!$root)
		{
			echo "error fetching root";
			return;
		}
		$doc->appendChild($root);
	}
	else
	{
		$root = $doc->documentElement;
	}
			
	$element = $doc->createElement("marker");
	if (!$element)
	{
		echo "error creating element\n";
	}
	
	
	$element->setAttribute('time', $aTime);
	$element->setAttribute('lat', $aLatitude);
	$element->setAttribute('lng', $aLongitude);
	$element->setAttribute('speed', $aSpeed);
	$element->setAttribute('alt', $anAltitude);
	$element->setAttribute('dir', $aDirection);
	$element->setAttribute('sat', $aNumsat);
	$element->setAttribute('hdop', $aHdop);
	$element->setAttribute('Bt_addr', $aBt_addr);
	$element->setAttribute('user', $aUser);
	$root->appendChild($element);

	// save it
	$doc->save($aFile);
}

/**
 * Writes data into an GPX-file.
 * @param $bAppend
 * true: appends data into existing file
 * false: creates a new file
 * @param $aFile
 * the name of the xml-file
 * @param $aLatitude
 * a latitude-value
 * @param $aLongitude
 * a longitude-value
 * @param $aSpeed
 * a speed-value
 * @param $anAltitude
 * an altitude-value
 * @param $aDirection
 * a direction-value
 */
function writeGPXfile($bAppend, 
					  $aFile, 
					  $aLatitude, 
					  $aLongitude, 
						$aTime,
					  $aSpeed, 
					  $anAltitude, 
					  $aDirection,
					  $aNumsat,
					  $aHdop)
{
	$doc = new DomDocument("1.0", "UTF-8");

	if (!$doc)
	{
		echo "error creating dom document";
		return;
	}
	
	if ($bAppend == true)
	{
		// we want to append to existing data in a file
		// check before, if it exists!
		if (file_exists($aFile))
		{
			$doc->load($aFile);
		}
	}
	
	// make the XML-file human-readable
	$doc->preserveWhiteSpace = false;
	$doc->formatOutput = true;

	// check, if we got a root element
	if (!$doc->documentElement)
	{
		$root = $doc->createElement("gpx");
		if (!$root)
		{
			echo "error fetching root";
			return;
		}
		$doc->appendChild($root);
	}
	else
	{
		$root = $doc->documentElement;
	}
	
	// adding some attributes for the root
	$root->setAttribute('xmlns', "http://www.topografix.com/GPX/1/1");
	$root->setAttribute('creator', "myLieu");
	$root->setAttribute('version', "1.1");
	$root->setAttribute('xmlns:xsi', "http://www.w3.org/2001/XMLSchema-instance");
	$root->setAttribute('xsi:schemaLocation', "http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd");
	
	
    // create trk-element, if not already there
    $trksegelement = $doc->getElementsbyTagName('trkseg')->item(0);
    if (!$trksegelement)
    {
	    $trkelement = $doc->createElement('trk');
	    $root->appendChild($trkelement);
	    $trksubElement = $doc->createElement('name', 'myLieu-'.substr($aTime,0,10));
	    $trkelement->appendChild($trksubElement);
	    
	    // create a trkseg-element
	    $trksegelement = $doc->createElement('trkseg');
	    $trkelement->appendChild($trksegelement);
    }
    
	// now create a new track-point in between the track-segment    
	$element = $doc->createElement("trkpt");
	if (!$element)
	{
		echo "error creating element\n";
	}

	$element->setAttribute('lat', $aLatitude);
	$element->setAttribute('lon', $aLongitude);
	
	$trksegelement->appendChild($element);
	
	$subElement = $doc->createElement('time', $aTime);
	$element->appendChild($subElement);
	$subElement = $doc->createElement('ele', $anAltitude);
	$element->appendChild($subElement);
	$subElement = $doc->createElement('speed', $aSpeed);
	$element->appendChild($subElement);
	$subElement = $doc->createElement('course', $aDirection);
	$element->appendChild($subElement);
	$subElement = $doc->createElement('hdop', $aHdop);
	$element->appendChild($subElement);
	$subElement = $doc->createElement('sat', $aNumsat);
	$element->appendChild($subElement);
	

	// save it
	$doc->save($aFile);
}

/*
 * This one converts a latitude given in degree, minute and second into the dezimal format
 * we need for google maps e.g.
 */
function raw2lat_dez($degree)
{ 
	$myLat_dezimal = 0;
	
	// we got e.g. 5028.2135 and need both for calculation
	$degreeParts=explode(".",$degree);
	
	if (count($degreeParts))
	{
		// take the first 2 digits from the part before the point as degree
		$degreePart=substr($degreeParts[0],0,2);
		$minute=substr($degreeParts[0],2,2);
		$second=$degreeParts[1];
        $iNumDigits=count($degreeParts[1]);
        // calculation
        $myLat_dezimal = ($second/pow(10,$iNumDigits) + $minute)/60 + $degreePart;
	}
	return $myLat_dezimal;
}

/*
 * This one converts a longitude given in degree, minute and second into the dezimal format
 * we need for google maps e.g.
 */
function raw2long_dez($degree)
{ 
	// we got e.g. 5028.2135 and need both for calculation
	$degreeParts=explode(".",$degree);
	// take the first 2 digits from the part before the point as degree
	$degreePart=substr($degreeParts[0],0,3);
	$minute=substr($degreeParts[0],3,2);
	$second=$degreeParts[1];  
    $iNumDigits=count($degreeParts[1]);
    // calculation
    $myLat_dezimal = ($second/pow(10,$iNumDigits) + $minute)/60 + $degreePart;
	return $myLat_dezimal;
}

?>
