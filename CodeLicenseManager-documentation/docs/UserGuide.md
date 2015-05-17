# Code License Manager 2.1.4

## Introduction

CodeLicenseManager (henceforth called CLM) was born out of frustration in handling licenses. Different IDE's have come up with different solutions for supplying ready "boilerplate" texts for different licenses, but go no further than that.

CLM solves this in an IDE independent way and also installs license texts of project  license and third party licenses including a list of which of the used third party  products uses each license. CLM can be used with any programming language and not only Java even though it is written in Java. It currently supports the following languages: Java (+ BeanShell), Java properties files, JSP, Groovy, Python, Ruby, 
C, C++, C#, CSS, HTML, XHTML, XML, XML schema, XSL Stylesheets, Bourne shell, AWK, Perl, but can easily be extended to other languages. See "Making your own ..." below for more information. 

CLM also makes it extremely easy to change license (does happen) and allows you to not decide on a license at the start of a project, a license can be applied at any time. 

If your CM tool allows pre checkin scripts to be run it can be plugged in at that time to make sure all checked in code has the license text. If a source file already has the license text nothing will be changed, if it doesn't it will be added. 

## Version changes

__Version 2.1.4__ 

Fixed bug in CodeLicenseManager-maven-plugin (PomExtractor) when parsing properties of poms to be able to resolve eventual versions and license specifications specified as property references. Any failure to resolve properties are now logged, but does not break the build any more!  

__Version 2.1.3__

Cleanup. No longer uses annotation based "boilerplate" text in source headers. This removes the annoying dependency on the actual annotations. Those where a bad idea to begin with. Me using them was even dumber :-). The annotation jars are still left, but is no longer used by CLM iteself. This in case some else have also made my mistake of using them. 

__Version 2.1.2__

Depenency version bug fixes. 

__Version 2.1.1__

* Greatly improved HTML to Markdown conversion of downloaded licenses.
* Fixed misspelling in OSGi license properties file.
* Made already made markdown versions of several licenses available in license library.

__Version 2.1__

* Generated APT and Markdown documents now links to license on web if possible instead of locally installed license file. This due to that all licenses that is not in local license library and that has an url to the license text on the web is not downloadable by a java.net.URL! Specifically Glassfish stuff that wants a certificate to download license (I keep my pesonal thoughts about that to myself!). If you click on a link that brings you to the page in a browser then the browser deals with the certificate request, but java.net.URL does not. It throws an exception. CLM will still install local license files and download if possible. Those files will just not be linked from the generated doc. 

* Last version tried to collect license information from all sub builds of a multi project maven build. It did this by using a static field in the maven plugin. I was originally tricked into beleiving this actually worked, but it doesn’t. Maven does everything it can to isolate the execution of a plugin, and rightly so, but in that effort it creates a new ClassLoader for each invokation and reloads all plugin classes so that static fields are reinitialized on each plugin invocation and is thus no better than a non static field in this case. So now I have solved it by saving to disk between plugin invocations. The target directory will now contain a `.ThirdpartyLicensesConfigCollector` file. This gets overwritten on each new build. It will not be added to between external builds, only between invocation within the same build. 

* For both auto collected license information and license information specified in pom, CLM will now complement it with URL if such is not specified but available in license library.

* No longer tries to best-effort-fake a library license when there are errors in the library definition. It will now fail the build in this case. I have just spent many, many hours trying to find a bug that was very hidden due to the best-effort-fake behavior! If it is wrong, the source of that wrong should be fixed instead of covering it up! The best-effort-fake was a really dumb thing to do in the first place.

* Added license url for all licenses in library.

* Added OSGi Specification License 2.0 to library.

* Did some internal cosmetic work like breaking out all APT and Markdown support code into their own classes rather than have them in the _CodeLicenseManager_ class. The APT code now resides in the maven plugin since it is maven specific. It is now more easy to add the markdown functionallity to the command line version, but it is not there yet in this version.

## Features

Code License Manager is more or less what it sounds like. Following is a short feature
list:

* Updating source code with license "boilerplate" text and copyrights.

* Very easy to change license.

* No need for special source templates containing the license boilerplate text.
    Just create new source files from whatever template or none at all, and then
    run CodeLicenseManager "apply" before checkin.


* Updating source code with project information (description, current version, etc).

* Installing license texts for project license and third party licenses.

* For third party licenses a file listing the external dependencies using each
    license is also produced.

* Configurable where license texts should be installed.

* Running own source editing scripts.

* Can be run from:

* Maven (plugin).

    * Can resolve project license, third party licenses, copyrights, project
    description, project version, etc from maven pom.

    * Can download a third party license full text from "licenses/license/url"
    in the pom if the license cannot be found in an available license library.

    * Can produce APT documents for maven-site-plugin with a page specifying the
    project license with a link to a generated license page, and all licenses
    used by third party products (with links to license) and a list of third
    party products using each license. This is a better replacement for the
    maven license report.

* Ant (task).

    * Currently not supported due to a classloading problem with beanshell when
    run within an Ant task.

* Command line (java -jar).

    * This to support running from makefiles for other languages than Java. This
    tool is not limited to only Java development, it can be used for any programming
    language. The platform the development is done on must however have a Java5
    or higher Java VM available for running CodeLicenseManager.

    * Requires an XML configuration file that is identical to the \<configuration\>
    section of the maven plugin.

* Uses license libraries for license boilerplate text and full license texts.

* A license library can point to the full license text on the web or include
    it as a text file.

* Includes a license library with the most common open source licenses.

* Easy to make own license libraries.

* Not only for open source. Can have private, closed source license in library.

* Uses source code update libraries (simply called source updaters) based on Bean
Shell scripts for different languages and formats. Each script gets a programmable
only text file editor instance with the current source file loaded and uses
the editor methods to manipulate the text.

* Includes source updaters for the following types of comments:

    * _/* ... */_ - Recognized language extensions: bsh, c, cpp, css, groovy,
    java.

    * _#_ - Recognized language extensions: sh, properties, ruby, py, perl,
    awk.

    * _\<!-- ... --\>_ - Recognized language extensions: htm, html, xml, xsd,
    xsl, xhtml, ent.

    * _\<%-- ... --%\>_ - Recognized language extensions: jsp.

    * Annotations - Recognized language extensions: java, groovy.

* Easy to make your own source updater.

* Has 2 variants of source updaters for Java and Groovy:

    * Project, license, and copyright information is provided in a comment block
    at the top of the file, just like for any other language.

* Users can specify own scripts to run on source files in configuration either
as separate script files or inline. These scripts can do whatever you want.
For example update a constant with the products current version number, or whatever
your creative mind can come up with. It is possible to run only user scripts
without running source updater libraries.

* Requires Java5 or higher.


## Definitions

_CLM_ - Short for "Code License Manager". 

_License library_ - A jar file containing one or more licenses. When CLM is 
run from Maven it needs to be available in classpath. From the command-line tool 
a library jar is pointed out via an argument. 

_Source Updater (library)_ - A jar file containing source code update scripts 
for a specific comment type, and a set of source code extensions that are known 
to use that comment type for automatic resolving of updater to use. 


## Using

There are 4 actions that can be preformed: 

_apply_ - Apply license on source files. 

_install_ - Install full license texts in build. 

_script_ - Runs only configuration scripts and nothing else. This action has 
nothing to do with license handling at all, but is a quite useful function that 
can be used even if you never run apply or install. 

_delete_ – Runs delete scripts if they are provided by the used updater. This 
is the opposite of "apply". 





# Configuration

No matter how you run CLM it has an identical configuration. Here is an example: 

<configuration>

The "project" section defines project information. It is required by _apply_ 
and _install_. 

    <project>
        <name>My Project</name>
        <description>This is a description of my test project.</description>
        <codeVersion>1.5</codeVersion>
        <license>
            <type>Apache</type>
            <version>2.0</version>
        </license>
        <copyright>
            <holder>Me myself & I</holder>
            <year>2010</year>
        </copyright>
        <copyright>
            <holder>...</holder>
            <year>...</year>
        </copyright>
    </project>

