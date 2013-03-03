package se.natusoft.tools.codelicmgr.config;

import se.natusoft.tools.codelicmgr.enums.Source;
import se.natusoft.tools.optionsmgr.annotations.Description;
import se.natusoft.tools.optionsmgr.annotations.Option;
import se.natusoft.tools.codelicmgr.annotations.*;

import java.io.Serializable;

/**
 * Third party extension to project license info.
 */
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
        email="opensource@biltmore.se",
        changes={
            @Change(when="2010-01-08", description="Created")
        }
    )
})
public class ThirdpartyLicenseConfig extends LicenseConfig implements Serializable {

    //
    // ProductsConfig
    //

    @Option
    @Description("The used third party products using this license. This is only relevant for third party licenses!")
    private ProductsConfig licenseProducts = new ProductsConfig();

    public void setLicenseProducts(ProductsConfig licenseProducts) {
        this.licenseProducts = licenseProducts;
    }

    public ProductsConfig getProducts() {
        return this.licenseProducts;
    }

    public void addProduct(ProductConfig product) {
        this.licenseProducts.addProduct(product);
    }

    //
    // licenseUrl
    //

    @Option
    @Description("The url to the license text on the web. This is required if the license cannot be found in a license library!")
    private String licenseUrl = "";

    public void setLicenseUrl(String licenseUrl) {
        this.licenseUrl = licenseUrl;
    }

    public String getLicenseUrl() {
        return this.licenseUrl;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("ThirdpartyLicenseConfig {");
        sb.append("type='");sb.append(getType());sb.append("', ");
        sb.append("version='");sb.append(getVersion());sb.append("', ");
        sb.append("licenseUrl='");sb.append(this.licenseUrl);sb.append("', ");
        sb.append(this.licenseProducts != null ? this.licenseProducts.toString() : "ProductsConfig {}");
        sb.append("}");

        return sb.toString();
    }
}
