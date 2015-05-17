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
package se.natusoft.tools.codelicmgr.config;

import se.natusoft.tools.codelicmgr.CodeLicenseException;
import se.natusoft.tools.optionsmgr.annotations.Description;
import se.natusoft.tools.optionsmgr.annotations.Option;
import se.natusoft.tools.optionsmgr.annotations.OptionsModel;

@OptionsModel(name="configuration", description="The following XML elements are available in the configuration:")
/**
 * This is the main configuration object holding all the other.
 */
public class Configuration {


    /**
     * Creates a new Configuration instance.
     * 
     * @param project The project information configuration.
     * @param thirdpartyLicenses The thirdparty licenses configuration.
     * @param codeOptions The code update options configuration.
     * @param userData The user data configuration values.
     * @param scripts In configuration script code to execute.
     */
    public Configuration(ProjectConfig project, ThirdpartyLicensesConfig thirdpartyLicenses, CodeOptionsConfig codeOptions, UserDataConfig userData, ScriptsConfig scripts) {
        this.project = project;
        this.thirdpartyLicenses = thirdpartyLicenses;
        this.codeOptions = codeOptions;
        this.installOptions = null;
        this.userData = userData;
        if (this.userData == null) {
            this.userData = new UserDataConfig(); // Provide an empty config.
        }
        this.scripts = scripts;
        if (this.scripts == null) {
            this.scripts = new ScriptsConfig();
        }
    }

    /**
     * Creates a new Configuration instance.
     *
     * @param codeOptions The code update options configuration.
     * @param userData The user data configuration values.
     * @param scripts In configuration script code to execute.
     */
    public Configuration(CodeOptionsConfig codeOptions, UserDataConfig userData, ScriptsConfig scripts) {
        this(null, null, codeOptions, userData, scripts);
    }

    /**
     * Creates a new Configuration instance.
     *
     * @param project The project information configuration.
     * @param thirdpartyLicenses The thirdparty licenses configuration.
     * @param installOptions The install options configuration.
     */
    public Configuration(ProjectConfig project, ThirdpartyLicensesConfig thirdpartyLicenses, InstallOptionsConfig installOptions) {
        this.project = project;
        this.thirdpartyLicenses = thirdpartyLicenses;
        this.codeOptions = null;
        this.installOptions = installOptions;
        this.userData = new UserDataConfig();
    }

    /**
     * Used when loading via OptionsManager.
     */
    public Configuration() {}

    //
    // Project
    //

    @Option
    @Description("Supplies project information.")
    private ProjectConfig project;

    /**
     * Sets the project configuration information.
     * 
     * @param project The project config to set.
     */
    public void setProject(ProjectConfig project) {
        this.project = project;
    }

    /**
     * Returns the project configuration information.
     */
    public ProjectConfig getProject() {
        return this.project;
    }

    //
    // ThirdpartyLicenses
    //

    @Option
    @Description("Third party licenses.")
    private ThirdpartyLicensesConfig thirdpartyLicenses;

    /**
     * Sets the configuration inforamtion for the third party licenses.
     * 
     * @param thirdpartyLicenses The third party license config to set.
     */
    public void setThirdpartyLicenses(ThirdpartyLicensesConfig thirdpartyLicenses) {
        this.thirdpartyLicenses = thirdpartyLicenses;
    }

    /**
     * Returns the configuration inforamtion for the third party licenses.
     */
    public ThirdpartyLicensesConfig getThirdpartyLicenses() {
        return this.thirdpartyLicenses;
    }

    //
    // Install Options
    //

    @Option
    @Description("Provides license file install options.")
    private InstallOptionsConfig installOptions;

    /**
     * Sets the install options configuration information.
     * 
     * @param installOptions The install option configuration information to set.
     */
    public void setInstallOptions(InstallOptionsConfig installOptions) {
        this.installOptions = installOptions;
    }

    /**
     * Returns the install options configuration information.
     */
    public InstallOptionsConfig getInstallOptions() {
        return this.installOptions;
    }

