package se.natusoft.tools.codelicmgr.config;

import se.natusoft.tools.optionsmgr.annotations.Description;
import se.natusoft.tools.optionsmgr.annotations.Option;
import se.natusoft.tools.codelicmgr.annotations.*;
import se.natusoft.tools.codelicmgr.enums.Source;

import java.io.Serializable;

/**
 * This holds information for a thirdparty product.
 */
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
        email="opensource@biltmore.se",
        changes={
            @Change(when="2010-01-08", description="Created")
        }
    )
})
public class ProductConfig implements Serializable {

    //
    // Name
    //

    @Option
    @Description("The name of the product.")
    private String name;

    /**
     * Sets the name of the product.
     *
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the product.
     */
    public String getName() {
        return this.name;
    }

    //
    // Version
    //

    @Option
    @Description("The version of the product.")
    private String version;

    /**
     * Sets the version of the product.
     *
     * @param version The version to set.
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Returns the version of the product.
     */
    public String getVersion() {
        return this.version;
    }

    //
    // web
    //

    @Option
    @Description("The products web site.")
    private String web;

    /**
     * Sets the web site url for the product.
     *
     * @param web The site url to set.
     */
    public void setWeb(String web) {
        this.web = web;
    }

    /**
     * Returns the web site url for the product.
     */
    public String getWeb() {
        return this.web;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("ProductConfig { ");
        sb.append("name='");sb.append(this.name);sb.append("', ");
        sb.append("web='");sb.append(this.web);sb.append("'");
        sb.append("}");

        return sb.toString();
    }

}
