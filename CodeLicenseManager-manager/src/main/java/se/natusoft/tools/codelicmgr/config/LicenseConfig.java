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

import java.io.Serializable;

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
 * Holds license information. Used for both project license and third party licenses.
 * <p>
 * Please note that for each license type and version a ${type}-${version}.properties
 * is expected to be found in a codelicmgr.licenses package.
 */
public class LicenseConfig  implements Serializable {

    //
    // Type
    //

    @Option
    @Description("The license type. Example \"LGPL\" or \"Apache\".")
    private String type = null;

    /**
     * Sets the license type.
     *
     * @param licenseType The license type to set.
     */
    public void setType(String licenseType) {
        this.type = licenseType;
    }

    /**
     * Returns the license type.
     */
    public String getType() {
        return type;
    }

    //
    // Version
    //

    @Option
    @Description("The version of the license. Example \"v3\" or \"2.0\".")
    private String version = null;

    /**
     * Sets the license version.
     *
     * @param version The version to set.
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Returns the license version.
     */
    public String getVersion() {
        return version;
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
        if (this.type == null) {
            throw new CodeLicenseException(parent + "/license/type must be specified!", CodeLicenseException.Type.BAD_CONFIGURATION);
        }
        if (this.version == null) {
            throw new CodeLicenseException(parent + "/license/version must be specified!", CodeLicenseException.Type.BAD_CONFIGURATION);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("LicenseConfig {");
        sb.append("type='");sb.append(this.type);sb.append("', ");
        sb.append("version='");sb.append(this.version);sb.append("'");
        sb.append("}");

        return sb.toString();
    }

}
