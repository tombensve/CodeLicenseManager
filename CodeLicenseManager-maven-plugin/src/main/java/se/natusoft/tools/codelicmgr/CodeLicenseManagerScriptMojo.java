/* 
 * 
 * PROJECT
 *     Name
 *         CodeLicenseManager-maven-plugin
 *     
 *     Code Version
 *         2.1.4
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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import se.natusoft.tools.codelicmgr.config.CodeOptionsConfig;
import se.natusoft.tools.codelicmgr.config.Configuration;
import se.natusoft.tools.codelicmgr.config.ScriptsConfig;
import se.natusoft.tools.codelicmgr.config.UserDataConfig;

/**
 * Goal which runs source code update scripts specified in configuration only.
 *
 * @goal script
 *
 * @phase generate-sources
 */
public class CodeLicenseManagerScriptMojo
    extends AbstractMojo
{

    //
    // Confiuration
    //

    /**
     * Provides code update options.
     *
     * @parameter
     * @optional
     */
    private CodeOptionsConfig codeOptions;

    /**
     * Provides user data
     *
     * @parameter
     * @optional
     */
    private UserDataConfig userData;

    /**
     * Provides in config script code to run.
     *
     * @parameter
     * @optional
     */
    private ScriptsConfig scripts;

    //
    // Internal maven mojo contract.
    //

    /**
     * This is to be able to extract project information from the maven pom.
     * This is strictly internal and always supplied by maven. This should never
     * be specified in the configuration section!
     *
     * @parameter expression="${project}"
     */
    private MavenProject mavenProject;

    //
    // Implementation
    //

    /**
     * Logs output.
     *
     * @param msg Message to log.
     */
    private void logMsg(String msg) {
        getLog().info(msg);
    }

    /**
     * Mojo starting point.
     * 
     * @throws MojoExecutionException
     */
    public void execute()
        throws MojoExecutionException
    {
        logMsg("Running CodeLicenseManagerScriptMojo ... ");
        
        Configuration config = new Configuration(this.codeOptions, this.userData, this.scripts);
        
//        System.out.println("" + config);

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

            codeLicMgr.updateSourceFilesUsingOnlyConfigScripts(this.mavenProject.getFile().getParentFile());

        }
        catch (CodeLicenseException cle) {
            logMsg(cle.getMessage());
            switch (cle.getType()) {
                case BAD_CONFIGURATION:
                    logMsg("This is a plugin <configuration> section error.");
                    break;
                default:
                    logMsg("Unknown error!");
            }
            throw new MojoExecutionException("Bad inputs when running apply!", cle);
        }
        catch (Exception e) {
            System.out.println("*** Unexpected problem! Here is a stacktrace:");
            e.printStackTrace();
            throw new MojoExecutionException(e.getMessage(), e);
        }
        finally {
            Display.setDisplay(null);
        }
    }
}
