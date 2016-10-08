# Code License Manager

Copyright © 2013 Natusoft AB

__Version:__ 2.2.0

__Author:__ Tommy Svensson (tommy@natusoft.se)

---

_A tool for managing code license with code boilerplate update and license + third party licenses installation at build._

User Guide: [Markdown](https://github.com/tombensve/CodeLicenseManager/blob/master/CodeLicenseManager-documentation/docs/UserGuide.md) /  [PDF](https://github.com/tombensve/CodeLicenseManager/blob/master/CodeLicenseManager-documentation/docs/CLM-User-Guide.pdf)

[Licenses](https://github.com/tombensve/CodeLicenseManager/blob/master/CodeLicenseManager-documentation/docs/licenses.md)

## Version history

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
