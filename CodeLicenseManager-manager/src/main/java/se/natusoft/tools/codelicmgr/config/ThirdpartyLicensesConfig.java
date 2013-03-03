package se.natusoft.tools.codelicmgr.config;

import se.natusoft.tools.codelicmgr.annotations.Authors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import se.natusoft.tools.codelicmgr.annotations.Author;
import se.natusoft.tools.codelicmgr.annotations.Change;
import se.natusoft.tools.codelicmgr.annotations.Copyright;
import se.natusoft.tools.codelicmgr.annotations.License;
import se.natusoft.tools.codelicmgr.annotations.Project;
import se.natusoft.tools.codelicmgr.enums.Source;
import se.natusoft.tools.codelicmgr.CodeLicenseException;
import se.natusoft.tools.optionsmgr.annotations.Description;
import se.natusoft.tools.optionsmgr.annotations.Name;
import se.natusoft.tools.optionsmgr.annotations.Option;
import se.natusoft.tools.optionsmgr.annotations.OptionsModel;
import se.natusoft.tools.optionsmgr.annotations.Type;

@Project(
    name="CodeLicenseManager-manager",
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
            @Change(when="2009-11-03", description="Created"),
            @Change(when="2010-01-04", description="Added toString(String indent) for a clean readable model dump.")
        }
    )
})

/**
 * Holds information about third party licenses used.
 */
@OptionsModel(name="thirdpartyLicenses")
public class ThirdpartyLicensesConfig implements Serializable, Iterable<ThirdpartyLicenseConfig> {

    //
    // License list
    //

    @Option
    @Name("license")
    @Type(ThirdpartyLicenseConfig.class)
    @Description("Specifies licenses of used third party products.")
    private List<ThirdpartyLicenseConfig> licenses = new ArrayList<ThirdpartyLicenseConfig>();


    /**
     * Setter for license entries. Please note that this setter can be called
     * several times and will add the entries to a list. This ofcourse breaks normal
     * bean behaviour, but is done this way to be compatible with maven. Maven does
     * not accept add{Property}() methods, but happily calls a setter over and over
     * for each matching entry it finds in the mojo configuration block.
     * <p>
     * These config models are designed to be reusable for maven, ant, and command
     * line usage.
     *
     * @param license
     */
    public void setLicense(ThirdpartyLicenseConfig license) {
        addLicense(license);
    }

    /**
     * Adds a license.
     *
     * @param license The license to add.
     */
    public void addLicense(ThirdpartyLicenseConfig license) {
        boolean haveLicense = false;
        for (ThirdpartyLicenseConfig tpLic : this.licenses) {
            if (license.getType() != null && license.getVersion() != null) {
                if (tpLic.getType() != null && tpLic.getVersion() != null) {
                    if (tpLic.getType().equals(license.getType()) && tpLic.getVersion().equals(license.getVersion())) {
                        haveLicense = true;
                    }
                }
                else if (tpLic.getLicenseUrl().equals(license.getLicenseUrl())) {
                    // In this case we update the previous license information.
                    tpLic.setType(license.getType());
                    tpLic.setVersion(license.getVersion());
                    haveLicense = true;
                }
            }
            else if (license.getLicenseUrl() != null) {
                if (tpLic.getLicenseUrl() != null) {
                    if (tpLic.getLicenseUrl().equals(license.getLicenseUrl())) {
                        haveLicense = true;
                    }
                }
            }
        }

        if (!haveLicense) {
            this.licenses.add(license);
        }
    }


    /**
     * Returns The specified licenses.
     */
    public List<ThirdpartyLicenseConfig> getLicenses() {
        return this.licenses;
    }

    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
    public Iterator<ThirdpartyLicenseConfig> iterator() {
        return getLicenses().iterator();
    }

    // -------------------------

    /**
     * Validates the config dat in this object.
     *
     * @throws se.natusoft.tools.codelicmgr.CodeLicenseException If validation fails.
     */
    public void validate() throws CodeLicenseException {
        for (LicenseConfig license : this.licenses) {
            license.validate("thirdpartyLicenses");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("ThirdpartyLicensesConfig {");
        for (LicenseConfig license : this.licenses) {
            sb.append(license.toString());
        }
        sb.append("}");

        return sb.toString();
    }

}
