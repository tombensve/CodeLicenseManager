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
 *         2014-07-09: Created!
 *         
 */
package se.natusoft.tools.codelicmgr.library;

import codelicmgr.licenses.LicenseResourceProvider;
import se.natusoft.tools.codelicmgr.CodeLicenseException;
import se.natusoft.tools.codelicmgr.CopyTool;
import se.natusoft.tools.codelicmgr.Display;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A License that is returned by the LicenseLibrary.
 */
public class LibraryLicense {

    //
    // Private Members
    //

    /** The license type. */
    private String type;

    /** The license version. */
    private String version;

    /** A license description (usually full name of license). */
    private String description;

    /** The source type of the license ("open"/"closed"). */
    private String sourceType;

    /** The name of the file containing the source block ("boilerplate"). */
    private String sourceBlockFileName;

    /** The name of the file containing the full license text. */
    private String fullTextFileName;

    /** The name of an optional file containing the full license text in markdown format. */
    private String fullTextMarkdownFileName;

    /** A possible url to the license on the web. */
    private String url;

    /**
     * Set to true for a downloadable entry that will require download from url of license text.
     * This means it is not available in the library and can thereby not be used as project license,
     * but as third party license that only require a license text which can be downloaded from url.
     */
    private boolean downloadable = false;

    //
    // Constructors
    //

    /**
     * Creates a new LibraryLicense instance.
     *
     * @param type The license type.
     * @param version The license version.
     * @param description The license description.
     * @param sourceType "open" or "closed".
     * @param sourceBlockFileName The name of the file containing the source block.
     * @param fullTextFileName The name of the file containing the full license text.
     *
     * @throws se.natusoft.tools.codelicmgr.CodeLicenseException on bad data.
     */
    /*package*/ LibraryLicense(String type, String version, String description, String sourceType, String sourceBlockFileName,
                               String fullTextFileName, String fullTextMarkdownFileName) throws CodeLicenseException {
        this(type, version, description, sourceType, sourceBlockFileName, fullTextFileName, fullTextMarkdownFileName, false);
    }

    /**
     * Creates a new LibraryLicense instance.
     *
     * @param type The license type.
     * @param version The license version.
     * @param description The license description.
     * @param sourceType "open" or "closed".
     * @param sourceBlockFileName The name of the file containing the source block.
     * @param fullTextFileName The name of the file containing the full license text.
     * @param downloadable true if this represents a downloadable third party only license.
     *
     * @throws se.natusoft.tools.codelicmgr.CodeLicenseException on bad data.
     */
    /*package*/ LibraryLicense(String type, String version, String description, String sourceType, String sourceBlockFileName,
                               String fullTextFileName, String fullTextMarkdownFileName, boolean downloadable) throws CodeLicenseException {
        this.type = type;
        this.version = version;
        this.description = description;
        this.sourceType = sourceType;
        this.sourceBlockFileName = sourceBlockFileName;
        this.fullTextFileName = fullTextFileName;
        this.fullTextMarkdownFileName = fullTextMarkdownFileName;
        this.url = null;
        this.downloadable = downloadable;
        
        validate();
    }

    /**
     * Creates a new LibraryLicense instance. This is the third party license constructor.
     *
     * @param type The license type.
     * @param version The license version.
     * @param url A possible url to the license on the web.
     *
     * @throws CodeLicenseException on bad data.
     */
    /*package*/ LibraryLicense(String type, String version, String url) throws CodeLicenseException {
        Display.msg("Remote license: '" + type + "', '" + version + "', '" + url + "'");
        
        this.type = type;
        this.version = version;
        this.description = null;
        this.sourceType = "Open";
        this.sourceBlockFileName = "none";
        this.fullTextFileName = url;
        this.url = url;

        validate();
    }

    //
    // Methods
    //

    /**
     * Sets an url for this LibraryLicense.
     *
     * @param url The url to set.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return true if this is a full library license specified in a license library. If false it is only a maven resolved license with name
     *         version and url.
     */
    public boolean isLibraryLicense() {
        return this.url == null;
    }

