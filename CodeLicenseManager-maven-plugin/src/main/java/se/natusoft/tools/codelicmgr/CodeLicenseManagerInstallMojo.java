/* 
 * 
 * PROJECT
 *     Name
 *         CodeLicenseManager-maven-plugin
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

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import se.natusoft.tools.codelicmgr.config.Configuration;
import se.natusoft.tools.codelicmgr.config.InstallOptionsConfig;
import se.natusoft.tools.codelicmgr.config.ProjectConfig;
import se.natusoft.tools.codelicmgr.config.ThirdpartyLicensesConfig;

import java.io.File;

/**
 * Goal which install license files for build.
 * 
 * @requiresDependencyResolution
 *
 * @goal install
 *
 * @phase install
 */
public class CodeLicenseManagerInstallMojo
    extends AbstractMojo
{

    //
    // Confiuration
    //

    /**
     * The name and description of the project.
     *
     * @parameter
     * @optional
     */
    private ProjectConfig project = new ProjectConfig();

    /**
     * Thrid party licences.
     *
     * @parameter
     * @optional
     */
    private ThirdpartyLicensesConfig thirdpartyLicenses = new ThirdpartyLicensesConfig();

    /**
     * Provides install options.
     *
     * @parameter
     * @optional
     */
    private InstallOptionsConfig installOptions = new InstallOptionsConfig();

    /**
     * If set to true third party licenses will be resolved from dependencies.
     * 
     * @parameter expression="true"
     * @optional
     */
    private boolean autoResolveThirdPartyLicenses = true;

    /**
     * Set this to false to not create .../site/licenses/licences.apt and specific license documents.
     *
     * @parameter expression="false"
     * @optional
     */
    private boolean createLicencesAPT = false;

    /**
     * Creates markdown documents with license information.
     *
     * @parameter expression="false"
     * @optional
     */
    private boolean createLicensesMarkdown = false;

    /**
     * Specifies a markdown document to append or update license information to.
     *
     * @parameter
     * @optional
     */
    private String appendUpdateLicensesMarkdownToMarkdownDocument = null;

    /**
     * Specifies the project subdir to create markdown license documents in.
     * The default is project base dir.
     *
     * @parameter
     * @optional
     */
    private String markdownTargetSubdir = null;

    /**
     * The prefix to use for links in license.md to generated license
     * markdown documents.
     *
     * @parameter
     * @optional
     */
    private String markdownLinkPrefix = null;

    /**
     * If this is set to true a pdf-<i>license</i>-<i>version</i>.apt will be created in addition
     * to <i>license</i>-<i>version</i>.apt. The pdf version is for use with maven-pdf-plugin which
     * can't handle page breaks for preformatted sections so the pdf version is pre page breaked.
     *
     * @parameter expression="false"
     */
    private boolean createMavenPDFPluginLicenseAPTVersions = false;

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

    /**
     * The local repository.
     * 
     * @parameter expression="${localRepository}"
     */
    private ArtifactRepository localRepository;

    /**
     * The projects base directory.
     * 
     * @parameter expression="${basedir}"
     */
    private String baseDir;

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
    public void execute() throws MojoExecutionException {
        logMsg("Running CodeLicenseManagerInstallMojo ... ");

        BuildDataCollector<ThirdpartyLicensesConfig> dataCollector =
                new BuildDataCollector<ThirdpartyLicensesConfig>(this.mavenProject) {
                    public ThirdpartyLicensesConfig newInstance() {return CodeLicenseManagerInstallMojo.this.thirdpartyLicenses;}
                    public String getName() {return "ThirdpartyLicensesConfig";}
                };
        dataCollector.load();
        ThirdpartyLicensesConfig collectedThirdpartyLicenses = dataCollector.getData();

        this.project = MojoUtils.updateConfigFromMavenProject(this.project, this.mavenProject);

        if (this.autoResolveThirdPartyLicenses) {
            logMsg("Trying to resolve third party licenses from dependencies ...");
            int before = collectedThirdpartyLicenses.getLicenses().size();

            MojoUtils.updateThirdpartyLicenseConfigFromMavenProject(
                    collectedThirdpartyLicenses, this.mavenProject, this.localRepository, getLog());

            int after = collectedThirdpartyLicenses.getLicenses().size();
            logMsg("Found " + (after - before) + " new licenses!");
        }

        MojoUtils.appendThirdpartyLicenses(collectedThirdpartyLicenses, this.thirdpartyLicenses);

        MojoUtils.complementThirdPartyLicensesWithLicenseLibraryData(collectedThirdpartyLicenses);

        Configuration config = new Configuration(this.project, collectedThirdpartyLicenses, this.installOptions);

        try {
            config.validateForInstall();

            Display display = new Display() {
                @Override
                public void display(String message) {
                    logMsg(message);
                }
            };

            Display.setDisplay(display);

            CodeLicenseManager codeLicMgr = new CodeLicenseManager(config);

            File rootDir = getRootDir();

            codeLicMgr.copyProjectLicense(rootDir);

            // The third party licenses need to be updated for each sub project being built to collect
            // all third party products used.
            codeLicMgr.copyThirdpartyLicenses(new File(rootDir, this.installOptions.getThirdpartyLicenseDir()));

            if (this.createLicencesAPT) {
                logMsg("Writing src/site/licenses/licenses.apt and specific license documents!");
                APTUtils.writeLicensesAPT(rootDir, this.createMavenPDFPluginLicenseAPTVersions, config);
            }

            boolean appendToDoc = this.appendUpdateLicensesMarkdownToMarkdownDocument != null &&
                    this.appendUpdateLicensesMarkdownToMarkdownDocument.trim().length() > 0;

            if (this.createLicensesMarkdown) {
                if (this.markdownTargetSubdir != null && this.markdownTargetSubdir.trim().length() == 0) {
                    this.markdownTargetSubdir = null;
                }
                String linkPrefix = this.markdownLinkPrefix != null ? this.markdownLinkPrefix : "";
                String subDir = this.markdownTargetSubdir != null ? this.markdownTargetSubdir + "/" : "";
                logMsg("Writing " + subDir + "licenses.md and specific license documents!");
                MarkdownUtils.writeLicensesMarkdown(new File(rootDir, subDir), appendToDoc, linkPrefix, config);

                if (appendToDoc) {
                    logMsg("Appending/uppdating license info to " + this.appendUpdateLicensesMarkdownToMarkdownDocument + "!");
                    MarkdownUtils.appendUppdateLicensesToMarkdownDoc(new File(rootDir, subDir),
                            this.appendUpdateLicensesMarkdownToMarkdownDocument, linkPrefix, config);
                }
            }
        }
        catch (CodeLicenseException cle) {
            cle.printStackTrace();
            logMsg(cle.getMessage());
            switch (cle.getType()) {
                case BAD_SOURCE_UPDATER_LIBRARY:
                    logMsg(cle.getType().getDescription());
                    logMsg("Check the source updater library dependencies.");
                    break;

                case BAD_LICENSE_LIBRARY:
                    logMsg(cle.getType().getDescription());
                    logMsg("Check the license library dependency.");
                    break;

                case BAD_CONFIGURATION:
                    logMsg("There is a plugin <configuration> section error.");
            }
            throw new MojoExecutionException("Bad inputs when running install!", cle);
        }
        catch (Exception e) {
            System.out.println("*** Unexpected problem! Here is a stacktrace:");
            e.printStackTrace();
            throw new MojoExecutionException(e.getMessage(), e);
        }
        finally {
            Display.setDisplay(null);
            dataCollector.save();
        }
    }

    /**
     * Returns a File representing the directory whose parent directory does not have a pom.xml.
     * In other words, the root of a multi-module build.
     */
    private File getRootDir() {
        File root = new File(this.baseDir);
        while (havePOM(root.getParentFile().listFiles())) {
            root = root.getParentFile();
        }

        return root;
    }

    /**
     * Checks if any of the passed files is a pom.xml.
     *
     * @param files The files to check.
     *
     * @return true if found, false otherwise.
     */
    private boolean havePOM(File[] files) {
        for (File file : files) {
            if (file.getName().toLowerCase().equals("pom.xml")) {
                return true;
            }
        }

        return false;
    }
}
