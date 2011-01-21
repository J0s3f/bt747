/**
 * 
 */


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SpringLayout.Constraints;

import org.jdesktop.swingx.JXMapKitNewMapViewer;
import org.jdesktop.swingx.JXMapLayer;
import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.JXMapKitNewMapViewer.DefaultProviders;
import org.jdesktop.swingx.mapviewer.DefaultTileFactory;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.Tile;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;
import org.jdesktop.swingx.painter.AbstractPainter;

import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;

/**
 * @author Mario
 * 
 */
public class WebMapLayer extends JPanel {
    protected static final String LS = System.getProperty("line.separator");

    final static String pageContent = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\r\n"
            + "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:v=\"urn:schemas-microsoft-com:vml\">\r\n"
            + "<head>\r\n"
            + "<title>GPS-20080915_1910</title>\r\n"
            + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\r\n"
            + "<meta name=\"description\" content=\"Tracks - Generated with BT747 V1.47b6 http://sf.net/projects/bt747 - powered by Google Maps\" />\r\n"
            + "\r\n"
            + "<script src=\"http://maps.google.com/maps?file=api&amp;v=2&amp\" type=\"text/javascript\">\r\n"
            + "</script>\r\n"
            + "<style type=\"text/css\">\r\n"
            + " v\\:* {\r\n"
            + "  behavior:url(#default#VML);\r\n"
            + " }\r\n"
            + " html, body, #map\r\n"
            + "  {\r\n"
            + "   width: 100%;\r\n"
            + "   height: 100%;\r\n"
            + "  }\r\n"
            + " body {\r\n"
            + "  margin-top: 0px;\r\n"
            + "  margin-right: 0px;\r\n"
            + "  margin-left: 0px;\r\n"
            + "  margin-bottom: 0px;\r\n"
            + " }\r\n"
            + "\r\n"
            + "</style>\r\n"
            + "</head>\r\n"
            + "\r\n"
            + "<body  onresize=\"setFooter()\" onunload=\"GUnload()\">\r\n"
            + "<div id=\"map\"> </div>\r\n"
            + "<div id=\"footer\">\r\n"
            + "\r\n"
            + "<script type=\"text/javascript\">\r\n"
            + "if (GBrowserIsCompatible()) {\r\n"
            + "function latlonTxt(latlon) {\r\n"
            + " if(latlon) {\r\n"
            + "     var s='<b>Last click: '+ latlon.toUrlValue()+' </b>';\r\n"
            + "  //document.getElementById(\"latlon\").innerHTML = s;\r\n"
            + " }\r\n"
            + "}\r\n"
            + "function latlonFunc() {\r\n"
            + " return function(overlay,latlon) {latlonTxt(latlon);}\r\n"
            + "}\r\n"
            + "function gotoPt(pt) {\r\n"
            + " if(!pt){\r\n"
            + "  document.getElementById(\"latlon\").innerHTML=\"Location not found\";\r\n"
            + " } else {\r\n"
            + "  map.setCenter(pt);latlonTxt(pt);\r\n"
            + " }\r\n"
            + "}\r\n"
            + "\r\n"
            + "function gotoAddr(){\r\n"
            + " var ad=document.getElementById(\"adr\").value;\r\n"
            + " new GClientGeocoder().getLatLng(ad,gotoPt);\r\n"
            + "}\r\n"
            + " function makeOpenerCaller(i) {  return function() { showMarkerInfo(i); }; }\r\n"
            + "\r\n"
            + "  function showMarkerInfo(i) {\r\n"
            + "   markers[i].openInfoWindowHtml(infoHtmls[i]);\r\n"
            + "  }\r\n"
            + "function getWindowHeight() {\r\n"
            + " var windowHeight=0;\r\n"
            + " if (typeof(window.innerHeight)=='number') {\r\n"
            + "  windowHeight=window.innerHeight;\r\n"
            + " } else {\r\n"
            + "  if (document.documentElement&&document.documentElement.clientHeight) {\r\n"
            + "  windowHeight = document.documentElement.clientHeight;\r\n"
            + "  } else {\r\n"
            + "   if (document.body&&document.body.clientHeight) {\r\n"
            + "    windowHeight=document.body.clientHeight;\r\n"
            + "   }\r\n"
            + "  }\r\n"
            + " }\r\n"
            + " return windowHeight;\r\n"
            + "}function setFooter() {\r\n"
            + " if (document.getElementById) {\r\n"
            + "  var windowHeight=getWindowHeight();\r\n"
            + "  var footerElement=document.getElementById('footer');\r\n"
            + "  var footerHeight=footerElement.offsetHeight;\r\n"
            + " // if (windowHeight-footerHeight>400) {\r\n"
            + " //  document.getElementById('map').style.height=\r\n"
            + " //   (windowHeight-footerHeight)+'px';\r\n"
            + " // } else {\r\n"
            + " //  document.getElementById('map').style.height=400;\r\n"
            + " // }\r\n"
            + " }\r\n"
            + "}\r\n"
            + "function trackClick(trk,val) {\r\n"
            + " if (val == 1) {\r\n"
            + "  map.addOverlay(trk);\r\n"
            + " } else {\r\n"
            + "  map.removeOverlay(trk);\r\n"
            + " } }\r\n"
            + "function makeLatLonInfo(h) {\r\n"
            + " return function(latlng) {\r\n"
            + "  latlonTxt(latlng);\r\n"
            + "  map.openInfoWindowHtml(latlng, h);\r\n"
            + " };\r\n"
            + "} var clickStr; clickStr=\"\"; function clickString() {\r\n"
            + "  document.write(clickStr);\r\n"
            + " }\r\n"
            + " var map = new GMap2(document.getElementById(\"map\"));\r\n"
            + " setFooter();\r\n"
            + " map.setCenter(new GLatLng(0,0));\r\n"
            + " var mgr = new GMarkerManager(map);\r\n"
            + " map.addMapType(G_PHYSICAL_MAP);\r\n"
            + "  map.enableScrollWheelZoom();\r\n"
            + " map.addControl(new GLargeMapControl());\r\n"
            + " map.addControl(new GMapTypeControl());\r\n"
            + " map.addControl(new GScaleControl());\r\n"
            + " map.addControl(new GOverviewMapControl());\r\n"
            + "var OSM = new GMapType(\r\n"
            + "[ new GTileLayer(null,1,18,\r\n"
            + "{ tileUrlTemplate: 'http://tile.openstreetmap.org/{Z}/{X}/{Y}.png',\r\n"
            + " isPng: true, opacity: 1.0 })],\r\n"
            + "new GMercatorProjection(19),\r\n"
            + "'OSM',\r\n"
            + "{ errorMessage:\"More OSM coming soon\"}\r\n"
            + ");\r\n"
            + "var OSMcycle = new GMapType(\r\n"
            + "[ new GTileLayer(null,1,15,\r\n"
            + "{ tileUrlTemplate: 'http://www.thunderflames.org/tiles/cycle/{Z}/{X}/{Y}.png',\r\n"
            + " isPng: true, opacity: 1.0 })],\r\n"
            + "new GMercatorProjection(19),\r\n"
            + "'Cycle',\r\n"
            + "{ errorMessage:\"More OSM coming soon\"}\r\n"
            + ");\r\n"
            + "var Osmarender = new GMapType(\r\n"
            + "[ new GTileLayer(null,1,18,\r\n"
            + "{ tileUrlTemplate: 'http://tah.openstreetmap.org/Tiles/tile/{Z}/{X}/{Y}.png',\r\n"
            + " isPng: true, opacity: 1.0 })],\r\n"
            + "new GMercatorProjection(19),\r\n"
            + "'Osmardr',\r\n"
            + "{ errorMessage:\"More OSM coming soon\"}\r\n"
            + ");\r\n"
            + "map.addMapType(OSM);\r\n"
            + "map.addMapType(OSMcycle);\r\n"
            + "map.addMapType(Osmarender);\r\n"
            + "map.setMapType(OSM);\r\n"
            + "function setCenter(lat,lon) {map.setCenter(new GLatLng(lat,lon));\r\n"
            + "};\r\n"
            + "function setZoom(zoom) {map.setZoom(zoom);\r\n"
            + "};\r\n"
            + "map.enableContinuousZoom();\r\n"
            + "map.enableDoubleClickZoom();\r\n"
            + "   }\r\n"
            + "   else {\r\n"
            + "     document.getElementById(\"quicklinks\").innerHTML = \"Your web browser is not compatible with this website.\"\r\n"
            + "   }\r\n"
            + "//]]>\r\n"
            + "</script>\r\n"
            + " </div>\r\n"
            + "</body>\r\n" + "</html>";

