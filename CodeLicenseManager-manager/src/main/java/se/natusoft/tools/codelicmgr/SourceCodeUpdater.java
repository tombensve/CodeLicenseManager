package se.natusoft.tools.codelicmgr;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import se.natusoft.tools.codelicmgr.annotations.Author;
import se.natusoft.tools.codelicmgr.annotations.Authors;
import se.natusoft.tools.codelicmgr.annotations.Change;
import se.natusoft.tools.codelicmgr.annotations.Copyright;
import se.natusoft.tools.codelicmgr.annotations.License;
import se.natusoft.tools.codelicmgr.annotations.Project;
import se.natusoft.tools.codelicmgr.enums.Source;
import se.natusoft.tools.codelicmgr.config.Configuration;
import se.natusoft.tools.codelicmgr.config.ScriptConfig;
import se.natusoft.tools.codelicmgr.scripting.ScriptingException;
import se.natusoft.tools.codelicmgr.scripting.SourceCodeUpdateScriptManager;
import se.natusoft.tools.fileeditor.Position;
import se.natusoft.tools.fileeditor.TextBuffer;
import se.natusoft.tools.fileeditor.TextFileEditor;

@Project(
    name="CodeLicenseManager-manager",
    codeVersion="2.1.1",
    description="Manages project and license information in project sourcecode" +
                "and provides license text files for inclusion in builds. Supports" +
                "multiple languages and it is relatively easy to add a new" +
                "language and to make your own custom source code updater."
)
@Copyright(year="2013", holder="Natusoft AB", rights="All rights reserved.")
@License(
    type="Apache",
    version="2.0",
    description="Apache Software License",
    source=Source.OPEN,
    text={
        "Licensed under the Apache License, Version 2.0 (the 'License');",
        "you may not use this file except in compliance with the License.",
        "You may obtain a copy of the License at",
        "",
        "  http://www.apache.org/licenses/LICENSE-2.0",
        "",
        "Unless required by applicable law or agreed to in writing, software",
        "distributed under the License is distributed on an 'AS IS' BASIS,",
        "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.",
        "See the License for the specific language governing permissions and",
        "limitations under the License."
    }
)
@Authors({
    @Author(
        name="Tommy Svensson",
        changes={
            @Change(when="2008-11-23", description="Created")
        }
    )
})
public class SourceCodeUpdater {

    //
    // Private Members
    //

    /** If true all verbose() calls will be shown otherwise not. */
    private boolean verbose = false;

    /** The source code update script manager to use for the language specific instance. */
    private SourceCodeUpdateScriptManager scriptMgr = null;

    /** An editor instance to use for updating source code. */
    private TextFileEditor editor = new TextFileEditor();

    /** Holds a reference to the current config. */
    private Configuration config;

    //
    // Constructors
    //

    /**
     * Creates a new SourceCodeUpdater instance.
     * 
     * @param lang The programming language used in the source files to update by this instance. Each instance is language specific. There must
     *             also be a source code updater library available in the classpath that supports this language.
     * @param licenseManager A configured CodeLicenseManager instance.
     * @param verbose The verbose state.
     *
     * @throws ScriptingException on scripting failure.
     */
    private SourceCodeUpdater(String lang, CodeLicenseManager licenseManager, boolean verbose) throws ScriptingException {
        super(); // interestingly enough this seems to be required when I reference "this.editor", but not when I simply
                 // reference "editor". This makes me wonder what the latter would actually reference!

        this.verbose = verbose;

        this.config = licenseManager.getConfig();

        Display.msg("Loading source code updater ...");
        Display.setIndent("  ");

        this.scriptMgr = new SourceCodeUpdateScriptManager(lang);

        // initialize the source code updater library.
        this.scriptMgr.runScript("init");

        configureScriptManager(licenseManager);
    }

    /**
     * Creates a new SourceCodeUpdater instance. This variant can only be used if no source code updater library scripts will
     * be run. Otherwise use the other constructor and supply a language.
     *
     * @param licenseManager A configured CodeLicenseManager instance.
     * @param verbose The verbose state.
     *
     * @throws ScriptingException on scripting failure.
     */
    private SourceCodeUpdater(CodeLicenseManager licenseManager, boolean verbose) throws ScriptingException {
        super(); // interestingly enough this seems to be required when I reference "this.editor", but not when I simply
                 // reference "editor". This makes me wonder what the latter would actually reference!

        this.verbose = verbose;

        this.config = licenseManager.getConfig();

        this.scriptMgr = new SourceCodeUpdateScriptManager();

        configureScriptManager(licenseManager);
    }

