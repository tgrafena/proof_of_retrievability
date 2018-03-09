package at.aau.syssec.por.prover;

import at.aau.syssec.por.Assert;
import at.aau.syssec.por.Block;
import at.aau.syssec.por.PorException;
import at.aau.syssec.por.Util;
import at.aau.syssec.por.hash.ChameleonHash;
import at.aau.syssec.por.hash.PrivateKeySet;
import at.aau.syssec.por.hash.PublicKeySet;
import at.aau.syssec.por.merkle.MerkleTree;
import org.junit.Test;

public class StorageTest {

    @Test
    public void testWriteFileBundle() {
        //generate necessary variables
        PrivateKeySet sKeys = ChameleonHash.keyGen(128);
        PublicKeySet pKeys = new PublicKeySet(sKeys);
        MerkleTree tree = initTree(3, sKeys);

        String hash = Util.bytesToHex(tree.getHash());
        FileBundle bundle = new FileBundle(tree, pKeys);

        try {
            //write and read bundle
            Storage.writeFileBundle(bundle);
            FileBundle readBundle = Storage.readFileBundle(hash);

            Assert.assertTrue("Failure_Test_Storage_WriteBlock: Couldn't delete file", Storage.removeFile(hash));

            //check if bundles are still the same
            Assert.assertEquals("Failure_Test_Storage_WriteFileBundle: Stored Bundle couldn't be read correctly!",
                    bundle, readBundle);
        } catch (PorException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testWriteBlock() {
        //generate necessary variables
        byte[] data = Util.generateChallenge((int) Math.pow(2, 14));
        String hash = Util.bytesToHex(Util.generateChallenge(32));

        try {
            //read and write block
            Storage.writeBlock(hash, data);
            byte[] readData = Storage.readBlock(hash);

            Assert.assertTrue("Failure_Test_Storage_WriteBlock: Couldn't delete file", Storage.removeFile(hash));

            //check if blocks are the same
            Assert.assertEquals("Failure_Test_Storage_WriteBlock: Block was not overwritten correctly!", data, readData);
        } catch (PorException e) {
            Assert.fail(e.getMessage());
        }
    }

    public MerkleTree initTree(int depth, PrivateKeySet sKeys) {
        MerkleTree tree = new MerkleTree(depth);

        // fill tree with random data and store data additionally in string
        // array
        for (int i = 0; i < (int) Math.pow(2, depth); i++) {
            byte[] data = Util.generateChallenge(1024);
            Block block = new Block(data, Util.secRand(128), Util.secRand(128), sKeys);
            tree.setBlock(i, block);
        }

        return tree;
    }

}
