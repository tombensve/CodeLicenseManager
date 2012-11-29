#!/bin/sh
#
# Test the install function of CodeLicenseManager command line tool.
#
version="1.0"

jar xf ../../../../CodeLicenseManager-dist/target/CodeLicenseManager-dist-${version}-installation.zip

java -jar CodeLicenseManager-dist-${version}/bin/CodeLicenseManager-command-line-${version}-exec.jar --config config.xml --licenselibrary CodeLicenseManager-dist-${version}/lib/license/CodeLicenseManager-licenses-common-opensource-${version}.jar  --sourceupdaters CodeLicenseManager-dist-${version}/lib/updaters/CodeLicenseManager-source-updater-slashstar-comment-${version}.jar --action install
