/* 
 * 
 * PROJECT
 *     Name
 *         CodeLicenseManager-manager
 *     
 *     Code Version
 *         2.1.5
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
 *         2014-07-09: Created!
 *         
 */
package se.natusoft.tools.codelicmgr.library;

import codelicmgr.licenses.LicenseResourceProvider;
import se.natusoft.tools.codelicmgr.CodeLicenseException;
import se.natusoft.tools.codelicmgr.Display;
import se.natusoft.tools.codelicmgr.config.LicenseConfig;
import se.natusoft.tools.codelicmgr.config.ThirdpartyLicenseConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class holds license data for use in source code update scripts.
 */
public class LicenseLibrary {

    //
    // Constants
    //

    private static final String PROP_TYPE = "type";
    private static final String PROP_VERSION = "version";
    private static final String PROP_DESCRIPTION = "description";
    private static final String PROP_SOURCE = "source";
    private static final String PROP_SOURCE_BLOCK = "sourceblock";
    private static final String PROP_FULL_TEXT = "fulltext";
    private static final String PROP_FULL_TEXT_MARKDOWN = "fullTextMarkdown";
    private static final String PROP_URL = "url";

    //
    // Methods
    //

    /**
     * Loads and return a license from the license libraries available in classpath.
     *
     * @param license    The project license config entry.
     * @param licenseUrl The url to the license on the web.
     * @throws se.natusoft.tools.codelicmgr.CodeLicenseException
     *          on failure to load license.
     */
    public static LibraryLicense getLicense(LicenseConfig license, String licenseUrl) throws CodeLicenseException {
        return getLicense(license.getType(), license.getVersion(), licenseUrl);
    }

    /**
     * Loads and return a license from the license libraries available in classpath.
     *
     * @param licenseType    The type of license.
     * @param licenseVersion The version of the license.
     * @param licenseUrl     The url for the license on the web.
     * @throws CodeLicenseException on failure to load license.
     */
    public static LibraryLicense getLicense(String licenseType, String licenseVersion, String licenseUrl) throws CodeLicenseException {
        Properties props = loadProperties(getLicenseFullName(licenseType, licenseVersion) + ".properties");

        LibraryLicense libLic = null;

        if (props != null) {
            String licType = props.getProperty(PROP_TYPE);
            String licVersion = props.getProperty(PROP_VERSION);

            libLic = new LibraryLicense(
                    licType,
                    licVersion,
                    props.getProperty(PROP_DESCRIPTION),
                    props.getProperty(PROP_SOURCE),
                    expandTypeAndVersion(props.getProperty(PROP_SOURCE_BLOCK), licType, licVersion),
                    expandTypeAndVersion(props.getProperty(PROP_FULL_TEXT), licType, licVersion),
                    expandTypeAndVersion(props.getProperty(PROP_FULL_TEXT_MARKDOWN), licType, licVersion)
            );
            // TODO: Add url to constructor along with the other properties!
            String licUrl = props.getProperty(PROP_URL);
            libLic.setUrl(licUrl != null ? licUrl : licenseUrl);
        } else {
            libLic = new LibraryLicense(licenseType, licenseVersion, licenseType, "open", null, null, null, true /* downloadable*/);
            libLic.setUrl(licenseUrl);
        }

        return libLic;
    }

    /**
     * Loads and returns a thid party license from the license libraries available in classpath or from the web
     * in not found in license library. The last part requires a valid url.
     *
     * @param thirdpartyLicense A third party license config entry.
     * @throws CodeLicenseException
     */
    public static LibraryLicense getThirdpartyLicense(ThirdpartyLicenseConfig thirdpartyLicense) throws CodeLicenseException {
        return getLicense(thirdpartyLicense, thirdpartyLicense.getLicenseUrl());
    }

    /**
     * Returns true if the specified license can be found among the license libraries available in the classpath.
     *
     * @param license The project license config entry.
     */
    public static boolean isLicenseAvailable(LicenseConfig license) {
        try {
            LibraryLicense libLic = getLicense(license, "");
            if (libLic != null) {
                return !libLic.isDownloadable();
            }
            return false;
        } catch (CodeLicenseException cle) {
        }
        return false;
    }

    /**
     * Convenience for constructing a complete license name with a version or no version.
     *
     * @param licenseType
     * @param licenseVersion
     */
    private static String getLicenseFullName(String licenseType, String licenseVersion) {
        String fullName = licenseType;
        if (licenseVersion != null && licenseVersion.trim().length() != 0) {
            fullName = fullName + "-" + licenseVersion;
        }
        return fullName;
    }

    /**
     * Loads properties from classpath using specified filename.
     *
     * @param propFile The name of the property file to load.
     * @throws CodeLicenseException on failure to load the license property file.
     */
    private static Properties loadProperties(String propFile) throws CodeLicenseException {
        Properties props = new Properties();
        InputStream licPropertyStream = LicenseResourceProvider.getInputStream(propFile);
        if (licPropertyStream == null) {
            propFile = propFile.replace(' ', '_');
            licPropertyStream = LicenseResourceProvider.getInputStream(propFile);
        }
        if (licPropertyStream == null) {
            return null;
        }
        try {
            props.load(licPropertyStream);
        } catch (IOException ioe) {
            throw new CodeLicenseException("Failed to load properties file \"" + propFile + "\"!",
                    CodeLicenseException.Type.BAD_LICENSE_LIBRARY, ioe);
        } finally {
            if (licPropertyStream != null) {
                try {
                    licPropertyStream.close();
                } catch (IOException ioe) {
                }
            }
        }

        validateProperty(props, PROP_TYPE);
        validateProperty(props, PROP_VERSION);
        validateProperty(props, PROP_SOURCE);
        validateProperty(props, PROP_SOURCE_BLOCK);
        validateProperty(props, PROP_FULL_TEXT);
        if (props.getProperty(PROP_DESCRIPTION) == null) {
            Display.msg("WARNING: License description missing in license library owning property file: " + propFile);
        }
        if (props.getProperty(PROP_URL) == null) {
            Display.msg("WARNING: License url is missing in license library owning property file: " + propFile);
        }

        return props;
    }

    /**
     * Validates a property.
     *
     * @param props The properties containing the property to validate.
     * @param prop  The property to validate.
     * @throws CodeLicenseException on validateion failure.
     */
    private static void validateProperty(Properties props, String prop) throws CodeLicenseException {
        if (props.getProperty(prop) == null) {
            throw new CodeLicenseException("'" + prop + "' property missing!", CodeLicenseException.Type.BAD_LICENSE_LIBRARY);
        }
    }

    /**
     * Replaces ${type} and ${version} in properties.
     *
     * @param value
     * @return
     */
    private static String expandTypeAndVersion(String value, String licenseType, String licenseVersion) {
        if (value == null) {
            return null;
        }
        return value.replaceAll("\\$\\{type\\}", licenseType).replaceAll("\\$\\{version\\}", licenseVersion);
    }

}
