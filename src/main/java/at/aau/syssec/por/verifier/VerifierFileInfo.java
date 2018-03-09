package at.aau.syssec.por.verifier;

import at.aau.syssec.por.BlockSize;
import at.aau.syssec.por.Config;
import at.aau.syssec.por.KeyValueFile;
import at.aau.syssec.por.Util;
import at.aau.syssec.por.hash.PrivateKeySet;
import org.bouncycastle.util.encoders.Base64;

import java.io.*;

/**
 * Contains all informations about a file.
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class VerifierFileInfo {

    private final static String NAME = "name";
    private final static String HASH = "hash";
    private final static String FILE_SIZE = "fileSize";
    private final static String DEPTH = "depth";
    private final static String BLOCK_SIZE = "blockSize";
    private final static String BLOCK_COUNT = "blockCount";
    private final static String CHALLENGES = "challenges";
    private final static String KEYS = "keys";
    private String fileName;
    private String hash;
    private BlockSize bs;
    private Challenges challenges;
    private PrivateKeySet keys;

    public VerifierFileInfo(String fileName, String hash, BlockSize bs,
                            Challenges challenges, PrivateKeySet keys) {
        this.fileName = fileName;
        this.hash = hash;
        this.bs = bs;
        this.challenges = challenges;
        this.keys = keys;
    }

    public static VerifierFileInfo readFile(String hash) throws IOException {
        KeyValueFile file = new KeyValueFile(Config.FILES_PATH + hash);

        Challenges challenges = objectToChallenges(deserializeObject(file.getValue(CHALLENGES)));
        PrivateKeySet keys = objectToKeys(deserializeObject(file.getValue(KEYS)));

        VerifierFileInfo info = new VerifierFileInfo(file.getValue(NAME),
                file.getValue(HASH), new BlockSize(
                file.getIntValue(FILE_SIZE),
                file.getIntValue(DEPTH),
                file.getIntValue(BLOCK_SIZE),
                file.getIntValue(BLOCK_COUNT)),
                challenges,
                keys);

        return info;
    }

    /**
     * Serializes an object. (necessary for storage)
     * @param object - object to be serialized
     * @return String
     */
    public static String serializeObject(Object object) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.close();

            byte[] buf = bos.toByteArray();

            return new String(Base64.encode(buf));
        } catch (IOException e) {
            return new String();
        }
    }

    /**
     * Deserializes a String to an Object.
     * @param input - String to be deserialized
     * @return Object
     */
    public static Object deserializeObject(String input) {

        try {
            byte[] buf = Base64.decode(input);
            ByteArrayInputStream bis = new ByteArrayInputStream(buf);
            ObjectInputStream in = new ObjectInputStream(bis);
            return in.readObject();
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            return null;
        } catch (ClassNotFoundException e) {
            System.err.println("ClassNotFoundException: " + e.getMessage());
            return null;
        }
    }

    /**
     * Casts an Object to type "Challenges"
     * @param object - Object to be casted
     * @return Challenges
     */
    public static Challenges objectToChallenges(Object object) {
        if (object instanceof Challenges) {
            return (Challenges) object;
        } else {
            return null;
        }
    }

    /**
     * Casts an Object to type "PrivateKeySet"
     * @param object - Object to be casted
     * @return PrivateKeySet
     */
    public static PrivateKeySet objectToKeys(Object object) {
        if (object instanceof PrivateKeySet) {
            return (PrivateKeySet) object;
        } else {
            return null;
        }
    }

    /**
     * Persists the file-information.
     * @throws IOException
     */
    public void persist() throws IOException {
        KeyValueFile file = new KeyValueFile(Config.FILES_PATH + hash);
        file.setValue(NAME, fileName)
                .setValue(HASH, hash)
                .setValue(FILE_SIZE, Integer.toString(bs.fileSize))
                .setValue(DEPTH, Integer.toString(bs.depth))
                .setValue(BLOCK_SIZE, Integer.toString(bs.blockSize))
                .setValue(BLOCK_COUNT, Integer.toString(bs.blockCount))
                .setValue(CHALLENGES, serializeObject(challenges))
                .setValue(KEYS, serializeObject(keys));
        file.persist();

        System.out.println("file info saved ...");
    }

    /* (non-Javadoc)
     * @see at.aau.syssec.por.verifier.VerfierFileInfo#getFileName()
     */
    public String getFileName() {
        return fileName;
    }

    /* (non-Javadoc)
     * @see at.aau.syssec.por.verifier.VerfierFileInfo#getHash()
     */
    public String getHash() {
        return hash;
    }

    /* (non-Javadoc)
     * @see at.aau.syssec.por.verifier.VerfierFileInfo#getBlockSize()
     */
    public BlockSize getBlockSize() {
        return bs;
    }

    /* (non-Javadoc)
     * @see at.aau.syssec.por.verifier.VerfierFileInfo#getChallenges()
     */
    public Challenges getChallenges() {
        return challenges;
    }

    /* (non-Javadoc)
     * @see at.aau.syssec.por.verifier.VerfierFileInfo#getKeys()
     */
    public PrivateKeySet getKeys() {
        return keys;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Filename: " + fileName + ", ");
        sb.append("Hash: " + Util.shorten(hash) + ", ");
        sb.append("BlockSize: " + bs + ", ");
        sb.append("Challenges: " + challenges + ", ");
        sb.append("Keys: " + keys);
        return sb.toString();
    }

}
