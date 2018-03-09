package at.aau.syssec.por.verifier;

import at.aau.syssec.por.*;
import at.aau.syssec.por.hash.ChameleonHash;
import at.aau.syssec.por.hash.PreImage;
import at.aau.syssec.por.hash.PrivateKeySet;
import at.aau.syssec.por.hash.PublicKeySet;
import at.aau.syssec.por.log.Debug;
import at.aau.syssec.por.merkle.AuthPath;
import at.aau.syssec.por.merkle.MerkleTree;
import at.aau.syssec.por.msg.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Includes all structural functions of the PoR.
 *
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class PoRStruct {

    /**
     * security parameter
     */
    private final int t;

    /**
     * challenges of a file
     */
    private Challenges challenges;

    /**
     * information of a file
     */
    private VerifierFileInfo fileInfo;


    private PoRStruct(int t, VerifierFileInfo fileInfo) {
        this.t = t;

        if (fileInfo != null) {
            this.fileInfo = fileInfo;
            this.challenges = fileInfo.getChallenges();
        }
    }

    /**
     * setup of the struct for a certain file
     *
     * @param t
     * @param fileInfo
     * @return
     */
    public static PoRStruct setup(int t, VerifierFileInfo fileInfo) {
        return new PoRStruct(t, fileInfo);
    }

    public VerifierFileInfo getFileInfo() {
        return fileInfo;
    }

    /**
     * encodes a file and sends it to the prover
     *
     * @param path    - file-path
     * @param handler - handler-type
     * @return
     */
    public byte[] encode(String path, MessageHandler handler) {
        SecureRandom secRnd = new SecureRandom();

        try {
            System.out.println("read file " + path);

            File file = new File(path);

            if (file.canRead()) {
                FileInputStream fis = new FileInputStream(file);
                BufferedInputStream in = new BufferedInputStream(fis);

                int size = fis.available();
                BlockSize bs = Util.calcBlockSize(size);

                System.out.println("generate private key set ...");
                PrivateKeySet keys = ChameleonHash.keyGen(Config.getIntValue(Config.SEC_PARAM));

                MerkleTree tree = new MerkleTree(bs.depth);
                List<byte[]> blocks = new ArrayList<byte[]>(bs.blockCount);

                // send init-msg
                System.out.println("send create msg " + bs + " ...");
                handler.sendMsg(new CreateMsg(bs, new PublicKeySet(keys)));

                for (int i = 0; i < bs.blockCount && in.available() > 0; i++) {
                    System.out.println("read block " + i + " ...");

                    byte[] buf = new byte[bs.blockSize];
                    in.read(buf);

                    Block block = new Block(buf, Util.secRand(t), Util.secRand(t), keys);

                    blocks.add(i, buf);
                    tree.setBlock(i, block);

                    Debug.info("pos:", i, block);

                    System.out.println("send block ...");

                    // sending
                    handler.sendMsg(new BlockMsg(i, buf, block));
                }

                if (challenges == null) {
                    challenges = new Challenges();
                }

                while (challenges.getSize() < Config.getIntValue(Config.MIN_CHALLENGES)) {
                    int pos = secRnd.nextInt(blocks.size());
                    byte[] buf = blocks.get(pos);
                    Block block = tree.findLeaf(pos).getValue();

                    challenges.createChallenge(pos, buf, tree.getPath(pos), block, keys);
                }

                System.out.println(challenges.getSize() + " challenges added ...");
                System.out.println("file read and sent ...");

                byte[] rootHash = tree.getHash();

                // write verifier config
                fileInfo = new VerifierFileInfo(file.getName(), Util.bytesToHex(rootHash), bs, challenges, keys);

                fis.close();

                return rootHash;
            } else {
                System.err.println("Couldn't find file " + path);
                return null;
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    /**
     * Generate new challenges for prover to proof
     *
     * @return challenges
     */

    public ChallengeMsg challenge(String hash, int challengeAmount) {
        ChallengeMsg msg = new ChallengeMsg(hash);
        Random rnd = new Random();

        for (int i = 0; i < challengeAmount; i++) {
            int pos = rnd.nextInt(fileInfo.getBlockSize().blockCount);
            msg.addChallenge(pos, challenges.getChallenge(pos).getChallenge());
        }

        return msg;
    }

    /**
     * Verify provers response
     *
     * @param res response message
     * @return true if all challenges have valid results
     */
    public boolean verify(ResponseMsg res) {
        boolean verified = challenges.verifyChallenge(res.getPos(), res.getChallenge(), res.getResponse());
        if (verified && challenges.getSize() <= Config.getIntValue(Config.MIN_CHALLENGES)) {
            // if response data is valid, create a new challenge for this block
            challenges.createChallenge(res.getPos(), res.getData(), res.getPath(), res.getBlock(), fileInfo.getKeys());
        }
        return verified;
    }

    /**
     * updates a file at a certain position
     *
     * @param rootHash  - filename at prover (file to be updated)
     * @param data      - update
     * @param pos       - position to be updated
     * @param blockHash - old block hash
     * @return
     */
    public UpdateMsg update(byte[] rootHash, int pos, byte[] data, byte[] blockHash, AuthPath path) {
        PrivateKeySet keys = fileInfo.getKeys();

        PreImage image = ChameleonHash.forge(blockHash, data, keys);
        Block newBlock = image.getBlock(keys);

        Debug.info(image);
        Debug.info("before:", blockHash, "after:", newBlock.getHash());

        // remove old challenges
        challenges.removeChallenges(pos);
        Debug.info("delete challenges");

        // create new challenges
        while (challenges.getSize() < Config.getIntValue(Config.MIN_CHALLENGES)) {
            Debug.info("update", newBlock);
            challenges.createChallenge(pos, data, path, newBlock, keys);
        }

        try {
            fileInfo.persist();
        } catch (IOException e) {
            Debug.err(e.getMessage());
        }

        return new UpdateMsg(rootHash, data, pos, newBlock);
    }

    /**
     * downloads the whole file from the prover
     *
     * @param hash - filename at prover (file to be downloaded)
     * @return
     */
    public ChallengeMsg extract(String hash) {
        int blockAmount = fileInfo.getBlockSize().blockCount;
        ChallengeMsg msg = new ChallengeMsg(hash);

        for (int i = 0; i < blockAmount; i++) {
            msg.addChallenge(i, challenges.getChallenge(i).getChallenge());
        }

        return msg;
    }
}