    final String singleExample = "<html>"
            + LS
            + "  <body>"
            + LS
            + "    <h1>Some header</h1>"
            + LS
            + "    <p>A paragraph with a <a href=\"http://www.google.com\">link</a>.</p>"
            + LS + "  </body>" + LS + "</html>";;

    final JWebBrowser webBrowser;

    private void doJavaScript(final String javaScript) {
        webBrowser.executeJavascript(javaScript);
        String org = webBrowser.getHTMLContent();
//        try {
//            webBrowser.setHTMLContent(webBrowser.getHTMLContent());
//        } catch (Throwable t) {
//            t.printStackTrace();
//        }
    }

    /**
     * 
     */
    private void setCenter(double lat, double lon) {

        Object[] args = { Double.valueOf(lat), Double.valueOf(lon) };
        doJavaScript(JWebBrowser.createJavascriptFunctionCall("setCenter", lat, lon));
    }

    private void setZoom(int zoom) {
        doJavaScript("setZoom(" + zoom + ");");
    }

    public WebMapLayer() {
        // JPanel webBrowserPanel = this;

        /* Set up the GUI */
        super(new BorderLayout());
        JPanel webBrowserPanel = new JPanel(new BorderLayout());
        webBrowserPanel.setBorder(BorderFactory
                .createTitledBorder("Native Web Browser component"));
        webBrowser = new JWebBrowser();
        webBrowser.setBarsVisible(false);
        webBrowser.setStatusBarVisible(true);
        final String htmlContent = pageContent;

        // Set the HTML content.
        webBrowser.setHTMLContent(htmlContent);
        
        webBrowserPanel.add(webBrowser, BorderLayout.CENTER);
        webBrowser.setBarsVisible(false);
        
        add(webBrowserPanel, BorderLayout.CENTER);
        JPanel configurationPanel = new JPanel(new BorderLayout());
        configurationPanel.setBorder(BorderFactory.createTitledBorder("Configuration"));
        final JTextArea configurationTextArea = new JTextArea(
            "document.bgColor = '#FFFF00';" + LS +
            "//window.open('http://www.google.com');" + LS);
        JScrollPane scrollPane = new JScrollPane(configurationTextArea);
        Dimension preferredSize = scrollPane.getPreferredSize();
        preferredSize.height += 20;
        scrollPane.setPreferredSize(preferredSize);
        configurationPanel.add(scrollPane, BorderLayout.CENTER);
        JPanel configurationButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
        JButton executeJavascriptButton = new JButton("Execute Javascript");
        executeJavascriptButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            webBrowser.executeJavascript(configurationTextArea.getText());
            setZoom(14);
            setCenter(51, 0);
          }
        });
        configurationButtonPanel.add(executeJavascriptButton);
        JCheckBox enableJavascriptCheckBox = new JCheckBox("Enable Javascript", true);

        enableJavascriptCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                webBrowser
                        .setJavascriptEnabled(e.getStateChange() == ItemEvent.SELECTED);
                // Javascript state only affects subsequent pages. Let's
                // reload the content.
                webBrowser.setHTMLContent(htmlContent);
            }
        });
        configurationButtonPanel.add(enableJavascriptCheckBox);
        configurationPanel.add(configurationButtonPanel, BorderLayout.SOUTH);
        add(configurationPanel, BorderLayout.NORTH);
        //setZoom(14);
        //setCenter(51, 0);
        setZoom(14);
        setCenter(51, 0);

    }

    /* Standard main method to try that test as a standalone application. */
    public static void main(String[] args) {
        UIUtils.setPreferredLookAndFeel();
        NativeInterface.open();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if(false) {
                JFrame frame = new JFrame("DJ Native Swing Test");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new WebMapLayer(),
                        BorderLayout.CENTER);
                frame.setSize(800, 600);
                frame.setLocationByPlatform(true);
                frame.setVisible(true);
                }
                
                JXMapKitNewMapViewer kit = new JXMapKitNewMapViewer();
                kit.setDefaultProvider(DefaultProviders.OpenStreetMap);
                Logger.getLogger(JXMapViewer.class.getName()).setLevel(Level.ALL);
                Logger.getLogger(Tile.class.getName());
                final int max = 19;
                TileFactoryInfo info = new TileFactoryInfo("osm", 1, max - 2, max,
                        256, true, true, // tile size is 256 and x/y orientation is normal
                        "http://tile.openstreetmap.org",//5/15/10.png",
                        "x", "y", "z") {

                    @Override
                    public String getTileUrl(int x, int y, int zoom) {
                        zoom = max - zoom;
                        String url = this.baseURL + "/" + zoom + "/" + x + "/" + y + ".png";
                        return url;
                    }

                };
                DefaultTileFactory tf = new DefaultTileFactory(info);
                tf.getTileCache().setDiskCacheDir(new java.io.File("c:/temp/mapcache"));
                LogManager lm = LogManager.getLogManager();
                java.util.Enumeration<String> iter = lm.getLoggerNames();
                while (iter.hasMoreElements()) {
                    Logger l = LogManager.getLogManager().getLogger(iter.nextElement());
                    l.setLevel(Level.ALL);
                }
                kit.setTileFactory(tf);
                kit.setZoom(14);
                kit.setAddressLocation(new GeoPosition(51.5, 0));

                AbstractPainter<JXMapViewer> webMap = new WebMapPainter();
                
                
                
                kit.getMainMap().setMapLayer(webMap);
                kit.getMainMap().setDrawTileBorders(true);
                kit.getMainMap().setRestrictOutsidePanning(true);
                kit.getMainMap().setHorizontalWrapped(false);

                {
                //((DefaultTileFactory)kit.getMainMap().getTileFactory()).setThreadPoolSize(8);
                JFrame frame2 = new JFrame("JXMapKit with web map layer test");
                frame2.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
                frame2.add(kit);
                frame2.pack();
                frame2.setSize(500, 300);
                frame2.setVisible(true);
                }

            }
        });
        NativeInterface.runEventPump();
    }
    
    private final static class WebMapPainter extends AbstractPainter<JXMapViewer> {
        JWebBrowser webBrowser;
        public WebMapPainter ()  {
            webBrowser = new JWebBrowser();
            setCacheable(false);
            {
                webBrowser.setBarsVisible(false);
                webBrowser.setStatusBarVisible(true);
                final String htmlContent = pageContent;

                // Set the HTML content.
                webBrowser.setHTMLContent(htmlContent);                    }
        }

        
        /* (non-Javadoc)
         * @see org.jdesktop.swingx.painter.AbstractPainter#doPaint(java.awt.Graphics2D, java.lang.Object, int, int)
         */
        @Override
        protected void doPaint(Graphics2D g, JXMapViewer object, int width,
                int height) {
            webBrowser.setSize(object.getSize());
            //webBrowser.getParent(getParent());
            webBrowser.paint(g);
        };

    }
}