The "thirdpartyLicenses" section supplies information about third party code used 
by the project. This is required by _install_. 

    <thirdpartyLicenses>
        <license>
            <type>LGPL</type>
            <version>v3</version>
            <licenseProducts>
                <product>
                    <name>BeanShell-1.3.0</name>
                    <web>http://www.beanshell.org/</web>
                </product>
                <product>
                    <name>Hibernate-3.5.0</name>
                    <web>https://www.hibernate.org/</web>
                </product>
            </licenseProducts>
        </license>
        <license>
            <type>ASF</type>
            <version>2.0</version>
            <licenseProducts>
                <product>
                    <name>log4j-1.2</name>
                    <web>http://logging.apache.org/log4j/1.2/</web>
                </product>
            </licenseProducts>
        </license>
    </thirdpartyLicenses>

The "scripts" section is a utility that lets projects make code edits in a simple 
way. This section is entirely optional, but when it is available it is used by 
_apply_ and _script_. 

    <scripts>
        <script>
            <fileFilter>.*CodeLicenseManager\.java</fileFilter>
            <code>
                if (editor.find("Display.msg\\(\"CodeLicenseManager ")) {
                    display("Updating version, copyright and maintainer!");
                    editor.deleteCurrentLine();
                    editor.insertLine("        Display.msg(\"CodeLicenseManager " +
                        "${app.version}\\nCopyright (C) ${copyrightYear} by " +
                        "${copyrightHolder}\\nMaintained by ${developer1Name} " +
                        "(${developer1Email})\");");
                }
            </code>
        </script>
        <script>
            <fileFilter>.*-source-updater-.*\.properties</fileFilter>
            <scriptFile>scripts/props.bsh</scriptFile>
        </script>
    </scripts>

The "codeOptions" section provides options for and is required by _apply_. 

    <codeOptions>
        <verbose>true</verbose>
        <codeLanguage>by-extension</codeLanguage>
        <updateLicenseInfo>true</updateLicenseInfo>
        <updateCopyright>true</updateCopyright>
        <updateProject>true</updateProject>
        <addAuthorsBlock>true</addAuthorsBlock>
        <sourceCodeDirs>
            src/main/java/**/.*\.java,
            src/main/resources/**/.*\.xml,
            src/main/resources/**/.*\.properties,
            src/main/webapp/**/.*\.jsp
        </sourceCodeDirs>
    </codeOptions>

The "installOptions" section provides options for and is required by _install_. 

    <installOptions>
        <verbose>true</verbose>
        <licenseDir>license</licenseDir>
        <thirdpartyLicenseDir>license/thirdparty</thirdpartyLicenseDir>
    </installOptions>

The "userData" section provides name and value pairs that will be available to 
scripts as String variables with the specified name. There are available to both 
"scripts" section scripts and scripts in source-updater libraries. This section 
is optional and when available used by _apply_ and _script_. 

    <userData>
        <name>...</name><value>...</value>
        <name>...</name><value>...</value>
    </userData>


## Configuration explanations

__configuration/project (section)__

Supplies project information. 

__configuration/project/name text__

The name of the project. 

__configuration/project/description text__

A description of the project. 

__configuration/project/codeVersion text__

The current version of the project code. 

__configuration/project/subProjectOf text__

A project of which this is subproject of. 

__configuration/project/license (section)__

The license of the project. 

__configuration/project/license/type text__ 

The license type. Example "LGPL" or "Apache". 

__configuration/project/license/version text__

The version of the license. Example "v3" or "2.0". 

__configuration/project/copyright (section)__

Copyright(s) held by the code. 

__configuration/project/copyright/year text__

The copyright year. 

__configuration/project/copyright/holder text__

The copyright holder. 

__configuration/project/copyright/rights text__

Is holds the string "All rights reserved." by default. If you want something else, 
change it. 

---

__configuration/thirdpartyLicenses (section)__ 

Third party licenses. 

__configuration/thirdpartyLicenses/license (section)__ 

Specifies licenses of used third party products. 

__configuration/thirdpartyLicenses/license/licenseProducts (section)__ 

The used third party products using this license. This is only relevant for third 
party licenses! 

__configuration/thirdpartyLicenses/license/licenseProducts/product (section)__ 

A used third party product having this license. 

__configuration/thirdpartyLicenses/license/licenseProducts/product/name text__ 

The name of the product. 

__configuration/thirdpartyLicenses/license/licenseProducts/product/version text__ 

The version of the product. 

__configuration/thirdpartyLicenses/license/licenseProducts/product/web text__ 

The products web site. 

__configuration/thirdpartyLicenses/license/licenseUrl text__ 

The url to the license text on the web. This is required if the license cannot 
be found in license library! 

__configuration/thirdpartyLicenses/license/type text__ 

The license type. Example "LGPL" or "Apache". 

__configuration/thirdpartyLicenses/license/version text__ 

The version of the license. Example "v3" or "2.0". 

---

__configuration/installOptions (section)__ 

Provides license file install options. 

__configuration/installOptions/verbose true/false__ 

If true verbose output is provided. 

__configuration/installOptions/licenseDir text__ 

The directory to where the license text should be copied. 

Defaults to 'license'. 

__configuration/installOptions/thirdpartyLicenseDir text__ 

The directory to where the third party license texts are copied. Defaults to 'license/thirdparty'. 

---

__configuration/codeOptions (section)__ 

Provides source code update options. 

__configuration/codeOptions/verbose true/false__ 

If true verbose output is provided. 

__configuration/codeOptions/codeLanguage text__ 

The language in which to process source code for. 

__configuration/codeOptions/updateLicenseInfo true/false__ 

If true the license information in the source will be updated. 

__configuration/codeOptions/updateCopyright true/false__ 

If true the Copyright information in the source will be updated. 

__configuration/codeOptions/updateProject true/false__ 

If true the Project information will be updated with the information specified. 

__configuration/codeOptions/addAuthorsBlock true/false__ 

If true The Authors information will be added if it does not exist. 

__configuration/codeOptions/sourceCodeDirs text__ 

A comma separated list of source paths of files to update. The directory part 
of the path can have ** wildcard which means all directories below. The file part 
of the path is regular expression. For example: ".*.java". 

Here are some examples: 

_.../myproj/src/main/java/\*\*/.*.java_ - All java files under .../src/main/java and below. 

_.../myproj/src/main/java/some/package_ - All files directly in the "package" directory. 

_.../myproj/src/main/resources/help/.*.hlp_ - All .hlp files in the help directory. 

_.../myproj/src/main/\*\*_ - All files in the main directory and below. 

---

__configuration/userData (section)__ 

Provides user information for use in personal source code updaters. A name/value 
pair can occur multiple times! 

__configuration/userData/name text__ 

Specifies name for the data. 

__configuration/userData/value text__ 

Specifies data value. 

---

__configuration/scripts (section)__ 

Specifies scripts to run on source files of specified file extension. 

__configuration/scripts/script (section)__ 

A script to execute. 

__configuration/scripts/script/fileFilter text (Required)__ 

A regular expression file filter for which files to apply script to. 

__configuration/scripts/script/code text__ 

Inline script code to run if \<scriptFile/\> is not specified. 

__configuration/scripts/script/scriptFile text__ 

This points to script in separate file to execute instead of the \<code/\> block. 


## Maven

