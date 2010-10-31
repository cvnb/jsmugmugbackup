/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jSmugmugBackup.model.accountLayer;

import jSmugmugBackup.view.LogLevelEnum;

/**
 *
 * @author paul
 */
public class VerifyOutputMessage
{
    private LogLevelEnum loglevel;
    private String message;

    public VerifyOutputMessage(LogLevelEnum loglevel, String message)
    {
        this.loglevel = loglevel;
        this.message = message;
    }

    public LogLevelEnum getLogLevel() { return this.loglevel; }
    public String getMessage() { return this.message; }

}