    //
    // Code Options
    //

    @Option
    @Description("Provides source code update options.")
    private CodeOptionsConfig codeOptions;

    /**
     * Sets the code update options configuration information.
     *
     * @param codeOptions The code update option configuration information to set.
     */
    public void setCodeOptions(CodeOptionsConfig codeOptions) {
        this.codeOptions = codeOptions;
    }

    /**
     * Returns the code update options configuration information.
     */
    public CodeOptionsConfig getCodeOptions() {
        return this.codeOptions;
    }

    //
    // User Data
    //

    @Option
    @Description("Provides user information for use in personal source code updaters. A name/value pair can occur multiple times!")
    private UserDataConfig userData;

    /**
     * Sets the user data configuration information.
     * @param userData
     */
    public void setUserData(UserDataConfig userData) {
        this.userData = userData;
    }

    /**
     * Returns the user data configuration information.
     */
    public UserDataConfig getUserData() {
        return this.userData;
    }

    //
    // scripts
    //

    @Option
    @Description("Specifies scripts to run on source files of specified file extension.")
    private ScriptsConfig scripts;

    /**
     * Sets the scripts configuration information.
     *
     * @param scripts The scripts to set.
     */
    public void setScripts(ScriptsConfig scripts) {
        this.scripts = scripts;
    }

    /**
     * Returns the scripts configuration information.
     */
    public ScriptsConfig getScripts() {
        return this.scripts;
    }

    // -------------------------

    /**
     * Validates the config dat in this object.
     *
     * @throws se.natusoft.tools.codelicmgr.CodeLicenseException If validation fails.
     */
    public void validateForInstall() throws CodeLicenseException {
        if (project == null) {
            throw new CodeLicenseException("Plugin <configuration> section error: 'project' specification is missing!", CodeLicenseException.Type.BAD_CONFIGURATION);
        }
        this.project.validate();

        if (this.installOptions == null) {
            throw new CodeLicenseException("Plugin <configuration> section error: Even if you use all the default options the <installOptions> tag must be specified!",
                    CodeLicenseException.Type.BAD_CONFIGURATION);
        }

        if (this.thirdpartyLicenses != null) {
            this.thirdpartyLicenses.validate();
        }
    }

    /**
     * Validates the config dat in this object.
     *
     * @throws CodeLicenseException If validation fails.
     */
    public void validateForCodeUpdate() throws CodeLicenseException {
        if (project == null) {
            throw new CodeLicenseException("Plugin <configuration> section error: 'project' specification is missing!", CodeLicenseException.Type.BAD_CONFIGURATION);
        }
        this.project.validate();

        if (this.codeOptions == null) {
            throw new CodeLicenseException("Plugin <configuration> section error: Even if you use all the default options the <codeOptions> tag must be specified!",
                    CodeLicenseException.Type.BAD_CONFIGURATION);
        }
    }

    /**
     * Validates the config dat in this object.
     *
     * @throws CodeLicenseException If validation fails.
     */
    public void validateForScriptsOnly() throws CodeLicenseException {

        if (this.codeOptions == null) {
            throw new CodeLicenseException("Plugin <configuration> section error: Even if you use all the default options the <codeOptions> tag must be specified!",
                    CodeLicenseException.Type.BAD_CONFIGURATION);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Configuration {");
        sb.append(this.project != null ? this.project.toString() : "ProjectConfig {}");
        sb.append(this.thirdpartyLicenses != null ? this.thirdpartyLicenses.toString() : "ThirdpartyLicensesConfig {}");
        sb.append(this.codeOptions != null ? this.codeOptions.toString() : "CodeOptionsConfig {}");
        sb.append(this.installOptions != null ? this.installOptions.toString() : "InstallOptionsConfig {}");
        sb.append(this.userData != null ? this.userData.toString() : "UserDataConfig {}");
        sb.append("}");

        return sb.toString();
    }
}
