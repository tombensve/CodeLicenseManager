package se.natusoft.tools.codelicmgr;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import se.natusoft.tools.codelicmgr.annotations.Author;
import se.natusoft.tools.codelicmgr.annotations.Authors;
import se.natusoft.tools.codelicmgr.annotations.Change;
import se.natusoft.tools.codelicmgr.annotations.Copyright;
import se.natusoft.tools.codelicmgr.annotations.License;
import se.natusoft.tools.codelicmgr.annotations.Project;
import se.natusoft.tools.codelicmgr.enums.Source;
import se.natusoft.tools.codelicmgr.config.Configuration;
import se.natusoft.tools.codelicmgr.config.ProductConfig;
import se.natusoft.tools.codelicmgr.config.ThirdpartyLicenseConfig;
import se.natusoft.tools.codelicmgr.library.LibraryLicense;
import se.natusoft.tools.codelicmgr.library.LicenseLibrary;
import se.natusoft.tools.codelicmgr.scripting.ScriptingException;
import se.natusoft.tools.fileeditor.TextFileEditor;

@Project(
    name="CodeLicenseManager-manager",
    codeVersion="2.0",
    description="Manages project and license information in project sourcecode" +
                "and provides license text files for inclusion in builds. Supports" +
                "multiple languages and it is relatively easy to add a new" +
                "language and to make your own custom source code updater."
)
@Copyright(year="2009", holder="Natusoft AB", rights="All rights reserved.")
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
            @Change(when="2008-11-23", description="Created"),
            @Change(when="2009-10-30", description="Updated with new optoins and project info."),
            @Change(when="2010-01-03", description="Now provides 'userName' and 'userEmail' variables to update scripts " +
                                                   "loaded from {user.home}/[.]clm-user.properties.")
        }
    )
})
/**
 * Manages Licence and project information. This is the central class that each executor
 * (maven plugin, ant task, java -jar main) should call with collected configuration.
 */
public class CodeLicenseManager {

    //
    // Private Members
    //

    /** Holds the tool configuration. */
    private Configuration config;

    /** The licence information block to insert into each source code file. */
    private String sourceBlock;

    /** The selected tpLicense loaded from the license library. */
    private LibraryLicense libLic = null;

    /** The name of the running user. */
    private String userName;

    /** The email of the running user. */
    private String userEmail;

    //
    // Constructors
    //

    /**
     * Creates a new CodeLicenceManager instance. One must be created for each type and version.
     * <p>
     * This will find a (licenceType)[-(licenceVersion)].properties file under codelicmgr/licences. The properties file
     * should contain a "type", "version", "description", "sourceblock", and a "fulltext" property. The type and version
     * properties should preferrably match those of the properties file name.
     * "sourceblock" and "fulltext" points to filenames from the same path that contains the block of text to insert from
     * source files and the full licence text to copy somewhere it can be included from the packaging.
     * <p>
     * It also needs a set of configuration with project, copyright and tpLicense information to update source code
     * headers with.
     *
     * @param config The configuration for this tool.
     *
     * @throws CodeLicenseException if bad licence data is passed.
     */
    public CodeLicenseManager(Configuration config) throws CodeLicenseException {
        this(config, true);
    }

