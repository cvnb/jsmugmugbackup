/*
 * Created on Sep 2, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.main;

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
            
            Helper.pause(1000); // wait a sec until gui is initialized
            view = SwingViewNGStarterApp.getView();
        }
        else if (args[0].equals("--console")) { view = new ConsoleView(model); }
        else { view = new CmdView(model, args); }
        
        
        //init controller
        @SuppressWarnings("unused")
		Controller 	controller 	= new Controller(model, view);
	}
}