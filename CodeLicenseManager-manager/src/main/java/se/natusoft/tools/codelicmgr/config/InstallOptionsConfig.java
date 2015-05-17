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

import se.natusoft.tools.optionsmgr.annotations.Description;
import se.natusoft.tools.optionsmgr.annotations.Option;
import se.natusoft.tools.optionsmgr.annotations.OptionsModel;

@OptionsModel(name="installOptions")
/**
 * Holds all the options for how to install licenses in build.
 */
public class InstallOptionsConfig {

    //
    // Verbose
    //

    @Option
    @Description("If true verbose output is provided.")
    private boolean verbose = false;

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public boolean isVerbose() {
        return this.verbose;
    }

    //
    // LicenseDir
    //

    @Option
    @Description("The directory to where the license text should be copied. Defaults to 'license'.")
    private String licenseDir = "license";

    public void setLicenseDir(String licenseDir) {
        this.licenseDir = licenseDir;
    }

    public String getLicenseDir() {
        return licenseDir;
    }

    //
    // ThirdpartyLicenseDir
    //

    @Option
    @Description("The directory to where the third party license texts are copied. Defaults to 'license/thirdparty'.")
    private String thirdpartyLicenseDir = "license/thirdparty";

    public void setThirdpartyLicenseDir(String thirdpartyLicenseDir) {
        this.thirdpartyLicenseDir = thirdpartyLicenseDir;
    }

    public String getThirdpartyLicenseDir() {
        return thirdpartyLicenseDir;
    }

    // -------------------------
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("InstallOptionsConfig {");
        sb.append("verbose='");sb.append(this.verbose);sb.append("', ");
        sb.append("licenseDir='");sb.append(this.licenseDir);sb.append("', ");
        sb.append("thirdpartyLicenseDir='");sb.append(this.thirdpartyLicenseDir);sb.append("'");
        sb.append("}");

        return sb.toString();
    }

}