    /**
     * Creates a new CodeLicenceManager instance. One must be created for each type and version.
     * <p>
     * This will find a (licenceType)[-(licenceVersion)].properties file under codelicmgr/licences. The properties file
     * should contain a "type", "version", "description", "sourceblock", and a "fulltext" property. The type and version
     * properties should preferrably match those of the properties file name.
     * "sourceblock" and "fulltext" points to filenames from the same path that contains the block of text to insert from
     * source files and the full licence text to copy somewhere it can be included from the packaging.
     * <p>
     * It also needs a set of configuration with project, copyright and tpLicense information to update source code
     * headers with.
     *
     * @param config The configuration for this tool.
     * @param loadLicenseLib if true the a tpLicense library will be loaded from the project configuration. This should from general be true.
     *
     * @throws CodeLicenseException if bad licence data is passed.
     */
    public CodeLicenseManager(Configuration config, boolean loadLicenseLib) throws CodeLicenseException {
        this.config = config;

        Display.msg("CodeLicenseManager 2.0\nMaintained by Tommy Svensson (tommy@natusoft.se)");

        if (loadLicenseLib) {

            this.libLic = LicenseLibrary.getLicense(this.config.getProject().getLicense(), ""/* unknown url. */);

            this.sourceBlock = this.libLic.getLicenseSourceBlock();
        }

        this.userName = System.getProperty("user.name");
        this.userEmail = "";

        // Check for a .clm-user.properties
        Properties userProps = new Properties();
        FileInputStream userPropsStream = null;
        try {
            File userPropsFile = new File(System.getProperty("user.home") + File.separator + ".clm-user.properties");
            userPropsStream = new FileInputStream(userPropsFile);
        }
        catch (FileNotFoundException fnfe) {
            try {
                File userPropsFile = new File(System.getProperty("user.home") + File.separator + "clm-user.properties");
                userPropsStream = new FileInputStream(userPropsFile);
            }
            catch (FileNotFoundException fnfe2) {
                // Do nothing, this is OK.
            }
        }
        if (userPropsStream != null) {
            try {
                userProps.load(userPropsStream);
                String name = userProps.getProperty("name");
                if (name == null) {
                    Display.msg("WARNING: Loaded [.]clm-user.properties, but found no 'name' property in it!");
                }
                else {
                    this.userName = name;
                }
                String email = userProps.getProperty("email");
                if (email == null) {
                    Display.msg("WARNING: Loaded [.]clm-user.properties, but found no 'email' property in it!");
                }
                else {
                    this.userEmail = email;
                }
            }
            catch (IOException ioe) {
                Display.msg("WARNING: Found a [.]clm-user.properties file, but failed to load it! [" + ioe.getMessage() + "]");
            }
        }
    }

    //
    // Methods
    //

    /**
     * Updates source files from all configured source directories with project, copyright and tpLicense information.
     * <p>
     * This will execute source update beanshell scripts from one or more "*-source-updater-*" packages.
     * Please note that some of those might be from a third party and not part of CodeLicenseManager
     * distribution!
     *
     * @throws IOException
     */
    public void updateSourceFilesForAllSourceDirs(File projectRoot) throws IOException, ScriptingException {
        for (String dir : this.config.getCodeOptions().getSourceCodeDirs()) {
            SourcePath sourcePath = new SourcePath(projectRoot, dir);
            Display.msg("Scanning for source files to update at " + sourcePath);
            updateSourceFiles(sourcePath);
        }
    }
    
    /**
     * Applies the source code updater on all found source files.
     * <p>
     * This will execute source update beanshell scripts from one or more "*-source-updater-*" packages.
     * Please note that some of those might be from a third party and not part of CodeLicenseManager
     * distribution!
     *
     * @param sourcePath A specification of files to update.
     *
     * @throws IOException when shit happens.
     * @throws ScriptingException if update scripts fail.
     */
    public void updateSourceFiles(SourcePath sourcePath) throws IOException, ScriptingException {
        List<File> files = sourcePath.getSourceFiles();
        
        for (File current : files) {
            updateSourceFile(current);
        }
    }

    /**
     * Updates the specified source file with the source block for the specified licence type.
     * <p>
     * This will execute source update beanshell scripts from one or more "*-source-updater-*" packages.
     * Please note that some of those might be from a third party and not part of CodeLicenseManager
     * distribution!
     *
     * @param file The file to update.
     *
     * @throws IOException when shit happens.
     */
    public void updateSourceFile(File file) throws IOException, ScriptingException {
        String lang = null;
        if (this.config.getCodeOptions().haveCodeLanguage()) {
            lang = this.config.getCodeOptions().getCodeLanguage();
        }
        SourceCodeUpdater.updateSrc(file, lang, this, this.config.getCodeOptions().isVerbose());
    }

