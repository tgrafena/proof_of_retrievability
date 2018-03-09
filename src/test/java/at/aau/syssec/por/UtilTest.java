package at.aau.syssec.por;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

public class UtilTest {

	@Test
	public void testCalcHash() {
		byte[] data = "this is a test".getBytes();
		String hash = "9fd09c38c2a5ae0a0bcd617872b735e37909ccc05c956460be7d3d03d881a0dc";

        Assert.assertEquals(hash, Util.bytesToHex(Util.calcHash(data)));
        Assert.assertEquals(hash, Util.bytesToHex(Util.calcHash(data)));

        for(int i = 0; i < 20; i++) {
            data = Util.generateChallenge(128);
            Assert.assertArrayEquals(Util.calcHash(data), Util.calcHash(data));
        }
	}

	@Test
	public void testGenerateChallenge() {
		int t = 100;
		byte[] data = Util.generateChallenge(t);
		
		Assert.assertEquals(t, data.length);
	}

	@Test
	public void testByteConcat() {
		byte[] d1 = new byte[] {123, 58};
		byte[] d2 = new byte[] {30, 120};
		byte[] res = new byte[] {123, 58, 30, 120};
		
		Assert.assertArrayEquals(res, Util.byteConcat(d1, d2));
	}

	@Test
	public void testBytesToHex() {
		byte[] data = new byte[] {(byte)171, (byte)205, 18, 52};
		String res = "abcd1234";
		
		Assert.assertEquals(res, Util.bytesToHex(data));
	}

	@Test
	public void testHexToBytes() {
		String data = "ab12cd34";
		byte[] res = new byte[] {(byte)171, 18, (byte)205, 52};
		
		Assert.assertArrayEquals(res, Util.hexToBytes(data));
	}

	@Test
	public void testLd() {
		int data = 1024;
		int res = 10;
		
		Assert.assertEquals(res, Util.ld(data));
	}

	@Test
	public void testCalcBlockSize() {
		int val = 123456789;
		BlockSize bs = Util.calcBlockSize(val);
		
		Assert.assertEquals(942, bs.blockCount);
		Assert.assertEquals(131072, bs.blockSize);
		Assert.assertEquals(10, bs.depth);
		Assert.assertEquals(val, bs.fileSize);
		
		for(int i = 0; i < 100; i++) {
			bs = Util.calcBlockSize((new Random()).nextInt());
			Assert.assertTrue(bs.blockCount+" <= 1024", bs.blockCount <= 1024);
			Assert.assertTrue(bs.blockSize+" >= 131072", bs.blockSize >= 131072);
			Assert.assertTrue(bs.depth+" > 0", bs.depth > 0);
		}
	}
}
