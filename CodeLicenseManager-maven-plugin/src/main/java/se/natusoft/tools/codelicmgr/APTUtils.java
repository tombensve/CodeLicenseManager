/* 
 * 
 * PROJECT
 *     Name
 *         CodeLicenseManager-maven-plugin
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
package se.natusoft.tools.codelicmgr;

import se.natusoft.tools.codelicmgr.config.Configuration;
import se.natusoft.tools.codelicmgr.config.ProductConfig;
import se.natusoft.tools.codelicmgr.config.ThirdpartyLicenseConfig;
import se.natusoft.tools.codelicmgr.library.LibraryLicense;
import se.natusoft.tools.codelicmgr.library.LicenseLibrary;

import java.io.*;

/**
 * Provides static support methods for APT support.
 */
public class APTUtils {
    /**
     * This will create a maven APT file listing both project tpLicense and third party licenses. This is of course maven specific
     * and is called by the maven install mojo.
     *
     * @param baseDir The project basedir.
     * @param pdfLicenseVersions If true the specific license apt documents created will also get a "pdf-" variant that
     *                           can be used with the maven-pdf-plugin.
     * @param config The same config as passed to CodeLicenseManager.
     *
     * @throws java.io.IOException On any failure to create the APT document.
     */
    public static void writeLicensesAPT(String baseDir, boolean pdfLicenseVersions, Configuration config) throws IOException {
        writeLicensesAPT(new File(baseDir), pdfLicenseVersions, config);
    }