    /**
     * Deletes information previously added by updateSourceFile*() in all files in all specified
     * source paths.
     *
     * @param projectRoot The project root dir.
     * 
     * @throws IOException
     * @throws ScriptingException
     */
    public void deleteInSourceFilesForAllSourceDirs(File projectRoot) throws IOException, ScriptingException {
        for (String dir : this.config.getCodeOptions().getSourceCodeDirs()) {
            SourcePath sourcePath = new SourcePath(projectRoot, dir);
            Display.msg("Scanning for source files to update at " + sourcePath);
            deleteInSourceFiles(sourcePath);
        }
    }

    /**
     * Deletes information previously added by updateSourceFile*() in all files included in the
     * specified source path.
     * 
     * @param sourcePath A specification of files to update.
     * 
     * @throws IOException
     * @throws ScriptingException
     */
    public void deleteInSourceFiles(SourcePath sourcePath) throws IOException, ScriptingException {
        List<File> files = sourcePath.getSourceFiles();

        for (File current : files) {
            deleteInSourceFile(current);
        }
    }

    /**
     * Deletes information previously added by updateSourceFile*(). 
     * 
     * @param file The file to update.
     *
     * @throws IOException
     * @throws ScriptingException
     */
    public void deleteInSourceFile(File file) throws IOException, ScriptingException {
        String lang = null;
        if (this.config.getCodeOptions().haveCodeLanguage()) {
            lang = this.config.getCodeOptions().getCodeLanguage();
        }
        SourceCodeUpdater.deleteSrc(file, lang, this, this.config.getCodeOptions().isVerbose());
    }

    /**
     * Updates the configured source files by only running scripts specified from configuration an not language scripts
     * from updater library. This is more of a utility and doesn't really have anything to do with updating tpLicense
     * information. This just lets users run their own scripts for their own purposes.
     *
     * @param projectRoot The project root dir.
     *
     * @throws IOException
     * @throws ScriptingException
     */
    public void updateSourceFilesUsingOnlyConfigScripts(File projectRoot) throws IOException, ScriptingException {
        for (String dir : this.config.getCodeOptions().getSourceCodeDirs()) {
            SourcePath sourcePath = new SourcePath(projectRoot, dir);
            Display.msg("Scanning for source files to update at " + sourcePath);
            updateSourceFilesUsingOnlyConfigScripts(sourcePath);
        }
    }

    /**
     * Updates the specified source file by only running scripts specified from configuration an not language scripts
     * from updater library. This is more of a utility and doesn't really have anything to do with updating tpLicense
     * information. This just lets users run their own scripts for their own purposes.
     *
     * @param sourcePath The source path to update.
     *
     * @throws IOException
     * @throws ScriptingException
     */
    public void updateSourceFilesUsingOnlyConfigScripts(SourcePath sourcePath) throws IOException, ScriptingException {
        List<File> files = sourcePath.getSourceFiles();

        for (File current : files) {
            updateSourceFileUsingOnlyConfigScripts(current);
        }
    }

    /**
     * Updates the specified source file by only running scripts specified from configuration an not language scripts
     * from updater library. This is more of a utility and doesn't really have anything to do with updating tpLicense
     * information. This just lets users run their own scripts for their own purposes.
     *
     * @param file The file to update.
     *
     * @throws IOException
     * @throws ScriptingException
     */
    public void updateSourceFileUsingOnlyConfigScripts(File file) throws IOException, ScriptingException {
        SourceCodeUpdater.updateSrcWithConfigScriptsOnly(file, this, this.config.getCodeOptions().isVerbose());
    }

    /**
     * Copies tpLicense files to tpLicense dir and thirdparty tpLicense dir. This is the simple copy-all-tpLicense-files method.
     *
     * @throws IOException when shit happens.
     */
    public void copyAllLicenses() throws IOException {
        copyProjectLicense();
        copyThirdpartyLicenses();
    }