    //
    // Methods
    //

    /**
     * Common setup of script manager.
     *
     * @param licenseManager The license manager that called us. 
     * @throws ScriptingException
     */
    private final void configureScriptManager(CodeLicenseManager licenseManager) throws ScriptingException {
        this.scriptMgr.importPackage(TextFileEditor.class.getPackage());
        this.scriptMgr.importClass(CodeLicenseManager.class);
        this.scriptMgr.importClass(SourceCodeUpdater.class);

        this.scriptMgr.provideInstance(this.editor, "editor");

        this.scriptMgr.provideInstance(this, "updater");
        this.scriptMgr.importPackage(Configuration.class.getPackage());
        this.scriptMgr.eval("void display(String str) {updater.display(str);}");
        this.scriptMgr.eval("void formatMultiLineTextToCode(TextBuffer buffer, String text, String lineEnd, String stringSpec, String escapedStringSpec, int indent, boolean trim) {" +
                "updater.formatMultiLineTextToCode(buffer, text, lineEnd, stringSpec, escapedStringSpec, indent, trim); }");
        this.scriptMgr.eval("void formatMultiLineTextToCode(TextBuffer buffer, String text, String lineBeg, String lineEnd, String stringSpec, String escapedStringSpec, int indent, boolean trim) {" +
                "updater.formatMultiLineTextToCode(buffer, text, lineBeg, lineEnd, stringSpec, escapedStringSpec, indent, trim); }");

        this.scriptMgr.eval("void updateLine(String staticPart, String dynamicPart) {if (editor.find(staticPart)) {editor.deleteCurrentLine();editor.insertLine(staticPart + dynamicPart);}}");

        this.scriptMgr.eval("boolean findInsertPosition(String endOfSection, String endOfCommentSearch, String startComment, String middleComment, String endComment, String searchStrings){return updater.findInsertPosition(editor,endOfSection,endOfCommentSearch,startComment,middleComment,endComment,searchStrings);}");

        this.scriptMgr.eval("void moveToEndOfSection(String startsWith, int noSpaceAt) {updater.moveToEndOfSection(editor, startsWith, noSpaceAt);}");
        this.scriptMgr.eval("void moveToEndOfSection(char starting, char ending) {updater.moveToEndOfSection(editor, starting, ending);}");

        for (String objName : licenseManager.getProjectAndLicenseDataMap().keySet()) {
            this.scriptMgr.provideInstance(licenseManager.getProjectAndLicenseDataMap().get(objName), objName);
        }

        for (String prop : this.config.getUserData().getProperties().stringPropertyNames()) {
            this.scriptMgr.provideInstance(this.config.getUserData().getProperties().getProperty(prop), prop);
        }
        this.scriptMgr.eval("String getUserData(String key, String defaultValue) {return updater.getUserData(key, defaultValue);}");
        this.scriptMgr.eval("boolean availableUserData(String name) {return updater.getUserData(name, null) != null;}");

        this.scriptMgr.provideInstance("se.natusoft.tools.codelicmgr.annotations", "annotationsPkg");
        this.scriptMgr.provideInstance("se.natusoft.tools.codelicmgr.enums", "enumsPkg");
        this.scriptMgr.provideInstance("se.natusoft.tools.codelicmgr", "codelicmgrPkg");
    }

    /**
     * Returns user data values.
     *
     * @param key The key to get the value for.
     * @param defaultValue The default value to use if no value is available for the key.
     */
    public String getUserData(String key, String defaultValue) {
        String userData = this.config.getUserData().getProperty(key);
        if (userData == null) {
            userData = defaultValue;
        }
        return userData;
    }

    /**
     * Displays output if verbose is true.
     *
     * @param text Text to verbose.
     * @deprecated Use verbose(text) instead!
     */
    public void display(String text) {
        verbose(text);
    }

