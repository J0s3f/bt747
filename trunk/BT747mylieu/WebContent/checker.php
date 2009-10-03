<?php
include "defaults.php"; 

/*
 * checker.php
 * 
 * Part of the project myLieu from Markus Schepp for c't.
 * 24.04.2009
 * 
 * This file is used in conjunction with Ajax-calls to precheck,
 * if myLieu will run on this server.
 * 
 */

// show every warning, error, info
error_reporting(0);

$myAction = "";

// check, if we got a client, which can not convert into dezimal format.
if (isset($_GET['action']))
{
	$myAction = $_GET['action'];
}


switch ($myAction)
{
	// check with this action, if the server has got the phpversion,
	// we need. Do parsing on clientside.
	case "phpversion":
		echo phpversion();
		break;

	// checks, if we are able to create a file on the sever
	// we need that for saving  the geodata
	case "filecreatepermission":
		$myFileName = "testFile.txt";
		$myFileHandle = fopen($myFileName, 'w');
		if (!$myFileHandle)
		{
			echo "0";	
		}
		else
		{
			fclose($myFileHandle);
			// immediately delete the file, we have created
			unlink($myFileHandle);
			echo "1";
		}
		break;
		
	// returns 1 or 0 depending on if the class
	// DomDocument is available
	case "isDomDocument":
		if (class_exists('DomDocument'))
		{
			echo "1";
		}
		else
		{
			echo "0";
		}
		break;
		
	case "isDomXmlOpenFile":
		if (function_exists('domxml_open_file'))
		{
			echo "1";
		}
		else
		{
			echo "0";
		}
		break;
		case "gmap":
		GmapMgmt::gmapScript();
		break;
		
	default:
		echo "call this script with<br><br>";
		echo "<b>checker.php?action=&lt;checkerAction&gt;<br><br></b>";
		echo "checkerActions:<br>";
		echo "phpversion<br>";
		echo "filecreatepermission<br>";
		echo "isDomDocument<br>";
		echo "isDomXmlOpenFile<br>";
        echo "gmap:<br>";
		break;
}

?>