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
package se.natusoft.tools.codelicmgr;

import se.natusoft.tools.codelicmgr.cmdline.config.CmdlineOptions;
import se.natusoft.tools.optionsmgr.CommandLineOptionsManager;

/**
 * This interface is implemented by CLM to provide an API for calling from
 * system classloader to CLM class loaded by URLClassLoader.
 */
public interface CLMMainAPI {

    /**
     * This is implemented by CLMMain to allow Main to call CLMMain from URLClassLoader loaded
     * class without using reflection.
     * 
     * @param args The user provided options.
     * @param optionsMgr The otions manager used to load the options.
     * 
     * @throws Exception
     */
    public void clmmain(CmdlineOptions options, CommandLineOptionsManager<CmdlineOptions> optionsMgr) throws Exception;
}
