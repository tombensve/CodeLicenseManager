/* 
 * 
 * PROJECT
 *     Name
 *         CodeLicenseManager-ant-task
 *     
 *     Code Version
 *         2.2.1
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
package se.natusoft.tools.codelicmgr;

import org.apache.tools.ant.BuildException;
import se.natusoft.tools.codelicmgr.config.CodeOptionsConfig;
import se.natusoft.tools.codelicmgr.config.Configuration;
import se.natusoft.tools.codelicmgr.config.ScriptsConfig;
import se.natusoft.tools.codelicmgr.config.UserDataConfig;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * This is an ant task for running scripts specified in the configuration, and only doing this.
 */
public class CodeLicenseManagerScriptAntTask {

    //
    // Private class members
    //

    /** Used for logging */
    private org.apache.tools.ant.Project antProject;

    /** Options config data. */
    private CodeOptionsConfig optionsConfig = null;

    /** User data config data. */
    private List<UserDataConfig> userDataConfigs = new ArrayList<UserDataConfig>();

    /** In config scripts config data. */
    private ScriptsConfig scripts = new ScriptsConfig();

    /** Provides debug output when true. */
    private boolean debug = false;

    //
    // Constructors
    //

    /**
     * Creates new GeniouzAntTask
     */
    public CodeLicenseManagerScriptAntTask() {
    }

    //
    // Config injection methods
    //

    /**
     * Receives the Ant project used for logging.
     *
     * @param project The received Ant project.
     */
    public void setProject(org.apache.tools.ant.Project project) {
        this.antProject = project;
    }

    /**
     * Enables extra debug output when true.
     *
     * @param debug true/false
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Receives CodeLicenseManager-manager options configuration loaded by ant.
     *
     * @param options The received options config.
     */
    public void addCodeOptions(CodeOptionsConfig options) {
        this.optionsConfig = options;
    }

    /**
     * Receives CodeLicenseManager-manager user data configuration loaded by ant.
     *
     * @param userData The received user data config.
     */
    public void addUserData(UserDataConfig userData) {
        this.userDataConfigs.add(userData);
    }

    /**
     * Receives CodeLicneseManager-manager scripts configuration loaded by ant.
     * 
     * @param scripts The received scripts config.
     */
    public void addScripts(ScriptsConfig scripts) {
        this.scripts = scripts;
    }

    //
    // Implementation methods
    //

    /**
     * Convenience for logging messages.
     * 
     * @param message
     */
    private void logMsg(String message) {
        this.antProject.log(message);
    }

    /**
     * Since Ant cannot collect all user data in the same entry as maven does, we have to collect many and then merge
     * them all into one.
     */
    public UserDataConfig mergeUserDataConfigs() {
        UserDataConfig mergeTo = new UserDataConfig();
        for (UserDataConfig userDataConfig : this.userDataConfigs) {
            for (String property : userDataConfig.getProperties().stringPropertyNames()) {
                String value = userDataConfig.getProperties().getProperty(property);

                mergeTo.setProperty(property, value);
            }
        }

        return mergeTo;
    }

    /**
     * This is the main task implementation!
     */
    public void execute() throws BuildException {
        Configuration config = new Configuration(this.optionsConfig, mergeUserDataConfigs(), this.scripts);

        if (this.debug) {
            logMsg("" + config);
        }
        
        try {
            config.validateForScriptsOnly();

            Display display = new Display() {
                @Override
                public void display(String message) {
                    logMsg(message);
                }
            };

            Display.setDisplay(display);

            CodeLicenseManager codeLicMgr = new CodeLicenseManager(config, false);

            codeLicMgr.updateSourceFilesUsingOnlyConfigScripts(new File("."));
        }
        catch (CodeLicenseException cle) {
            logMsg(cle.getMessage());
            logMsg(cle.getType().getDescription());
            throw new BuildException("Bad inputs when running apply!", cle);
        }
        catch (Exception e) {
            try {
                logMsg("*** Unexpected problem! Here is a stacktrace:");
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                logMsg(sw.toString());
                pw.close();
                sw.close();
            }
            catch (Exception ee) {/* dont care! */}

            throw new BuildException(e.getMessage(), e);
        }
        finally {
            Display.setDisplay(null);
        }
    }

}
