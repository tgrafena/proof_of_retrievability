package at.aau.syssec.por.verifier;

import at.aau.syssec.por.*;
import at.aau.syssec.por.hash.ChameleonHash;
import at.aau.syssec.por.hash.PrivateKeySet;
import at.aau.syssec.por.merkle.AuthPath;
import at.aau.syssec.por.merkle.MerkleTree;
import at.aau.syssec.por.msg.ChallengeMsg;
import at.aau.syssec.por.msg.ResponseMsg;
import org.junit.Before;
import org.junit.Test;
import java.util.List;


public class PoRStructTest {

    private PoRStruct porStruct;
    private List<Block> blocks;
    private List<byte[]> data;
    private MerkleTree tree;

    @Before
    public void init() {
        Config.setup();
        data = TestUtil.createData(4);
        blocks = TestUtil.createBlocks(data);
        porStruct = PoRStruct.setup(0, TestUtil.createFileInfo(blocks));
        tree = new MerkleTree(Util.ld(data.size()));

        for(int pos = 0; pos < blocks.size(); pos++) {
            tree.setBlock(pos, blocks.get(pos));
        }
    }

    @Test
    public void testChallenge() {
        String hash = Util.bytesToHex(Util.generateChallenge(128));
        int challengeAmount = 32;
        ChallengeMsg msg = porStruct.challenge(hash, challengeAmount);

        Assert.assertEquals("Failed to produce requested amount of challenges!",
                challengeAmount, msg.getChallenges().size());
    }

    @Test
    public void testVerify() {
        Challenges challenges = porStruct.getFileInfo().getChallenges();

        for (VerifierChallenge vc : challenges) {
            vc.send(); // usually done by verifier
            Block block = blocks.get(vc.getPos());
            byte[] d = data.get(vc.getPos());
            AuthPath path = tree.getPath(vc.getPos());
            
            ResponseMsg msg = new ResponseMsg(vc.getPos(),vc.getChallenge(), vc.getHash(), d, block, path);
            Assert.assertTrue("Wrongly rejected correct response!", porStruct.verify(msg));
        }

        challenges.addPseudoChallenge(0, new Challenge(0, Util.generateChallenge(32)));
        AuthPath path = tree.getPath(0);
        Block block = blocks.get(0);
        ResponseMsg msg = new ResponseMsg(0, Util.generateChallenge(32), Util.generateChallenge(32), new byte[0], block, path);
        Assert.assertFalse("Wrongly accepted incorrect response!", porStruct.verify(msg));
    }

    @Test
    public void testCreateChallenge() {
        String hash = Util.bytesToHex(Util.generateChallenge(32));
        int challengeAmount = 2;
        ChallengeMsg msg = porStruct.challenge(hash, challengeAmount);

        Assert.assertEquals("hashes do not match", hash, msg.getHash());
        Assert.assertEquals("challenge-amount does not match", challengeAmount, msg.getChallenges().size());
    }
}
