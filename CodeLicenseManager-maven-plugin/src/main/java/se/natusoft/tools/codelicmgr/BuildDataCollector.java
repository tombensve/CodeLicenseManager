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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.*;

/**
 * This collects information from all module project part of a complete build
 * by saving to disk between each project build since maven is so nasty as
 * to create a new ClassLoader for each plugin invocation causing static
 * fields to not survive. Well, maven is correct in doing that, it just
 * makes collecting total information more difficult :-)
 */
public abstract class BuildDataCollector<T> {
    //
    // Private Members
    //

    /** Holds the collected data and is stored on disk between invocations. */
    private T data = null;

    /** The current maven project. */
    private MavenProject mavenProject = null;

    /** The file we read and write the data from/to. */
    private File dataFile = null;

    //
    // Constructors
    //

    /**
     * Creates a new BuildDataCollector.
     *
     * @param mavenProject The current maven project.
     *
     * @throws MojoExecutionException
     */
    public BuildDataCollector(MavenProject mavenProject) throws MojoExecutionException {
        this.mavenProject = mavenProject;
    }

    //
    // Methods
    //

    /**
     * Get the data object. This cannot be called before load() has been called!
     */
    public T getData() {
        return this.data;
    }

    /**
     * Loads the data object from disk.
     *
     * @throws MojoExecutionException on failure.
     */
    @SuppressWarnings("unchecked")
    public void load() throws MojoExecutionException {
        this.dataFile = new File(getRootDir(), "target");
        this.dataFile = new File(this.dataFile, "." + getName() + "Collector");
        this.dataFile.getParentFile().mkdirs();

        if (!this.mavenProject.isExecutionRoot() && this.dataFile.exists()) {
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(new FileInputStream(this.dataFile));
                this.data = (T)ois.readObject();
            }
            catch (IOException ioe) {
                throw new MojoExecutionException(ioe.getMessage(), ioe);
            }
            catch (ClassNotFoundException cbfe) {
                throw new MojoExecutionException("Can't load due to missing class!", cbfe);
            }
            finally {
                if (ois != null) {
                    try {ois.close();} catch (IOException ignored) {}
                }
            }
        }
        else {
            this.data = newInstance();
        }
    }

    /**
     * Saves the current version of the data object to disk.
     *
     * @throws MojoExecutionException
     */
    public void save() throws MojoExecutionException {
        if (this.dataFile == null) {
            throw new MojoExecutionException("load() must be called before save() can be done!");
        }
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(this.dataFile));
            oos.writeObject(this.data);
        }
        catch (IOException ioe) {
            throw new MojoExecutionException(ioe.getMessage(), ioe);
        }
        finally {
            if (oos != null) {
                try {oos.close();} catch (IOException ignored) {}
            }
        }
    }

    /**
     * @return A new instance of the data object.
     */
    public abstract T newInstance();

    /**
     * @return A unique name the save file name will be based on.
     */
    public abstract String getName();

    /**
     * Returns a File representing the directory whose parent directory does not have a pom.xml.
     * In other words, the root of a multi-module build.
     */
    private File getRootDir() {
        File root = this.mavenProject.getBasedir();
        while (havePOM(root.getParentFile().listFiles())) {
            root = root.getParentFile();
        }

        return root;
    }

    /**
     * Checks if any of the passed files is a pom.xml.
     *
     * @param files The files to check.
     *
     * @return true if found, false otherwise.
     */
    private boolean havePOM(File[] files) {
        for (File file : files) {
            if (file.getName().toLowerCase().equals("pom.xml")) {
                return true;
            }
        }

        return false;
    }
}
