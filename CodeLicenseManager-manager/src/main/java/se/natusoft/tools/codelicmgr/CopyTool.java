/* 
 * 
 * PROJECT
 *     Name
 *         CodeLicenseManager-manager
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

import java.io.*;

/**
 * Supplies a static file copy method.
 */

public class CopyTool {
   
    /**
     * Copies one stream to another.
     *
     * @param from The stream to copy from.
     * @param to The stream to copy to.
     *
     * @throws java.io.IOException
     */
    public static void copyFile(InputStream from, OutputStream to) throws IOException {
        int b;
        while ((b = from.read()) != -1) {
            to.write(b);
        }
        to.close();
    }

    /**
     * Copies one file to another.
     * 
     * @param from The file to copy from.
     * @param to The file to copy to.
     *           
     * @throws IOException on any IO failure.
     */
    public static void copyFile(File from, File to) throws IOException {
        copyFile(new FileInputStream(from), to);
    }

    /**
     * Copies an InputStream to a file.
     *
     * @param from The InputStream to copy from.
     * @param to The target file to copy to.
     *
     * @throws IOException on any IO failure.
     */
    public static void copyFile(InputStream from, File to) throws IOException {
        if (!to.exists()) {
            to.getParentFile().mkdirs();
        }
        FileOutputStream toStream = new FileOutputStream(to);
        try {
            copyFile(from, toStream);
        }
        finally {
            try { toStream.close(); } catch (IOException ioe) {}
        }
    }
}
