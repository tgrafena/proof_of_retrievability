package at.aau.syssec.por;

import at.aau.syssec.por.hash.ChameleonHash;
import at.aau.syssec.por.hash.PrivateKeySet;
import at.aau.syssec.por.verifier.Challenges;
import at.aau.syssec.por.verifier.VerifierChallenge;
import at.aau.syssec.por.verifier.VerifierFileInfo;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by unki2aut on 10.03.14.
 */
public class TestUtil {


    private static final int BLOCK_SIZE = 1024;
    private static final PrivateKeySet keys = ChameleonHash.keyGen(128);

    public static List<byte[]> createData(int blockCount) {
        ArrayList<byte[]> list = new ArrayList<byte[]>(blockCount);

        for (int i = 0; i < blockCount; i++) {
            list.add(Util.generateChallenge(BLOCK_SIZE));
        }

        return list;
    }

    public static List<Block> createBlocks(List<byte[]> blocks) {
        ArrayList<Block> list = new ArrayList<Block>(blocks.size());

        for (byte[] data : blocks) {
            BigInteger rho = Util.secRand(128);
            BigInteger delta = Util.secRand(128);
            list.add(new Block(data, rho, delta, keys));
        }

        return list;
    }

    public static VerifierFileInfo createFileInfo(List<Block> blocks) {
        // variables----------------------------------------------------------------
        String fileName = "TestName";

        String hash = Util.bytesToHex(Util.generateChallenge(32));
        BlockSize bs = new BlockSize(blocks.size() * BLOCK_SIZE, Util.ld(blocks.size()), BLOCK_SIZE, blocks.size());
        Challenges challenges = new Challenges();

        // create
        // challenge----------------------------------------------------------
        for (int i = 0; i < blocks.size(); i++) {
            Block block = blocks.get(i);
            byte[] challenge = Util.generateChallenge(128);
            byte[] chunk = Util.byteConcat(challenge, block.getHash());
            byte[] response = ChameleonHash.hash(chunk, block.getRho(), block.getDelta(), keys);
            challenges.addChallenge(i, new VerifierChallenge(i, challenge, response));
        }

        return new VerifierFileInfo(fileName, hash, bs, challenges, keys);
    }
}
