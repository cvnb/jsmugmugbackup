/*
 * JSmugmugBackupApp.java
 */

package jSmugmugBackup.view.ng;

import jSmugmugBackup.view.ng.SwingViewNG;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class SwingViewNGStarterApp extends SingleFrameApplication
{
    private static SwingViewNG view = null;

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup()
    {
        SwingViewNGStarterApp.view = new SwingViewNG(this);

        show(SwingViewNGStarterApp.view);
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of JSmugmugBackupApp
     */
    public static SwingViewNGStarterApp getApplication() {
        return Application.getInstance(SwingViewNGStarterApp.class);
    }

    public static SwingViewNG getView()
    {
        return SwingViewNGStarterApp.view;
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(SwingViewNGStarterApp.class, args);
    }
}
