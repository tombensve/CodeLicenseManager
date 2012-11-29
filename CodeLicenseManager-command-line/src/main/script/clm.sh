#!/bin/sh
#
# This scripts runs the CodeLicenseManger command line tool.
#

java -jar `dirname $0`/CodeLicenseManager-command-line-*-exec.jar "$@"
