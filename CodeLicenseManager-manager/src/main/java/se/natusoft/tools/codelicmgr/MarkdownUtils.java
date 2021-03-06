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
package se.natusoft.tools.codelicmgr;

import se.natusoft.tools.codelicmgr.config.Configuration;
import se.natusoft.tools.codelicmgr.config.ProductConfig;
import se.natusoft.tools.codelicmgr.config.ThirdpartyLicenseConfig;
import se.natusoft.tools.codelicmgr.library.LibraryLicense;
import se.natusoft.tools.codelicmgr.library.LicenseLibrary;
import se.natusoft.tools.fileeditor.TextFileEditor;

import java.io.*;

/**
 * Static utility methods for markdown support.
 */
public class MarkdownUtils {
    /**
     * This will create markdown (http://daringfireball.net/projects/markdown/) documents. One listing project license
     * and used third party licenses with links to each license and third party project web, and one for each license
     * type referenced by the first.
     *
     * @param licensesMDDir The directory to write markdown version of licenses in.
     * @param licsOnly If this is true then only the documents for each license will be written. This is useful in
     *                 conjunction with appendUppdateLicensesToMarkdownDoc() to avoid duplicate information.
     *
     * @throws java.io.IOException
     */
    public static void writeLicensesMarkdown(File licensesMDDir, boolean licsOnly, String linkPrefix, Configuration config) throws IOException {
        licensesMDDir.mkdirs();

        if (!licsOnly) {
            File licensesMD = new File(licensesMDDir, "licenses.md");

            PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(licensesMD)));
            try {
                writeLicensesMarkdown(ps, linkPrefix, config);
            }
            finally {
                if (ps != null) {
                    ps.close();
                }
            }
        }

        LibraryLicense libraryLic = LicenseLibrary.getLicense(config.getProject().getLicense(), "" /* unknown url. */);

        writeMDLicense(licensesMDDir, libraryLic);

        for (ThirdpartyLicenseConfig tpLicense : config.getThirdpartyLicenses().getLicenses()) {
            LibraryLicense tpLibraryLic = LicenseLibrary.getThirdpartyLicense(tpLicense);

            if (tpLicense.getProducts().getProducts() != null) {
                writeMDLicense(licensesMDDir, tpLibraryLic);
            }
        }
    }

    /**
     * Writes the markdown on the specified PrintStream.
     *
     * @param ps The PrintStream to write to.
     * @param linkPrefix A prefix to put before each relative link path.
     *
     * @throws IOException
     */
    private static void writeLicensesMarkdown(PrintStream ps, String linkPrefix, Configuration config) throws IOException {
        String licType = config.getProject().getLicense().getType();
        String licVersion = config.getProject().getLicense().getVersion();

        LibraryLicense libraryLic = LicenseLibrary.getLicense(config.getProject().getLicense(), ""/* Unknown url. */);
        String licDesc = libraryLic.getDescription() != null ? libraryLic.getDescription() : licType;

        ps.println("<!-- Created by CodeLicenseManager -->");
        ps.println("## Project License");
        ps.println();
        if (libraryLic.getURL() != null && libraryLic.getURL().trim().length() > 0) {
            ps.println("__[" + licDesc + " version " + licVersion + "](" + libraryLic.getURL() + ")__");
        }
        else {
            ps.println("__[" + licDesc + " version " + licVersion + "](" + linkPrefix + licType + "-" + licVersion + ".md)__");
        }
        ps.println();

        ps.println("## Third Party Licenses");
        ps.println();
        for (ThirdpartyLicenseConfig tpLicense : config.getThirdpartyLicenses()) {
            LibraryLicense tpLibraryLic = LicenseLibrary.getThirdpartyLicense(tpLicense);

            String tpLicDesc = tpLibraryLic.getDescription() != null ? tpLibraryLic.getDescription() : tpLibraryLic.getType();

            if (tpLicense.getLicenseUrl() != null && tpLicense.getLicenseUrl().trim().length() > 0) {
                ps.println("__[" + tpLicDesc + " version " + tpLibraryLic.getVersion() + "](" + tpLicense.getLicenseUrl() + ")__");
            }
            else {
                ps.println("__[" + tpLicDesc + " version " + tpLibraryLic.getVersion() + "](" + linkPrefix + tpLibraryLic.getType() + "-" +
                        tpLibraryLic.getVersion() + ".md)__");
            }
            ps.println();

            if (tpLicense.getProducts().getProducts() != null) {
                ps.println("The following third party products are using this license:");
                ps.println();

                for (ProductConfig product : tpLicense.getProducts().getProducts()) {
                    ps.println("* [" + product.getName() + "-" + product.getVersion() + "](" + product.getWeb() + ")");
                }
                ps.println();
            }
        }
        ps.println("<!-- CLM -->");
    }

    /**
     * This creates a markdown document for a specific license.
     *
     * @param licensesMDDir The directory to write the license markdown document in.
     * @param libraryLic The license to create a markdown document for.
     *
     * @throws IOException
     */
    private static void writeMDLicense(File licensesMDDir, LibraryLicense libraryLic) throws IOException {
        File licenseMD = new File(licensesMDDir, libraryLic.getType() + "-" + libraryLic.getVersion() + ".md");

        if (libraryLic.hasMarkdownFullTextLicense()) {
            InputStream from = libraryLic.getFullTextMarkdownStream();
            try {
                CopyTool.copyFile(from, licenseMD);
            }
            finally {
                try { from.close();} catch (IOException ioe) {}
            }
        }
        else {
            convertLicenseToMarkdown(licenseMD, libraryLic);
        }
    }

    /**
     * This creates a markdown document for a specific license.
     *
     * @param licenseMD The {license}.md file to produce.
     * @param libraryLic The license to create a markdown document for.
     *
     * @throws IOException
     */
    private static void convertLicenseToMarkdown(File licenseMD, LibraryLicense libraryLic) throws IOException {
        if (libraryLic.getFullTextStream() == null) {
            Display.msg("Error: Can't convert license to markdown: have no license text to convert! [" + libraryLic + "]");
            return;
        }

        String licDesc = libraryLic.getDescription() != null ? libraryLic.getDescription() : libraryLic.getType();
        String title = licDesc + " version " + libraryLic.getVersion();

        PrintStream ps = null;
        BufferedReader from = null;
        try {
            ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(licenseMD)));
            ps.println("<!--");
            ps.println("  This was created by CodeLicenseManager");
            ps.println("-->");
            ps.println("##" + title);
            ps.println();
            String line;
            boolean htmlSource = false;

            from = new BufferedReader(new InputStreamReader(libraryLic.getFullTextStream()));
            if (libraryLic.isDownloadable() && libraryLic.getURL() != null) {
                htmlSource = libraryLic.getURL().endsWith(".html") || libraryLic.getURL().endsWith(".htm");
            }

            if (!htmlSource) {
                while ((line = from.readLine()) != null) {
                    ps.println("\t" + line);
                }
            }
            else {
                String ln = from.readLine();
                while (!ln.trim().startsWith("<body") && ln != null) {
                    ln = from.readLine();
                }
                if (ln == null) {
                    from = new BufferedReader(new InputStreamReader(libraryLic.getFullTextStream()));
                }

                StringWriter sw = new StringWriter();
                int c = from.read();
                boolean write = true;
                while (c != -1) {
                    if (c == 'p' && !write) {
                        ps.println();
                    }
                    if (c == '>' && !write) {
                        write = true;
                    }
                    else if (c == '<' && write) {
                        write = false;
                    }
                    else if (write) {
                        sw.write(c);
                    }

                    c = from.read();
                }

                String licText = sw.toString();
                sw.close();
                licText = licText.replace("&quot;", "\"");
                licText = licText.replace("&gt;", ">");
                licText = licText.replace("&lt;", "<");

                ps.print(licText);

            }
        }
        catch (IOException ioe) {
            String msg = "Failed to read license text for '" + libraryLic.getType() + "-" + libraryLic.getVersion() + "' due to '" +
                    ioe.getMessage() + "'!";
            ps.println(msg);
            Display.msg(msg);
        }
        finally {
            if (ps != null) {
                ps.flush();
                ps.close();
            }
            if (from != null) {
                from.close();
            }
        }
    }

    /**
     * Appends to or updates license information at the bottom of an existing markdown document.
     *
     * @param docDir The directory of the markdown doc to append to.
     * @param mdDoc The markdown document to append/update (relative to docDir).
     * @param linkPrefix The prefix to use for links.
     *
     * @throws IOException
     */
    public static void appendUppdateLicensesToMarkdownDoc(File docDir,  String mdDoc, String linkPrefix, Configuration config)
            throws IOException {
        File doc = new File(docDir, mdDoc);

        TextFileEditor editor = new TextFileEditor();
        editor.load(doc);

        if (editor.find("<!-- Created by CodeLicenseManager -->")) {
            editor.startSelection();
            if (!editor.find("<!-- CLM -->")) {
                Display.msg("Found beginning of license inf block, but not the end! Skipping update!");
                return;
            }
            editor.moveToEndOfLine();
            editor.endSelection();
            editor.deleteSelection();
            editor.deleteCurrentLine();
        }
        else {
            editor.moveToEndOfFile();
        }

        ByteArrayOutputStream licDataBytes = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(licDataBytes);
        writeLicensesMarkdown(ps, linkPrefix, config);
        ps.close();
        String licData = new String(licDataBytes.toByteArray());

        editor.insertLine(licData);

        editor.save();
    }

}