    /**
     * Displays output if verbose is true.
     *
     * @param text Text to verbose.
     */
    public void verbose(String text) {
        if (this.verbose) {
            Display.msg(text);
        }
    }

    /**
     * Updates a source code file.
     * <p>
     * This makes use of beanshell scripts to update different information parts in source code. There is one set of scripts required
     * for each language to support. There can of course be several set of scripts for the same language handling the information
     * in different ways. For example one that uses annotations and one that uses comment block.
     * <p>
     * The structure of a source code updater looks like this:<br>
     * The "codelicmgr.sourcecodeupdaters" package should have a property file named "{language}.properties". Please note that
     * the {language} part should preferrably be the extension used by source files of that language. That makes it work with
     * extension matching (see below). The property file has the following properties:
     * <p>
     * <pre>
     *   Required:
     *     lang.fullname       - The full name of the language. For example cpp.properties could have lang.fullname=C++
     *     lang.scriptdir      - The path relative to the properties file where the scripts for the updater are. This
     *                           allows for having several different updaters in the same jar.
     *
     *   Optional:
     *     updater.properties  - Names another property file in the same path that defines the other optional properties.
     *                           This allows for several language properties to point to the same updater information.
     *     updater.description - A description of the updater.
     *     updater.copyright   - A copyright message for the updater.
     *     updater.maintainer  - A name and email to the mainainer(s) of the updater.
     * </pre>
     * <p>
     * The language to update for is either resolved from source file extension (default if not set in configuration) or
     * by configuring the language. When configured all source files must be of the same language. When going by extension
     * there can be a mixture. The following scripts are expected to be found in each scriptdir:
     * <p>
     * <pre>
     *     init.bsh
     *         Supplies imports, support functions, classes, object instances, etc needed by the other scripts.
     *         All scripts are executed in the same context and in the order they are specified here.
     *
     *     updateImports.bsh
     *         Checks for required imports (needed if annotations are used) and adds required imports if not found.
     *
     *     updateProjectInfo.bsh
     *         Checks if project information is available in the source file and replaces it with the latest information
     *         from the configuration. If no project information is available it is added. If this script is run or not
     *         is determined by the configuration.
     *
     *     updateCopyrightInfo.bsh
     *         Checks if copyright information is available in the source file and replaces it with the latest information
     *         from the configuration. If not copyright information is available it is added. If this script is run or not
     *         is determined by the configuraiton.
     *
     *     updateLicenseInfo.bsh
     *         Checks if license information is available in the source file and replaces it with the latest information
     *         from the configuration. If no license information is available it is added. This script always runs.
     *
     *     updateAuthorsBlock.bsh
     *         Checks if authors information is available. If it is nothing is done. If not it is added with name of user
     *         running and current date. If this script is run or not is determined by the configuration.
     *
     *     userBefore.bsh
     *         This is run after init.bsh but before anything else. It is for user specific updaters that uses
     *         information in the &lt;userData/&gt; config section. You can of course put user data information in
     *         any of the scripts when you make your own updater. This is just a convenience that allows you to extend
     *         another updater by only supplying this and/or userAfter.bsh and then put your extension first in classpath.
     *
     *     userAfter.bsh
     *         This is run after all other scripts. It is for user specific updaters that uses
     *         information in the <userData/> config section. You can ofcourse put user data information in
     *         any of the scripts when you make your own updater. This is just a convenience that allows you to extend
     *         another updater by only supplying this and/or userBefore.sh adn then put your extension first in classpath.
     * </pre>
     * <p>
     * Any script that you dont want to support for a language implementation can be left blank. The script must however
     * be available.
     * <p>
     * Different variants of the same language must ofcourse reside in separate jars and the jar of the variant wanted
     * should be made available in the classpath, but implementations for different languages can be made available in
     * the same jar. The most flexible solution is however to provide a separate jar for each language implementation.
     *
     * @param file The file to update.
     * @param language If set will override file extension to determine programming language. Empty string and null will be treated as not set.
     * @param licenseManager The CodeLicenseManager instance to use.
     * @param verbose If true verbose output is provided.
     *
     * @throws IOException
     * @throws ScriptingException
     */
    public static void updateSrc(File file, String language, CodeLicenseManager licenseManager, boolean verbose) throws IOException, ScriptingException {

        String useLang = null;
        if (language == null) {
            int extIx = file.getName().lastIndexOf(".");
            String ext = file.getName().substring(extIx + 1);
            useLang = ext;
        }
        else {
            useLang = language;
        }

        SourceCodeUpdater updater = new SourceCodeUpdater(useLang, licenseManager, verbose);

        Display.setIndent("  ");

        updater.updateSrc(file);
        
        Display.setIndent("");
    }