    /**
     * Copies tpLicense files to tpLicense dir.
     *
     * @throws IOException when shit happens.
     */
    public void copyProjectLicense() throws IOException {
        File licDir = new File(this.config.getInstallOptions().getLicenseDir());
        licDir.mkdirs();
        copyFullLicenseTextToDir(licDir);
    }

    /**
     * Copies tpLicense files to tpLicense dir.
     *
     * @param rootDir The root dir to which the install dir is relative.
     *
     * @throws IOException when shit happens.
     */
    public void copyProjectLicense(File rootDir) throws IOException {
        File licDir = new File(rootDir, this.config.getInstallOptions().getLicenseDir());
        licDir.mkdirs();
        copyFullLicenseTextToDir(licDir);
    }

    /**
     * Copies third party tpLicense files to third party tpLicense dir.
     *
     * @throws IOException when shit happens.
     */
    public void copyThirdpartyLicenses() throws IOException {
        copyThirdpartyLicenses(this.config.getInstallOptions().getThirdpartyLicenseDir());
    }

    /**
     * Copies third party tpLicense files to third party tpLicense dir.
     *
     * @param thirdpartyLicenseDir The directory to copy the third party licenses to.
     *
     * @throws IOException when shit happens.
     * @throws CodeLicenseException If a third party tpLicense cannot be found from or loaded from tpLicense library.
     */
    public void copyThirdpartyLicenses(String thirdpartyLicenseDir) throws IOException, CodeLicenseException {
        copyThirdpartyLicenses(new File(thirdpartyLicenseDir));
    }

    /**
     * Copies third party tpLicense files to third party tpLicense dir.
     *
     * @param thirdpartyLicenseDir The directory to copy the third party licenses to.
     *
     * @throws IOException when shit happens.
     * @throws CodeLicenseException If a third party tpLicense cannot be found from or loaded from tpLicense library.
     */
    public void copyThirdpartyLicenses(File thirdpartyLicenseDir) throws IOException, CodeLicenseException {
        if (this.config.getThirdpartyLicenses() != null) {
            if (this.config.getThirdpartyLicenses().getLicenses() != null && !this.config.getThirdpartyLicenses().getLicenses().isEmpty()) {
                thirdpartyLicenseDir.mkdirs();
                for (ThirdpartyLicenseConfig tpLicense : this.config.getThirdpartyLicenses().getLicenses()) {
                    try {
                        LibraryLicense tpLibraryLic = LicenseLibrary.getThirdpartyLicense(tpLicense);
                        if (!tpLibraryLic.isDownloadable()) {
                            try {
                                copyFullLicenseTextToDir(thirdpartyLicenseDir, tpLibraryLic, tpLicense.getType() + "-" +
                                        tpLicense.getVersion(), tpLicense);
                            }
                            catch (IOException ioe) {
                                Display.msg("Failed to copy license '" + tpLicense.getType() + "-" + tpLicense.getVersion() + "' to '" +
                                        thirdpartyLicenseDir + "' due to '" + ioe.getMessage() + "'!");
                            }
                        }
                        else {
                            downloadLicenseTextToDir(thirdpartyLicenseDir, tpLicense.getLicenseUrl(), tpLicense.getType() + "-" +
                                    tpLicense.getVersion());
                        }
                        writeLicenseUsers(thirdpartyLicenseDir, tpLicense);
                    }
                    catch (CodeLicenseException cle) {
                        Display.msg("Failed to find license properties for license of type '" + tpLicense.getType() + "' and version '" +
                                tpLicense.getVersion() + "'. Cannot copy this license text to " + thirdpartyLicenseDir +"!");
                    }
                }
            }
        }
    }

