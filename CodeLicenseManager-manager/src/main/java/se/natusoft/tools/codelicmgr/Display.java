package se.natusoft.tools.codelicmgr;

import se.natusoft.tools.codelicmgr.annotations.*;
import se.natusoft.tools.codelicmgr.enums.Source;

/**
 * Used to display output to the user. The different users of this project (maven plugin, ant task, command line) can subclas
 * this to provide an output implementation.
 */
@Project(
    name="CodeLicenseManager-manager",
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
            @Change(when="2010-01-09", description="Created")
        }
    )
})
public abstract class Display {

    /** The display instance to use for displaying messages. */
    private static Display instance = null;

    /** The current message indent. */
    private static String indent = "";

    /**
     * Displays the specified message to the user.
     * 
     * @param message The message to display.
     */
    public abstract void display(String message);

    /**
     * Sets a Display instance to use.
     *
     * @param display The display instance to set.
     */
    public static void setDisplay(Display display) {
        Display.instance = display;
    }

    /**
     * Changes the message indent.
     * 
     * @param indent The indent to set.
     */
    public static void setIndent(String indent) {
        Display.indent = indent;
    }

    /**
     * Displays a message to the user.
     *
     * @param message The message to display.
     */
    public static void msg(String message) {
        if (Display.instance != null) {
            Display.instance.display(Display.indent + message);
        }
    }
}