When you are using maven the \<configuration\>...\</configuration\> section above 
is part of the maven plugin specification in the pom.xml file. Example: 

    ...
    <plugins>
        <plugin>
            <groupId>se.natusoft.tools.codelicmgr</groupId>
            <artifactId>CodeLicenseManager-maven-plugin</artifactId>
            <version>2.0</version>
    
            <dependencies>
                <dependency>
                    <groupId>se.natusoft.tools.codelicmgr</groupId>
                    <artifactId>
                        CodeLicenseManager-licenses-common-opensource
                    </artifactId>
                    <version>2.0</version>
                </dependency>
            </dependencies>
    
            <configuration>
                    ...
            </configuration>
    
            <executions>
                <execution>
                    <id>install-lics</id>
                    <goals>
                        <goal>install</goal>
                    </goals>
                    <phase>install</phase>
                    <configuration>
                        <installOptions>
                        ...
                        </installOptions>
                    </configuration>
                </execution>
            </executions>            
        </plugin>
        ...
    </plugins>
    ...

If you are running the _apply_ goal you also need one or more source updater libraries 
among the dependencies. 

### Maven special feature 1 - Skip project section

With maven you can skip the project section since that information can be resolved 
elsewhere in the pom: 

    <project ...>
        ...
        <version>1.0</version>
        ...
        <name>CodeLicenseManager</name>
        <description>
            Manages project and license information in project sourcecode
            and provides license text files for inclusion in builds. Supports
            multiple languages and it is relatively easy to add a new
            language and to make your own custom source code updater.
        </description>
        <licenses>
            <license>
                <!-- 
                    Name needs to be in "{type} {version}" or 
                    "{type}-{version} format to be reused by the plugin. 
                -->
                <name>Apache 2.0</name>
                <url>http://www.apache.org/licenses/LICENSE-2.0</url>
            </license>
        </licenses>
        <organization>
            <!-- copyright holder -->
            <name>Natusoft AB</name>
            <url>http://www.natusoft.se/</url>
        </organization>
        <!-- copyright year -->
        <inceptionYear>2009</inceptionYear>
        <developers>
            <developer>
                <name>Tommy Svensson</name>
                <email>tommy@natusoft.se</email>
            </developer>
        </developers>
        ...
    </project>


### Maven special feature 2 - Automatic resolve of third party licenses

Unless you specify: 

    ...
    <configuration>
        ...
        <autoResolveThirdPartyLicenses>false</autoResolveThirdPartyLicenses>
    </configuration>
    ...

the "install" goal will inspect all the dependencies and try to resolve the licenses 
used by third party code. For this to work it requires that the dependency has 
specified license information in its pom. Most pom's out there in the central 
repository are unfortunately quite sloppy. Any dependencies having the same 
group id as the project being built will be ignored. Any dependency with 
scope "test" will also be ignored. 

I recommend running the _install_ goal in the "install" phase of the standard 
maven build lifecycle. I also recommend running the _apply_ goal in an "apply-lics" 
profile in the "generate-sources" phase of the standard maven build lifecycle 
and do "mvn -P apply-lics generate-sources" whenever an update is needed, for 
example when new source files have been created. 


### Maven special feature 3 - Better license documents for use with maven-site-plugin.

#### APT generation

If you specify: 

    ...
    <configuration>
        ...
        <createLicencesAPT>true</createLicencesAPT>
    </configuration>
    ...

then an apt document for each license will be written to _src/site/apt/licenses_ 
plus a licenses.apt document. This generated page shows the project license with 
a link to the generated apt document for that license and all third party licenses 
where license name is both a section title and a link to the generated apt document 
for that license. The section then lists the products using the license and a 
link to the products website. 

The generated licenses.apt document should be specified in the site.xml instead 
of license.html (you should actually not include the standard maven site license 
report when using this!): 

    ...
    <menu name="Code License Manager">
        ...
        <item name="Project License" href="licenses/licenses.html"/>

        <item name="Issue Tracking" href="issue-tracking.html"/>
        <item name="Project Team" href="team-list.html"/>
        <item name="Source Repository" href="source-repository.html"/>
    </menu>
    ...

Note that you only need to include the licenses/licenses.html page since it provides 
links to each license page. 

If you are also using the maven-pdf-plugin you might have noticed that the pdf 
plugin does not handle pre-formatted texts larger than a page, it will only include 
one page and the rest is cut off. To go around this problem you can modify the 
above config by adding one more parameter: 

    ...
    <configuration>
        ...
        <createLicencesAPT>true</createLicencesAPT>
        <createMavenPDFPluginLicenseAPTVersions>
            true
        </createMavenPDFPluginLicenseAPTVersions>
    </configuration>
    ...

As this very long parameter suggests this will also generate a PDF plugin version 
of the license apt documents that contains appropriate page breaks. These variants 
will be prefixed with "pdf-". Use the licenses.apt and these pdf variants in pdf.xml: 

    ...
    <toc name="Table of Contents">
      <item name="User Guide" ref="UserGuide.apt"/>

      <item name="Licenses" ref="licenses/licenses.apt"/>
      <item name="Apache Software License 2.0" ref="licenses/pdf-Apache-2.0.apt"/>
      <item name="Lesser Gnu Public License v3" ref="licenses/pdf-LGPL-v3.apt"/>
    </toc>
    ...


#### Markdown generation

If you use services like GitHub and Bitbucket and want to document in markdown
without having to generate a whole site with the maven-site-plugin then you can
specify the following options:

	...
	<configuration>
		...
		<createLicensesMarkdown>true</createLicensesMarkdown>  (1) 
        <markdownTargetSubdir>docs</markdownTargetSubdir>      (2)
        <appendUpdateLicensesMarkdownToMarkdownDocument>       (3)
        	MyMarkdownDoc.md
        </appendUpdateLicensesMarkdownToMarkdownDocument>
        
    </configuration>
    ...

1. This will generate the same document as for APT listing project license and 
   thirdparty licenses with links, but in markdown format. 
2. By default the licenses.md file and the _licence_.md files for each license 
   will be generated in the project root, but specifying the second option in the 
   example above specifies another relative path to put it in.
3. Specifying the third option in the example above will append/update another
   markdown document with the project license and third party licenses instead
   of generating it as a separate document. If it doesn't exist in the document
   already then it will be added to the bottom of the document. If it already
   exists the existing will be updated with a new version. When first added 
   the license information is always put at the bottom, but if you manually
   move it somewhere else in the document (including the start and end html
   comment) then it will be updated in its new place. 
   
Putting the following into your document will place the license info there from the start:

    <!-- Created by CodeLicenseManager -->
    <!-- CLM -->


## Command-line

The command line variant can be run with: 

	cd .../CodeLicenseManager-dist-2.0
	java -jar bin/CodeLicenseManager-command-line-2.0-exec.jar arg ... arg

or 

	bin/clm.sh arg ... arg

  
The following arguments are available: 

__--config path (Required)__ 

This specifies config file to use for this run. This is the one described in the 
"Configuration" section above. 

__--action (constrained text) (Required)__

Specifies the action. Valid values are: "apply", "install", "script", "delete". 

The "apply" action updates source files according to the supplied configuration 
file. 

The "install" action installs license files in paths specified in the supplied 
configuration file. 

The "script" action only runs the scripts specified in the supplied configuration 
file. 

The "delete" action runs delete scripts in an source updater. This is basically 
the opposite of "apply". 

The \<codeOptions\> section is only used by the "apply" and "script" action, and
the \<installOptions\> section is only used by the "install" action.

__--licenselibrary text (Required)__

Specifies the license library jar to use. Please note that this is an url! (Example: 
file:lib/license/CodeLicenseManager-licenses-common-opensource-2.0.jar).

__--sourceupdaters text (Required)__ 

A comma separated list of source updater jars to use. At least one have to be 
specified. Please note that each specified source updater must be an url! (Example: 
file:lib/updaters/CodeLicenseManager-source-updater-slashstar-comment-2.0.jar).

__--help__

Provides help information. 


## Ant

Classloading for beanshell fails when it is invoked from an Ant task! Resolving 
this not being a #1 priority, I will currently not support Ant. 


## User Specific Configuration

