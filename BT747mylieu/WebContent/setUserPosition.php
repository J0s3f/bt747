<?php 

/*
 * setUserPosition.php
 * 
 * Part of the project myLieu from Markus Schepp for c't.
 * 03.04.2009
 * 
 * Storing data as trackpoints.
 * Karl Linne, 05.06.2009
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

// check, if we got a client, which can not convert into dezimal format.
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
	
	// check, if we got a client, which can not convert into dezimal format.
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



// add an entry in an single-node xml-file
$myFile = 'geodata.xml';

$myDayString = date("Y_m_d");

// the dayfile should be in the format 2009_03_23_geodata.xml
$myDayFile = $myDayString.'_geodata.xml';
$myDayFileGPX = $myDayString.'_geodata.gpx';

// create both file-entries
writeXMLfile(false, $myFile, $myLatitude, $myLongitude, $mySpeed, $myAltitude, $myDirection);
writeGPXfile(true, $myDayFileGPX, $myLatitude, $myLongitude, $mySpeed, $myAltitude, $myDirection);
writeXMLfile(true, $myDayFile, $myLatitude, $myLongitude, $mySpeed, $myAltitude, $myDirection);



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
					  $aSpeed, 
					  $anAltitude, 
					  $aDirection)
{
	$doc = new DomDocument("1.0", "iso-8859-1");

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
	
	$myLieu = getdate();
	$myMonth = $myLieu['mon'];
	if ($myMonth < 10)
	{
		$myMonth = "0".$myMonth;
	}
	$myDays = $myLieu['mday'];
	if ($myDays < 10)
	{
		$myDays = "0".$myDays;
	}
	$myHours = $myLieu['hours'];
	if ($myHours < 10)
	{
		$myHours = "0".$myHours;
	}
	$myMinutes = $myLieu['minutes'];
	if ($myMinutes < 10)
	{
		$myMinutes = "0".$myMinutes;
	}
	$mySeconds = $myLieu['seconds'];
	if ($mySeconds < 10)
	{
		$mySeconds = "0".$mySeconds;
	}
	$myTime = $myLieu['year']."-".$myMonth."-".$myDays."T".$myHours.":".$myMinutes.":".$mySeconds.".000Z+1:00";
	$element->setAttribute('time', $myTime);
	$element->setAttribute('lat', $aLatitude);
	$element->setAttribute('lng', $aLongitude);
	$element->setAttribute('speed', $aSpeed);
	$element->setAttribute('alt', $anAltitude);
	$element->setAttribute('dir', $aDirection);
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
					  $aSpeed, 
					  $anAltitude, 
					  $aDirection)
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
	
	
	$myLieu = getdate();
	$myMonth = $myLieu['mon'];
	if ($myMonth < 10)
	{
		$myMonth = "0".$myMonth;
	}
	$myDays = $myLieu['mday'];
	if ($myDays < 10)
	{
		$myDays = "0".$myDays;
	}
	$myHours = $myLieu['hours'];
	if ($myHours < 10)
	{
		$myHours = "0".$myHours;
	}
	$myMinutes = $myLieu['minutes'];
	if ($myMinutes < 10)
	{
		$myMinutes = "0".$myMinutes;
	}
	$mySeconds = $myLieu['seconds'];
	if ($mySeconds < 10)
	{
		$mySeconds = "0".$mySeconds;
	}
	$myTime = $myLieu['year']."-".$myMonth."-".$myDays."T".$myHours.":".$myMinutes.":".$mySeconds.".000Z+1:00";
	
    // create trk-element, if not already there
    $trksegelement = $doc->getElementsbyTagName('trkseg')->item(0);
    if (!$trksegelement)
    {
	    $trkelement = $doc->createElement('trk');
	    $root->appendChild($trkelement);
	    $trksubElement = $doc->createElement('name', 'myLieu-'.substr($myTime,0,10));
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
	
	$subElement = $doc->createElement('time', $myTime);
	$element->appendChild($subElement);
	$subElement = $doc->createElement('ele', $anAltitude);
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
		// calculation
		$myLat_dezimal = ($second/10000 + $minute)/60 + $degreePart;
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
	// calculation
	$myLat_dezimal = ($second/10000 + $minute)/60 + $degreePart;
	return $myLat_dezimal;
}

?>