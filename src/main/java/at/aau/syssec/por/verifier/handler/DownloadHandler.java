package at.aau.syssec.por.verifier.handler;

import at.aau.syssec.por.BlockSize;
import at.aau.syssec.por.merkle.MerkleTree;
import at.aau.syssec.por.msg.ResponseMsg;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Set;

/**
 * For handling response messages after a download.
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class DownloadHandler extends AbstractHandler<ResponseMsg> {

    private List<Integer> keys;
    private BlockSize bs;
    private MerkleTree tree;
    private File file;
    private RandomAccessFile raf;

    public DownloadHandler(String fileName, List<Integer> keys, BlockSize bs) throws IOException {
        file = new File(fileName);
        file.getParentFile().mkdirs();
        file.createNewFile();

        raf = new RandomAccessFile(file, "rw");
        raf.setLength(bs.fileSize);

        this.keys = keys;
        this.tree = new MerkleTree(bs.depth);
        this.bs = bs;
    }

    @Override
    public RETURN_TYPE handleMessage(ResponseMsg res) {
        tree.setBlock(res.getPos(), res.getBlock());

        try {
            raf.seek(res.getPos() * bs.blockSize);
            raf.write(res.getData());
            keys.remove(new Integer(res.getPos()));
        } catch (IOException e) {
            setMsg("couldn't write file!");
            return RETURN_TYPE.ERROR;
        }

        if (keys.isEmpty()) {
            setMsg("whole file is restored in " + file.getPath());
            return RETURN_TYPE.FINISHED;
        } else {
            return RETURN_TYPE.CONTINUE;
        }
    }
}
