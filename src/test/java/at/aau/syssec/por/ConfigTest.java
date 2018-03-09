package at.aau.syssec.por;

import org.junit.Test;

/**
 * Created by unki2aut on 7/3/14.
 */
public class ConfigTest {
    @Test
    public void testDefault() {
        Config.setup();

        Assert.assertEquals("sec param", 512, Config.getIntValue(Config.SEC_PARAM));
        Assert.assertEquals("max messages", 5, Config.getIntValue(Config.MAX_CHALLENGE_MESSAGES));
        Assert.assertEquals("min challenges", 10, Config.getIntValue(Config.MIN_CHALLENGES));
        Assert.assertEquals("max challenges", 10, Config.getIntValue(Config.MAX_CHALLENGES));
        Assert.assertEquals("server port", 3370, Config.getIntValue(Config.SERVER_PORT));
        Assert.assertEquals("provider", "127.0.0.1", Config.getValue(Config.PROVIDER));
    }
}