When source code is updated there is an option to add author information if it 
is not already available. For the "slashstar" updater it will look like this for 
example: 

	* AUTHORS
	*     Tommy Svensson (tommy@natusoft.se)
	*         Changes:
	*         2009-11-09: Created!

When an author block is added, information about the user is read from 
_.clm-user.properties_ in the users home directory (/home/_user_ on most unix 
systems, /Users/_user_ on Mac OS X, and C:\\Documents and Settings\\_user_ on 
windows). This property file contains the following properties: 

	name=
	email=

If this file is not found the standard "user" system property is used to determine the 
system username, and email will not be provided. 





# Updating Source Code

This requires the "project" and "codeOptions" configuration sections. The "scripts" 
and "userData" sections are optional. 


## Maven

Please remember that the "project" section is optional if the information is specified 
elsewhere in the pom. See the "Configuration/Maven" section above. 

Use the CodeLicenseManager-maven-plugin with the "apply" goal. Here is an example: 

    <plugin>

        <groupId>se.natusoft.tools.codelicmgr</groupId>
        <artifactId>CodeLicenseManager-maven-plugin</artifactId>
        <version>2.1.3</version>

        <dependencies>
            <dependency>
                <groupId>se.natusoft.tools.codelicmgr</groupId>
                <artifactId>CodeLicenseManager-licenses-common-opensource</artifactId>
                <version>2.1.3</version>
            </dependency>
            <dependency>
                <!-- For the bsh scripts. -->
                <groupId>se.natusoft.tools.codelicmgr</groupId>
                <artifactId>CodeLicenseManager-source-updater-slashstar-comment</artifactId>
                <version>2.1.3</version>
            </dependency>
            <dependency>
                <!-- For properties. -->
                <groupId>se.natusoft.tools.codelicmgr</groupId>
                <artifactId>CodeLicenseManager-source-updater-hash-comment</artifactId>
                <version>2.1.3</version>
            </dependency>
        </dependencies>

        <configuration>

            <project>
                <name>My Project</name>
                <description>This is a description of my project.</description>
                <codeVersion>${project.version}</codeVersion>
                <license>
                    <type>Apache</type>
                    <version>2.0</version>
                </license>
                <copyright>
                    <holder>Me myself & I</holder>
                    <year>2010</year>
                </copyright>
            </project>

        </configuration>
        <executions>
            <execution>
                <id>apply-licence-info</id>
                <goals>
                    <goal>apply</goal>
                </goals>
                <phase>generate-sources</phase>
                <configuration>

                    <codeOptions>
                        <verbose>true</verbose>
                        <codeLanguage>by-extension</codeLanguage>
                        <updateLicenseInfo>true</updateLicenseInfo>
                        <updateCopyright>true</updateCopyright>
                        <updateProject>true</updateProject>
                        <addAuthorsBlock>true</addAuthorsBlock>
                        <sourceCodeDirs>
                            src/main/java/**/.*.java,
                            src/main/resources/**/.*.bsh,
                            src/main/resources/**/.*.properties
                        </sourceCodeDirs>
                    </codeOptions>

                </configuration>
            </execution>
        </executions>
    </plugin>

The "apply" goal should preferably be run within a profile since it is unnecessary 
to run it on every build. 


## Command line

Create a configuration file. For example: apply.xml: 

    <configuration>

        <project>
            <name>My Project</name>
            <description>This is a description of my project.</description>
            <codeVersion>1.0</codeVersion>
            <license>
                <type>Apache</type>
                <version>2.0</version>
            </license>
            <copyright>
                <holder>Me myself & I</holder>
                <year>2010</year>
            </copyright>
        </project>

        <codeOptions>
            <verbose>true</verbose>
            <codeLanguage>by-extension</codeLanguage>
            <updateLicenseInfo>true</updateLicenseInfo>
            <updateCopyright>true</updateCopyright>
            <updateProject>true</updateProject>
            <addAuthorsBlock>true</addAuthorsBlock>
            <sourceCodeDirs>
                src/main/java/**/.*.java,
                src/main/resources/**/.*.bsh,
                src/main/resources/**/.*.properties
            </sourceCodeDirs>

        </codeOptions>

    </configuration>

Then run: 

> java -jar bin/CodeLicenseManager-command-line-2.0-exec.jar --config apply.xml --action apply
> --licenselibrary lib/license/CodeLicenseManager-licenses-common-opensource-2.0.jar
> --sourceupdaters lib/updaters/CodeLicenseManager-source-updater-slashstar-comment-2.0.jar,
> lib/updaters/CodeLicenseManager-source-updater-hash-comment-2.0.jar

This is preferably put in a script, which in turn can be called from a makefile. 
The above example assumed you were standing in the distribution root directory. 





# Installing License Files

Installing license files requires the "project" (only "license" part), "thirdpartyLicenses", 
and "installOptions" configuration sections.


## maven

Please remember that the "project" section is optional if the information is specified 
elsewhere in the pom. See the "Configuration/Maven" section above. 

When run with maven CLM will try to automatically resolve third party licenses from their
poms for any license not configured in the plugin (se below). When a license is found that
is not available in a license library the license text will be downloaded from the web
and installed as an html file if a license url specification is available.

If APT generation of licenses have been configured and the license is a downloaded such
it will be generated only if the license url does not end in .html or .htm! If the url
does not have one of these extensions it is assumed to be a text file and can still be
used to generate APT. HTML can not.

if Markdown generation of licenses have been configured and the license is a downloaded
such it will still be generated since Markdown supports HTML mixed with Markdown. The
HTML will however be slightly filtered to improve the result. It will also obviously not
be generated as a code block as is done for text licenses.

Use the CodeLicenseManager-maven-plugin with the "install" goal. Here is an example:

    <plugin>
        <groupId>se.natusoft.tools.codelicmgr</groupId>
        <artifactId>CodeLicenseManager-maven-plugin</artifactId>
        <version>2.0</version>
    
        <dependencies>
            <dependency>
                <groupId>se.natusoft.tools.codelicmgr</groupId>
                <artifactId>CodeLicenseManager-licenses-common-opensource</artifactId>
                <version>2.0</version>
            </dependency>
        </dependencies>
    
        <configuration>
    
            <project>
                <license>
                    <type>Apache</type>
                    <version>2.0</version>
                </license>
            </project>
            <thirdpartyLicenses>
                <license>
                    <type>LGPL</type>
                    <version>v3</version>
                    <licenseProducts>
                        <product>
                            <name>bsh</name>
                            <version>1.3.0</version>
                            <web>http://www.beanshell.org/</web>
                        </product>
                    </licenseProducts>
                </license>
                <license>
                    <type>Apache</type>
                    <version>2.0</version>
                    <licenseProducts>
                        <product>
                            <name>FileEditor</name>
                            <version>2.0</version>
                            <web>http://fileeditor.sf.net/</web>
                        </product>
                        <product>
                            <name>OptionsManager</name>
                            <version>2.0</version>
                            <web>http://optionsmanager.sf.net/</web>
                        </product>
                    </licenseProducts>
                </license>
            </thirdpartyLicenses>
    
        </configuration>
    
        <executions>
            <execution>
                <id>install-lics</id>
                <goals>
                    <goal>install</goal>
                </goals>
                <phase>install</phase>
                <configuration>
    
                    <installOptions>
                        <verbose>true</verbose>
                        <licenseDir>
                            target/license
                        </licenseDir>
                        <thirdpartyLicenseDir>
                            target/license/thirdparty
                        </thirdpartyLicenseDir>
                    </installOptions>

                    <!-- APT generation (se section above about special maven features) -->
                    <createLicencesAPT>
                        true
                    </createLicencesAPT>
                    <createMavenPDFPluginLicenseAPTVersions>
                        true
                    </createMavenPDFPluginLicenseAPTVersions>

                    <!-- Markdown generation (se section above about special maven features) -->
            		<createLicensesMarkdown>true</createLicensesMarkdown>
                    <markdownTargetSubdir>docs</markdownTargetSubdir>
                    <appendUpdateLicensesMarkdownToMarkdownDocument>
        	            MyMarkdownDoc.md
                    </appendUpdateLicensesMarkdownToMarkdownDocument>
                </configuration>
            </execution>
        </executions>
    
    </plugin>


