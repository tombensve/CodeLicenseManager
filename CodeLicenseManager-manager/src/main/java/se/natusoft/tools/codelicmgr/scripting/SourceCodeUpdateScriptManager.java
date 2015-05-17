/* 
 * 
 * PROJECT
 *     Name
 *         CodeLicenseManager-manager
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
package se.natusoft.tools.codelicmgr.scripting;

import bsh.EvalError;
import codelicmgr.sourcecodeupdaters.SourceCodeUpdaterResourceProvider;
import se.natusoft.tools.codelicmgr.CodeLicenseException;
import se.natusoft.tools.codelicmgr.Display;

import java.io.*;
import java.util.Properties;

/**
 * This class is responsible for actually executing the source code update scripts.
 */
public class SourceCodeUpdateScriptManager {

    private static final String SCRIPTDIR_PROP = "lang.scriptdir";
    private static final String FULLNAME_PROP = "lang.fullname";
    private static final String UPDATER_PROPERTIES_PROP = "updater.properties";
    private static final String UPDATER_DESCRIPTION_PROP = "updater.description";
    private static final String UPDATER_COPYRIGHT_PROP = "updater.copyright";
    private static final String UPDATER_MAINTAINER_PROP = "updater.maintainer";
    private static final String UPDATER_SUPPORT_PROP = "updater.support";

    //
    // Private Members
    //
    
    /** The interpreter to use for this instance. */
    private bsh.Interpreter interpreter = null;

    /** The language we are updating source for. */
    private String lang = null;

    /** The language.properties file relative directory path to the scripts. */
    private String scriptDir = null;

    /** The full name of the language this instance will be updating source code for. */
    private String fullName = null;

    //
    // Constructors
    //

    public SourceCodeUpdateScriptManager() {
        this.interpreter = new bsh.Interpreter();
    }

    /**
     * Creates a new script manager instance. 
     *
     * @param lang The source code language to update source for.
     *
     * @throws se.natusoft.tools.codelicmgr.CodeLicenseException On any problems using source code updater library for language.
     */
    public SourceCodeUpdateScriptManager(String lang) throws CodeLicenseException {
        this.lang = lang;

        this.interpreter =  new bsh.Interpreter();

        Properties langProps = new Properties();
        InputStream propStream = null;
        try {
            propStream = SourceCodeUpdaterResourceProvider.getInputStream(this.lang + ".properties");
            if (propStream == null) {
                throw new CodeLicenseException("No source code update scripts found for language \"" + lang + "\"! ",
                        CodeLicenseException.Type.BAD_SOURCE_UPDATER_LIBRARY);
            }
            langProps.load(propStream);
        }
        catch (IOException ioe) {
            throw new CodeLicenseException("Found, but failed to load properties for language \"" + lang + "\"!",
                    CodeLicenseException.Type.BAD_SOURCE_UPDATER_LIBRARY, ioe);
        }
        finally {
            if (propStream != null) {
                try {propStream.close();} catch(IOException ioe) {}
            }
        }

        this.scriptDir = langProps.getProperty(SCRIPTDIR_PROP);
        if (this.scriptDir == null || this.scriptDir.trim().length() == 0) {
            throw new CodeLicenseException("Found \"" + lang + ".properties\", but there is no '" + SCRIPTDIR_PROP + "' property available in it!",
                    CodeLicenseException.Type.BAD_SOURCE_UPDATER_LIBRARY);
        }

        propStream = SourceCodeUpdaterResourceProvider.getInputStream(this.scriptDir);
        if (propStream == null) {
            throw new CodeLicenseException("The script directory '" + this.scriptDir + "' pointed out by the '" + SCRIPTDIR_PROP + "' property in " +
                    SourceCodeUpdaterResourceProvider.getResourceUrl(lang + ".properties") + " does not exist!",
                    CodeLicenseException.Type.BAD_SOURCE_UPDATER_LIBRARY);
        }

        this.fullName = langProps.getProperty(FULLNAME_PROP);
        if (this.fullName == null || this.fullName.trim().length() == 0) {
            this.fullName = lang;
        }

        Properties updaterProps = langProps;

        String updaterProperties = langProps.getProperty(UPDATER_PROPERTIES_PROP);
        if (updaterProperties != null) {
            propStream = null;
            try {
                propStream = SourceCodeUpdaterResourceProvider.getInputStream(updaterProperties + ".properties");
                if (propStream != null) {
                    updaterProps = new Properties();
                    updaterProps.load(propStream);
                }
            }
            catch (IOException ioe) {
                throw new CodeLicenseException("Found, but failed to load " + updaterProperties + ".properties!",
                        CodeLicenseException.Type.BAD_SOURCE_UPDATER_LIBRARY, ioe);
            }
            finally {
                if (propStream != null) {
                    try {propStream.close();} catch(IOException ioe) {}
                }
            }

        }

        String updaterDescription = updaterProps.getProperty(UPDATER_DESCRIPTION_PROP);
        if (updaterDescription != null) {
            Display.msg(updaterDescription);
        }
        String updaterCopyright = updaterProps.getProperty(UPDATER_COPYRIGHT_PROP);
        if (updaterCopyright != null) {
            Display.msg(updaterCopyright);
        }
        String updaterMaintainer = updaterProps.getProperty(UPDATER_MAINTAINER_PROP);
        if (updaterMaintainer != null) {
            Display.msg(updaterMaintainer);
        }
        String updaterSupport = updaterProps.getProperty(UPDATER_SUPPORT_PROP);
        if (updaterSupport != null) {
            Display.msg(updaterSupport);
        }

    }

