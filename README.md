# Code License Manager

----

**.: THIS PROJECT IS NO LONGER MAINTAINED! :.**

It is quite old and a lot of stuff here is no longer relevant! 

Putting a licence text in the top of each source file is something that
an IDE should do, but I don't think IDEA does this. 

I will from now on ignore that requirement (maybe changed ?), and only
supply the license text. 

----

Copyright © 2013 Natusoft AB

__Version:__ 2.2.6

__Author:__ Tommy Svensson (tommy@natusoft.se)

    State: OLD AND DEPRECATED! (DOES NOT BUILD) 
    Don't expect me to do anything about this anytime soon, if ever!

----

A tool for managing code license with code boilerplate update and license + third party licenses installation at build. This actually looks at both your code and dependencies and collects the licenses found and the dependencies using the license and can generate a report.

I use this in my other tools on GitHub. See <https://github.com/tombensve/MarkdownDoc/tree/master/Docs/lics> for example. The licenses.md is generated by this tool. It is also included in the generted README.md for the project. 

For finding third party licenses it does require maven. Unfortunately far from all provide clear license info ...

This tool also contain a maven plugin for updateing source code with license header. 

User Guide: [Markdown](https://github.com/tombensve/CodeLicenseManager/blob/master/CodeLicenseManager-documentation/docs/UserGuide.md) /  [PDF](https://github.com/tombensve/CodeLicenseManager/blob/master/CodeLicenseManager-documentation/docs/CLM-User-Guide.pdf)

[Licenses](https://github.com/tombensve/CodeLicenseManager/blob/master/CodeLicenseManager-documentation/docs/licenses.md)

# Binaries

[Accessing binaries](https://github.com/tombensve/CommonStuff/blob/master/docs/AccessingBinaries.md)

## Version history

__Version 2.2.6__

Very minor update that that now only adds "(email-address)" after name if an email address have been 
provided. So no more `"firstName lastName ()"`! That just irritated me so much that I had to fix it even
though I'm busy with another project! Maybe that is also why I a sloppy job at first (2.2.5), but in the
future I want to redo this completely! The base idea is good, the implementation can be improved! This
was done 10 years ago!!

__Version 2.2.5__

This version was skipped due to a dumb mistake that made me start over with 2.2.6.
This version should not be in the repo, and if it accidentally is, don't use it!

__Version 2.2.4__

Bumped Ant version to 1.10.12 due to GitHub security warning.

__Version 2.2.2__

Removed generation of current version in source header comments. Including the version number was
a rather stupid thing to do. It caused all source files to be updated as soon as you changed version
in pom.xml.

__Version 2.2.1__

Extended the index introduced in version 2.2.0 with more name variants for EPL.
 
Fixed NPE when looking up licenses in library. If absolutely nothing is found it will now try a download instead.

__Version 2.2.0__

Added a licenses.index to the license library that looks something like this:

    agpl,gnuafferogeneralpubliclicense,gnuagpl|v3,3,3.0|GNUAGPL-v3
    apache,apache-license,apache-software-license,apache software license,asl|2,2.0|Apache-2.0
    cddl|1.0,1|CDDL-1.0
    epl|1.0,1|EPL-1.0
    gpl|v2,2,2.0|GPL-v2
    gpl|v3m3m3.0|GPL-v3
    lgpl,lessergnupubliclicense|2.1|LGPL-2.1
    lgpl,lessergnupubliclicense|v3|LGPL-v3
    mit|1.0,1|MIT-1.0
    mozilla|1.1|Mozilla-1.1
    osgi|2.0|OSGi-2.0
    osgisl,osgispecificationlicense|2.0|OSGiSL-2.0

This is now used to match up license specifications in maven poms. Each word in the pom license string is matched up against the first part, and when a match is found each word is matched against the second part which is the version, and when both have been found then the third part is uses as the license to lookup in the library. This should decrease the problem of 3 to 4 different spellings of a license is interpreted as different licenses, and some is found in the library others are downloaded. Now most of the variants should be matched against the library. 

__Version 2.1.6__

Only a fix in maven plugin that returns correct error messages about missing tags in pom rather than throwing a NullPointerException.

__Version 2.1.5__

For some strange reason all properties files in CodeLicenseManager-licenses-common-opensource lost their properties in version 2.1.4. This meant that no licenses could be found. On top of that I managed to release this version without running itself on it or anything else either. The 2.1.4 version were still using version 2.1.2. of itself! Sorry, I really fucked this up! This version have now been tested on itself.

There is still a bug in this version: Applying license info on a properties file seems to delete all properties in the file leaving only the license header comment. It is far from clear why this happens. It is probably the 'CodeLicenseManager-source-updater-hash-comment' that is the problem here, so be careful with that one for now.

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

---

[Maven repo setup](https://github.com/tombensve/CommonStuff/blob/master/docs/MavenRepository.md)
