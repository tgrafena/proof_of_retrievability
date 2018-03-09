package at.aau.syssec.por.prover;

import at.aau.syssec.por.Config;
import at.aau.syssec.por.PorException;
import at.aau.syssec.por.Util;

import java.io.*;

/**
 * Helper class to read and write java-serialized MerkleTrees and data-blocks
 * from the PoR-data-structure.
 *
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class Storage {

    private static File createFile(String hash) {
        String dir = hash.substring(0, 2);
        String name = hash.substring(2);
        String path = Config.OBJECTS_PATH + dir + Config.SEPARATOR + name;

        File file = new File(path);
        file.getParentFile().mkdirs();

        return file;
    }

    public static boolean removeFile(String hash) {
        File file = createFile(hash);
        return file.delete();
    }

    /**
     * Serialize and write a MerkleTree into the PoR-data-structure.
     * path: server-path/<first 2 digits of root-hash>/<rest of the root-hash>
     *
     * @param bundle FileBunlde to write
     */
    public static void writeFileBundle(FileBundle bundle) throws PorException {
        String hash = Util.bytesToHex(bundle.getTree().getHash());
        File file = createFile(hash);

        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(bundle);
            oos.close();
        } catch (FileNotFoundException e) {
            throw new PorException("Storage: couldn't find file " + file.getPath());
        } catch (IOException e) {
            throw new PorException("Storeage: couldn't write file " + file.getPath());
        }
    }

    /**
     * Serialize and write a data-block into the PoR-data-structure.
     * path: server-path/<first 2 digits of the hash>/<rest of the hash>
     *
     * @param hash  hash of the block
     * @param block data to write
     */
    public static void writeBlock(String hash, byte[] block) throws PorException {
        File file = createFile(hash);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(block);
            fos.close();
        } catch (FileNotFoundException e) {
            throw new PorException("Storage: couldn't find file " + file.getPath());
        } catch (IOException e) {
            throw new PorException("Storeage: couldn't write file " + file.getPath());
        }
    }

    /**
     * Read and deserialize a FileBundle for a given hash
     * path: server-path/<first 2 digits of root-hash>/<rest of the root-hash>
     *
     * @param hash file to read
     * @return deserialized FileBundle
     */
    public static FileBundle readFileBundle(String hash) throws PorException {
        File file = createFile(hash);

        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            Object obj = ois.readObject();
            ois.close();

            if (obj instanceof FileBundle) {
                return (FileBundle) obj;
            } else {
                return null;
            }
        } catch (FileNotFoundException e) {
            throw new PorException("Storage: couldn't find bundle " + file.getPath());
        } catch (IOException e) {
            throw new PorException("Storage: couldn't read bundle " + file.getPath());
        } catch (ClassNotFoundException e) {
            throw new PorException("Storage: couldn't convert file to bundle");
        }
    }

    /**
     * Read and deserialize a data-block for a given file-hash.
     * path: server-path/<first 2 digits of the hash>/<rest of the hash>
     *
     * @param hash file-hash
     * @return read data-block
     */
    public static byte[] readBlock(String hash) throws PorException {
        File file = createFile(hash);

        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] block = new byte[fis.available()];
            fis.read(block);
            fis.close();

            return block;
        } catch (FileNotFoundException e) {
            throw new PorException("Storage: couldn't find block " + file.getPath());
        } catch (IOException e) {
            throw new PorException("Storage: couldn't read block " + file.getPath());
        }
    }
}
