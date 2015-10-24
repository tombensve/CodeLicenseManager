/* 
 * 
 * PROJECT
 *     Name
 *         CodeLicenseManager-manager
 *     
 *     Code Version
 *         2.1.6
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

/**
 * This exception is thrown if correct license information for the specified
 * licence cannot be found on the classpath.
 */
public class CodeLicenseException extends RuntimeException {

    //
    // Private Members
    //

    /** A type of problem. */
    private Type type;

    //
    // Constructors
    //

    /**
     * Creates a new CodeLicenceException instance.
     * 
     * @param message The exception message.
     * @param type The type of exception.
     */
    public CodeLicenseException(String message, Type type) {
        super(message);
        this.type = type;
    }

    /**
     * Creates a new CodeLicenceException instance.
     *
     * @param message The exception message.
     * @param type The type of exception.
     * @param cause The exception that caused this one.
     */
    public CodeLicenseException(String message, Type type, Throwable cause) {
        super(message, cause);
        this.type = type;
    }

    //
    // Methods
    //
    
    /**
     * Returns the exception type.
     */
    public Type getType() {
        return this.type;
    }

    //
    // Inner Classes
    //

    /**
     * Provides a set of specific problems with messages.
     */
    public static enum Type {

        BAD_SOURCE_UPDATER_LIBRARY("The found source code updater library for the language are incorrectly configured!"),
        BAD_LICENSE_LIBRARY("Badly configured license library for license type!"),
        BAD_CONFIGURATION("Badly configured CodeLicenseManager execution configuration!");

        /** A description of the type. */
        private String description;

        /**
         * Creates a new Type.
         *
         * @param description The description of the type.
         */
        Type(String description) {
            this.description = description;
        }

        /**
         * Returns the type description.
         */
        public String getDescription() {
            return this.description;
        }
    }
}
