package se.natusoft.tools.codelicmgr.config;

import java.util.ArrayList;
import java.util.List;
import se.natusoft.tools.codelicmgr.annotations.Author;
import se.natusoft.tools.codelicmgr.annotations.Authors;
import se.natusoft.tools.codelicmgr.annotations.Change;
import se.natusoft.tools.codelicmgr.annotations.Copyright;
import se.natusoft.tools.codelicmgr.annotations.License;
import se.natusoft.tools.codelicmgr.annotations.Project;
import se.natusoft.tools.codelicmgr.enums.Source;
import se.natusoft.tools.codelicmgr.CodeLicenseException;
import se.natusoft.tools.optionsmgr.annotations.Description;
import se.natusoft.tools.optionsmgr.annotations.Name;
import se.natusoft.tools.optionsmgr.annotations.Option;
import se.natusoft.tools.optionsmgr.annotations.Type;

@Project(
    name="CodeLicenseManager-manager",
    codeVersion="2.1.1",
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
            @Change(when="2009-11-05", description="Created"),
            @Change(when="2010-01-04", description="Added toString(String indent) for a clean readable model dump.")
        }
    )
})
/**
 * Holds a list of copyright information.
 */
public class CopyrightsConfig {

    @Option
    @Name("copyright")
    @Type(CopyrightConfig.class)
    @Description("Specifies the copyrights.")
    private List<CopyrightConfig> copyrights = new ArrayList<CopyrightConfig>();

    /**
     * Adds a copyright to the list.
     *
     * @param copyright The copyright to add.
     */
    public void addCopyright(CopyrightConfig copyright) {
        this.copyrights.add(copyright);
    }

    /**
     * Returns the list of copyrights.
     */
    public List<CopyrightConfig> getCopyrights() {
        return this.copyrights;
    }

    /**
     * Returns the first copyright entry. This is a convenience for when hasMoreThanOneCopyright() returns false.
     */
    public CopyrightConfig getCopyright() {
        return this.copyrights.get(0);
    }

    /**
     * Returns true if there is more than one copyright in the list.
     */
    public boolean hasMoreThanOneCopyright() {
        return this.copyrights.size() > 1;
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
        if (this.copyrights.size() == 0) {
            throw new CodeLicenseException("At least one " + parent + "/copyright must be specified!", CodeLicenseException.Type.BAD_CONFIGURATION);
        }

        for (CopyrightConfig copyright : this.copyrights) {
            copyright.validate(parent);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CopyrightsConfig { ");
        String comma = "";
        for (CopyrightConfig copyright : this.copyrights) {
            sb.append(comma);
            sb.append(copyright.toString());
            comma = ", ";
        }
        sb.append("}");

        return sb.toString();
    }

}
