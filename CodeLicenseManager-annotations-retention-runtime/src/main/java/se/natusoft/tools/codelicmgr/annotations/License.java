/* 
 * 
 * PROJECT
 *     Name
 *         CodeLicenseManager-annotations-retention-runtime
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
package se.natusoft.tools.codelicmgr.annotations;

import se.natusoft.tools.codelicmgr.enums.Source;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation specifies the licence that applies to the code.
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE,ElementType.PACKAGE})
public @interface License {

    /**
     * The type of licence. Corresponding licence file should be available under se.biltmore.codelicmgr.licences
     * or codelicmgr.licences.
     */
    String type();

    /**
     * An optional version of the licence. "-" + version() will be appended to type() to get the name of the
     * licence text provider if specified.
     */
    String version() default "";

    /**
     * A short description of the licence. 
     */
    String description() default "";

    /**
     * This can be set to anything or left blank, but must be specified. The CodeLicenceManager-maven-plugin
     * will update this with the licence text for the specified licence.
     */
    String[] text() default {};

    /**
     * An URL to the complete licence text on the web.
     */
    String licenceURL() default "";

    /**
     * Defines the source as open or closed.
     */
    Source source() default Source.OPEN;
}
