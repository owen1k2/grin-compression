package edu.grinnell.csc207.compression;

import java.util.ArrayList;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * A HuffmanTree derives a space-efficient coding of a collection of byte
 * values.
 *
 * The huffman tree encodes values in the range 0--255 which would normally
 * take 8 bits. However, we also need to encode a special EOF character to
 * denote the end of a .grin file. Thus, we need 9 bits to store each
 * byte value. This is fine for file writing (modulo the need to write in
 * byte chunks to the file), but Java does not have a 9-bit data type.
 * Instead, we use the next larger primitive integral type, short, to store
 * our byte values.
 */
public class HuffmanTree {

    private Node root;

    class Node implements Comparable<Node> {
        int val;
        short ch;
        boolean visited;
        Node left;
        Node right;

        Node(int val, short ch, boolean visited, Node left, Node right) {
            this.val = val;
            this.ch = ch;
            this.visited = visited;
            this.left = left;
            this.right = right;
        }

        Node(int val, short ch) {
            this(val, ch, false, null, null);
        }

        Node(int val) {
            this(val, (short) -1, false, null, null);
        }

        @Override
        public int compareTo(Node arg0) {
            if (arg0.val > this.val) {
                return -1;
            } else if (arg0.val < this.val) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    /**
     * Constructs a new HuffmanTree from a frequency map.
     * 
     * @param freqs a map from 9-bit values to frequencies.
     */
    public HuffmanTree(Map<Short, Integer> freqs) {
        PriorityQueue<Node> p = new PriorityQueue<>();
        ArrayList<Short> arr = new ArrayList<>(freqs.keySet());
        for (int i = 0; i < freqs.size(); i++) {
            Node n = new Node(freqs.get(arr.get(i)), arr.get(i));
            p.add(n);
        }

        while (p.size() > 1) {
            Node nLeft = p.poll();
            Node nRight = p.poll();
            Node n = new Node(nLeft.val + nRight.val, (short) -1, false, nLeft, nRight);
            p.add(n);
        }
        root = p.poll();
    }

    /**
     * Helps to create the huffman tree
     * 
     * @param in
     * @param n
     */
    private void huffmanTreeHelper(BitInputStream in, Node n) {
        n.val = in.readBit();
        if (n.val == 1) {
            n.left = new Node(-1);
            n.right = new Node(-1);
            huffmanTreeHelper(in, n.left);
            huffmanTreeHelper(in, n.right);
        } else {
            n.ch = (short) in.readBits(9);
        }
    }

    /**
     * Constructs a new HuffmanTree from the given file.
     * 
     * @param in the input file (as a BitInputStream)
     */
    public HuffmanTree(BitInputStream in) {
        if (!in.hasBits()) {
            throw new IllegalArgumentException();
        }
        if (in.readBits(32) != 1846) {
            throw new IllegalArgumentException();
        }
        root = new Node(in.readBit());
        huffmanTreeHelper(in, root);
    }

    /**
     * A helper method to serialize my tree for BitOutputStream
     * 
     * @param out BitOutputStream : the output stream
     * @param n   Node : The node being checked for leaf or node
     */
    private void serializeHelper(BitOutputStream out, Node n) {
        if (n.left != null) {
            out.writeBit(1);
            serializeHelper(out, n.left);
            serializeHelper(out, n.right);
        } else {
            out.writeBit(0);
            out.writeBits(n.ch, 9);
        }
    }

    /**
     * Writes this HuffmanTree to the given file as a stream of bits in a
     * serialized format.
     * 
     * @param out the output file as a BitOutputStream
     */
    public void serialize(BitOutputStream out) {
        out.writeBits(1846, 32);
        serializeHelper(out, root);
    }

    /**
     * Encodes the file given as a stream of bits into a compressed format
     * using this Huffman tree. The encoded values are written, bit-by-bit
     * to the given BitOuputStream.
     * 
     * @param in  the file to compress.
     * @param out the file to write the compressed output to.
     */
    public void encode(BitInputStream in, BitOutputStream out) {
        // TODO: fill me in!
        while (in.hasBits()) {
            int bits = in.readBits(8);
            ArrayList<Integer> path = new ArrayList<>();

            Node temp = root;
            while (temp.ch != bits) {
                temp.visited = true;
                temp = temp.left;

                // I know this isn't done yet but will be soon
            }
            for (int i = 0; i < path.size(); i++) {
                out.writeBit(path.get(i));
            }
        }
    }

    /**
     * Decodes a stream of huffman codes from a file given as a stream of
     * bits into their uncompressed form, saving the results to the given
     * output stream. Note that the EOF character is not written to out
     * because it is not a valid 8-bit chunk (it is 9 bits).
     * 
     * @param in  the file to decompress.
     * @param out the file to write the decompressed output to.
     */
    public void decode(BitInputStream in, BitOutputStream out) {
        Node temp = root;
        while (temp.ch != 256) {
            if (temp.val == 0) {
                out.writeBits(temp.ch, 8);
                temp = root;
            } else {
                int bit = in.readBit();
                if (bit == 0) {
                    temp = temp.left;
                } else if (bit == 1) {
                    temp = temp.right;
                }
            }
        }
    }
}
