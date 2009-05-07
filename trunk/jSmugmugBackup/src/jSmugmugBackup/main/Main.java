/*
 * Created on Sep 2, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.main;

//import jSmugmugBackup.view.console.ConsoleView;
import jSmugmugBackup.view.console.CmdView;
import jSmugmugBackup.controller.Controller;
import jSmugmugBackup.model.*;
import jSmugmugBackup.view.*;
import jSmugmugBackup.view.ng.SwingViewNGStarterApp;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		//init model
        Model      	model      	= new Model();
        
        //init view
        IView view = null;
        
        //decide which view to use
        if (args.length == 0)
        {
            // start old swing view (designed in eclipse)
            //view = new SwingView(model);
            
            // start new Swing view, designed with NetBeans
            SwingViewNGStarterApp.launch(SwingViewNGStarterApp.class, args);
            Helper.pause(2000); // wait a sec until gui is initialized
            SwingViewNGStarterApp.getView().init(model);
            view = SwingViewNGStarterApp.getView();

            // starting cmdview, for the time the gui is deactivated
            //view = new CmdView(model, args);
        }
        //else if (args[0].equals("--console")) { view = new ConsoleView(model); }
        else { view = new CmdView(model, args); }
        
        
        //init controller
        @SuppressWarnings("unused")
		Controller 	controller 	= new Controller(model, view);
	}
}
