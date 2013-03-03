package se.natusoft.tools.codelicmgr.config;

import java.util.Properties;
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

/**
 * Holds user supplied information that can be used in update scripts.
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
        changes={
            @Change(when="2009-11-10", description="Created"),
            @Change(when="2010-01-04", description="Added toString(String indent) for a clean readable model dump.")
        }
    )
})
public class UserDataConfig {

    /** The properties to populate. */
    private Properties userProps = new Properties();

    /** The current property name. */
    private String currPropName = null;

    /**
     * Sets a property.
     * 
     * @param name The name of the property.
     * @param value The value of the property.
     */
    public void setProperty(String name, String value) {
        this.userProps.setProperty(name, value);
    }

    /**
     * Sets the current property name.
     *
     * @param name The name to set.
     */
    @Option
    @Description("Specifies a name for the data.")
    public void setName(String name) {
        this.currPropName = name;
    }

    /**
     * Sets the current property value.
     *
     * @param value The value to set.
     */
    @Option
    @Description("Specifies a data value.")
    public void setValue(String value) {
        if (this.currPropName == null) {
            throw new RuntimeException("UserDataConfig: A name needs to be provided before a value can be set!");
        }
        this.userProps.setProperty(this.currPropName, value);
        this.currPropName = null;
    }

    /**
     * Returns the set properties.
     */
    public Properties getProperties() {
        return this.userProps;
    }

    /**
     * A convenience for getProperties().getProperty(key).
     *
     * @param key The property key to get.
     */
    public String getProperty(String key) {
        return this.userProps.getProperty(key);
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
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("UserDataConfig {");
        String comma = "";
        for (String property : this.userProps.stringPropertyNames()) {
            sb.append(comma);
            sb.append(property);
            sb.append("=");
            sb.append(this.userProps.getProperty(property));
            comma = ", ";
        }
        sb.append("}");

        return sb.toString();
    }

}
