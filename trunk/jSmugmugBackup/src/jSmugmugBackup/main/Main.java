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
        
        //decide weather to use Swing or the commandline
        if (args.length == 0) { view = new SwingView(model); }
        else { view = new CmdView(model, args); }
        
        //only use the commandline-interface
        //view = new CmdView(model, args);
        
        //init controller
        @SuppressWarnings("unused")
		Controller 	controller 	= new Controller(model, view);
	}
}