    /**
     * Writes a file listing users of a tpLicense.
     *
     * @param dir The directory to write tpLicense users file from.
     * @param license The third pary license configuration.
     * @throws IOException
     */
    private void writeLicenseUsers(File dir, ThirdpartyLicenseConfig license) throws IOException {
        File outFile = new File(dir, license.getType() + "-" + license.getVersion() + "-users.txt");
        PrintStream ps = new PrintStream(new FileOutputStream(outFile));

        ps.println("The following products are using the " + license.getType() + " version " + license.getVersion() + " license:");
        ps.println();
        for (ProductConfig product : license.getProducts().getProducts()) {
            ps.println(product.getName() + " version " + product.getVersion());
            ps.println(product.getWeb());
            ps.println();
        }

        ps.close();
    }



    /**
     * Copies the full licence text as (licenceType)[-(licenceVersion)]-licence.txt into the specified directory.
     *
     * @param dir The directory to copy licence text into.
     *
     * @throws IOException when shit happens.
     */
    public void copyFullLicenseTextToDir(File dir) throws IOException {
        copyFullLicenseTextToDir(dir, this.libLic);
    }

    /**
     * Copies the full licence text as (licenceType)[-(licenceVersion)]-licence.txt into the specified directory.
     * <p>
     * This variant is for installing third party licences.
     *
     * @param dir The directory to copy licence text into.
     * @param libraryLicense The library tpLicense whose text should be copied.
     *
     * @throws IOException when shit happens.
     */
    public void copyFullLicenseTextToDir(File dir, LibraryLicense libraryLicense) throws IOException {
        copyFullLicenseTextToDir(dir, libraryLicense, this.config.getProject().getLicense().getType() + "-" +
                this.config.getProject().getLicense().getVersion());
    }

    /**
     * Copies the full licence text as (licenceType)[-(licenceVersion)]-licence.txt into the specified directory.
     * <p>
     * This variant is for installing third party licences.
     *
     * @param dir The directory to copy licence text into.
     * @param libraryLicense The library tpLicense whose text should be copied.
     * @param thirdpartyLicense third party tpLicense information for third party licenses. For a project tpLicense this will be null.
     *
     * @throws IOException when shit happens.
     */
    public void copyFullLicenseTextToDir(File dir, LibraryLicense libraryLicense, ThirdpartyLicenseConfig thirdpartyLicense) throws IOException {
        copyFullLicenseTextToDir(dir, libraryLicense, this.config.getProject().getLicense().getType() + "-" +
                this.config.getProject().getLicense().getVersion(), thirdpartyLicense);
    }

    /**
     * Copies the full licence text as (licenceType)[-(licenceVersion)]-licence.txt into the specified directory.
     * <p>
     * This variant is for installing third party licences.
     *
     * @param dir The directory to copy licence text into.
     * @param libraryLicense The library tpLicense whose text should be copied.
     * @param targetName The name of the target copy, but without an extension. The extension will be .txt for text files
     *                   and .html for html files.
     *
     * @throws IOException when shit happens.
     */
    public void copyFullLicenseTextToDir(File dir, LibraryLicense libraryLicense, String targetName) throws IOException {
        copyFullLicenseTextToDir(dir, libraryLicense, targetName, null);
    }

    /**
     * Copies the full licence text as (licenceType)[-(licenceVersion)]-licence.txt into the specified directory.
     * <p>
     * This variant is for installing third party licences.
     *
     * @param dir The directory to copy licence text into.
     * @param libraryLicense The library tpLicense whose text should be copied.
     * @param targetName The name of the target copy, but without an extension. The extension will be .txt for text files
     *                   and .html for html files.
     * @param thirdpartyLicense third party tpLicense information for third party licenses. For a project tpLicense this will be null.
     *
     * @throws IOException when shit happens.
     */
    public void copyFullLicenseTextToDir(File dir, LibraryLicense libraryLicense, String targetName, ThirdpartyLicenseConfig thirdpartyLicense) throws IOException {
        copyFullLicenseTextToDir(dir, null, libraryLicense, targetName, thirdpartyLicense);
    }
    
