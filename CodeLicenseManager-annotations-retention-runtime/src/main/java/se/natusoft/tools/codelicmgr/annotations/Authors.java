package se.natusoft.tools.codelicmgr.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import se.natusoft.tools.codelicmgr.enums.Source;
import se.natusoft.tools.codelicmgr.annotations.*;

/**
 * This specifies a list of authors.
 */
@Project(
    name="CodeLicenseManager-annotations-retention-runtime",
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
        changes={
            @Change(when="2008-11-23", description="Created")
        }
    )
})

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE,ElementType.PACKAGE})
public @interface Authors {

    /**
     * Specifies a set of authors.
     */
    Author[] value();
}
