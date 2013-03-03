package se.natusoft.tools.codelicmgr.config;

import se.natusoft.tools.codelicmgr.annotations.Author;
import se.natusoft.tools.codelicmgr.annotations.Authors;
import se.natusoft.tools.codelicmgr.annotations.Change;
import se.natusoft.tools.codelicmgr.annotations.Copyright;
import se.natusoft.tools.codelicmgr.annotations.License;
import se.natusoft.tools.codelicmgr.annotations.Project;
import se.natusoft.tools.codelicmgr.enums.Source;
import se.natusoft.tools.codelicmgr.CodeLicenseException;
import se.natusoft.tools.optionsmgr.annotations.Description;
import se.natusoft.tools.optionsmgr.annotations.Option;

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
            @Change(when="2009-11-03", description="Created"),
            @Change(when="2010-01-04", description="Added toString(String indent) for a clean readable model dump.")
        }
    )
})
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
