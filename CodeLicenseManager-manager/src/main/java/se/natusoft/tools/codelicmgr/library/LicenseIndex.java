package se.natusoft.tools.codelicmgr.library;

import codelicmgr.licenses.LicenseResourceProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Manages the index of all licenses.
 */
public class LicenseIndex {

    public static final LicenseIndex index = new LicenseIndex();

    private class IndexEntry {
        //
        // Private Members
        //

        private Map<String, String> names = new HashMap<>();
        private Map<String, String> versions = new HashMap<>();
        private String libraryResource;

        //
        // Constructors
        //

        /**
         * Creates a new IndexEntry instance.
         *
         * @param line A line from the licenses.index file.
         */
        public IndexEntry(String line) {
            String[] parts = line.split("\\|");

            String[] _names = parts[0].split(",");
            for (String name : _names) {
                this.names.put(name, name);
            }

            String[] _versions = parts[1].split(",");
            for (String version : _versions) {
                this.versions.put(version, version);
            }

            this.libraryResource = parts[2];
        }

        //
        // Methods
        //

        /**
         * Returns true if this entry contains the specified name.
         *
         * @param name The name to check for.
         */
        public boolean containsName(String name) {
            return this.names.containsKey(name);
        }

        /**
         * Returns true if this entry contains the specified version.
         *
         * @param version The version to check for.
         */
        public boolean containsVersion(String version) {
            return this.versions.containsKey(version);
        }
    }

    //
    // Private Members
    //

    /** Holds the loaded index entries. */
    private List<IndexEntry> indexEntries = new LinkedList<>();

    //
    // Constructor
    //

    /**
     * Creates a new LicenseIndex instance.
     */
    public LicenseIndex() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(LicenseResourceProvider.getInputStream("licenses.index")));
            String line = br.readLine();
            while (line != null) {
                this.indexEntries.add(new IndexEntry(line));
                line = br.readLine();
            }
            br.close();
        }
        catch (IOException ioe) {
            ioe.printStackTrace(System.err);
        }
    }

    //
    // Methods
    //

    /**
     * Resolves a library reference from a license string. If non null the result can be passed to LicenseResourceProvider.
     *
     * @param licenseRef The license reference to search with.
     */
    public String resolveLibraryResource(String licenseRef) {
        String[] searchWords = licenseRef.replace('-',' ').split("\\s+");
        for (IndexEntry entry : indexEntries) {
            for (String searchWord : searchWords) {
                if (entry.containsName(searchWord.toLowerCase())) {
                    for (String searchWord2 : searchWords) {
                        if (entry.containsVersion(searchWord2)) {
                            return entry.libraryResource;
                        }
                    }
                }
            }
        }

        return null;
    }

}
