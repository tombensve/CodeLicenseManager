package test;

import se.natusoft.tools.codelicmgr.enums.Source;
import se.natusoft.tools.codelicmgr.annotations.*;

/**
 * This is not a unit test! This is a test subject for testing update of
 * project and license info.
 */
@Project(
    name="CodeLicenceManager"
)
@Copyright(year="2009", holder="Biltmore Group AB")
@License(
    type="Apache",
    version="2.0",
    description="Apache License",
    source=Source.OPEN,
    text=[
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
    ]
)
public class testsource {


    public testsource() {

    }

}
