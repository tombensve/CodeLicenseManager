package se.natusoft.tools.codelicmgr.library;


import org.junit.Test;

import static org.junit.Assert.*;

public class LibraryTest {

    @Test
    public void testLibraryLookup() throws Exception {
        LibraryLicense lic = LicenseLibrary.getLicense("Apache 2.0", "http://www.apache.org/");
        assertNotNull(lic);

    }
}
