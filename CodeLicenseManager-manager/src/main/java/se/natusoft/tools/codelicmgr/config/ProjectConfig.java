/* 
 * 
 * PROJECT
 *     Name
 *         CodeLicenseManager-manager
 *     
 *     Code Version
 *         2.1.6
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
import se.natusoft.tools.optionsmgr.annotations.*;

@OptionsModel(name="project")
public class ProjectConfig {

    //
    // Name
    //

    @Option
    @Description("The name of the project.")
    private String name;

    /**
     * Sets the name of the project.
     * 
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns The name of the project.
     */
    public String getName() {
        return name;
    }

    //
    // Description
    //

    @Option
    @Description("A description of the project.")
    private String description = "";

    /**
     * Sets the project description.
     *
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns The description of the project.
     */
    public String getDescription() {
        return this.description;
    }

    //
    // CodeVersion
    //

    @Option
    @Description("The current version of the project code.")
    private String codeVersion = "";

    /**
     * Sets the version of the project code.
     * 
     * @param codeVersion The version to set.
     */
    public void setCodeVersion(String codeVersion) {
        this.codeVersion = codeVersion;
    }

    /**
     * Returns The current version of the project code.
     */
    public String getCodeVersion() {
        return this.codeVersion;
    }

    //
    // SubProjectOf
    //

    @Option
    @Description("A project of which this is a subproject of.")
    private String subProjectOf = "";

    /**
     * Sets the project this project is a subproject of.
     * 
     * @param subProjectOf The name of the parent project.
     */
    public void setSubProjectOf(String subProjectOf) {
        this.subProjectOf = subProjectOf;
    }

    /**
     * Returns The project this is a subproject of.
     */
    public String getSubProjectOf() {
        return this.subProjectOf;
    }

    //
    // License
    //

    @Option
    @Description("The license of the project.")
    private LicenseConfig license = null;

    /**
     * Sets a license specificaiton.
     *
     * @param license the licenseSpec to set
     */
    public void setLicense(LicenseConfig license) {
        this.license = license;
    }

    /**
     * Returns the license specification.
     */
    public LicenseConfig getLicense() {
        return this.license;
    }

    //
    // Copyrights
    //

    @Option
    @Name("copyright")
    @Type(CopyrightConfig.class)
    @Description("Copyrights held by the code.")
    private CopyrightsConfig copyrights = new CopyrightsConfig();

    /**
     * Sets a copyright specificaiton. Please note that this setter can be called
     * several times and will add the entries to a list. This ofcourse breaks normal
     * bean behaviour, but is done this way to be compatible with maven. Maven does
     * not accept add{Property}() methods, but happily calls a setter over and over
     * for each matching entry it finds in the mojo configuration block.
     * <p>
     * These config models are designed to be reusable for maven, ant, and command
     * line usage.
     *
     * @param copyright The copyright specification to set.
     */
    public void setCopyright(CopyrightConfig copyright) {
        addCopyright(copyright);
    }

    /**
     * Adds a copyright specifiecation.
     *
     * @param copyright The copyright specification to add.
     */
    public void addCopyright(CopyrightConfig copyright) {
        this.copyrights.addCopyright(copyright);
    }

    /**
     * Returns the copyright specification.
     */
    public CopyrightsConfig getCopyrights() {
        return this.copyrights;
    }

    // -------------------------

    /**
     * Validates the config dat in this object.
     *
     * @throws CodeLicenseException If validation fails.
     */
    public void validate() throws CodeLicenseException {
        if (this.license == null) {
            throw new CodeLicenseException("project/license must be specified!", CodeLicenseException.Type.BAD_CONFIGURATION);
        }

        this.license.validate("project");

        this.copyrights.validate("project");
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("ProjectConfig {");
        sb.append("name='");sb.append(this.name);sb.append("', ");
        sb.append("description='");sb.append(this.description);sb.append("', ");
        sb.append("codeVersion='");sb.append(this.codeVersion);sb.append("', ");
        sb.append("subProjectOf='");sb.append(this.subProjectOf);sb.append("', ");
        sb.append(this.license != null ? this.license.toString() : "LicenseConfig {}, ");
        sb.append(this.copyrights != null ? this.copyrights.toString() : "CopyrightsConfig {}");
        sb.append("}");

        return sb.toString();
    }

}
