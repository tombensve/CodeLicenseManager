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
