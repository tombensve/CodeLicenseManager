package se.natusoft.tools.codelicmgr.config;

import java.util.ArrayList;
import java.util.List;
import se.natusoft.tools.optionsmgr.annotations.Description;
import se.natusoft.tools.optionsmgr.annotations.Option;
import se.natusoft.tools.codelicmgr.annotations.*;
import se.natusoft.tools.codelicmgr.enums.Source;


/**
 * Hodls a set of ProductConfigs.
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
public class ProductsConfig {
    
    private ArrayList<ProductConfig> products = new ArrayList<ProductConfig>();

    /**
     * Maven setter. Each set product is added to products list.
     *
     * @param product The product to add.
     */
    public void setProduct(ProductConfig product) {
        addProduct(product);
    }

    // OptionsManager adder.
    @Option
    @Description("A used third party product having this license.")
    public void addProduct(ProductConfig product) {
        this.products.add(product);
    }

    /**
     * Returns a list of all ProductConfig entries.
     */
    public List<ProductConfig> getProducts() {
        return this.products;
    }

    @Override
    public String toString() {
        return toString("");
    }

    public String toString(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent);
        sb.append("ProductsConfig {\n");
        for (ProductConfig product : this.products) {
            sb.append(product.toString(indent + "    "));;
        }
        sb.append(indent);
        sb.append("}\n");

        return sb.toString();
    }
}