## Command line

Create a configuration file. For example: install.xml: 

    <configuration>

        <project>
            <license>
                <type>Apache</type>
                <version>2.0</version>
            </license>
        </project>

        <thirdpartyLicenses>
            <license>
                <type>LGPL</type>
                <version>v3</version>
                <licenseProducts>
                    <product>
                        <name>bsh</name>
                        <version>1.3.0</version>
                        <web>http://www.beanshell.org/</web>
                    </product>
                </licenseProducts>
            </license>
            <license>
                <type>Apache</type>
                <version>2.0</version>
                <licenseProducts>
                    <product>
                        <name>FileEditor</name>
                        <version>2.0</version>
                        <web>http://fileeditor.sf.net/</web>
                    </product>
                    <product>
                        <name>OptionsManager</name>
                        <version>2.0</version>
                        <web>http://optionsmanager.sf.net/</web>
                    </product>
                </licenseProducts>
            </license>
        </thirdpartyLicenses>

        <installOptions>
            <verbose>true</verbose>
            <licenseDir>
                .../CodeLicenseManager/license
            </licenseDir>
            <thirdpartyLicenseDir>
                .../CodeLicenseManager/license/thirdparty
            </thirdpartyLicenseDir>
        </installOptions>

    </configuration>

Then run: 

> java -jar bin/CodeLicenseManager-command-line-2.0-exec.jar
> --config install.xml --action install 
> --licenselibrary lib/license/CodeLicenseManager-licenses-common-opensource-2.0.jar

This is preferably put in a script, which in turn can be called from a makefile. 
The above example assumed you were standing in the distribution root directory. 





# Running Own Configurable Code Update Scripts

This requires the "codeOptions" (only "sourceCodeDirs") and the "scripts" configuration 
sections. 

You may notice in the examples below that there are 2 different file specifications 
in 2 places (codeOptions/sourceCodeDirs and scripts/script/fileFilter). Both use 
regular expressions to match files and both must be specified. The first 
(codeOptions/sourceCodeDirs) specifies all the files to process. The second 
(scripts/script/fileFilter) limits the first result even more. The reason for this 
is that the scripts section can also be used for the "apply" goal/action and in that 
case you might not want to run the script for all files that have a license boilerplate
applied. The "script" goal/action are basically the same as the "apply" goal/action. 
The only difference is that the "script" goal/action never runs the "apply" parts, but 
only the scripts. Thereby the "script" goal/action also has a subset of the "apply"
configuration. So everything that can be specified for the "script" goal/action can 
also be specified for the "apply" goal/action. 


## maven

Use the CodeLicenseManager-maven-plugin with the "script" goal. Here is an example: 

    <plugin>
    
        <groupId>se.natusoft.tools.codelicmgr</groupId>
        <artifactId>CodeLicenseManager-maven-plugin</artifactId>
        <version>2.0</version>
    
        <dependencies>
        </dependencies>
    
        <configuration>
    
            <scripts>
                <script>
                    <fileFilter>.*CodeLicenseManager.java</fileFilter>
                    <code>
                        editor.moveToTopOfFile();
                        if (editor.find("Display.msg\\(\"CodeLicenseManager ")) {
                            display("Updating version, copyright and maintainer!");
                            editor.deleteCurrentLine();
                            editor.insertLine("        Display.msg(\"CodeLicenseManager " +
                                "${app.version}\\nMaintained by " + 
                                "${developer1Name} (${developer1Email})\");");
                        }
                </code>
                <!-- alternative:
                <scriptFile>scripts/updVersion.bsh</scriptFile>
                -->
                </script>
            </scripts>
    
        </configuration>
        <executions>
            <execution>
                <id>run-scripts</id>
                <goals>
                    <goal>script</goal>
                </goals>
                <phase>generate-sources</phase>
                <configuration>
    
                    <codeOptions>
                        <verbose>true</verbose>
                        <sourceCodeDirs>
                            src/main/java/**/.*.java
                        </sourceCodeDirs>
                    </codeOptions>
    
                </configuration>
            </execution>
        </executions>
    </plugin>
  

## Command line

Create a configuration file. For example scripts.xml: 

    <configuration>

       <codeOptions>
            <verbose>true</verbose>
            <sourceCodeDirs>
                src/main/java/**/.*.java
            </sourceCodeDirs>
        </codeOptions>

        <scripts>
            <script>
                <fileFilter>.*CodeLicenseManager.java</fileFilter>
                <code>
                    editor.moveToTopOfFile();
                    if (editor.find("Display.msg\\(\"CodeLicenseManager ")) {
                        display("Updating version, copyright and maintainer!");
                        editor.deleteCurrentLine();
                        editor.insertLine("        Display.msg(\"CodeLicenseManager " +
                            "${app.version}\\nMaintained by " + 
                            "${developer1Name} (${developer1Email})\");");
                    }
               </code>
               <!-- alternative:
               <scriptFile>scripts/updVersion.bsh</scriptFile>
               -->
            </script>
        </scripts>

    </configuration>

Then run: 

> java -jar bin/CodeLicenseManager-command-line-2.0-exec.jar
> --config script.xml --action script 

# Deleting license / project information from source

__Note__ that this function is not well tested, and not entirely bug free either!

This requires the "codeOptions" (only "sourceCodeDirs") configuration section. 

This will do the opposite of "apply". Why is this available ? Each source updater 
works only with itself. That is it recognizes only information added by itself. 
You cannot use one source updater to apply information to your code and then change 
to another source updater and expect it to update the old information with the 
new. In that case you will get both. So if you for some reason want to switch 
to another source updater (for example, you have decided to make your own updater) 
then you must run the "delete" goal/action using the same updater that created 
the information in the first place. Then you can switch to another updater and 
apply again. 

Also note that it is entirely optional for updaters to support delete! Currently 
only "java-annotation" and "groovy-annotation" does! 


## maven

Use the CodeLicenseManager-maven-plugin with the "delete" goal. Here is an example: 

    <plugin>
    
        <groupId>se.natusoft.tools.codelicmgr</groupId>
        <artifactId>CodeLicenseManager-maven-plugin</artifactId>
        <version>2.0</version>
    
        <dependencies>
            <dependency>
                <groupId>se.natusoft.tools.codelicmgr</groupId>
                <artifactId>CodeLicenseManager-source-updater-java-annotation</artifactId>
                <version>2.0</version>
            </dependency>
        </dependencies>
    
        <configuration>
    
        </configuration>
        <executions>
            <execution>
                <id>delete</id>
                <goals>
                    <goal>delete</goal>
                </goals>
                <phase>generate-sources</phase>
                <configuration>
    
                    <codeOptions>
                        <verbose>true</verbose>
                        <sourceCodeDirs>
                            src/main/java/**/.*.java
                        </sourceCodeDirs>
                    </codeOptions>
    
                </configuration>
            </execution>
        </executions>
    </plugin>


## Command line

Create a configuration file. For example delete.xml: 

    <configuration>

        <codeOptions>
            <verbose>true</verbose>
            <sourceCodeDirs>
                src/main/java/**/.*.java
            </sourceCodeDirs>
        </codeOptions>

    </configuration>

Then run: 

> java -jar bin/CodeLicenseManager-command-line-2.0-exec.jar
> --config delete.xml --action delete 





# Available Libraries

## License

