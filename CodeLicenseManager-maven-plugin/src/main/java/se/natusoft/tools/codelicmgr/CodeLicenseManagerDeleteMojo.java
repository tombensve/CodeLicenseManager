package se.natusoft.tools.codelicmgr;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import se.natusoft.tools.codelicmgr.annotations.*;
import se.natusoft.tools.codelicmgr.config.*;
import se.natusoft.tools.codelicmgr.enums.Source;

/**
 * Goal which deletes previously dded/updated information in source code.
 *
 * @goal delete
 *
 * @phase generate-sources
 */
@Project(
    name="CodeLicenseManager-maven-plugin",
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
        changes={
            @Change(when="2009-01-26", description="Created")
        }
    )
})
public class CodeLicenseManagerDeleteMojo
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
        logMsg("Running CodeLicenseManagerDeleteMojo ... ");
        
        Configuration config = new Configuration(new ProjectConfig(), new ThirdpartyLicensesConfig(), this.codeOptions, new UserDataConfig(), new ScriptsConfig());
        
//        System.out.println("" + config);

        try {

            Display display = new Display() {
                @Override
                public void display(String message) {
                    logMsg(message);
                }
            };

            Display.setDisplay(display);

            CodeLicenseManager codeLicMgr = new CodeLicenseManager(config, false);

            codeLicMgr.deleteInSourceFilesForAllSourceDirs(this.mavenProject.getFile().getParentFile());

        }
        catch (CodeLicenseException cle) {
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
                    logMsg("This is a plugin <configuration> section error.");
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
