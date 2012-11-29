/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package se.natusoft.tools.codelicmgr;

import junit.framework.TestCase;

import java.io.File;
import java.util.List;

/**
 *
 * @author tommy
 */
public class SourcePathTest extends TestCase {
    
    public SourcePathTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of getSourceFiles method, of class SourcePath.
     */
    public void testGetSourceFiles() {
        System.out.println("getSourceFiles");
        SourcePath sp = new SourcePath(new File("."), "src/main/java/se/natusoft/tools/**/.*LicenseManager.java");
        List<File> files = sp.getSourceFiles();
        assert(files.size() == 1);
        assert(files.get(0).getName().endsWith("CodeLicenseManager.java"));

        sp = new SourcePath(new File("."), "src/main/java/se/natusoft/tools/codelicmgr/.*LicenseManager.java");
        files = sp.getSourceFiles();
        assert(files.size() == 1);
        assert(files.get(0).getName().endsWith("CodeLicenseManager.java"));

    }

    /**
     * Test of toString method, of class SourcePath.
     */
    public void testToString() {
        System.out.println("toString");
        SourcePath sp = new SourcePath(new File("/my/path"), true, ".*.groovy");
        assert(sp.toString().equals("/my/path/**/.*.groovy"));

        sp = new SourcePath(new File("/my/path"), false, ".*.groovy");
        assert(sp.toString().equals("/my/path/.*.groovy"));

        sp = new SourcePath(new File("/my/path"), false, null);
        assert(sp.toString().equals("/my/path"));
    }

}
