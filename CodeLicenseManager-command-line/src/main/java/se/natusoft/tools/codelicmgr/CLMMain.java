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
import se.natusoft.tools.codelicmgr.config.Configuration;
import se.natusoft.tools.optionsmgr.CommandLineOptionsManager;
import se.natusoft.tools.optionsmgr.OptionsModelException;
import se.natusoft.tools.optionsmgr.XMLElementOptionsManager;

import java.io.File;

/**
 * This instantiates the CodeLicenseManager and runs it.
 */
public class CLMMain implements CLMMainAPI {

    /**
     * Entry point.
     *
     * @param options The user arguments.
     * @param optionsMgr The options manager used for printing help.
     */
    public void clmmain(CmdlineOptions options, CommandLineOptionsManager<CmdlineOptions> optionsMgr) throws Exception {
        try {
            if (options.isHelp()) {
                System.out.println("CodeLicenseManager 1.0");
                printHelp(optionsMgr);
            }
            else {
                if (options.getAction() != Action.SCRIPT && options.getAction() != Action.DELETE) {
                    optionsMgr.validateLoadedOptions("--", "");
                }
                run(options);
            }
        }
        // We have to handle this exception here since Main does not have it in its classpath.
        catch (CodeLicenseException cle) {
            System.out.println(cle.getMessage());
            switch (cle.getType()) {
                case BAD_SOURCE_UPDATER_LIBRARY:
                    System.out.println(cle.getType().getDescription());
                    System.out.println("Check the source updater libraries specified with the --sourceupdaters argument.");
                    break;

                case BAD_LICENSE_LIBRARY:
                    System.out.println(cle.getType().getDescription());
                    System.out.println("Check the license library specified with the --licencelibrary argument.");
                    break;

                case BAD_CONFIGURATION:
                    System.out.println("Check the configuration specified with the --config argument.");
            }
        }
    }

    /**
     * Prints help text.
     *
     * @param optionsMgr The OptionsManager to use for printing help text.
     */
    private static void printHelp(CommandLineOptionsManager<CmdlineOptions> optionsMgr) {
        optionsMgr.printHelpText("--", "", System.out);

        System.out.println();

        try {
            XMLElementOptionsManager<Configuration> config = new XMLElementOptionsManager<Configuration>(Configuration.class);
            config.setModelPathSeparator("/");
            config.printHelpTextFull("", "/", System.out);
        }
        catch (OptionsModelException oe) {
            // This only happens if there is a bug in the models which have been tested.
            oe.printStackTrace();
        }
    }

    /**
     * Runs CodeLicenseManager.
     *
     * @param options The tool command line options.
     *
     * @throws Exception
     */
    public void run(CmdlineOptions options) throws Exception {
        XMLElementOptionsManager<Configuration> xmlOptsMgr = new XMLElementOptionsManager<Configuration>(Configuration.class);
        Configuration config = xmlOptsMgr.loadOptions(options.getConfigFile());

//        System.out.println("" + config);

        CodeLicenseManager codeLicMgr = null;

        Display display = new Display() {
            @Override
            public void display(String message) {
                System.out.println(message);
            }
        };

        Display.setDisplay(display);

        switch(options.getAction()) {
            case APPLY:
                config.validateForCodeUpdate();
                codeLicMgr = new CodeLicenseManager(config);
                codeLicMgr.updateSourceFilesForAllSourceDirs(new File("."));
                break;

            case INSTALL:
                config.validateForInstall();
                codeLicMgr = new CodeLicenseManager(config);
                codeLicMgr.copyAllLicenses();
                break;

            case SCRIPT:
                config.validateForScriptsOnly();
                codeLicMgr = new CodeLicenseManager(config, false);
                codeLicMgr.updateSourceFilesUsingOnlyConfigScripts(new File("."));
                break;

            case DELETE:
                codeLicMgr = new CodeLicenseManager(config, false);
                codeLicMgr.deleteInSourceFilesForAllSourceDirs(new File("."));
                break;
        }
    }
}
