package se.natusoft.tools.codelicmgr;

import se.natusoft.tools.codelicmgr.enums.Source;
import se.natusoft.tools.codelicmgr.annotations.*;
import se.natusoft.tools.codelicmgr.cmdline.config.CmdlineOptions;
import se.natusoft.tools.optionsmgr.CommandLineOptionsManager;

/**
 * This interface is implemented by CLM to provide an API for calling from
 * system classloader to CLM class loaded by URLClassLoader.
 */
@Project(
    name="CodeLicenseManager-command-line",
    codeVersion="2.1",
    description="Manages project and license information in project sourcecode" +
                "and provides license text files for inclusion in builds. Supports" +
                "multiple languages and it is relatively easy to add a new" +
                "language and to make your own custom source code updater."
)
@Copyright(year="2013", holder="Natusoft AB", rights="All rights reserved.")
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
            @Change(when="2009-12-29", description="Created"),
            @Change(when="2010-01-04", description="Updated to include CommandLineOptionsManager, which is now needed " +
                                                   "by CLMMain to print help.")
        }
    )
})
public interface CLMMainAPI {

    /**
     * This is implemented by CLMMain to allow Main to call CLMMain from URLClassLoader loaded
     * class without using reflection.
     * 
     * @param args The user provided options.
     * @param optionsMgr The otions manager used to load the options.
     * 
     * @throws Exception
     */
    public void clmmain(CmdlineOptions options, CommandLineOptionsManager<CmdlineOptions> optionsMgr) throws Exception;
}