    /**
     * Deletes information previously added by updateSrc().
     *
     * @param file The file to update.
     * @param language If set will override file extension to determine programming language. Empty string and null will be treated as not set.
     * @param licenseManager The CodeLicenseManager instance to use.
     * @param verbose If true verbose output is provided.
     *
     * @throws IOException
     * @throws ScriptingException
     */
    public static void deleteSrc(File file, String language, CodeLicenseManager licenseManager, boolean verbose) throws IOException, ScriptingException {
        String useLang = null;
        if (language == null) {
            int extIx = file.getName().lastIndexOf(".");
            String ext = file.getName().substring(extIx + 1);
            useLang = ext;
        }
        else {
            useLang = language;
        }

        SourceCodeUpdater updater = new SourceCodeUpdater(useLang, licenseManager, verbose);

        Display.setIndent("  ");

        updater.deleteSrc(file);

        Display.setIndent("");
    }

    /**
     * This will updater source files by running only the scripts specified in the configuration. No language updater library will be applied.
     *
     * @param file The source file to update.
     * @param licenseManager The license manager instance to use.
     * @param verbose provide verbose output if true.
     *
     * @throws IOException On failure to load file into editor.
     * @throws ScriptingException On failure to execute scripts.
     */
    public static void updateSrcWithConfigScriptsOnly(File file, CodeLicenseManager licenseManager, boolean verbose) throws IOException, ScriptingException {
        SourceCodeUpdater updater = new SourceCodeUpdater(licenseManager, verbose);

        Display.setIndent("  ");

        updater.loadFileAndrunConfigScripts(file);

        Display.setIndent("");
    }

    /**
     * Updates the licence information in the specified file.
     *
     * @param file The file to update.
     *
     * @throws IOException
     * @throws ScriptingException
     */
    private void updateSrc(File file) throws IOException, ScriptingException {

        verbose("Updating file '" + file + "' of language '" + this.scriptMgr.getLanguageFullname() + "' ...");

        this.editor.load(file);
        this.editor.setAllowLoadSave(false); // Scripts should not load or save anything!

        this.editor.moveToTopOfFile();
        this.scriptMgr.runScriptIfAvailable("userBefore");

        this.editor.moveToTopOfFile();
        this.scriptMgr.runScript("updateImports");

        if (this.config.getCodeOptions().isUpdateLicenseInfo()) {
            this.editor.moveToTopOfFile();
            this.scriptMgr.runScript("updateLicenseInfo");
        }

        if (this.config.getCodeOptions().isUpdateCopyright()) {
            this.editor.moveToTopOfFile();
            this.scriptMgr.runScript("updateCopyrightInfo");
            this.scriptMgr.runLocalScript("copyrightSupport");
         
        }
        if (this.config.getCodeOptions().isUpdateProject()) {
            this.editor.moveToTopOfFile();
            this.scriptMgr.runScript("updateProjectInfo");
        }
        if (this.config.getCodeOptions().isAddAuthorsBlock()) {
            this.editor.moveToTopOfFile();
            this.scriptMgr.runScript("updateAuthorsBlock");
        }

        this.editor.moveToTopOfFile();
        this.scriptMgr.runScriptIfAvailable("userAfter");

        runConfigScripts(file);

        this.editor.setAllowLoadSave(true);
        this.editor.save();
            
    }

