/* 
 * 
 * PROJECT
 *     Name
 *         CodeLicenseManager-manager
 *     
 *     Code Version
 *         2.1.3
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
package se.natusoft.tools.codelicmgr.config;

import se.natusoft.tools.optionsmgr.annotations.Description;
import se.natusoft.tools.optionsmgr.annotations.Option;
import se.natusoft.tools.optionsmgr.annotations.Required;

/**
 * This allows providing small scripts directly in the configuration and when script
 * should be run or not is determined by file extension.
 */
public class ScriptConfig {

    //
    // langExt
    //

    @Option
    @Description("A regular expression file filter for which files to apply script to.")
    @Required
    private String fileFilter;

    /**
     * Sets a regular expression file filter for which files to apply script to.
     *
     * @param fileFilter The file filter to set.
     */
    public void setFileFilter(String fileFilter) {
        this.fileFilter = fileFilter;
    }

    /**
     * Returns the file filter for which files to apply script to.
     */
    public String getFileFilter() {
        return this.fileFilter;
    }

    //
    // code
    //

    @Option
    @Description("Script code to run if <scriptFile/> is not specified.")
    private String code;

    /**
     * Sets the code to run.
     *
     * @param code The code to run.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Returns the code to run.
     */
    public String getCode() {
        return this.code;
    }

    //
    // scriptFile
    //

    @Option
    @Description("This points to a script in a separate file to execute instead of the <code/> block.")
    private String scriptFile;

    /**
     * Sets the path to a script file to execute.
     *
     * @param scriptFile The script file path to set.
     */
    public void setScriptFile(String scriptFile) {
        this.scriptFile = scriptFile;
    }

    /**
     * Returns the path to the script file to execute.
     */
    public String getScriptFile() {
        return this.scriptFile;
    }
}
