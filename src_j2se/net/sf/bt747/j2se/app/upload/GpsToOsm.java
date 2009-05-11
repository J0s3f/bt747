/**
 * 
 */
package net.sf.bt747.j2se.app.upload;

/** This will upload a GPX file to the OSM
 * @author Mario
 *
 */
public class GpsToOsm {
//    You can upload a GPX file through the API:
//
//        POST /api/0.5/gpx/create
//
//        This is the only API call using the POST method. It expects the following POST parameters in a multipart/form-data HTTP message:
//        parameter       description
//        file    The GPX file containing the track points. Note that for successful processing, the file must contain trackpoints (<trkpt>), not only waypoints, and the trackpoints must have a valid timestamp. Since the file is processed asynchronously, the call will complete successfully even if the file cannot be processed. The file may also be a .tar, .tar.gz or .zip containing multiple gpx files, although it will appear as a single entry in the upload log.
//        description     The trace description.
//        tags    A string containing tags for the trace.
//        public  1 if the trace is public, 0 if not.
//
//        HTTP basic authentication is required.
//
//        You access a GPX file's details and download the full file:
//
//        GET /api/0.5/gpx/<id>/details
//        GET /api/0.5/gpx/<id>/data
//
//        HTTP basic authentication is required, although theoretically these calls should be allowed without authentication if the trace is marked public. If the trace is not public, only the owner may access the data.
//
//        Example "details" response:
//
//        <?xml version="1.0" encoding="UTF-8"?>
//        <osm version="0.5" generator="OpenStreetMap server">
//          <gpx_file id="38698" 
//            name="rathfarnham_churchtown_nutgrove.gpx" lat="53.285644054" lon="-6.238367558" 
//            user="robfitz" public="true" pending="false" 
//            timestamp="2007-09-13T23:28:41+01:00"/>
//        </osm>
//
//        The "data" response will be the exact file uploaded. 

}
