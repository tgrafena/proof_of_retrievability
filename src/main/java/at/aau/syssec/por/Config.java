package at.aau.syssec.por;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * contains settings for the PoR
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class Config {

    public static String SEPARATOR = System.getProperty("file.separator");
    public static String PATH = System.getProperty("user.home") + SEPARATOR + ".por" + SEPARATOR;
    public static String CONFIG_PATH = PATH + "por.conf";
    public static String OBJECTS_PATH = PATH + "objects" + SEPARATOR;
    public static String FILES_PATH = PATH + "files" + SEPARATOR;

    public static String SEC_PARAM = "secparam";
    public static String MAX_CHALLENGE_MESSAGES = "maxchallengemsgs";
    public static String MIN_CHALLENGES = "minchallenges";
    public static String MAX_CHALLENGES = "maxchallenges";
    public static String SERVER_PORT = "port";
    public static String PROVIDER = "provider";
    public static String DEBUG = "debug";

    public static HashMap<String, String> defaults = new HashMap<String, String>();

    static {
        // secure parameter t
        defaults.put(SEC_PARAM, "512");
        // maximum amount of requested challenges
        defaults.put(MAX_CHALLENGE_MESSAGES, "5");
        // min-amount of stored challenges
        defaults.put(MIN_CHALLENGES, "10");
        // max-amount of stored challenges
        defaults.put(MAX_CHALLENGES, "10");
        // server port
        defaults.put(SERVER_PORT, "3370");
        // provider
        defaults.put(PROVIDER, "127.0.0.1");
        // debugging
        defaults.put(DEBUG, "false");
    }

    private static KeyValueFile config;

    /**
     * TODO: describe
     */
    public static void setup() {
        try {
            config = new KeyValueFile(CONFIG_PATH);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            System.err.println("can't write config file " + config.getPath());
            System.exit(1);
        }
    }

    /**
     * returns the String-value of a given configuration-key
     * @param key
     * @return String
     */
    public static String getValue(String key) {
        String value = config.getValue(key);
        if (value == null) {
            value = defaults.get(key);
        }
        return value;
    }

    /**
     * returns the int-value of a given configuration-key
     * @param key
     * @return 
     */
    public static int getIntValue(String key) {
        String value = config.getValue(key);
        if (value == null) {
            value = defaults.get(key);
        }
        return Integer.parseInt(value);
    }

    /**
     * returns the boolean-value of a given configuration-key
     * @param key
     * @return
     */
    public static boolean getBoolValue(String key) {
        String value = config.getValue(key);
        if (value == null) {
            value = defaults.get(key);
        }
        return Boolean.parseBoolean(value);
    }

    /**
     * TODO: describe
     * @param key
     * @return
     */
    public static String[] getArrayValue(String key) {
        return config.getArrayValue(key);
    }

    /**
     * TODO: describe
     * @param key
     * @param value
     */
    public static boolean removeArrayValue(String key, String value) {
        String[] old = getArrayValue(key);
        ArrayList<String> list = new ArrayList<String>(old.length - 1);
        boolean ret = false;

        for (int i = 0; i < old.length; i++) {
            if (old[i].equals(value)) {
                ret = true;
            } else {
                list.add(old[i]);
            }
        }

        setArrayValue(key, list.toArray(new String[list.size()]));

        return ret;
    }

    /**
     * sets the value of a configuration-key
     * @param key
     * @param value
     */
    public static void setValue(String key, String value) {
        System.out.println("config "+key+", "+value);

        config.setValue(key, value);

        try {
            config.persist();
        } catch (IOException e) {
            System.err.println("Couldn't write file " + config.getPath());
        }
    }

    /**
     * TODO: describe
     * @param key
     * @param values
     */
    public static void setArrayValue(String key, String[] values) {
        config.setArrayValue(key, values);
        try {
            config.persist();
        } catch (IOException e) {
            System.err.println("Config - couldn't write to config " + CONFIG_PATH);
        }
    }

    /**
     * TODO: describe
     * @param key
     * @param value
     */
    public static void appendArrayValue(String key, String value) {
        List<String> values = new ArrayList<String>();

        if (config.containsKey(key)) {
            values = new ArrayList<String>(Arrays.asList(getArrayValue(key)));
        }

        values.add(value);
        String[] array = values.toArray(new String[values.size()]);
        setArrayValue(key, array);
    }

    /**
     * prints the value of a given configuration-key
     * @param key
     */
    public static void printValue(String key) {
        if (config.containsKey(key)) {
            System.out.println(key + KeyValueFile.PAIR_SEPARATOR + config.getValue(key));
        } else if (defaults.containsKey(key)) {
            System.out.println(key + KeyValueFile.PAIR_SEPARATOR + defaults.get(key) + " (default)");
        } else {
            System.out.println(key + " not found!");
        }
    }

    /**
     * prints the values of all configuration-keys
     */
    public static void printValues() {
        for (String key : defaults.keySet()) {
            if (!config.containsKey(key)) {
                System.out.println(key + KeyValueFile.PAIR_SEPARATOR + defaults.get(key) + " (default)");
            }
        }
        for (String key : config.getPairs().keySet()) {
            System.out.println(key + KeyValueFile.PAIR_SEPARATOR + config.getValue(key));
        }
    }
}