    /**
     * Copies the full licence text as (licenceType)[-(licenceVersion)]-licence.txt into the specified directory.
     * <p>
     * This variant is for installing third party licences.
     *
     * @param dir The directory to copy licence text into. This is mutually exclusive with out. Of this is specified out must be null.
     * @param out The output stream to write to. This is mutually exclusive with dir. If this is specified dir must be null.
     * @param libraryLicense The library tpLicense whose text should be copied.
     * @param targetName The name of the target copy, but without an extension. The extension will be .txt for text files
     *                   and .html for html files.
     * @param thirdpartyLicense third party tpLicense information for third party licenses. For a project tpLicense this will be null.
     * 
     * @throws IOException when shit happens.
     */
    public void copyFullLicenseTextToDir(File dir, OutputStream out, LibraryLicense libraryLicense, String targetName, ThirdpartyLicenseConfig thirdpartyLicense) throws IOException {
        InputStream from = libraryLicense.getFullTextStream();
        String extension = libraryLicense.getFullTextExtension();

        if (from == null) {
            throw new IOException(libraryLicense.errorFilter("Specified licence to copy \"${fullLicenseTextFile}\" was not found!"));
        }

        Display.msg("Installing " + targetName + " into " + dir.toString());

        OutputStream to = null;
        if (dir != null) {
            dir.mkdirs();
            to = new BufferedOutputStream(new FileOutputStream(new File(dir, targetName + extension)));
        }
        else {
            to = out;
        }

        try {
            CopyTool.copyFile(from, to);
        }
        finally {
            try { from.close(); } catch (IOException ioe) {}
            to.flush();
            if (dir != null) {
                try { to.close(); } catch (IOException ioe) {}
            }
        }
    }

    public void downloadLicenseTextToDir(File dir, String licenseUrl, String targetName) {
        Display.msg("Didn't find license '"+ targetName + "' in local license library, so downloading license text from: " + licenseUrl);

        OutputStream to = null;
        InputStream from = null;
        try {
            URL url = new URL(licenseUrl);

            if (dir != null) {
                dir.mkdirs();
                to = new BufferedOutputStream(new FileOutputStream(new File(dir, targetName + ".html")));
            }

            from = url.openStream();
            CopyTool.copyFile(from, to);
        }
        catch (IOException ioe) {
            Display.msg("Error: failed to download license: " + ioe.getMessage());
        }
        finally {
            try { if (from != null) from.close(); } catch (IOException ioe) {}
            try { if (to != null) {to.flush();to.close();}} catch (IOException ioe) {}
        }
    }

    /**
     * Supplies data to the scripts.
     */
    public Map<String, Object> getProjectAndLicenseDataMap() {
        Map<String, Object> data = new HashMap<String, Object>();
        if (this.config.getProject() != null) {
            data.put("projectName", this.config.getProject().getName());
            data.put("hasProjectDescription", this.config.getProject().getDescription() != null && this.config.getProject().getDescription().trim().length() > 0);
            data.put("projectDescription", this.config.getProject().getDescription());
            data.put("hasProjectCodeVersion", this.config.getProject().getCodeVersion() != null && this.config.getProject().getCodeVersion().trim().length() > 0);
            data.put("projectCodeVersion", this.config.getProject().getCodeVersion());
            data.put("hasProjectSubProjectOf", this.config.getProject().getSubProjectOf() != null && this.config.getProject().getSubProjectOf().trim().length() > 0);
            data.put("projectSubProjectOf", this.config.getProject().getSubProjectOf());
            data.put("copyrights", this.config.getProject().getCopyrights());
        }
        if (this.libLic != null) {
            data.put("licenseType", this.libLic.getType());
            data.put("licenseVersion", this.libLic.getVersion());
            data.put("licenseDescription", this.libLic.getDescription());
            data.put("source", this.libLic.getSourceType() == Source.OPEN ? "Open" : "Closed");
            data.put("sourceBlock", this.sourceBlock);
        }
        data.put("userName", this.userName);
        data.put("userEmail", this.userEmail);

        return data;
    }

    /**
     * Returns the config.
     */
    public Configuration getConfig() {
        return this.config;
    }
}
