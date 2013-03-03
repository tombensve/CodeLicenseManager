package codelicmgr.sourcecodeupdaters;

import java.io.InputStream;
import java.net.URL;
import se.natusoft.tools.codelicmgr.annotations.Author;
import se.natusoft.tools.codelicmgr.annotations.Authors;
import se.natusoft.tools.codelicmgr.annotations.Change;
import se.natusoft.tools.codelicmgr.annotations.Copyright;
import se.natusoft.tools.codelicmgr.annotations.License;
import se.natusoft.tools.codelicmgr.annotations.Project;
import se.natusoft.tools.codelicmgr.enums.Source;

@Project(
    name="CodeLicenseManager-manager",
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
        changes={
            @Change(when="2008-11-23", description="Created")
        }
    )
})
/**
 * This class is used to load resources of the classpath relative to the package of this class.
 * This has to be done since this class can be managed by the maven class loader and not the system
 * class loader. ClassLoader.getSystemResourceAsStream("codelicmgr/sourcecodeupdaters/...") will not find
 * anything while getResourceAsStream(...) on this class will even if it is managed by the maven
 * class loader.
 */
public class SourceCodeUpdaterResourceProvider {

    /**
     * Returns an InputStream to the classpath:/codelicmgr/sourcecodeupdaters relative path.
     *
     * @param path The path to get an InputStream for.
     *
     * @return An InputStream or null if path does not point to an existing resource.
     */
    public static InputStream getInputStream(String path) {
        return SourceCodeUpdaterResourceProvider.class.getResourceAsStream(path);
    }

    /**
     * Returns an url pointing the the classpath:/codelicmgr/sourcecodeupdaters relative path.
     *
     * @param path The path to the the url to.
     *
     * @return An url if found or null otherwise.
     */
    public static URL getResourceUrl(String path) {
        return SourceCodeUpdaterResourceProvider.class.getResource(path);
    }
}