    /**
     * This will create a maven APT file listing both project tpLicense and third party licenses. This is of course maven specific
     * and is called by the maven install mojo.
     *
     * @param baseDir The project basedir.
     * @param pdfLicenseVersions If true the specific license apt documents created will also get a "pdf-" variant that
     *                           can be used with the maven-pdf-plugin.
     * @param config The same config as passed to CodeLicenseManager.
     *
     * @throws IOException On any failure to create the APT document.
     */
    public static void writeLicensesAPT(File baseDir, boolean pdfLicenseVersions, Configuration config) throws IOException {
        File licensesAPT = baseDir;
        licensesAPT = new File(licensesAPT, "src");
        licensesAPT = new File(licensesAPT, "site");
        licensesAPT = new File(licensesAPT, "apt");
        licensesAPT = new File(licensesAPT, "licenses");
        licensesAPT.mkdirs();
        licensesAPT = new File(licensesAPT, "licenses.apt");

        PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(licensesAPT)));
        try {
            String licType = config.getProject().getLicense().getType();
            String licVersion = config.getProject().getLicense().getVersion();

            LibraryLicense libraryLic = LicenseLibrary.getLicense(config.getProject().getLicense(), ""/* unknown url. */);

            String licDesc = libraryLic.getDescription() != null ? libraryLic.getDescription() : licType;

            ps.println("~~");
            ps.println("~~ This was created by CodeLicenseManager (http://codelicmgr.sf.net/)");
            ps.println("~~");
            ps.println("Project License");
            ps.println();
            if (libraryLic.getURL() != null) {
                ps.println("  <<{{{" + libraryLic.getURL() + "}" + licDesc + " version " + licVersion + "}}>>");
            }
            else {
                ps.println("  <<{{{" + licType + "-" + licVersion + ".html}" + licDesc + " version " + licVersion + "}}>>");
            }
            ps.println();

            writeAPTLicense(baseDir, libraryLic, pdfLicenseVersions);

            ps.println("Third Party Licenses");
            ps.println();

            for (ThirdpartyLicenseConfig tpLicense : config.getThirdpartyLicenses().getLicenses()) {
                LibraryLicense tpLibraryLic = LicenseLibrary.getLicense(tpLicense.getType(), tpLicense.getVersion(), tpLicense.getLicenseUrl());

                String tpLicDesc = tpLibraryLic.getDescription() != null ? tpLibraryLic.getDescription() : tpLibraryLic.getType();

                ps.println("* {{{" + tpLibraryLic.getType() + "-" + tpLibraryLic.getVersion() + ".html}" + tpLicDesc + " version " + tpLibraryLic.getVersion() + "}}");
                ps.println();

                if (tpLicense.getProducts().getProducts() != null) {
                    ps.println("  The following third party products are using this license:");
                    ps.println();

                    for (ProductConfig product : tpLicense.getProducts().getProducts()) {
                        ps.println("  <<{{{" + product.getWeb() + "}" + product.getName() + "-" + product.getVersion() + "}}>>");
                        ps.println();
                    }

                    writeAPTLicense(baseDir, tpLibraryLic, pdfLicenseVersions);
                }
            }
        }
        finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    /**
     * Support method for writeLicensesAPT(). Will write each tpLicense as a separate APT file. If the tpLicense text
     * is an html file then that html is copied directly to target/site.
     * <p>
     * The tpLicense documents written by this method is linked by the document written by writeLicenseAPT.
     *
     * @param baseDir The project basedir.
     * @param libraryLic The license to write.
     * @param pdfVersion if true a maven-pdf-plugin compatible version will also be created.
     *
     * @throws IOException On failure to write tpLicense APT.
     */
    private static void writeAPTLicense(File baseDir, LibraryLicense libraryLic, boolean pdfVersion) throws IOException {
        String name = null;
        File target = baseDir;

        boolean htmlSource = false;
        if (libraryLic != null && libraryLic.isDownloadable() && libraryLic.getURL() != null) {
            htmlSource = libraryLic.getURL().endsWith(".html") || libraryLic.getURL().endsWith(".htm");
        }

        if (libraryLic != null && !htmlSource) {

            if (libraryLic.getFullTextExtension().equals(".html")) {
                name = libraryLic.getType() + "-" + libraryLic.getVersion() + ".html";
                target = new File(target, "src");
                target = new File(target, "site");
                target = new File(target, "resources");
                target = new File(target, "licenses");
                target.mkdirs();
                target = new File(target, name);

                InputStream from = libraryLic.getFullTextStream();
                OutputStream to = new BufferedOutputStream(new FileOutputStream(target));

                try {
                    CopyTool.copyFile(from, to);
                }
                catch (IOException ioe) {
                    Display.msg("Failed to copy license '" + libraryLic.getType() + "-" + libraryLic.getVersion() + "' to '" + target + "' " +
                            "due to '" + ioe.getMessage() + "'!");
                }
                finally {
                    try {from.close();} catch (IOException ioe) {}
                    to.flush();
                    try {to.close();} catch (IOException ioe) {}
                }
            }
            else {
                name = libraryLic.getType() + "-" + libraryLic.getVersion() + ".apt";
                if (pdfVersion) {
                    name = "pdf-" + name;
                }
                target = new File(target, "src");
                target = new File(target, "site");
                target = new File(target, "apt");
                target = new File(target, "licenses");
                target.mkdirs();
                target = new File(target, name);

                String licDesc = libraryLic.getDescription() != null ? libraryLic.getDescription() : libraryLic.getType();
                String title = licDesc + " version " + libraryLic.getVersion();

                String preformatted = "+------------------------------+";
                if (pdfVersion) {
                    preformatted = "--------------------------------";
                }

                PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(target)));
                BufferedReader from = null;
                try {
                    ps.println("~~");
                    ps.println("~~ This was created by CodeLicenseManager (http://codelicmgr.sf.net/)");
                    ps.println("~~");
                    ps.println(title);
                    ps.println();
                    ps.println(preformatted);
                    from = new BufferedReader(new InputStreamReader(libraryLic.getFullTextStream()));
                    String line;
                    int lines = 0;
                    while ((line = from.readLine()) != null) {
                        if (!line.startsWith("  ")) {
                            ps.print("  ");
                        }
                        ps.println(line);

                        if (pdfVersion) {
                            ++lines;
                            if (lines >= 55) {
                                lines = 0;
                                ps.println(preformatted);
                                ps.println();
                                ps.println(preformatted);

                            }
                        }
                    }
                }
                catch (IOException ioe) {
                    String msg = "Failed to read license text for '" + libraryLic.getType() + "-" + libraryLic.getVersion() + "' due to '" +
                            ioe.getMessage() + "'!";
                    ps.println(msg);
                    Display.msg(msg);
                }
                finally {
                    ps.println(preformatted);
                    ps.println();

                    if (ps != null) {
                        ps.flush();
                        ps.close();
                    }
                    if (from != null) {
                        from.close();
                    }
                }

                if (pdfVersion) {
                    writeAPTLicense(baseDir, libraryLic, false);
                }
            }
        }
        else {
            if (libraryLic != null & libraryLic.isDownloadable()) {
                Display.msg("Cannot write APT license for " + libraryLic.getType() + " " + libraryLic.getVersion() + " since this " +
                        "is a downloaded version in html format!");
            }
            else {
                throw new IllegalArgumentException("writeAPTLicenses(String, LibraryLicense) was called without a LibraryLicense!");
            }
        }
    }

}
