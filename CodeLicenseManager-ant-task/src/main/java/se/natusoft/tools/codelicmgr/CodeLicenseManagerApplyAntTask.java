package se.natusoft.tools.codelicmgr;

import org.apache.tools.ant.BuildException;
import se.natusoft.tools.codelicmgr.annotations.*;
import se.natusoft.tools.codelicmgr.config.*;
import se.natusoft.tools.codelicmgr.enums.Source;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

@Project(
    name="CodeLicenseManager-ant-task",
    codeVersion="2.1",
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
        email="",
        changes={
            @Change(when="2009-11-25", description="Created")
        }
    )
})

/**
 * This is an ant task for running CodeLicenseManager to apply licenses to sourcecode.
 */
public class CodeLicenseManagerApplyAntTask {

    //
    // Private class members
    //

    /** Used for logging */
    private org.apache.tools.ant.Project antProject;

    /** Project config data. */
    private ProjectConfig projectConfig = null;

    /** Third party license config data. */
    private ThirdpartyLicensesConfig thirdPartyConfig = new ThirdpartyLicensesConfig();

    /** Options config data. */
    private CodeOptionsConfig optionsConfig = null;

    /** User data config data. */
    private List<UserDataConfig> userDataConfigs = new ArrayList<UserDataConfig>();

    /** In config scripts config data. */
    private ScriptsConfig scripts = new ScriptsConfig();

    /** Provides debug output when true. */
    private boolean debug = false;

    //
    // Constructors
    //

    /**
     * Creates new GeniouzAntTask
     */
    public CodeLicenseManagerApplyAntTask() {
    }

    //
    // Config injection methods
    //

    /**
     * Receives the Ant project used for logging.
     *
     * @param project The received Ant project.
     */
    public void setProject(org.apache.tools.ant.Project project) {
        this.antProject = project;
    }

    /**
     * Enables extra debug output when true.
     *
     * @param debug true/false
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Receives CodeLicenseManager-manager project configuration loaded by ant.
     *
     * @param project The received project config.
     */
    public void addProject(ProjectConfig project) {
        this.projectConfig = project;
    }

    /**
     * Receives CodeLicenseManager-manager copyright configuration loaded by ant.
     * <p>
     * This can be called more than one.
     *
     * @param copyright The received copyright config.
     */
    public void addCopyright(CopyrightConfig copyright) {
        this.projectConfig.addCopyright(copyright);
    }

    /**
     * Receives CodeLicenseManager-manager license configuration loaded by ant.
     *
     * @param license The received license config.
     */
    public void addLicense(LicenseConfig license) {
        this.projectConfig.setLicense(license);
    }

    /**
     * Receives CodeLicenseManager-manager third party license configuration loaded by ant.
     * <p>
     * This can be called more than once.
     *
     * @param license The received third party license config.
     */
    public void addThirdPartyLicense(ThirdpartyLicenseConfig license) {
        this.thirdPartyConfig.addLicense(license);
    }

    /**
     * Receives CodeLicenseManager-manager options configuration loaded by ant.
     *
     * @param options The received options config.
     */
    public void addCodeOptions(CodeOptionsConfig options) {
        this.optionsConfig = options;
    }

    /**
     * Receives CodeLicenseManager-manager user data configuration loaded by ant.
     *
     * @param userData The received user data config.
     */
    public void addUserData(UserDataConfig userData) {
        this.userDataConfigs.add(userData);
    }

    /**
     * Receives CodeLicneseManager-manager scripts configuration loaded by ant.
     * 
     * @param scripts The received scripts config.
     */
    public void addScripts(ScriptsConfig scripts) {
        this.scripts = scripts;
    }

    //
    // Implementation methods
    //

    /**
     * Convenience for logging messages.
     * 
     * @param message
     */
    private void logMsg(String message) {
        this.antProject.log(message);
    }

    /**
     * Since Ant cannot collect all user data in the same entry as maven does, we have to collect many and then merge
     * them all into one.
     */
    public UserDataConfig mergeUserDataConfigs() {
        UserDataConfig mergeTo = new UserDataConfig();
        for (UserDataConfig userDataConfig : this.userDataConfigs) {
            for (String property : userDataConfig.getProperties().stringPropertyNames()) {
                String value = userDataConfig.getProperties().getProperty(property);

                mergeTo.setProperty(property, value);
            }
        }

        return mergeTo;
    }

    /**
     * This is the main task implementation!
     */
    public void execute() throws BuildException {
        Configuration config = new Configuration(this.projectConfig, this.thirdPartyConfig, this.optionsConfig, mergeUserDataConfigs(), this.scripts);

        if (this.debug) {
            logMsg("" + config);
        }
        
        try {
            config.validateForCodeUpdate();

            Display display = new Display() {
                @Override
                public void display(String message) {
                    logMsg(message);
                }
            };

            Display.setDisplay(display);

            CodeLicenseManager codeLicMgr = new CodeLicenseManager(config);

            codeLicMgr.updateSourceFilesForAllSourceDirs(new File("."));
        }
        catch (CodeLicenseException cle) {
            logMsg(cle.getMessage());
            switch (cle.getType()) {
                case BAD_SOURCE_UPDATER_LIBRARY:
                    logMsg(cle.getType().getDescription());
                    logMsg("Check the source updater libraries provided in the classpath.");
                    break;

                case BAD_LICENSE_LIBRARY:
                    logMsg(cle.getType().getDescription());
                    logMsg("Check the license library provided in the classpath.");
                    break;

                case BAD_CONFIGURATION:
                    logMsg(cle.getType().getDescription());
            }
            throw new BuildException("Bad inputs when running apply!", cle);
        }
        catch (Exception e) {
            try {
                logMsg("*** Unexpected problem! Here is a stacktrace:");
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                logMsg(sw.toString());
                pw.close();
                sw.close();
            }
            catch (Exception ee) {/* dont care! */}

            throw new BuildException(e.getMessage(), e);
        }
        finally {
            Display.setDisplay(null);
        }
    }

}
