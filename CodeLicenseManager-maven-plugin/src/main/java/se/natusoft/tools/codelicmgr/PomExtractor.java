/* 
 * 
 * PROJECT
 *     Name
 *         CodeLicenseManager-maven-plugin
 *     
 *     Code Version
 *         2.1.3
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
package se.natusoft.tools.codelicmgr;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * This extracts information from a maven pom.
 * <p>
 * I first tried to use DefaultMavenProjectBuilder to produce a MavenProject from a dependency Artifact,
 * but it kept throwing NullPointerExceptions, which made me get the source code and conclude that it
 * contains several private non initialized members that are only used, and never set. I strongly suspect
 * that most classes in maven have some form of dependency injection into private fields just like maven
 * plugins. There is some class somewhere that sets this upp. I however gave up trying to resolve this
 * just to access a few values in the pom. I decided to take another simpler approach that should be good
 * enough for my need: This class.
 */
public class PomExtractor {

    //
    // Private Members
    //

    /** The url to the product on the web. */
    private String productUrl = null;

    /** The name of the license. */
    private String licenseName = null;

    /** The url to the license on the web. */
    private String licenseUrl = null;

    /** Our parent pom if one exist. */
    private PomExtractor parent = null;

    /** Holds properties defined by the pom. */
    private Properties properties = new Properties();

    //
    // Constructors
    //

    /**
     * Creates a new PomExtractor instance.
     *
     * @param localRepository A local repository injected to maven plugin.
     * @param artifact The artifact whose pom to extract information from.
     *
     * @throws IOException on failure to read pom.
     */
    public PomExtractor(ArtifactRepository localRepository, Artifact artifact) throws IOException {
        this(localRepository, artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion());
    }

    /**
     * Creates a new PomExtractor instance.
     *
     * @param localRepository A local repository injected to maven plugin.
     * @param groupId The artifacts, whose pom to read, group id
     * @param artifactId The artifacts, whose pom to read, artifact id.
     * @param atifact version The artifacts, whose pom to read, version.
     *
     * @throws IOException on failure to read pom.
     */
    public PomExtractor(ArtifactRepository localRepository, String groupId, String artifactId, String artifactVersion) throws IOException {
        File pomFile = new File(localRepository.getBasedir());
        pomFile = new File(pomFile, groupId.replace('.', File.separatorChar));
        pomFile = new File(pomFile, artifactId);
        pomFile = new File(pomFile, artifactVersion);
        pomFile = new File(pomFile, artifactId + "-" + artifactVersion + ".pom");
        load(localRepository, pomFile);
    }

    //
    // Methods
    //

    /**
     * Loads the pom and extract values from it.
     *
     * @param localRepository A local repository injected to maven plugin.
     * @param pomFile The .pom file to read in the repository. This is always a full path to the file!
     *
     * @throws IOException on failure to read pom.
     */
    private final void load(ArtifactRepository localRepository, File pomFile) throws IOException {
        StringBuilder sb = new StringBuilder();

        FileInputStream fileStream = new FileInputStream(pomFile);
        int b;
        while ((b = fileStream.read()) != -1) {
            sb.append((char)b);
        }

        fileStream.close();

        String pom = sb.toString();

        String pomVersion = extractValue(pom, "project", "modelVersion");
        if (pomVersion != null && pomVersion.equals("4.0.0")) {

            // Extract product url
            this.productUrl = extractValue(pom, "project", "url");
            if (this.productUrl == null) {
                this.productUrl = extractValue(pom, "project", "organization", "url");
            }

            // Extract license name
            this.licenseName = extractValue(pom, "project", "licenses", "license", "name");

            // Extract license url
            this.licenseUrl = extractValue(pom, "project", "licenses", "license", "url");

            // Extract parent
            String pgroupId = extractValue(pom, "project", "parent", "groupId");
            if (pgroupId != null) {
                String partifactId = extractValue(pom, "project", "parent", "artifactId");
                if (partifactId != null) {
                    String pversion = extractValue(pom, "project", "parent", "version");
                    if (pversion != null) {
                        this.parent = new PomExtractor(localRepository, pgroupId, partifactId, pversion);
                    }
                }
            }

            // Extract properties
            String props = extractValue(pom, "project", "properties");
            if (props != null) {
                loadProperties(props);
            }

            if (this.parent != null) {
                this.properties.putAll(this.parent.properties);
            }
        }
        else {
            if (pomVersion == null) {
                System.out.println("WARNING: unknown pom model version! Ignoring file '" + pomFile + "'!");
            }
            else {
                System.out.println("WARNING: unsupported pom model version '" + pomVersion + "'! This tool supports version '4.0.0'! Ignoring file '" + pomFile + "'!");
            }
        }
    }

