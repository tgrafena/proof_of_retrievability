package at.aau.syssec.por;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Helper class to read and write key-value-pairs from a given file.
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class KeyValueFile {

    public static String PAIR_SEPARATOR = " = ";

    private String path;
    private HashMap<String, String> pairs;

    /**
     * Construct a file for key-value-pairs, create the folders and the file
     * if not present and read key-value-pairs of an existing file.
     *
     * @param path - path to the file
     * @throws IOException
     */
    public KeyValueFile(String path) throws IOException {
        this.path = path;
        pairs = new HashMap<String, String>();

        File file = new File(path);
        file.getParentFile().mkdirs();
        file.createNewFile();

        Scanner in = new Scanner(file);

        while (in.hasNext()) {
            String[] parts = in.nextLine().split(PAIR_SEPARATOR);
            pairs.put(parts[0], parts[1]);
        }

        in.close();
    }

    /**
     * return file path
     *
     * @return path - path of the file
     */
    public String getPath() {
        return path;
    }

    /**
     * get a HashMap of the key-value-pairs
     *
     * @return key-value-pairs
     */
    public HashMap<String, String> getPairs() {
        return pairs;
    }

    /**
     * get a simple {@literal String} value
     *
     * @param key key
     * @return value - String
     */
    public String getValue(String key) {
        return pairs.get(key);
    }

    /**
     * Convert the value for a given key into {@literal int}
     *
     * @param key key
     * @return int-value
     */
    public int getIntValue(String key) {
        return Integer.parseInt(pairs.get(key));
    }

    /**
     * Convert the value for a given key into a String-array
     * format: [value1,value2,value3,...]
     *
     * @param key key
     * @return int-value
     */
    public String[] getArrayValue(String key) {
        String str = getValue(key);

        if (str == null)
            return null;
        else if(str.compareTo("[]") == 0) {
            return new String[0];
        } else {
            String[] vals = str.substring(1, str.length() - 1).split(",");

            for (int i = 0; i < vals.length; i++) {
                vals[i] = vals[i].trim();
            }

            return vals;
        }
    }

    /**
     * set/add a key-value pair
     *
     * @param key   Key
     * @param value Value
     * @return this for method chaining
     */
    public KeyValueFile setValue(String key, String value) {
        pairs.put(key, value);
        return this;
    }

    /**
     * set/add a key-value pair with a list of values
     *
     * @param key    Key
     * @param values array of values
     * @return this for method chaining
     */
    public KeyValueFile setArrayValue(String key, String[] values) {
        pairs.put(key, Arrays.toString(values));
        return this;
    }

    /**
     * check for the existence of a given key
     *
     * @param key key
     * @return {@literal true} if the pairs contain the key
     */
    public boolean containsKey(String key) {
        return pairs.containsKey(key);
    }

    /**
     * write all the pairs into the file
     *
     * @throws IOException
     */
    public void persist() throws IOException {
        File file = new File(path);
        file.createNewFile();

        FileWriter out = new FileWriter(file);

        for (String key : pairs.keySet()) {
            out.write(key + PAIR_SEPARATOR + pairs.get(key) + "\n");
        }

        out.close();
    }
}
