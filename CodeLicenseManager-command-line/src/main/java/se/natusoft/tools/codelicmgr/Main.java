/* 
 * 
 * PROJECT
 *     Name
 *         CodeLicenseManager-command-line
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

import se.natusoft.tools.codelicmgr.cmdline.config.CmdlineOptions;
import se.natusoft.tools.optionsmgr.CommandLineOptionsManager;
import se.natusoft.tools.optionsmgr.OptionsException;
import se.natusoft.tools.optionsmgr.OptionsModelException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * Main class. Runs CodeLincenseManager from command line.
 */
public class Main {

    /**
     * Entry point.
     *
     * @param args The user arguments.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            args = new String[] {"--help"};
        }
        
        CommandLineOptionsManager<CmdlineOptions> optionsMgr = null;
        try {
            optionsMgr = new CommandLineOptionsManager<CmdlineOptions>(CmdlineOptions.class);

            CmdlineOptions options = optionsMgr.loadOptions(args);

            URL[] urls = new URL[0];

            if (options.getAction() != Action.SCRIPT) {
                urls = buildClasspath(options.getLicenselibrary(), options.getSourceUpdaters());
            }

            // for debugging
//            for (URL currurl : urls) {
//                System.out.println("" + currurl);
//            }

            // Setup our classloader.
            URLClassLoader urlClassLoader = new URLClassLoader(urls);

            // This is needed for BeanShell to behave as expected!
            Thread.currentThread().setContextClassLoader(urlClassLoader);

            // Load our real main class having access to an extended classpath.
            Class clmClass = urlClassLoader.loadClass("CLMMain");

            // Instantiate and call our real main.
            CLMMainAPI clm = (CLMMainAPI)clmClass.newInstance();
            clm.clmmain(options, optionsMgr);
        }
        catch (OptionsModelException ome) {
            // Model problems during load can only be missed for optional options never tested. For
            // this all options are required, and on top of that also tested :-). This should in
            // other words only possibly happen during testing, never for end users.
            ome.printStackTrace();
        }
        catch (OptionsException oe) {
            // This however means bad input from the user.
            System.out.println(oe.getMessage());
            System.out.println();
            System.out.println("Run with --help flag for more information!");
            oe.printStackTrace();
        }
        catch (IllegalArgumentException iae) {
            System.out.println(iae.getMessage());
        }
        catch (Exception e) {
            System.out.println("Unexpected problem! Here is a stack trace:");
            e.printStackTrace();
        }
    }

    /**
     * Builds a classpath for URLClassLoader. It adds all jars under lib/dependencies
     * relative to the jar this class belongs to. 
     * 
     * @param licenseLibrary The user specified license library.
     * @param updaters The user specified updaters to use.
     * 
     * @return An URL array of jars to include in classpath.
     * 
     * @throws MalformedURLException
     */
    private static URL[] buildClasspath(String licenseLibrary, List<String> updaters) throws MalformedURLException {
        // Find the url to the jar we are running.
        URL url = Main.class.getResource("Main.class");
        String selfUrlPath = url.toString().substring(4); // remove "jar:".
        int ix = selfUrlPath.indexOf("!");
        selfUrlPath = selfUrlPath.substring(0, ix);
        ix = selfUrlPath.lastIndexOf("/");
        selfUrlPath = selfUrlPath.substring(0, ix);

        // Build classpath
        List<URL> classpath = new ArrayList<URL>();

        // Starting with known dependencies.
        String selfDirPath = selfUrlPath.substring(5);
        File libDepsDir = new File(selfDirPath);
        libDepsDir = libDepsDir.getParentFile();
        libDepsDir = new File(libDepsDir, "lib");
        // OK, we get a little bit more than we need here, but it doesn't hurt and it makes
        // things so much simpler!
        for (File currentFile : libDepsDir.listFiles()) {
            if (currentFile.isFile() && currentFile.getName().endsWith(".jar")) {
                classpath.add(currentFile.toURI().toURL());
            }
        }

        // Then continue with provided arguments.
        classpath.add(urlSafe(licenseLibrary));
        for (String updater : updaters) {
            classpath.add(urlSafe(updater));
        }

        // Convert classpath to URL array.
        URL[] urls = new URL[classpath.size()];
        urls = classpath.toArray(urls);

        return urls;
    }

    /**
     * Makes sure the path is an url by makeing it a file: url if it does not start with file:/http/ftp.
     * 
     * @param path The path to make into an url.
     * 
     * @throws MalformedURLException
     */
    private static URL urlSafe(String path) throws MalformedURLException {
        if (path == null || path.length() == 0) {
            return null; // this is OK. ArrayList ignores null adds.
        }
        URL url = null;

        if (path.startsWith("file:")) {
            path = path.substring(5);
        }
        if (!path.startsWith("file:") && !path.startsWith("http") && !path.startsWith("ftp")) {
            File filePath = new File(path);
            url = filePath.toURI().toURL();
        }
        else {
            url = new URL(path);
        }
        
        try {
            url.openStream();
        } catch (IOException ioe) {
            throw new IllegalArgumentException("File: '" + path + "' was not found!", ioe);
        }

        return url;
    }
}
