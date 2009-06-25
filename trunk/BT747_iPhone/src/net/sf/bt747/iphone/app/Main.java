/**
 * 
 */
package net.sf.bt747.iphone.app;

/**
 * @author Mario De Weerd
 * 
 * Currently just a copy of test application (to start from).
 * 
 */

import gps.connection.GPSrxtx;

import bt747.model.Controller;

import java.awt.Color;

import net.sf.bt747.iphone.system.IphoneJavaTranslations;

import org.xmlvm.iphone.*;

import bt747.model.Model;
import bt747.sys.JavaLibBridge;

public class Main extends UIApplication {

    /**
     * Initialize the lower level interface class. Needed for BT747 to work.
     */
    static {
        JavaLibBridge
                .setJavaLibImplementation(new IphoneJavaTranslations());
        // Set the serial port class instance to use (also system specific).
        if (!GPSrxtx.hasDefaultPortInstance()) {
            //TODO: Create Bluetooth connection protocol.
//            GPSrxtx
//                    .setDefaultGpsPortInstance(new gps.connection.GPSRxTxPort());
        }
    }

    public void applicationDidFinishLaunching(UIApplication app) {
        UIScreen screen = UIScreen.mainScreen();
        CGRect rect = screen.applicationFrame();
        UIWindow window = new UIWindow(rect);

        rect.origin.x = rect.origin.y = 0;
        UIView mainView = new UIView(rect);
        window.addSubview(mainView);

        final UILabel title = new UILabel(rect);
        title.setBackgroundColor(Color.WHITE);
        title.setText("-- No button pressed --");
        title.setTextAlignment(UITextAlignment.UITextAlignmentCenter);
        mainView.addSubview(title);

        UIButton roundedRectButton = UIButton
                .buttonWithType(UIButtonType.UIButtonTypeRoundedRect);
        roundedRectButton.setFrame(new CGRect(10, 10, 90, 60));
        // roundedRectButton.setFont(new Font("Arial", Font.BOLD, 14));
        roundedRectButton.setTitle("Rounded",
                UIControlState.UIControlStateNormal);
        roundedRectButton.addTarget(new UIControlDelegate() {

            @Override
            public void raiseEvent() {
                title.setText("roundedRectButton pressed");
            }

        }, UIControl.UIControlEventTouchUpInside);
        mainView.addSubview(roundedRectButton);

        UIButton alertDialogLightButton = UIButton
                .buttonWithType(UIButtonType.UIButtonTypeRoundedRect);
        alertDialogLightButton.setFrame(new CGRect(115, 10, 90, 60));
        alertDialogLightButton.setBackgroundColor(new Color(122, 126, 150));
        alertDialogLightButton.setAlpha(191);
        alertDialogLightButton.setTitleColor(Color.WHITE,
                UIControlState.UIControlStateNormal);
        alertDialogLightButton.setTitleShadowOffset(new CGSize(0, -1),
                UIControlState.UIControlStateNormal);
        alertDialogLightButton.setEdgeDiameter(8);
        // detailDisclosureButton.setFont(new Font("Times New Roman",
        // Font.BOLD,
        // 16));
        alertDialogLightButton.setTitle("alertLight",
                UIControlState.UIControlStateNormal);
        alertDialogLightButton.addTarget(new UIControlDelegate() {

            @Override
            public void raiseEvent() {
                title.setText("alertDialogLightButton pressed");
            }

        }, UIControl.UIControlEventTouchUpInside);
        mainView.addSubview(alertDialogLightButton);

        UIButton alertDialogDarkButton = UIButton
                .buttonWithType(UIButtonType.UIButtonTypeRoundedRect);
        alertDialogDarkButton.setFrame(new CGRect(220, 10, 90, 60));
        alertDialogDarkButton.setBackgroundColor(new Color(76, 82, 113));
        alertDialogDarkButton.setAlpha(191);
        alertDialogDarkButton.setTitleColor(Color.WHITE,
                UIControlState.UIControlStateNormal);
        alertDialogDarkButton.setTitleShadowOffset(new CGSize(0, -1),
                UIControlState.UIControlStateNormal);
        alertDialogDarkButton.setEdgeDiameter(8);
        alertDialogDarkButton.setTitle("alertDark",
                UIControlState.UIControlStateNormal);
        alertDialogDarkButton.addTarget(new UIControlDelegate() {

            @Override
            public void raiseEvent() {
                title.setText("alertDialogDarkButton pressed");
            }

        }, UIControl.UIControlEventTouchUpInside);
        mainView.addSubview(alertDialogDarkButton);

        /*
         * UIButton detailDisclosureButton =
         * UIButton.buttonWithType(UIButtonType.UIButtonTypeDetailDisclosure);
         * detailDisclosureButton.setFrame(new CGRect(115, 10, 90, 60)); //
         * detailDisclosureButton.setFont(new Font("Times New Roman",
         * Font.BOLD, 16)); detailDisclosureButton.setTitle("Detail",
         * UIControlState.UIControlStateNormal);
         * mainView.addSubview(detailDisclosureButton);
         * 
         * UIButton contactAddButton =
         * UIButton.buttonWithType(UIButtonType.UIButtonTypeContactAdd);
         * contactAddButton.setFrame(new CGRect(220, 10, 90, 60));
         * contactAddButton.setTitle("Contact",
         * UIControlState.UIControlStateNormal);
         * mainView.addSubview(contactAddButton);
         */

        UIButton darkGrayButton = UIButton
                .buttonWithType(UIButtonType.UIButtonTypeRoundedRect);
        darkGrayButton.setFrame(new CGRect(10, 390, 90, 60));
        darkGrayButton.setBackgroundColor(new Color(10, 10, 10));
        darkGrayButton.setPressedBackgroundColor(Color.BLUE);
        darkGrayButton.setPressedTitleColor(Color.WHITE);
        darkGrayButton.setAlpha(191);
        darkGrayButton.setTitleColor(Color.BLACK,
                UIControlState.UIControlStateNormal);
        darkGrayButton.setTitleShadowOffset(new CGSize(0, -1),
                UIControlState.UIControlStateNormal);
        darkGrayButton.setEdgeDiameter(8);
        darkGrayButton.setTitle("darkGray",
                UIControlState.UIControlStateNormal);
        darkGrayButton.addTarget(new UIControlDelegate() {

            @Override
            public void raiseEvent() {
                title.setText("darkGrayButton pressed");
            }

        }, UIControl.UIControlEventTouchUpInside);
        mainView.addSubview(darkGrayButton);

        /*
         * UIButton infoLightButton =
         * UIButton.buttonWithType(UIButtonType.UIButtonTypeInfoLight);
         * infoLightButton.setFrame(new CGRect(10, 390, 90, 60));
         * infoLightButton.setTitle("Light",
         * UIControlState.UIControlStateNormal);
         * mainView.addSubview(infoLightButton);
         * 
         * UIButton infoDarkButton =
         * UIButton.buttonWithType(UIButtonType.UIButtonTypeInfoDark);
         * infoDarkButton.setFrame(new CGRect(220, 390, 90, 60));
         * infoDarkButton.setTitle("Dark",
         * UIControlState.UIControlStateNormal);
         * mainView.addSubview(infoDarkButton);
         */
        /*
         * UIButton customButton = new
         * UIButton(UIButtonType.UIButtonTypeCustom, new CGRect(10, 10, 90,
         * 60)); customButton.setFont(new Font("Arial", Font.BOLD |
         * Font.ITALIC, 12)); customButton.setTitle("Custom",
         * UIControlState.UIControlStateNormal);
         * customButton.setTitleShadowColor(Color.DARK_GRAY,
         * UIControlState.UIControlStateNormal);
         * customButton.setTitleShadowOffset(new CGSize(2, 2),
         * UIControlState.UIControlStateNormal);
         * mainView.addSubview(customButton);
         * mainView.bringSubviewToFront(customButton);
         */
        window.makeKeyAndVisible();
    }

    public static void main(String[] args) {
//        final J2SEAppModel m = new J2SEAppModel();
//        final J2SEAppController c = new J2SEAppController(m);

        // Shoul load settings
        //Controller.initAppSettings();
        
        final Model m = new Model();
        m.init(); // Doing init because initAppSettings missing.
        final Controller c = new Controller(m);

        UIApplication.main(args, Main.class);
    }
}