    //
    // Methods
    //

    /**
     * Returns the languages full name from the languages source code updaters properties file.
     */
    public String getLanguageFullname() {
        return this.fullName;
    }

    /**
     * Imports a package.
     *
     * @param pkg The package to import.
     *
     * @throws ScriptingException
     */
    public void importPackage(Package pkg) throws ScriptingException {
        importPackage(pkg.getName());
    }

    /**
     * Imports a package.
     *
     * @param pkgName The package to import.
     *
     * @throws ScriptingException
     */
    public void importPackage(String pkgName) throws ScriptingException {
        if (this.interpreter == null) {
            throw new ScriptingException("No interpreter available!");
        }
        try {
            this.interpreter.eval("import " + pkgName + ".*;");
        }
        catch(EvalError ee) {
            throw new ScriptingException("Failed to import package '" + pkgName + ".*;'!", ee);
        }
    }

    /**
     * Imports the specified class into the scripting interpreter making the class available in scripts.
     * 
     * @param clazz The class to import.
     * 
     * @throws ScriptingException
     */
    public void importClass(Class clazz) throws ScriptingException {
        if (this.interpreter == null) {
            throw new ScriptingException("No interpreter available!");
        }
        try {
            this.interpreter.eval("import " + clazz.getPackage().getName() + "." + clazz.getSimpleName() + ";");
        }
        catch(EvalError ee) {
            throw new ScriptingException("Failed to import class '" + clazz + "'!", ee);
        }
    }

    /**
     * Makes the specified instance available to the interpreter as the specified name.
     *
     * @param instance The instance to make available.
     * @param asName The name to make the instance available as.
     *
     * @throws ScriptingException
     */
    public void provideInstance(Object instance, String asName) throws ScriptingException {
        try {
            this.interpreter.set(asName, instance);
        }
        catch (EvalError ee) {
            throw new ScriptingException("Failed to provide instance '" + instance + "' as name '" + asName + "'!", ee);
        }
    }

    /**
     * Evaluates the string within the interpreter.
     *
     * @param toEvaluate The string to evaluate.
     * 
     * @throws ScriptingException
     */
    public void eval(String toEvaluate) throws ScriptingException {
        try {
            this.interpreter.eval(toEvaluate);
        }
        catch (EvalError ee) {
            throw new ScriptingException("Failed to evaluate '" + toEvaluate + "'!", ee, "eval(\"" + toEvaluate + "\")");
        }
    }

