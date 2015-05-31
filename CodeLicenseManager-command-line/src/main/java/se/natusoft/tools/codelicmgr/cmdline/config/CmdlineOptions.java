/* 
 * 
 * PROJECT
 *     Name
 *         CodeLicenseManager-command-line
 *     
 *     Code Version
 *         2.1.5
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
package se.natusoft.tools.codelicmgr.cmdline.config;

import se.natusoft.tools.codelicmgr.Action;
import se.natusoft.tools.optionsmgr.annotations.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Holds the command line options.
 */
@OptionsModel(name="", description="This tool lets you run CodeLicenseManager from the command line, for use in makefiles.")
public class CmdlineOptions {

    //
    // config
    //

    @Option
    @Required
    @Name("config")
    @Description("This specifies a config file to use for this run.")
    private File configFile = null;

    /**
     * Sets the config file to load. This is an XML file.
     *
     * @param configFile
     */
    public void setConfig(File configFile) {
        this.configFile = configFile;
    }

    /**
     * Returns the config file to use.
     */
    public File getConfigFile() {
        return this.configFile;
    }

    //
    // action
    //

    @Option
    @Required
    @Description("Specifies the action." + Description.NLNL +
                 "The \"apply\" action updates source files according to the supplied configuration file." + Description.NLNL +
                 "The \"install\" action installs license files in paths specified in the supplied configuration file." + Description.NLNL +
                 "The \"script\" action only runs the scripts specified in the supplied configuration file." + Description.NLNL +
                 "The \"delete\" action runs delete scripts in an source updater. This is basically the opposite of \"apply\"." + Description.NLNL +
                 "The <codeOptions> section is only used by the \"apply\" and \"script\" action, and the <installOptions> section is only used by the \"install\" action.")
    private Action action = null;

    /**
     * Sets the action to execute.
     *
     * @param action "apply" or "install"
     */
    public void setAction(Action action) {
        this.action = action;
    }

    /**
     * Returns the specified action.
     */
    public Action getAction() {
        return this.action;
    }

    //
    // licenselibrary
    //

    @Option
    @Required
    @Name("licenselibrary")
    @Description("Specifies the license library jar to use. Please note that this is an url!")
    private String licenseLibrary = null;

    /**
     * Specifies the license library jar to use.
     *
     * @param licenseLibrary The license library jar to use.
     */
    public void setLicenselibrary(String licenseLibrary) {
        this.licenseLibrary = licenseLibrary;
    }

    /**
     * Returns the license library jar to use.
     */
    public String getLicenselibrary() {
        return this.licenseLibrary;
    }

    //
    // sourceupdaters
    //

    @Option
    @Required
    @Name("sourceupdaters")
    @Description("A comma separated list of source updater jars to use. At least one have to be specified. " +
                 "Please note that each specified source updater must be an url!")
    private String sourceUpdaters = "";

    /**
     * Sets a comma separated list of source updater jars to use. At least one have to be specified.
     *
     * @param sourceUpdaters The updater list to set.
     */
    public void setSourceupdaters(String sourceUpdaters) {
        this.sourceUpdaters = sourceUpdaters;
    }

    /**
     * Returns the source updaters to use as a List of strings.
     */
    public List<String> getSourceUpdaters() {
        StringTokenizer tokenizer = new StringTokenizer(this.sourceUpdaters, ",");
        List<String> updaters = new ArrayList<String>();

        while(tokenizer.hasMoreTokens()) {
            updaters.add(tokenizer.nextToken());
        }

        return updaters;
    }

    //
    // help
    //

    @Option
    @Description("Provides help information.")
    @Flag
    private boolean help = false;

    /**
     * Sets the help flag.
     *
     * @param help
     */
    public void setHelp(boolean help) {
        this.help = help;
    }

    /**
     * Returns the state of the help flag.
     */
    public boolean isHelp() {
        return this.help;
    }
}