    /**
     * Deletes previously added project and license information from source.
     *
     * @param file The file to update.
     * 
     * @throws IOException
     * @throws ScriptingException
     */
    private void deleteSrc(File file) throws IOException, ScriptingException {

        verbose("Updating file '" + file + "' of language '" + this.scriptMgr.getLanguageFullname() + "' ...");

        this.editor.load(file);
        this.editor.setAllowLoadSave(false); // Scripts should not load or save anything!

        this.editor.moveToTopOfFile();
        this.scriptMgr.runScriptIfAvailable("userDeleteBefore");

        if (this.config.getCodeOptions().isUpdateProject()) {
            this.editor.moveToTopOfFile();
            this.scriptMgr.runScriptIfAvailable("deleteProjectInfo");
        }

        if (this.config.getCodeOptions().isUpdateCopyright()) {
            this.editor.moveToTopOfFile();
            this.scriptMgr.runScriptIfAvailable("deleteCopyrightInfo");
        }

        if (this.config.getCodeOptions().isUpdateLicenseInfo()) {
            this.editor.moveToTopOfFile();
            this.scriptMgr.runScriptIfAvailable("deleteLicenseInfo");
        }

        this.editor.moveToTopOfFile();
        this.scriptMgr.runScriptIfAvailable("deleteImports");

        this.editor.moveToTopOfFile();
        this.scriptMgr.runScriptIfAvailable("userDeleteAfter");

        this.editor.setAllowLoadSave(true);
        this.editor.save();
        
    }

    /**
     * Runs the scripts specified in the configuraton.
     * 
     * @param file The file these scripts are being applied to.
     * 
     * @throws ScriptingException on any script failure.
     */
    private void runConfigScripts(File file) throws ScriptingException {
        if (this.config.getScripts() != null) {
            for (ScriptConfig script : this.config.getScripts().getScripts()) {
                if (file.toString().matches(script.getFileFilter())) {
                    verbose("Updating file '" + file + "' ...");
                    this.editor.moveToTopOfFile();
                    if (script.getScriptFile() != null && script.getScriptFile().trim().length() > 0) {
                        this.scriptMgr.runScriptFromFile(new File(script.getScriptFile()));
                    }
                    else {
                        this.scriptMgr.eval(script.getCode());
                    }
                }
            }
        }
    }

    /**
     * This will load a file into the editor and call runConfigScripts(). This is an alternative to updateSrc()
     * for running only the scripts in the configuration.
     * 
     * @param file The file to load into the editor.
     *
     * @throws IOException on failure to load file.
     * @throws ScriptingException on failure to execute scripts.
     */
    private void loadFileAndrunConfigScripts(File file) throws IOException, ScriptingException {
        this.editor.load(file);
        this.editor.setAllowLoadSave(false); // Scripts should not load or save anything!

        runConfigScripts(file);

        this.editor.setAllowLoadSave(true);
        this.editor.save();
    }



    /**
     * An implementation of this interface must be passed to formatToTextBuffer(...).
     */
    public interface FormatToTextBufferLineProvider {
        public String provideLine(String origLine);
    }

    /**
     * Convert a possible multi line string into a TextBuffer.
     * 
     * @param buffer The TextBuffer to put result in.
     * @param possiblyMultiLineText The String to convert.
     * @param continueOnNextLineEnding The ending of a line that indicates the string continues on the next line. Example: "," or " +".
     * @param trim if true lines are trimmed.
     * @param lineProvider A FormatToTextBufferLineProvider implementation to supply a possible formated line to add to the buffer.
     * 
     * @return The passed buffer.
     */
    public TextBuffer formatToTextBuffer(TextBuffer buffer, String possiblyMultiLineText, String continueOnNextLineEnding, boolean trim, FormatToTextBufferLineProvider lineProvider) {
        StringReader stringReader = new StringReader(possiblyMultiLineText);
        BufferedReader br = new BufferedReader(stringReader);
        try {
            String lineEnd = continueOnNextLineEnding;
            List<String> lines = new ArrayList<String>();
            String line = br.readLine();
            while (line != null) {
                if (trim) {
                    line = line.trim();
                }
                lines.add(lineProvider.provideLine(line));
                line = br.readLine();
            }
            for (int i = 0; i < lines.size(); i++) {
                if ((i + 1) == lines.size()) {
                    lineEnd = "";
                }
                buffer.addLine(lines.get(i) + lineEnd);
            }
        }
        catch (IOException ioe) {
            // This really should not happen!
            buffer.addLine(ioe.getMessage());
        }
        finally {
            try {
                br.close();
            }
            catch (IOException ioe) {
                // Do nothing!
            }
        }

        return buffer;
    }

