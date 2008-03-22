/*
 * ufolib - Full unicode font range for Superwaba
 * Copyright (C) 2002 Oliver Erdmann
 * http://jdict.sf.net 
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License Version 2.1 as published by the Free Software Foundation
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

/*
 * This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/).
 */

package ufolib.fontizer;

import java.io.*;

// XML
import dom.*;
import org.apache.xerces.dom.TextImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * 
 *
 */
public class ProfileManager {

    // Profile data
    private FontRange m_range;
    private String m_profileComment;

    // misc
    private String m_filename;   // XML filename


    // ****************************************************************
    // public methods

    public ProfileManager() {
	m_range = new FontRange();
	m_range.addRange( 0, 1 );
	m_range.addRange( 4, 7 );
	m_range.addRange( 12, 60 );
    }

    /**
     * open profile file
     *
     * @param name filename
     * @return false, if it fails
     */
    public boolean openFile( String name ) {
	m_filename = name;
	return doImport();
    }


    /**
     * save profile file
     *
     * @param name filename
     * @return false, if it fails
     */
    public boolean saveFile( String name ) {
	m_filename = name;
	return doExport();
    }


    public String getProfileComment() {
	return m_profileComment;
    }

    public void setProfileComment( String comm ) {
	m_profileComment = comm;
    }

    /**
     * Get the ranges
     */
    public FontRange range() {
	return m_range;
    }


    // *****************************************************************
    // XML read

    /**
     * read XML file
     */
    private boolean doImport() {
	if ( m_filename != null ) {
	    return gotoParser();
	}
	return false;
    }

    
    // *************************************************************************
    // The XML parser

    /** Default parser name. */
    private static final String
	DEFAULT_PARSER_NAME = "dom.wrappers.DOMParser";
    

    private boolean runParser( Document doc ) {
	Element elem;
	int r1, r2;
	boolean success = true;

	// read data
	FontRange fontrange = new FontRange();
	String profileComment = null;

	// get data
	NodeList listRR = doc.getElementsByTagName( "RANGES" );
	if ( listRR.getLength() > 0 ) {
	    // "RANGES" found
	    Node nodeRR = listRR.item( 0 );  // there should only be one RANGES
	    // every element in RANGES...
	    NodeList listR = ( (Element) nodeRR ).getElementsByTagName( "RANGE" );
	    int cListR = listR.getLength();
	    for ( int index = 0; ( index < cListR ) && success; index++ ) {
		// ...is a range
		elem = (Element) listR.item( index );
		r1 = 0;
		r2 = 0;
		try {
		    r1 = Integer.parseInt( elem.getAttribute( "START" ) );
		}
		catch ( NumberFormatException e ) {
		    System.err.println( "Error in XML file, START  can not be recognized as an integer." );
		    success = false;
		}
		try {
		    r2 = Integer.parseInt( elem.getAttribute( "END" ) );
		}
		catch ( NumberFormatException e ) {
		    System.err.println( "Error in XML file, END can not be recognized as an integer." );
		    success = false;
		}

		// set range
		if ( success ) {
		    fontrange.addRange( r1, r2 );
		}
	    }

	}  // "RANGES" found
	else {
	    // "RANGES" _not_ found
	    success = false;
	}
	// still success?
	if ( success ) {
	    NodeList listCC = doc.getElementsByTagName( "TITLECOMMENT" );
	    if ( listCC.getLength() > 0 ) {
		// "TITLECOMMENT" found
		Element elemCC = (Element) listCC.item( 0 );  // there should only be one TITLECOMMENT
		profileComment = elemCC.getAttribute( "VALUE" );
	    }
	    else {
		// "TITLECOMMENT" not found
		// no matter, its optional
		profileComment = null;
	    }
	}
		
	// finish
	if ( success ) {
	    // make data persistent
	    m_range = fontrange;
	    m_profileComment = profileComment;
	}

	return success;
    }

    
    private boolean gotoParser() {
	boolean success = false;

	try {
            DOMParserWrapper parser =
		(DOMParserWrapper)Class.forName(DEFAULT_PARSER_NAME).newInstance();
	    parser.setFeature( "http://apache.org/xml/features/dom/defer-node-expansion",
			       true );
            parser.setFeature( "http://xml.org/sax/features/validation", 
                               false );
            parser.setFeature( "http://xml.org/sax/features/namespaces",
                               true );
            parser.setFeature( "http://apache.org/xml/features/validation/schema",
                               true );
            parser.setFeature( "http://apache.org/xml/features/validation/schema-full-checking",
                               false );
	    
            Document document = parser.parse( m_filename );
            
	    success = runParser( document );

        } catch (org.xml.sax.SAXParseException spe) {
        } catch (org.xml.sax.SAXNotRecognizedException ex ){
        } catch (org.xml.sax.SAXNotSupportedException ex ){
        } catch (org.xml.sax.SAXException se) {
            if (se.getException() != null)
                se.getException().printStackTrace(System.err);
            else
                se.printStackTrace(System.err);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

	return success;
    }


    // *****************************************************************
    // XML write


    /**
     * write XML
     */
    private boolean doExport() {
	boolean success = false;

	try {
	    OutputStreamWriter os = new OutputStreamWriter( new FileOutputStream( m_filename ), "UTF-8" );
	    // Header
	    os.write( "<?xml version=\"1.0\"?>\n" );
	    os.write( "<UFFPROFILE>\n" );

	    // comment
	    if ( m_profileComment != null ) {
		os.write( "  <TITLECOMMENT VALUE=\"" + xmlstring( m_profileComment ) + "\"/>\n" );
	    }
	    // ranges
	    os.write( "  <RANGES>\n" );
	    int cc = m_range.size();
	    for ( int index = 0; index < cc; index++ ) {
		os.write( "    <RANGE START=\"" + m_range.getRangeStart( index ) + "\" END=\"" +  m_range.getRangeEnd( index ) + "\"/>\n" );
	    }
	    os.write( "  </RANGES>\n" );

	    os.write( "</UFFPROFILE>\n" );

	    os.close();

	    success = true;
	}	    
	catch ( UnsupportedEncodingException e ) {
	    System.err.println( e );
	}
	catch ( sun.io.MalformedInputException e ) {
	    System.err.println( e );
	    System.err.println( "This may be caused because the file is not (a vaild) UTF-8 encoded (file)." );
	    
	}
	catch ( IOException e ) {
	    System.err.println( e );
	}

	return success;
    }


    /**
     * transform to xml-konform TEXT
     */
    private String xmlstring( String si ) {
	char c;
	String app = "";
	StringBuffer so = new StringBuffer();
 
	// <, > and ? is illegal
	if ( si != null ) {
	    int le = si.length();
	    for ( int index = 0; index < le; index++ ) {
		c = si.charAt( index );
		if ( c == '<' ) {
		    app = "&lt;";
		}
		else if ( c == '>' ) {
		    app = "&gt;";
		}
		else if ( c == '&' ) {
		    app = "&amp;";
		}
		else {
		    // self-insert
		    app = ( new Character( c ) ).toString();
		}

		so.append( app );
	    }
	   
	    return so.toString();
	}
	else {
	    return null;
	}
    }
    

}


