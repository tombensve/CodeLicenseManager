/* 
 * 
 * PROJECT
 *     Name
 *         CodeLicenseManager-manager
 *     
 *     Code Version
 *         2.1.6
 *     
 *     Description
 *         Manages project and license information in project sourcecode
 *         and provides license text files for inclusion in builds. Supports
 *         multiple languages and it is relatively easy to add a new
 *         language and to make your own custom source code updater.
 *         
 * COPYRIGHTS
 *     Copyright (C) 2013 by Natusoft AB All rights reserved.
 *     
 * LICENSE
 *     Apache 2.0 (Open Source)
 *     
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *     
 *       http://www.apache.org/licenses/LICENSE-2.0
 *     
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *     
 * AUTHORS
 *     tommy ()
 *         Changes:
 *         2014-07-09: Created!
 *         
 */
package se.natusoft.tools.codelicmgr.scripting;

/**
 * This exception represents an interpreter exception. It is used to encapsulate the actual scripting interpreter used.
 */
public class ScriptingException extends RuntimeException {

    /** This will be set to true in the case of script not found. */
    private boolean scriptNotFound = false;

    /** The script file that caused the exception. */
    private String scriptFile = "unknown";

    /**
     * Constructs an instance of <code>InterpreterException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ScriptingException(String msg) {
        super(msg);
    }
    
    /**
     * Constructs an instance of <code>InterpreterException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ScriptingException(String msg, boolean scriptNotFound) {
        super(msg);
        this.scriptNotFound = scriptNotFound;
    }

    /**
     * Constructs an instance of <code>InterpreterException</code> with the specified detail message.
     *
     * @param msg the detail message.
     * @param cause The cause of the exception.
     */
    public ScriptingException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * Constructs an instance of <code>InterpreterException</code> with the specified detail message.
     *
     * @param msg the detail message.
     * @param cause The cause of the exception.
     * @param scriptFile The name of the script that caused the exception.
     */
    public ScriptingException(String msg, Throwable cause, String scriptFile) {
        super(msg, cause);
        this.scriptFile = scriptFile;
    }

    /**
     * @return The script not found state.
     */
    public boolean isSricptNotFound() {
        return this.scriptNotFound;
    }

    /**
     * Sets the name of the script that caused the exception.
     *
     * @param scriptFile The script file name to set.
     */
    public void setScriptFile(String scriptFile) {
        this.scriptFile = scriptFile;
    }

    /**
     * Returns the exception message.
     */
    @Override
    public String getMessage() {
        return "Scriptfile: " + this.scriptFile + ", message: " + super.getMessage();
    }
}