    /**
     * Formats a multiline text block to source code.
     *
     * @param buffer The buffer to put formatted result in.
     * @param text The text to format.
     * @param lineEnd The string that terminates each line. For example " +" or ",".
     * @param stringSpec A character or set of characters that indicates start and end of a String. For eample "\"" or "'".
     * @param escapedStringSpec A character or set of characters that indicates an escaped start and end of a string. For example "\\\�" or "\\'"
     * @param indent The number of spaces to indent each line.
     * @param trim If true each line is also trimmed.
     */
    public void formatMultiLineTextToCode(TextBuffer buffer, String text, String lineEnd, final String stringSpec, final String escapedStringSpec, int indent, boolean trim) {
        formatMultiLineTextToCode(buffer, text, "", lineEnd, stringSpec, escapedStringSpec, indent, trim);
    }

    /**
     * Formats a multiline text block to source code.
     *
     * @param buffer The buffer to put formatted result in.
     * @param text The text to format.
     * @param lineEnd The string that terminates each line. For example " +" or ",".
     * @param lineBeg The beginning of each line. The indent will be applied after this.
     * @param stringSpec A character or set of characters that indicates start and end of a String. For eample "\"" or "'".
     * @param escapedStringSpec A character or set of characters that indicates an escaped start and end of a string. For example "\\\�" or "\\'"
     * @param indent The number of spaces to indent each line.
     * @param trim If true each line is also trimmed.
     */
    public void formatMultiLineTextToCode(TextBuffer buffer, String text, final String lineBeg, String lineEnd, final String stringSpec, final String escapedStringSpec, int indent, boolean trim) {
        final String indentStr = createIndent(indent);

        formatToTextBuffer(buffer, text, lineEnd, trim,
            new FormatToTextBufferLineProvider() {
                public String provideLine(String line) {
                    return lineBeg + indentStr + stringSpec + line.replaceAll(stringSpec, escapedStringSpec) + stringSpec;
                }
            }
        );
    }

