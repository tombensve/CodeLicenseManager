package se.natusoft.tools.codelicmgr;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.project.MavenProject;
import se.natusoft.tools.codelicmgr.annotations.*;
import se.natusoft.tools.codelicmgr.config.*;
import se.natusoft.tools.codelicmgr.enums.Source;
import se.natusoft.tools.codelicmgr.library.LibraryLicense;
import se.natusoft.tools.codelicmgr.library.LicenseLibrary;

import java.io.IOException;
import java.util.*;

/**
 * Some common utility methods used by booth mojos.
 */
@Project(
    name="CodeLicenseManager-maven-plugin",
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
public class MojoUtils {

    /*
     * Make this static utility class non instantiable.
     */
    private MojoUtils() {}

    /**
     * Tests a string value for emptiness. Both null and blank string counts as empty.
     *
     * @param str The string to test.
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * Returns the first non empty alternative of a set of values, or "" if
     * all alternatives are empty.
     *
     * @param values The values to choose from.
     */
    public static String getFirstNonEmpty(String... values) {
        String result = "";
        for (int i = 0; i < values.length; i++) {
            if (!isEmpty(values[i])) {
                result = values[i];
                break;
            }
        }

        return result;
    }

    /**
     * This will take config values that are not specified or specified as blank and
     * look for that information elsewhere in the maven project and if found, update
     * the configuration with that information.
     * <p>
     * The reason for this is to minimize duplication of information in the pom. Some
     * of the information managed by CodeLicenseManager is also available as standard
     * information in a pom and is used by site:site to generate documentation.
     *
     * @param project The CLM project config.
     * @param mavenProject Maven project info from pom.
     */
    public static ProjectConfig updateConfigFromMavenProject(ProjectConfig project, MavenProject mavenProject) {
        // Project
        if (project == null) { // Will be null if no <project> tag is specified!
            project = new ProjectConfig();
        }
        if(isEmpty(project.getName())) {
            project.setName(getFirstNonEmpty(mavenProject.getName(), mavenProject.getArtifactId()));
        }
        if (isEmpty(project.getDescription())) {
            project.setDescription(mavenProject.getDescription());
        }

        // License
        LicenseConfig licenseConf = project.getLicense();
        if (licenseConf == null) {
            licenseConf = new LicenseConfig();
            project.setLicense(licenseConf);
        }
        if (licenseConf.getType() == null) {
            List<org.apache.maven.model.License> lics = mavenProject.getLicenses();
            if (lics != null && lics.size() >= 1) {
                org.apache.maven.model.License lic = lics.get(0);

                String licName = getLicenseName(lic.getName().replaceAll("-", " "));
                if (licName.trim().length() > 0) {
                    licenseConf.setType(licName);
                }

                String licVer = getLicenseVersion(lic.getName().replaceAll("-", " "));
                if (licVer.trim().length() > 0) {
                    licenseConf.setVersion(licVer);
                }
            }
        }

        // Copyright
        if (project.getCopyrights().getCopyrights().size() == 0) {
            CopyrightConfig copyrightConf = new CopyrightConfig();
            copyrightConf.setHolder(mavenProject.getOrganization().getName());
            copyrightConf.setYear(mavenProject.getInceptionYear());
            project.getCopyrights().addCopyright(copyrightConf);
        }
        
        return project;
    }

    /**
     * Updates third party license information from maven project information.
     *
     * @param thirdpartyLicenses The third party license configuration to update.
     * @param mavenProject The running maven project.
     * @return The passed configuration.
     */
    public static ThirdpartyLicensesConfig updateThirdpartyLicenseConfigFromMavenProject(
            ThirdpartyLicensesConfig thirdpartyLicenses,
            MavenProject mavenProject,
            ArtifactRepository localRepository)
    {

        Set dependencies = mavenProject.getDependencyArtifacts();
        if (dependencies != null) {
            for (Iterator<Artifact> it = (Iterator<Artifact>)dependencies.iterator(); it.hasNext();) {
                Artifact depArtifact = it.next();
                if (!depArtifact.getType().equals("pom") && 
                        // Avoid test scope dependencies
                        !depArtifact.getScope().equals(Artifact.SCOPE_TEST) &&
                        // Avoid internal dependencies.
                        !depArtifact.getGroupId().equals(mavenProject.getArtifact().getGroupId())) {
                    try {

                        PomExtractor depPom = new PomExtractor(localRepository, depArtifact);

                        ProductConfig productConfig = new ProductConfig();
                        productConfig.setName(depArtifact.getArtifactId());
                        productConfig.setVersion(depArtifact.getVersion());
                        productConfig.setWeb(depPom.getProductUrl());

                        if (depPom.getLicenseName() != null) {
                            String licName = getLicenseName(depPom.getLicenseName());
                            String licVer = getLicenseVersion(depPom.getLicenseName());

                            // Lets try the name we extracted first
                            ThirdpartyLicenseConfig tplConfig = lookupThirdpartyLicense(thirdpartyLicenses, licName);

                            // That failed, try to construct an acronym of the name instead.
                            if (tplConfig == null) {
                                String altLicName = getLicenseNameAcronym(depPom.getLicenseName());
                                tplConfig = lookupThirdpartyLicense(thirdpartyLicenses, altLicName);
                                if (tplConfig != null) {
                                    licName = altLicName;
                                }
                                else {// Not among the already known third party licenses, look in license library next.
                                    try {
                                        LibraryLicense libLic = LicenseLibrary.getLicense(altLicName, licVer, "");
                                        if (libLic != null && !libLic.isDownloadable()) {
                                            String type = libLic.getType();
                                            if (type != null) {
                                                licName = type;
                                            }
                                        }
                                    }
                                    catch (CodeLicenseException cle) {}
                                }
                            }

                            // That also failed, now we try to remove all spaces from the name.
                            if (tplConfig == null) {
                                String altLicName = getLicenseName(depPom.getLicenseName()).replace(" ", "");
                                tplConfig = lookupThirdpartyLicense(thirdpartyLicenses, altLicName);
                                if (tplConfig != null) {
                                    licName = altLicName;
                                }
                                else {// Not among the already known third party licenses, look in license library next.
                                    try {
                                        LibraryLicense libLic = LicenseLibrary.getLicense(altLicName, licVer, "");
                                        if (libLic != null && !libLic.isDownloadable()) {
                                            String type = libLic.getType();
                                            if (type != null) {
                                                licName = type;
                                            }
                                        }
                                    }
                                    catch (CodeLicenseException cle) {}
                                }
                            }
                            // Still no go! Just create a new config and use what we have. Please note that if the
                            // license was not found among the already known third party licenses, but found in a
                            // license library the license name have been modified to the official name in the license
                            // library.
                            if (tplConfig == null) {
                                tplConfig = new ThirdpartyLicenseConfig();
                                tplConfig.setType(licName);
                                tplConfig.setVersion(licVer);
                                tplConfig.setLicenseUrl(depPom.getLicenseUrl());
                                thirdpartyLicenses.addLicense(tplConfig);
                            }

                            // Check if current artifact already exists
                            boolean artifactExists = false;
                            for (ProductConfig prodConfig : tplConfig.getProducts().getProducts()) {
                                if (prodConfig.getName().trim().toLowerCase().equals(productConfig.getName().trim().toLowerCase())) {
                                    artifactExists = true;
                                }
                            }

                            // If not add it.
                            if (!artifactExists) {
                                tplConfig.getProducts().addProduct(productConfig);
                            }
                        }
                        else {
                            if (lookupArtifactInConfiguredThirdpartyProduct(thirdpartyLicenses, depArtifact) == null) {
                                System.out.println("WARNING: Artifact '" + depArtifact + "' has no license information and has not been configured " +
                                        "under the <thirdpartyLicenses><license><products> section!");
                            }
                        }

                    } catch (IOException ioe) {
                        System.out.println("WARNING: Failed to extract information from maven dependency: " +
                                depArtifact.getArtifactId() + "-" + depArtifact.getVersion() + " [" + ioe.getMessage() + "]");
                    }
                }
            }
        }

        return thirdpartyLicenses;
    }

    /**
     * Looks up an artifact in the configured thrird party licenses.
     *
     * @param thirdpartyLicenses The third party license information to search.
     * @param artifact The artifact to look for.
     */
    private static ProductConfig lookupArtifactInConfiguredThirdpartyProduct(ThirdpartyLicensesConfig thirdpartyLicenses, Artifact artifact) {
        ProductConfig found = null;

        for (ThirdpartyLicenseConfig thirdpartyLicense : thirdpartyLicenses.getLicenses()) {
            for (ProductConfig product : thirdpartyLicense.getProducts().getProducts()) {
                if (product.getName().startsWith(artifact.getArtifactId())) {
                    found = product;
                    break;
                }
            }
        }
        
        return found;
    }

    /**
     * Copies all data in 'from' that does not already exists in 'to', to 'to'.
     * 
     * @param to The config to append to.
     * @param from The congig to append from.
     */
    public static void appendThirdpartyLicenses(ThirdpartyLicensesConfig to, ThirdpartyLicensesConfig from) {
        if (from != null) {
            for (ThirdpartyLicenseConfig fromTPLic : from.getLicenses()) {
                ThirdpartyLicenseConfig toLic = null;

                for (ThirdpartyLicenseConfig toTPLic : to.getLicenses()) {
                    if (toTPLic.getType().equals(fromTPLic.getType()) && toTPLic.getVersion().equals(fromTPLic.getVersion())) {
                        toLic = toTPLic;
                        break;
                    }
                }

                if (toLic == null) {
                    toLic = fromTPLic;
                    to.getLicenses().add(toLic);
                }
                else {
                    for (ProductConfig fromProduct : fromTPLic.getProducts().getProducts()) {
                        ProductConfig toProd = null;

                        for (ProductConfig toProduct : toLic.getProducts().getProducts()) {
                            if (fromProduct.getVersion() != null && toProduct.getVersion() != null) {
                                if (toProduct.getName().equals(fromProduct.getName()) && toProduct.getVersion().equals(fromProduct.getVersion())) {
                                    toProd = toProduct;
                                }
                            }
                            else {
                                if (toProduct.getName().equals(fromProduct.getName())) {
                                    toProd = toProduct;
                                }
                            }
                        }

                        if (toProd == null) {
                            toLic.getProducts().addProduct(fromProduct);
                        }
                    }
                }
            }
        }
    }

    //
    // Internal support methods
    //
    
    /**
     * Splits the specified string into a list of space separated parts.
     * 
     * @param string The string to convert to a list of string.
     */
    private static List<String> stringToList(String string) {
        StringTokenizer stringTokenizer = new StringTokenizer(string.trim());
        ArrayList<String> parts = new ArrayList<String>();
        while (stringTokenizer.hasMoreTokens()) {
            parts.add(stringTokenizer.nextToken());
        }

        return parts;
    }

    /**
     * Extracts license name.
     *
     * @param license The string to extract license name from.
     */
    private static String getLicenseName(String license) {
        if (license == null) {
            return "";
        }
        List<String> parts = stringToList(license);
        String space = "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < (parts.size() - 1); i++) {
            if (!parts.get(i).toLowerCase().equals("version") && ! parts.get(i).toLowerCase().equals("ver")) {
                sb.append(space);
                sb.append(parts.get(i));
                space = " ";
            }
        }
        return sb.toString().replace(',', ' ').replace("The", "").trim();
    }
    
    /**
     * Extracts an alternative name (acronym).
     * 
     * @param license The string to extract the alternative name from. 
     */
    private static String getLicenseNameAcronym(String license) {
        if (license == null) {
            return "";
        }
        license = getLicenseName(license);
        List<String> parts = stringToList(license);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < (parts.size()); i++) {
            sb.append(parts.get(i).substring(0, 1).toUpperCase());
        }
        return sb.toString();
    }
    
    /**
     * Returns the license version.
     *
     * @param license The string to extract license version from.
     */
    private static String getLicenseVersion(String license) {
        if (license == null) {
            return "";
        }
        List<String> parts = stringToList(license);
        return parts.get(parts.size() - 1);
    }

    /**
     * Looks up a specific ThirdpartyLicenseConfig entry by name.
     *
     * @param thirdpartyLicenses The third party licenses to search in.
     * @param lookupLic The name to lookup.
     * @return The found third party license config or null if not found.
     */
    private static ThirdpartyLicenseConfig lookupThirdpartyLicense(ThirdpartyLicensesConfig thirdpartyLicenses, String lookupLic) {
        ThirdpartyLicenseConfig result = null;

        for (ThirdpartyLicenseConfig tplConf : thirdpartyLicenses.getLicenses()) {
            if (tplConf.getType().trim().toLowerCase().equals(lookupLic.trim().toLowerCase())) {
                result = tplConf;
                break;
            }
            try {
                if (tplConf.getType().trim().toLowerCase().equals(lookupLic.trim().toLowerCase().split(" ")[0])) {
                    result = tplConf;
                    break;
                }
            }
            catch (IndexOutOfBoundsException iobe) {/* OK! */}
        }

        return result;
    }
}
