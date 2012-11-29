package se.natusoft.tools.codelicmgr;

import se.natusoft.tools.codelicmgr.annotations.*;
import se.natusoft.tools.codelicmgr.enums.Source;

/**
 * This provides available actions.
 */
@Project(
    name="CodeLicenseManager-command-line",
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
            @Change(when="2009-12-29", description="Created")
        }
    )
})
public enum Action {
    
    /** Updates source code. */
    APPLY,

    /** Installs license files. */
    INSTALL,

    /** Runs the scripts specified in the configuration and nothing but that. */
    SCRIPT,

    /** Runs delete script to do the opposite of APPLY. */
    DELETE;
}