A license library contains both a full license text and a boilerplate text for 
source files required by the license. All opensource licenses are available on 
the web and it is possible for a license library to point to a license text on 
the web instead of a local copy, but the license boilerplate text for source files 
are difficult to extract from the license info on the web so that must be available 
as a local in library text file. All the licenses in the supplied license library 
also have the full license text locally in the library. 

__CodeLicenseManager-licenses-common-opensource__

This contains the most common open source licenses: 

* Apache Public License (APL) 2.0

* Common Development and Distribution License (CDDL) 1.0

* Eclipse Public License (EPL) 1.0

* GNU General Public License (GPL) v2

* GNU General Public License (GPL) v3

* GNU Affero General Public License v3

* GNU Lesser General Public License (LGPL) v2.1

* GNU Lesser General Public License (LGPL) v3

* MIT License (MIT) 1.0

* Mozilla Public License (MPL) 1.1


The information for these are taken from [www.opensource.org](http://www.opensource.org/). 
  

## Source Updaters

A source updater is a set of beanshell [www.beanshell.org](http://www.beanshell.org/)
scripts that uses preloaded [FileEditor](http://fileeditor.sf.net)
instance to update source files with license information and boilerplate text, 
and optionally also project information and author information. There are different 
updaters for different kinds of comment types. 

__CodeLicenseManager-source-updater-slashstar-comment__ 

This updates source code with all license and other information within a 

	/*
	 * ...
	 */

comment block. If the source file does not start with such a comment one is added 
otherwise the information is inserted within the existing comment, but first in 
the comment block. 

This updater recognizes the following source code extensions: 

* bsh 

* c 

* cpp 

* cs 

* css 

* groovy 

* java 

If you know of other valid source code extensions using this comment format please 
inform me about it and I will update this. See contact information at the bottom 
of this document. I'm quite sure there are things I've missed. 

__CodeLicenseManager-source-updater-hash-comment__

This updates source code with all license and other information within a 

	#
	# ...	
	#

comment block. If the source file does not start with such a comment one is added 
otherwise the information is inserted within the existing comment, but first in 
the comment block. 

This updater recognizes the following source code extensions: 

* awk 

* perl 

* properties 

* py 

* ruby 

* sh 

If you know of other valid source code extensions using this comment format please 
inform me about it and I will update this. See contact information at the bottom 
of this document. 

  

__CodeLicenseManager-source-updater-html-xml__

This updates source code with all license and other information within a 

    <!--
        ...
    -->
    

comment block. If the source file does not start with such a comment one is added 
otherwise the information is inserted within the existing comment, but first in 
the comment block. 

This updater recognizes the following source code extensions: 

* ent 

* htm 

* html 

* xhtml 

* xml 

* xsd 

* xsl

* dtd

_Please note_ that for html and xml files and others requiring a tag like

    <?xml version="1.0" encoding="UTF-8"?>

as the first line, start your file like this:

    <?xml version="1.0" encoding="UTF-8"?>
    <!--
    -->
    ...

before running this updater or it will insert the whole comment block before the `<?xml ...` tag!
This is  not as easy as it might sound to fix since this is not a bug in itself but a side-effect
of a more complex bug.

If you know of other valid source code extensions using this comment format please 
inform me about it and I will update this. See contact information at the bottom 
of this document. 

__CodeLicenseManager-source-updater-jsp__

This updates jsp pages with all license and other information within a 

    <%--
        ...
    --%>

comment block. If the jsp file does not start with such a comment one is added 
otherwise the information is inserted within the existing comment, but first in 
the comment block. 





# Making Your Own ...

## License Library

The "common-opensource" license library only contains reusable open source licenses. 
If you think it is missing some license that belongs there, please inform me. 
CLM is however not only for open source. You can make a license library with closed 
source company license(s). Some open source licenses are not reusable by others 
and can only be used by a certain organization. For such licenses a organization 
private license library needs to be created to use with CLM. Creating a license 
library is however quite easy. 

Create the following structure: 

	codelicmgr/
    	licenses/

In the "licenses" directory create a java property file having the name 
_licensename_-_licenseversion_.properties. 
  
Here is an example of Apache-2.0.properties: 

    type=Apache
    version=2.0
    description=Apache Software License
    source=open
    sourceblock=${type}-${version}-SourceBlock.txt
    fulltext=${type}-${version}-License.txt

It is more or less self explanatory. The "source" property can only have "open" 
or "closed" as value. The "sourceblock" and "fulltext" properties points to files 
containing the source block (usually called license boilerplate text) to put in 
source files, and the full license text. The "fulltext" property can also be an 
http url in which case no local fulltext needs to be available. Also note the 
"$\{type\}" and "$\{version\}" variables. They can only be used for the "sourceblock" 
and "fulltext" properties. The specified path to the text files are relative to 
the properties file. It is possible to put the texts in a subdirectory. 

Do not indent the text in the file pointed to by "sourceblock"! CLM will handle 
any indents when the text is applied to source code. 

Some licenses might be referenced with different names. For example the Apache 
license again is known as "Apache", "ASL", "Apache Software License". When maven 
is used and thirdparty licenses are automatically resolved the license reference 
in a pom might use any of the known names for a license. To support different 
names for a license, copy/duplicate the property file to cover the different names. 
For multi word names the spaces can be removed in the property file name. CLM 
will try to remove spaces from multi word license specifications and lookup the 
license again if it fails the first time. 

When the property/properties file(s) and the referenced text files are in place, 
jar the codelicmgr directory. To use the license library the jar should be made 
available on the classpath or specified with --licenselibrary for the command 
line version. 


## Source Updater

Why would you want to make an own source code updater ? Well, you might work with 
some source files having a comment variant not supported by default by CLM. Or 
you might think that the output of the supplied CLM updaters really suck. You 
might want to follow an organizational standard layout. Making an own source updater 
gives you more personalization than is possible with the execution options. Please 
note however that a source updater is not just configuration, it contains script 
java code (through beanshell) and if you implement errors/bugs CLM might fail 
when calling it. That is why an updater supplies maintainer and support information 
that is displayed on execution to make a clear line between CLM and the updater. 
I don't want to get bug reports on private updaters :-). 

The simplest way to get started on making an own source updater is to copy an 
existing and modify it. But to create one from scratch do the following: 

Create the following structure: 

       codelicmgr/
        sourcecodeupdaters/

In the "sourcecodeupdaters" directory you place a property file having the name 
of a source code extension (for example "java") and extension "properties". Example 
java.properties: 

    # The full name of the langugae.
    lang.fullname=Java
    
    # The property file relative directory of the scripts for updating 
    # source code of this language.
    lang.scriptdir=scripts
    
    # the properties file to read for general updater information.
    updater.properties=updater

The last property "updater.properties" points to another property file containing 
general information relevant for all \<lang extension\>.properties files. It can 
have any name, but I have used updater.properties: 

    # A description of the updater.
    updater.description=CodeLicenseManagers /* ... */ comment block multilanguage 
    source code updater.
    
    # A copyright message (optional).
    updater.copyright=
    
    # The name and email of the maintainer.
    updater.maintainer=Maintained by Tommy Svensson (tommy@natusoft.se)
    
    # Where to turn for support.
    updater.support=For support goto http://sourceforge.net/projects/codelicmgr/support 
    or contact maintainer.

If the "updater.copyright" property is specified the copyright message will be 
displayed on updater execution. 

As said above this information is to make it clear who is responsible for the 
specific updater. 

Copy/duplicate the _lang___extension_.properties file to any valid extensions 
for the updater. 

When CLM sees a "whatever.java" file and want to update it, it looks for a 
codelicmgr/sourcecodeupdaters/java.properties 
file in the classpath and as per normal classpath resource lookup will use the 
first found. So if you have several source updater libraries in the classpath 
supporting the same language the order they are provided are important. CLM itself 
for example uses the "annotation" updater for java but the "slashstar" updater 
for bsh scripts. The "slashstar" updater also supports java so the "annotation" 
updater is placed before the "slashstar" updater in the classpath. 

The "lang.scriptdir" property points to a subdirectory containing the standard 
scripts for an updater. Always use a subdirectory for the scripts! Never specify 
"lang.scriptdir=.". The "lang.scriptdir" directory contains the following scripts: 

__init.bsh__ 

This script is only run once when an updater is loaded. It should setup things 
needed by the other scripts, like imports and support functions. Here is an example: 

    import java.util.*;
    import java.text.*;
    
    
    boolean findInsertPosition(searchStrings) {
        String endOfSection = "findEndOfBlock();";
        String endOfCommentSearch = "^[^ ^\\*].*|^ \\*/.*|^[a-zA-Z &#\\*][a-zA-Z &#][a-zA-Z &#].*";
        return findInsertPosition(endOfSection, endOfCommentSearch, "/* ", " * ", " */", searchStrings);
    }
    
    /**
     * Support function for finding the end of a specific comment block.
     */
    void findEndOfBlock() {
        boolean done = false;
        while (!done) {
            editor.moveDown(1);
            String line = editor.getLine();
            if (line.trim().equals("*/") || 
                (line.startsWith(" * ") && line.length() >= 4 && line.charAt(3) != ' ') ||
                editor.isOnLastLine() || 
                line.matches("^[a-zA-Z &#][a-zA-Z &#][a-zA-Z &#].*")) {
            done = true;          
            }        
        }
        editor.moveUp(1);
        editor.moveToEndOfLine();
    }

  

__updateAuthorsBlock.bsh__

This will only be run if "updateAuthorsBlock" is set to true in the "codeOptions". 
It will add author information if and only if it is not already available. Once 
added this is never modified. So this only provides initial information after 
it is added it need to be manually maintained. This sections also list changes 
done by each author. When this block is added information about the user is read 
from .clm-user.properties in the users home directory. See the "User specific 
configuration" section above for more information. The user and email is provided 
to the script by CLM. Here is an example: 

    if (!editor.find(" \\* AUTHORS")) {
    
        findInsertPosition(
        "find=first;insert=after;section=true;search='^ \\* LICENSE *'," +
        "find=first;insert=after;section=true;search='^ \\* COPYRIGHTS *'," +
        "find=first;insert=after;section=true;search='^ \\* PROJECT *'," +
        "find=first;insert=before;section=false;search='^package.*'," +
        "find=first;insert=before;section=false;search='^/\\*.*'"
        );
    
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        display("Adding AUTHORS");
        editor.insertLine(" * AUTHORS");
        editor.insertLine(" *     " + userName + " (" + userEmail + ")");
        editor.insertLine(" *         Changes:");
        editor.insertLine(" *         " + sdf.format(new Date()) + ": Created!");
        editor.insertLine(" *         ");
    }

__updateCopyrightInfo.bsh__

This will only run if "updateCopyrights" is set to true in the "codeOptions". 
It will add copyright information to a source file if it is not already available 
or update the existing copyright information (easiest accomplished by removing 
old and inserting new). 

This differs from other scripts since there can be more than one copyright and 
looping through the copyrights are handled by CLM, so the "updatecopyrightInfo.bsh" 
script must define a set of functions explained by the following example: 


    /**
     *   setup
     *       Does whatever setup is required, like finding and removing an
     *       old copyright block.
     */
    void setup(TextFileEditor editor) {
    if (editor.find(" \\* COPYRIGHTS")) {
        display("Updating COPYRIGHTS");
        editor.startSelection();
        findEndOfBlock(); // Defined in init.bsh!
        editor.endSelection();
        editor.deleteSelection();
        editor.moveUp(1);
    }
    else {
        display("Adding COPYRIGHTS");
    
        findInsertPosition(
            "find=first;insert=after;section=true;search='^ \\* PROJECT'," +
            "find=first;insert=before;section=true;search='^ \\* LICENSE'," +
            "find=first;insert=before;section=true;search='^ \\* AUTHOR'," +
            "find=first;insert=before;section=false;search='^package.*'," +
            "find=first;insert=before;section=false;search='^/\\*.*'"
        );
    
    }
    editor.insertLine(" * COPYRIGHTS");
    }
    
    
    /**
     *   startCopyrights
     *       This will only be called if there are more than one copyright.
     */
    void startCopyrights(TextFileEditor editor) {
    }
    
    /**
     *   forEachCopyright
     *       This will be called once for each copyright specification. last
     *       will be true for the last entry.
     */
    void forEachCopyright(TextFileEditor editor, String year, String holder,
        String rights, boolean last) {
        editor.insertLine(" *     Copyright (C) " + year + " by " + holder + " " + rights);
    }
    
    /**
     *   endCopyrights
     *       This will only be called if there are more than one copyright.
     */
    void endCopyrights(TextFileEditor editor) {
    }
    
    /**
     *   finish
     *       Adds anything that needs to be added efter each copyright is processed.
     */
    void finish(TextFileEditor editor) {
        editor.insertLine(" *     ");
    }

__updateImports.bsh__

This is always run, but for most updaters it will be empty. It should add any 
required imports. The 2 updaters using annotations use this to add imports for 
the annotations. 

__updateLicenseInfo.bsh__

This is only run if "updateLicenseInfo" is set to true in "codeOptions". It adds 
or updates license information in the source file. Here is an example: 

    TextBuffer getLicenseTextAsTextBuffer() {
        TextBuffer buffer = new TextFileBuffer();
        buffer.addLine(" * LICENSE");
        buffer.addLine(" *     " + licenseType + " " + licenseVersion + " (" + source + " Source)");
        buffer.addLine(" *     ");
    
        formatMultiLineTextToCode(buffer, sourceBlock, " * ", "", "", "", 4, false);
    
        buffer.addLine(" *     ");
    
        return buffer;
    }
    
    if (editor.find(" \\* LICENSE")) {
        display("Updating LICENSE!");
        editor.startSelection();
        findEndOfBlock(); // Defined in init.bsh!
        editor.endSelection();
        editor.replaceSelectionWithTextBuffer((TextBuffer)getLicenseTextAsTextBuffer());
    }
    else {
        display("Adding LICENSE!");
        editor.moveToTopOfFile();
        
        if (!findInsertPosition(
        "find=first;insert=after;section=true;search='^ \\* COPYRIGHTS *'," +
        "find=first;insert=after;section=true;search='^ \\* PROJECT *'," +
        "find=first;insert=before;section=true;search='^ \\* AUTHOR *'," +
        "find=first;insert=before;section=false;search='^package.*'," +
        "find=first;insert=before;section=false;search='^/\\*.*'"
        )) {
            editor.insertLineAbove(" * ");
        }
    
        editor.insertBuffer(getLicenseTextAsTextBuffer());
    }

__updateProjectInfo.bsh__

This is only run if "updateProjectInfo" is set to true in "codeOptions". It adds 
or updates project information in the source file. Here is an example: 

    if (editor.find("^ \\* PROJECT")) {
        display("Updating PROJECT");
        editor.startSelection();
        findEndOfBlock();
        editor.endSelection();
        editor.deleteSelection();
        editor.moveUp(1);
    }
    else {
        display("Adding PROJECT");
    
        findInsertPosition(
        "find=first;insert=before;search='^ \\* COPYRIGHTS *'," +
        "find=first;insert=before;search='^ \\* LICENSE *'," +
        "find=first;insert=before;search='^ \\* AUTHOR *'," +
        "find=first;insert=before;section=false;search='^package.*'," +
        "find=first;insert=before;section=false;search='^/\\*.*'"
        );
    
    }
    
    editor.insertLine(" * PROJECT");
    
    editor.insertLine(" *     Name");
    editor.insertLine(" *         " + projectName);
    editor.insertLine(" *     ");
    
    if (hasProjectSubProjectOf) {
        editor.insertLine(" *     Subproject Of");
        editor.insertLine(" *         " + projectSubProjectOf);
        editor.insertLine(" *     ");
    }
    
    if (hasProjectCodeVersion) {
        editor.insertLine(" *     Code Version");
        editor.insertLine(" *         " + projectCodeVersion);
        editor.insertLine(" *     ");
    }
    
    if (hasProjectDescription) {
        editor.insertLine(" *     Description");
        TextBuffer buffer = new TextFileBuffer();
        formatMultiLineTextToCode(buffer, projectDescription, " * ","", "", "", 8, true);
        editor.insertBuffer(buffer);
        editor.insertLine(" *         ");
    }

__userBefore.bsh / userAfter.bsh__

These are optional and are only executed if they are available. userBefore.bsh 
will execute after init.bsh but before any other scripts. userAfter.bsh will execute 
after all other scripts. 

These are not very useful as part of an updater. These are intended for extending 
an existing updater. Place the script(s) in the same path as the updater you want 
to extend, and place it before the extended updater in the classpath. When the 
command line version is used the order of updaters specified with --sourceupdaters 
is the same order they are added to the classpath. 

---

When all properties and scripts have been provided, jar from the "codelicmgr" 
directory. 





# Standard functions and objects available to scripts

The following objects are available to this script: 

__editor : TextFileEdtior__

A TextFileEditor instance with the current source code file loaded. You are not 
allowed to do load(file) or save() on it! If you feel the need to do that then 
you are probably doing something strange and should reconsider what you are doing 
:-). All other methods on this object can be called. 

The TextFileEditor is part of the FileEditor tool available at <http://github.com/tombensve/FileEditor>.

__projectName : String__

The name of the project. 

__hasProjectDescripion : boolean__

True if a project description has been provided. 

__projectDescription : String__

The projectDescription if 'hasProjectDescription' is true. 

__hasProjectCodeVersion : boolean__

True if a project code version has been provided. 

__projectCodeVersion : String__

The version of the project code if 'hasProjectCodeVersion' is true. 

__hasProjectSubProjectOf : boolean__

True if the 'subProjectOf' config has been provided. 

__projectSubProjectOf : String__

The name of the parent project the project is a subproject of if 'hasSubProjectOf' 
is true. 

__licenseType : String__

The type of the license. Example: "Apache" or "GPL". 

__licenseVersion : String__

The version of the license. Example "2.0" or "v3". 

__licenseDescription : String__ 

A short description of the license, usually just the full name of the license. 
Provided by license library. 

__source : String__

"Open" or "Closed" depending on license type. 

__sourceBlock : String__ 

The license notice to put at the top of the source file. Sometimes called the 
license boilerplate. Use the formatMultiLineTextToCode(...) function with this. 

--

The following functions are available: 

__display(String str)__

_str_ - The string to display. This will only be displayed if verbose is turned 
on! 

__formatMultiLineTextToCode(TextBuffer buffer, String text, String lineEnd, final 
String stringSpec, final String escapedStringSpec, int indent, boolean trim)__ 

_buffer_ - The buffer to put formatted result in. 

_text_ - The text to format. 

_lineEnd_ - The string that terminates each line. For example " +" or ",". 

_stringSpec_ - A character or set of characters that indicates start and end of 
a String. For example "\"" or "'". 

_escapedStringSpec_ - A character or set of characters that indicates an escaped 
start and end of a string. For example "\\\"" or "\\'" 

_indent_ - The number of spaces to indent each line. 

_trim_ - If true each line is also trimmed. 

__formatMultiLineTextToCode(TextBuffer buffer, String text, String lineBeg, String 
lineEnd, final String stringSpec, final String escapedStringSpec, int indent, 
boolean trim)__ 

_buffer_ - The buffer to put formatted result in. 

_text_ - The text to format. 

_lineBeg_ - The beginning of each line. The indent will be added after this. 

_lineEnd_ - The string that terminates each line. For example " +" or ",". 

_stringSpec_ - A character or set of characters that indicates start and end of 
a String. For eample "\"" or "'". 

_escapedStringSpec_ - A character or set of characters that indicates an escaped 
start and end of a string. For example "\\\"" or "\\'" 

_indent_ - The number of spaces to indent each line. 

_trim_ - If true each line is also trimmed. 

The following functions makes use of the current editor instance, but they receive 
it automatically, it does not have to be specified!! 

__moveToEndOfSection(char starting, char ending)__ 

Use this when you can specify the end of a section (project, copyright, license, 
author) with a starting character that end with a matching ending character at 
the same level. For example '(' and ')'. 

__moveToEndOfSection(String startsWith, noSpaceAt)__ 

Use this when each section starts with "comment-char + space + section-heading" 
and all text in the section are indented with at least one more space than the 
heading. This actually finds the next section or end of comment to determine the 
end of a section. 

_startsWith_ - The line indicating a new section must start with this. 

_noSpaceAt_ - The line indicating a new section must not have a space at this 
position. 

__findInsertPosition(String endOfSection, String endOfCommentSearch, String commentStart, 
String commentMiddle, String commentEnd, String searchStrings)__ 

This should be used if a specific section is not already available in the file 
to find an optimal position to insert the section in. 

Please note that this function actually returns a boolean and will be false if 
none of the specified matches came true. This information is mostly useful in 
updateLicenseInfo.bsh which is the first section update script run and might require 
an editor.insertLineAbove("# "); (or whatever comment character is relevant) if 
this returns false. 

_endOfSection_ - This is a string with a call to any function that will move the 
end of a section. eval() will be done on this string when it is needed. Specify 
one of the above 2 functions or provide your own in init.bsh. 

_endOfCommentSearch_ - This is only needed if you specify the "belowComment" 
attribute in searchStrings (see below). If that attribute is specified and this 
value is not null then 'commentStart' and 'commentEnd' will be used to identify 
a top of file comment, usually a class javadoc comment for java. This value specifies 
a regular expression that gets passed to a special version of editor.find(...) 
and that will stop the search if the current line matches this regular expression. 
This is to avoid finding the wrong comment if no comment is available at the top 
of the file (i.e class comment). This used by the java and groovy source updaters 
that uses annotations instead of comments. It is also used when all searchString:s 
fail to identify a comment to position in. If that also fails a new comment will 
be created, but only if all 3 comment sections are non null and non blank. 

_commentStart_ - A string that indicates the beginning of a comment block. If 
it is a comment type that has no start or end just specify the same value for 
all 3 comment specifications. The "belowComment" attribute of searchStrings will 
also make us of this, and in this case it can be a regular expression since it 
will never be used to create a comment in that case. In all other cases it cannot 
be a regular expression! 

_commentMiddle_ - A string that indicates the middle of a comment. 

_commentEnd_ - A string that indicates the end of a comment. The "belowComment" 
attribute of searchStrings will also make us of this, and in this case it can 
be a regular expression (see start comment). 

_searchStrings_ - This specifies a set of comma separated search expressions with 
semicolon separated attributes used to find a position. The first that matches 
will stop the search and the function will return with the editor at that position, 
ready for insert. It has the following format: attribute=value;...;attribute=value, 
... ,attribute=value;...;attribute=value 

The following attributes are available: 

_find_ - first (default) or last. If first the first match found will be used. 
If last the last match found will be used. 

_insert_ - before or after (default). If before the insert position will be before 
the matched line. If after the insert position will be after the matched line. 

_section_ - true or false (default). If true the specified match is the start 
of a section and the endOfSection value will be used if insert==after. 

_belowComment_ - true or false (default). If true then "commentStart","commentEnd", 
and "endOfCommentSearch" will be used to move the insert position down below a 
possible comment. 

_newLine_ - true or false (default). If true a new empty line will be inserted 
at the insert position leaving an empty line between the match and the new insert 
position. 

_search_ - This is a regular expression search string surrounded by ''. Example: 
'^# LICENSE'. This attribute must always be specified.

