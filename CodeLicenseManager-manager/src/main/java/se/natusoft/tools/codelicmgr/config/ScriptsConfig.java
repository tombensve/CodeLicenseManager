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
package se.natusoft.tools.codelicmgr.config;

import se.natusoft.tools.optionsmgr.annotations.Description;
import se.natusoft.tools.optionsmgr.annotations.Option;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds a list of ScriptConfig entries.
 */
public class ScriptsConfig {

    private List<ScriptConfig> scripts = new ArrayList<ScriptConfig>();

    /**
     * Adds a script.
     *
     * @param script The script to add.
     */
    @Option
    @Description("A script to execute.")
    public void addScript(ScriptConfig script) {
        this.scripts.add(script);
    }

    /**
     * Maven setter. This just calls addScript(script).
     *
     * @param script The script to add.
     */
    public void setScript(ScriptConfig script) {
        addScript(script);
    }

    /**
     * Returns a list of the config script code blocks to run.
     */
    public List<ScriptConfig> getScripts() {
        return this.scripts;
    }
}
