package at.aau.syssec.por.prover;

import at.aau.syssec.por.*;
import at.aau.syssec.por.hash.ChameleonHash;
import at.aau.syssec.por.log.Debug;
import at.aau.syssec.por.log.LogInputStream;
import at.aau.syssec.por.log.LogOutputStream;
import at.aau.syssec.por.merkle.AuthPath;
import at.aau.syssec.por.merkle.Leaf;
import at.aau.syssec.por.merkle.MerkleTree;
import at.aau.syssec.por.msg.*;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Set;
import java.util.TreeSet;

/**
 * Handling class for the prover to handle incoming messages from the verifier.
 *
 * @author Thomas Grafenauer (tgrafena@edu.uni-klu.ac.at)
 * @author Martin Fillafer (mfillafe@edu.uni-klu.ac.at)
 */
public class ClientHandler extends Thread {
    private Socket socket;
    private LogOutputStream out;
    private boolean terminate = false;
    private FileBundle tmpBundle;
    private Set<Integer> keySet;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            this.out = new LogOutputStream("server", new ObjectOutputStream(socket.getOutputStream()));
            System.out.println("new client " + socket.getInetAddress());
        } catch (IOException e) {
            System.err.println("ClientHandler: Couldn't connect to client!");
        }
    }

    @Override
    public void run() {
        try {
            LogInputStream ois = new LogInputStream("server", new ObjectInputStream(socket.getInputStream()));

            try {
                while (!terminate) {
                    Object o = ois.readObject();

                    if (o instanceof CreateMsg) {
                        handleCreateMsg((CreateMsg) o);
                    } else if (o instanceof BlockMsg) {
                        handleBlockMsg((BlockMsg) o);
                    } else if (o instanceof ChallengeMsg) {
                        handleChallengeMsg((ChallengeMsg) o);
                    } else if (o instanceof UpdateMsg) {
                        handleUpdateMsg((UpdateMsg) o);
                    } else if (o instanceof DeleteMsg) {
                        handleDeleteMsg((DeleteMsg) o);
                    }
                }
            } catch (PorException e) {
                out.writeObject(new ErrorMsg(e.getMessage()));
                terminate = true;
            }

            ois.close();
            out.close();
        } catch (SocketException e) {
            System.out.println("client disconnected: " + socket.getInetAddress());
            terminate = true;
        } catch (EOFException e) {
            System.out.println("client " + socket.getInetAddress() + " had an error.");
            terminate = true;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("IOException: " + e.getMessage());
            terminate = true;
        } catch (ClassNotFoundException e) {
            System.err.println("ClassNotFoundException: " + e.getMessage());
            terminate = true;
        }
    }

    private void handleCreateMsg(CreateMsg msg) {
        if (tmpBundle != null)
            System.err.println("ClientThread: message conflicts");

        System.out.println("new create msg from " + socket.getInetAddress());

        tmpBundle = new FileBundle(new MerkleTree(msg.getBlockSize().depth),
                msg.getProverKeys());
        keySet = new TreeSet<Integer>();

        for (int i = 0; i < msg.getBlockSize().blockCount; i++) {
            keySet.add(i);
        }
    }

    private void handleBlockMsg(BlockMsg msg) throws PorException, IOException {
        if (tmpBundle == null)
            throw new PorException("ClientThread: tree not initialized");

        System.out.println("new block msg (" + msg.getPos() + ") from " + socket.getInetAddress());

        byte[] hash = ChameleonHash.hash(msg.getData(), msg.getBlock()
                .getRho(), msg.getBlock().getDelta(), tmpBundle.getKeys());

        Storage.writeBlock(Util.bytesToHex(hash), msg.getData());
        tmpBundle.getTree().setBlock(msg.getPos(), msg.getBlock());

        keySet.remove(msg.getPos());

        // verify it's the last block and none is missing
        if (keySet.isEmpty()) {
            System.out.println("all file blocks read!");
            Storage.writeFileBundle(tmpBundle);
            out.writeObject(new SuccessMsg(tmpBundle.getTree().getHash()));
            terminate = true;
        }
    }

    private void handleChallengeMsg(ChallengeMsg msg) throws IOException, PorException {
        FileBundle bundle = Storage.readFileBundle(msg.getHash());

        if (bundle == null) {
            out.writeObject(new ErrorMsg("couldn't find tree for hash "
                    + msg.getHash()));
            terminate = true;
        } else {
            for (Challenge challenge : msg.getChallenges()) {
                AuthPath path = bundle.getTree().getPath(challenge.getPos());
                Leaf leaf = bundle.getTree().findLeaf(challenge.getPos());

                if (leaf.getValue() == null) {
                    out.writeObject(new ErrorMsg("couldn't find leaf " + challenge.getPos()));
                    terminate = true;
                    return;
                } else {
                    Block block = leaf.getValue();
                    byte[] data = Storage.readBlock(block.getHex());
                    byte[] hash = path.calcRootHash(data, challenge.getChallenge(), block.getRho(), block.getDelta(), bundle.getKeys());
                    out.writeObject(new ResponseMsg(challenge.getPos(), challenge.getChallenge(), hash, data, block, path));
                }
            }

            terminate = !msg.keepConnection();
        }
    }

    private void handleUpdateMsg(UpdateMsg msg) throws IOException, PorException {
        String hash = Util.bytesToHex(msg.getHash());
        FileBundle bundle = Storage.readFileBundle(hash);

        Debug.info(msg);

        if (bundle == null) {
            out.writeObject(new ErrorMsg("couldn't find tree for hash " + hash));
            terminate = true;
        } else {
        	byte[] before = bundle.getTree().findLeaf(msg.getPos()).getValue().getHash();

            Block block = msg.getBlock();
            // byte[] after = block.updateHash(msg.getData(), bundle.getKeys());

            // Debug.info("block-hash before: ", before, " block-hash after: ", after); // must be equal
            Debug.info("update", block);

            if (bundle.getTree().setBlock(msg.getPos(), block)) {
                Debug.info(msg.getPos(), block, block.getHex().length());
                Storage.writeBlock(block.getHex(), msg.getData());
                Storage.writeFileBundle(bundle);
                out.writeObject(new SuccessMsg(bundle.getTree().getHash()));
                terminate = !msg.keepConnection();
            } else {
                out.writeObject(new ErrorMsg("no block position " + msg.getPos() + " found!"));
                terminate = true;
            }
        }
    }

    private void handleDeleteMsg(DeleteMsg msg) throws IOException, PorException {
        FileBundle bundle = Storage.readFileBundle(msg.getHash());

        if (bundle == null) {
            out.writeObject(new ErrorMsg("couldn't find tree for hash " + msg.getHash()));
            terminate = true;
        } else {
            MerkleTree tree = bundle.getTree();
            byte[] treeHash = tree.getHash();
            boolean ret = true;

            for (int i = 0; i < Math.pow(2, bundle.getTree().getDepth()); i++) {
                Block block = tree.findLeaf(i).getValue();

                if (block != null) {
                    ret &= Storage.removeFile(block.getHex());
                }
            }

            ret &= Storage.removeFile(Util.bytesToHex(treeHash));

            if (ret) {
                out.writeObject(new SuccessMsg(treeHash));
                terminate = !msg.keepConnection();
            } else {
                out.writeObject(new ErrorMsg("couldn't delete files for hash "
                        + msg.getHash()));
                terminate = true;
            }
        }
    }
}