    /**
     * Evalues the script in the specified file.
     *
     * @param script The script file to evaluate.
     *
     * @throws ScriptingException
     */
    public void runScriptFromFile(File script) throws ScriptingException {
        FileInputStream scriptStream = null;
        try {
            scriptStream = new FileInputStream(script);
            this.interpreter.eval(new BufferedReader(new InputStreamReader(scriptStream)));
        }
        catch (EvalError ee) {
            throw new ScriptingException("Script failed!", ee, script.toString());
        }
        catch (IOException ioe) {
            throw new ScriptingException("Script failed!", ioe, script.toString());
        }
        finally {
            try { if (scriptStream != null) scriptStream.close(); } catch (IOException ioe) {/* Well, we at least tried! */}
        }
    }

    /**
     * Runs the specified script within the interpreter. The script must be available under the path specified by "lang".properties under codelicmge/sourcecodeupdaters.
     * and suffixed with ".bsh" or ".java".
     *
     * @param scriptName The name of the script to run.
     *
     * @throws ScriptingException
     */
    public boolean runScriptIfAvailable(String scriptName) throws ScriptingException {
        return runScriptIfAvailable(scriptName, null);
    }

    /**
     * Runs the specified script within the interpreter. The script must be available under the path specified by "lang".properties under codelicmge/sourcecodeupdaters.
     * and suffixed with ".bsh" or ".java".
     *
     * @param scriptName The name of the script to run.
     * @param warning A warning to display if script is not available.
     *
     * @throws ScriptingException
     */
    public boolean runScriptIfAvailable(String scriptName, String warning) throws ScriptingException {
        boolean found = false;
        try {
            runScript(scriptName);
            found = true;
        }
        catch (ScriptingException se) {
            if (!se.isSricptNotFound()) {
                throw se;
            }
            if (warning != null) {
                Display.msg("WARNING: " + warning);
            }
        }

        return found;
    }

    /**
     * Runs the specified script within the interpreter. The script must be available under the path specified by "lang".properties under codelicmge/sourcecodeupdaters.
     * and suffixed with ".bsh" or ".java"
     *
     * @param scriptName The name of the script to run.
     *
     * @throws ScriptingException
     */
    public void runScript(String scriptName) throws ScriptingException {
        runScript(this.scriptDir, scriptName);
    }
    
    /**
     * Runs the specified script within the interpreter. The script must be available under the path specified by "lang".properties under codelicmge/sourcecodeupdaters.
     * and suffixed with ".bsh" or ".java".
     *
     * @param scriptName The name of the script to run.
     *
     * @throws ScriptingException
     */
    public void runLocalScript(String scriptName) throws ScriptingException {
        runScript("scripts", scriptName);
    }

    /**
     * Runs the specified script within the interpreter. The script must be available under the path specified by "lang".properties under codelicmge/sourcecodeupdaters.
     * and suffixed with ".bsh" or ".java".
     *
     * @param scriptDir The directory of the script.
     * @param scriptName The name of the script to run.
     *
     * @throws ScriptingException
     */
    private void runScript(String scriptDir, String scriptName) throws ScriptingException {
        InputStream scriptStream = SourceCodeUpdaterResourceProvider.getInputStream(scriptDir + "/" + scriptName + ".bsh");
        if (scriptStream == null) {
            scriptStream = SourceCodeUpdaterResourceProvider.getInputStream(scriptDir + "/" + scriptName + ".java");
        }
        if (scriptStream == null) {
            throw new ScriptingException("Source code updater script '" + SourceCodeUpdaterResourceProvider.class.getPackage().getName().replace('.', '/') + "/" + scriptDir + "/" + scriptName + ".bsh' was not found!", true);
        }
        try {
            this.interpreter.eval(new BufferedReader(new InputStreamReader(scriptStream)));
        }
        catch (EvalError ee) {
            throw new ScriptingException("Script failed!", ee, scriptName);
        }
        finally {
            try { scriptStream.close(); } catch (IOException ioe) {/* Well, we at least tried! */}
        }
    }
}