    /**
     * Validates our inputs.
     *
     * @throws CodeLicenseException
     */
    private void validate() throws CodeLicenseException {

        if (isLibraryLicense() && !this.downloadable) {
            String errors = "";

            // Verify that we have a source block. Not all licenses have source blocks!
            if (!this.sourceBlockFileName.trim().equals("-") && !this.sourceBlockFileName.trim().equals("none")) {
                InputStream licTextStream = LicenseResourceProvider.getInputStream(this.sourceBlockFileName);
                if (licTextStream == null && this.url == null) {
                    errors += "Source block file ('" + this.sourceBlockFileName + "') not found! ";
                }
            }

            // Verify that we really have the full licence text file.
            InputStream checkFullTextFile = null;
            String errorExtra = "";

            if (this.fullTextFileName.startsWith("http:")) {
                File cache = getLicenseTextCacheFile();
                if (!cache.exists()) {
                    try {
                        checkFullTextFile = new URL(this.fullTextFileName).openStream();
                    }
                    catch (MalformedURLException mue) {
                        errorExtra = "(The full license text is specified with a bad url!)";
                    }
                    catch (IOException ioe) {
                        errorExtra = "(Could not access full license text url!)";
                    }
                }
            }

            if (!this.fullTextFileName.startsWith("http")) {
                checkFullTextFile = LicenseResourceProvider.getInputStream(this.fullTextFileName);
            }

            if (checkFullTextFile == null) {
                errors += "Specified full licence text file not found! " + errorExtra + "[" + this.fullTextFileName + "]";
            }
            else {
                try {checkFullTextFile.close();} catch (IOException ioe) {}
            }

            if (errors.length() != 0) {
                throw new CodeLicenseException(errors, CodeLicenseException.Type.BAD_LICENSE_LIBRARY);
            }
        }
    }

    /**
     * Returns the license type (or name if you like).
     */
    public String getType() {
        return this.type;
    }

    /**
     * Returns the license version.
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Returns a description of the license (usually a full name of the license).
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Return the type of the license: open/closed.
     */
    public String getSourceType() {
        return this.sourceType.equals("open") ? "Open" : "Closed";
    }

