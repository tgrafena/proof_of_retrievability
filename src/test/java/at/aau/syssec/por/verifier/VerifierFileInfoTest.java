package at.aau.syssec.por.verifier;

import at.aau.syssec.por.Assert;
import at.aau.syssec.por.TestUtil;
import at.aau.syssec.por.hash.PrivateKeySet;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class VerifierFileInfoTest {

    private VerifierFileInfo fileInfo;

    @Before
    public void init() {
        // variables----------------------------------------------------------------
        fileInfo = TestUtil.createFileInfo(TestUtil.createBlocks(TestUtil.createData(4)));
    }

    @Test
    public void testPersist() {

        // initialize VerifierFileInfo and
        // persist------------------------------------------------------------

        try {
            fileInfo.persist();
        } catch (IOException e) {
            Assert.fail("failed to persist fileInfo: " + e.getMessage());
        }

        // read persisted data-------------------------------------------
        try {
            VerifierFileInfo readFile = VerifierFileInfo.readFile(fileInfo.getHash());

            Assert.assertEquals("persisted and read fileInfo do not match", fileInfo, readFile);
        } catch (IOException e) {
            Assert.fail("failed to persist fileInfo: " + e.getMessage());
        }
    }

    @Test
    public void testSerializationObject() {
        String serialized = VerifierFileInfo.serializeObject(fileInfo.getKeys());
        PrivateKeySet keys = VerifierFileInfo.objectToKeys(VerifierFileInfo.deserializeObject(serialized));

        Assert.assertNotNull("deserialized keys are null", keys);
        Assert.assertEquals("serialized keys and deserialized object do not match", fileInfo.getKeys(), keys);

        serialized = VerifierFileInfo.serializeObject(fileInfo.getChallenges());
        Challenges challenges = VerifierFileInfo.objectToChallenges(VerifierFileInfo
                .deserializeObject(serialized));

        Assert.assertEquals("serialized challenges and deserialized object do not match each other!",
                fileInfo.getChallenges(), challenges);
    }
}
