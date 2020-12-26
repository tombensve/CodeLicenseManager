/* 
 * 
 * PROJECT
 *     Name
 *         CodeLicenseManager-manager
 *     
 *     Code Version
 *         2.2.1
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

/**
 * Holds copyright information. There can be more than one of these for a project.
 */
public class CopyrightConfig {

    //
    // Year
    //

    @Option
    @Description("The copyright year.")
    private String year = null;

    /**
     * Returns the copyright year.
     */
    public String getYear() {
        return this.year;
    }

    /**
     * Sets the copyright year.
     *
     * @param year The year to set.
     */
    public void setYear(String year) {
        this.year = year;
    }

    //
    // Holder
    //

    @Option
    @Description("The copyright holder.")
    private String holder = null;

    /**
     * Returns The name of the copyright holder.
     */
    public String getHolder() {
        return holder;
    }

    /**
     * Sets the copyright holder.
     *
     * @param holder The name of the copyright holder.
     */
    public void setHolder(String holder) {
        this.holder = holder;
    }

    //
    // Rights
    //

    @Option
    @Description("Is holds the string \"All rights reserved.\" by default. If you want something else, change it.")
    private String rights = "All rights reserved.";

    /**
     * Returns The rights string.
     */
    public String getRights() {
        return this.rights;
    }

    /**
     * Sets the rights string.
     *
     * @param rights The rights string to set.
     */
    public void setRights(String rights) {
        this.rights = rights;
    }

    // -------------------------

    /**
     * Validates the config dat in this object.
     *
     * @param parent Name of parent config.
     *
     * @throws se.natusoft.tools.codelicmgr.CodeLicenseException If validation fails.
     */
    public void validate(String parent) throws CodeLicenseException {
        if (this.year == null) {
            throw new CodeLicenseException(parent + "/copyright/year must be specified!", CodeLicenseException.Type.BAD_CONFIGURATION);
        }
        if (this.holder == null) {
            throw new CodeLicenseException(parent + "/copyright/holder must be specified!", CodeLicenseException.Type.BAD_CONFIGURATION);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("CopyrightConfig {");
        sb.append("year='");sb.append(this.year);sb.append("', ");
        sb.append("holder='");sb.append(this.holder);sb.append("', ");
        sb.append("rights='");sb.append(this.rights);sb.append("'");
        sb.append("}");

        return sb.toString();
    }

}