    /**
     * Returns the source block for this license.
     *
     * @throws CodeLicenseException on failure to provide it.
     */
    public String getLicenseSourceBlock() throws CodeLicenseException {
        String licText = "";

        if (this.sourceBlockFileName != null) {

            InputStream licTextStream = LicenseResourceProvider.getInputStream(this.sourceBlockFileName);
            if (licTextStream == null) {
                throw new CodeLicenseException("Licence source block not found at \"" + this.sourceBlockFileName + "\"!", CodeLicenseException.Type.BAD_LICENSE_LIBRARY);
            }
            if (licTextStream != null) {
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(licTextStream));
                    StringBuilder sb = new StringBuilder();
                    String line = br.readLine();
                    while (line != null) {
                        sb.append(line);
                        sb.append('\n');
                        line = br.readLine();
                    }
                    licText = sb.toString();
                }
                catch (IOException ioe) {
                    throw new CodeLicenseException("Failed to load source block from \"" + this.sourceBlockFileName + "\"!",
                            CodeLicenseException.Type.BAD_LICENSE_LIBRARY, ioe);
                }
                finally {
                    try { licTextStream.close(); } catch (IOException ioe) {}
                }
            }
        }

        return licText;
    }

    /**
     * Filters error messages.
     */
    public String errorFilter(String errorMessage) {
        return errorMessage.replace("${fullLicenseTextFile}", this.fullTextFileName).replace("${sourceBlockFile}", this.sourceBlockFileName);
    }

    /**
     * Returns an input stream to the full license text.
     *
     * @throws IOException on I/O problems.
     */
    public InputStream getFullTextStream() throws IOException {
        InputStream from = null;

        if (this.fullTextFileName == null) {
            if (this.url != null) {
                this.fullTextFileName = this.url;
            }
            else {
                if (this.downloadable) {
                    if (this.url != null) {
                        return new URL(this.url).openStream();
                    }
                    else {
                        throw new IOException("This license is not available in a library and no url have been found for it either!");
                    }
                }
                else {
                    throw new IOException("No full text filename nor an url have been provided!");
                }
            }
        }

        if (this.fullTextFileName.startsWith("http:")) {
            File cache = getLicenseTextCacheFile();
            if (!cache.exists()) {
                Display.msg("Downloading license!");
                CopyTool.copyFile(new URL(this.fullTextFileName).openStream(), cache);
            }
            else {
                Display.msg("Taking remote license from cache!");
            }
            
            from = new FileInputStream(cache);
        }
        else {
            from = LicenseResourceProvider.getInputStream(this.fullTextFileName);
        }

        return from;
    }

    /**
     * Returns the full path to the license text file or an url.
     *
     * @throws IOException
     */
    public String getFullTextResource() throws IOException {
        String resource = null;

        if (this.fullTextFileName == null) {
            if (this.url != null) {
                this.fullTextFileName = this.url;
            }
            else {
                if (this.downloadable) {
                    if (this.url != null) {
                        return new URL(this.url).toString();
                    }
                    else {
                        throw new IOException("This license is not available in a library and no url have been found for it either!");
                    }
                }
                else {
                    throw new IOException("No full text filename nor an url have been provided!");
                }
            }
        }

        if (this.fullTextFileName.startsWith("http:")) {
            File cache = getLicenseTextCacheFile();
            if (!cache.exists()) {
                CopyTool.copyFile(new URL(this.fullTextFileName).openStream(), cache);
            }

            resource = cache.getAbsolutePath();
        }
        else {
            resource = this.fullTextFileName;
        }

        return resource;
    }

    /**
     * @return A possible cache file for a license text. Check with exists() to se if it exists.
     */
    private File getLicenseTextCacheFile() {
        String userHome = System.getProperty("user.home");
        if (userHome == null) {
            userHome = "";
        }
        File cache = new File(userHome);
        cache = new File(cache, ".clm/cache/" + getType() + "_" + getVersion() + ".lic");
        
        return cache;
    }
    
    /**
     * Returns the file name extension
     */
    public String getFullTextExtension() {
        String extension = ".txt";
        if (this.fullTextFileName.startsWith("http:")) {
            extension = ".html";
        }

        return extension;
    }

    /**
     * Returns true if there is a markdown version of the license full text.
     */
    public boolean hasMarkdownFullTextLicense() {
        return this.fullTextMarkdownFileName != null;
    }

    /**
     * Returns an InputStream to the markdown version of the license full text.
     *
     * @throws IOException for the obvious reason.
     */
    public InputStream getFullTextMarkdownStream() throws IOException {
        return LicenseResourceProvider.getInputStream(this.fullTextMarkdownFileName);
    }

    /**
     * Returns the url or null if not available.
     */
    public String getURL() {
        return this.url;
    }

    /**
     * Sets status of this entry as downloadable.
     */
    public void markAsDownloadable() {
        this.downloadable = true;
    }

    /**
     * Returns the downloadable status of this entry.
     */
    public boolean isDownloadable() {
        return this.downloadable;
    }

    /**
     * @return A String representation of this LibraryLicense.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("type=" + this.type + ", ");
        sb.append("version=" + this.version + ", ");
        sb.append("sourceType=" + this.sourceType + ", ");
        sb.append("sourceBlockFileName=" + this.sourceBlockFileName + ", ");
        sb.append("FullTextFileName=" + this.fullTextFileName + ", ");
        sb.append("fullTextMarkdownFileName=" + this.fullTextMarkdownFileName + ", ");
        sb.append("url=" + this.url);
        sb.append("}");
        return sb.toString();
    }

}