    /**
     * Creates a String with size amount of spaces.
     * 
     * @param size The number of spaces to create.
     */
    public String createIndent(int size) {
        StringBuilder indentsb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            indentsb.append(" ");
        }
        return indentsb.toString();
    }

    /**
     * This will try to position the editor in an appropriate insert position according to the arguments.
     *
     * @param edtitor The editor to position.
     * @param endOfSection The end of section command. Examples: "matching:()" or "startsWith,noSpaceAt:'# ',2"
     * @param endOfCommentSearch A regular expression that terminates the search for a comment before reaching end of file. Can be null.
     * @param startComment The start of a comment.
     * @param middleComment The middle of a comment.
     * @param endComment The end of a comment.
     * @param searchStrings Comma separated search strings where each begings with 'f/l' for first/last, and then 'b/a' for insert before/after,
     *                      followed by ':' and then the search value.
     */
    public boolean findInsertPosition(TextFileEditor edtitor, String endOfSection, String endOfCommentSearch, String startComment, String middleComment, String endComment, String searchStrings) {

        StringTokenizer tokenizer = new StringTokenizer(searchStrings, ",");

        boolean found = false;
        while (tokenizer.hasMoreTokens() && !found) {
            String searchString = tokenizer.nextToken();

            String search = null;
            boolean findFirst = true;
            boolean insertAfter = true;
            boolean section = false;
            boolean belowComment = false;
            boolean newLine = false;

            StringTokenizer argsTokenizer = new StringTokenizer(searchString, ";");
            while (argsTokenizer.hasMoreTokens()) {
                String argValue = argsTokenizer.nextToken();

                StringTokenizer argValueTokenizer = new StringTokenizer(argValue, "=");
                String arg = argValueTokenizer.nextToken();
                if (!argValueTokenizer.hasMoreTokens()) {
                    throw new IllegalArgumentException("Each comma separated part of the searchStrings contains a semicolon separated arg=value part. Arg '" +
                            arg + "' is however missing a value!");
                }
                String value = argValueTokenizer.nextToken();

                if (arg.equals("find")) {
                    if (value.equals("last")) {
                        findFirst = false;
                    }
                }
                else if (arg.equals("insert")) {
                    if (value.equals("before")) {
                        insertAfter = false;
                    }
                }
                else if (arg.equals("section")) {
                    if (value.equals("true")) {
                        section = true;
                    }
                }
                else if (arg.equals("search")) {
                    if (value.charAt(0) == '\'') {
                        value = value.substring(1, value.length() - 1);
                    }
                    search = value;
                }
                else if (arg.equals("belowComment")) {
                    if (value.equals("true")) {
                        belowComment = true;
                    }
                }
                else if (arg.equals("newLine")) {
                    if (value.equals("true")) {
                        newLine = true;
                    }
                }
                else {
                    throw new IllegalArgumentException("Unknown \"findInsertPosition\" argument '" + arg + "'!");
                }
            }

            if (findFirst) {
                found = editor.find(search);
            }
            else {
                found = editor.findLast(search);
            }

            if (found) {
                if (!insertAfter) {
                    editor.moveUp(1);
//                    if (editor.isOnFirstLine()) {
//                        editor.insertLineAbove("");
//                    }
                }
                else {
                    if (section) {
                        this.scriptMgr.eval(endOfSection);
                    }
                }

                if (belowComment) {
                    Position position = editor.getPosition();
                    if (editor.find(startComment, endOfCommentSearch)) {
                        if (editor.getPosition().getLine() > 30) {
                            editor.setPosition(position);
                        }
                        else {
                            if (!editor.find(endComment)) {
                                throw new IllegalArgumentException("Found a start comment, but no end comment! Bad source code!");
                            }
                        }
                    }
                }

                if (newLine) {
                    editor.insertLine("");
                }
            }
        }

        if (!found) {
            if (startComment.trim().length() > 0 && middleComment.length() > 0 && endComment.trim().length() > 0) {
                if (!editor.find(startComment.replace("-", "\\-").replace("*", "\\*").replace("[", "\\[").replace("]", "\\]").
                        replace("(", "\\(").replace(")", "\\)").replace("^", "\\^"), endOfCommentSearch)) {
                    if (editor.isOnFirstLine()) {
                        editor.insertLineAbove(startComment);
                    }
                    else {
                        editor.insertLine(startComment);
                    }
                    editor.insertLine(middleComment);
                    editor.insertLine(endComment);
                    editor.moveUp(1);
                }
            }
        }
        else {
            if (startComment.trim().length() > 0 && middleComment.length() > 0 && endComment.trim().length() > 0) {
                if (!editor.getLine().startsWith(startComment) && !editor.getLine().startsWith(middleComment) && !editor.getLine().startsWith(endComment)) {
                    if (editor.isOnFirstLine()) {
                        editor.insertLineAbove(startComment);
                    }
                    else {
                        editor.insertLine(startComment);
                    }
                    editor.insertLine(middleComment);
                    editor.insertLine(endComment);
                    editor.moveUp(1);
                }
            }
        }

        return found;
    }

    /**
     * Moves to the end of a section.
     *
     * @param editor The editor to move.
     * @param starting the starting char whose matching ending char indicates the end of the section.
     * @param ending The ending char matching the starting char and that indicates the end of the section.
     */
    public void moveToEndOfSection(TextFileEditor editor, char starting, char ending) {
        editor.moveToMatching(starting, ending);
    }

    /**
     * Moves to the end of a section. An end of section is indicated by both startsWith and noSpaceAt
     * being true. 
     *
     * @param editor The editor to move.
     * @param startsWith The string the end of section line should start with.
     * @param noSpaceAt The character at this position should not contain a space.
     */
    public void moveToEndOfSection(TextFileEditor editor, String startsWith, int noSpaceAt) {
        boolean done = false;
        while (!done) {
            editor.moveDown(1);
            String line = editor.getLine();
            if (line.trim().equals("") || (line.startsWith(startsWith) && line.length() > noSpaceAt && line.charAt(noSpaceAt) != ' ') || editor.isOnLastLine()) {
              done = true;
            }
        }
        editor.moveUp(1);
        editor.moveToEndOfLine();
    }
}