    /**
     * Loads properties.
     * 
     * @param props The properties section of the pom.
     * 
     * @throws RuntimeException on bad XML.
     */
    private void loadProperties(String props) {
        int propNameStart = props.indexOf('<');
        while (propNameStart != -1) {
            int propNameEnd = props.indexOf('>', propNameStart);
            if (props.charAt(propNameEnd - 1) == '/') {
                propNameStart = props.indexOf('<', propNameEnd);
                continue;
            } 
            if (propNameEnd == -1) {
                throw new RuntimeException("Bad pom XML! Starting '<' not terminated by '>'.");
            }
            String propName = props.substring(propNameStart + 1, propNameEnd);

            int propValueStart = propNameEnd + 1;
            int propValueEnd = props.indexOf("</", propValueStart);
            if (propValueEnd == -1) {
                throw new RuntimeException("Bad pom XML! tag '<" + propName + ">' not terminated!" );
            }
            String propValue = props.substring(propValueStart, propValueEnd);

            this.properties.put(propName, propValue);

            int endTagEnd = props.indexOf('>', propValueEnd);
            if (endTagEnd == -1) {
                throw new RuntimeException("Bad pom XML! Missing '>' on '</" + propName + "' end tag!");
            }

            propNameStart = props.indexOf('<', endTagEnd);
        }
    }

    /**
     * Expands any ${property} with the property value.
     * 
     * @param value The value to expand property references in.
     * 
     * @return An expanded string.
     */
    private String expandProperties(String value) {
        if (value != null) {
            for (String propName : this.properties.stringPropertyNames()) {
                String propValue = this.properties.getProperty(propName);

                value = value.replace("${" + propName + "}", propValue);
            }
        }
        
        return value;
    }

    /**
     * Extracts a value from the pom.
     *
     * @param pom The complete pom as a string.
     * @param path A varargs string path where each string is a tag name.
     *
     * @return The extracted value.
     */
    private final String extractValue(String pom, String... path) {
        return extractValue(pom, 0, path);
    }

    /**
     * Extracts a value from the pom.
     *
     * @param pom The complete pom as a string.
     * @param start The position in the pom to start looking for the specified path.
     * @param path A varargs string path where each string is a tag name.
     *
     * @return The extracted value or null if not found.
     */
    private final String extractValue(String pom, int start, String... path) {
        int currentPosition = start;
        String tag = "";
        int pathPosition= 0;
        for (pathPosition = 0; pathPosition < path.length; pathPosition++) {
            tag = "<" + path[pathPosition] + ">";
            String tag2 = "<" + path[pathPosition];
            int resultPosition = pom.indexOf(tag, currentPosition);
            if (resultPosition == -1) {
                resultPosition = pom.indexOf(tag2, currentPosition);
            }
            if (resultPosition == -1) {
                return null;
            }
            currentPosition = resultPosition;
        }
        --pathPosition;
        
        int startPosition = currentPosition + tag.length();

        tag = "</" + path[pathPosition] + ">";
        int endPosition = pom.indexOf(tag, startPosition);

        return pom.substring(startPosition, endPosition);
    }

    /** 
     * Returns the product url.
     */
    public String getProductUrl() {
        return expandProperties(this.productUrl != null ? this.productUrl : (this.parent != null ? this.parent.getProductUrl() : null));
    }

    /**
     * Returns the license name.
     */
    public String getLicenseName() {
        return expandProperties(this.licenseName != null ? this.licenseName : (this.parent != null ? this.parent.getLicenseName() : null));
    }

    /**
     * Returns the license url.
     */
    public String getLicenseUrl() {
        return expandProperties(this.licenseUrl != null ? this.licenseUrl : (this.parent != null ? this.parent.getLicenseUrl() : null));
    }

    @Override
    public String toString() {
        return "PomExtractor {productUrl='" + getProductUrl() + "', licenseName='" + getLicenseName() + "', licenseUrl='" + getLicenseUrl() + "'}\n";
    }
}
