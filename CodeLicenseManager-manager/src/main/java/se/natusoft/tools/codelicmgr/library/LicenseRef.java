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
 *         2020-12-26: Created!
 *         
 */
package se.natusoft.tools.codelicmgr.library;

/**
 * This is a simple reference to a license.
 */
public class LicenseRef {
    //
    // Private Members
    //

    /** The name of the license. */
    private String name;

    /** The version of the license. */
    private String version;

    /** The license library resource of the license. */
    private String licenseResource;

    //
    // Constructors
    //

    /**
     * Creates a new empty LicenseRef.
     */
    public LicenseRef() {}

    /**
     * Creates a new populated LicenseRef.
     *
     * @param name The license name.
     * @param version The licence version.
     * @param licenseResource The license properties resource.
     */
    public LicenseRef(String name, String version, String licenseResource) {
        setName(name);
        setVersion(version);
        setLicenseResource(licenseResource);
    }

    //
    // Methods
    //

    /**
     * Sets the name of the license.
     *
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the licence.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the version of the license.
     *
     * @param version The version to set.
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Returns the version of the license.
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Sets the library resource for loading license library properties.
     *
     * @param licenseResource The license resource to set.
     */
    public void setLicenseResource(String licenseResource) {
        this.licenseResource = licenseResource;
    }

    /**
     * Returns the library resource.
     */
    public String getLicenseResource() {
        return this.licenseResource;
    }
}
