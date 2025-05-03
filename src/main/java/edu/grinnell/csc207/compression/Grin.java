package edu.grinnell.csc207.compression;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The driver for the Grin compression program.
 */
public class Grin {
    /**
     * Decodes the .grin file denoted by infile and writes the output to the
     * .grin file denoted by outfile.
     * 
     * @param infile  the file to decode
     * @param outfile the file to ouptut to
     * @throws IOException
     */
    public static void decode(String infile, String outfile) throws IOException {
        BitInputStream bI = new BitInputStream(infile);
        BitOutputStream bO = new BitOutputStream(outfile);
        HuffmanTree decoder = new HuffmanTree(bI);
        decoder.decode(bI, bO);
    }

    /**
     * Creates a mapping from 8-bit sequences to number-of-occurrences of
     * those sequences in the given file. To do this, read the file using a
     * BitInputStream, consuming 8 bits at a time.
     * 
     * @param file the file to read
     * @return a freqency map for the given file
     * @throws IOException
     */
    public static Map<Short, Integer> createFrequencyMap(String file) throws IOException {
        BitInputStream b = new BitInputStream(file);
        Map<Short, Integer> m = new HashMap<Short, Integer>();
        while (b.hasBits()) {
            int val = b.readBits(8);
            m.put((short) val, m.getOrDefault((short) val, 0) + 1);
        }
        return m;
    }

    /**
     * Encodes the given file denoted by infile and writes the output to the
     * .grin file denoted by outfile.
     * 
     * @param infile  the file to encode.
     * @param outfile the file to write the output to.
     * @throws IOException
     */
    public static void encode(String infile, String outfile) throws IOException {
        BitInputStream bI = new BitInputStream(infile);
        BitOutputStream bO = new BitOutputStream(outfile);
        HuffmanTree decoder = new HuffmanTree(bI);
        decoder.decode(bI, bO);
        // fix back and forward issue
    }

    /**
     * The entry point to the program.
     * 
     * @param args the command-line arguments.
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        // TODO: fill me in!
        System.out.println("Usage: java Grin <encode|decode> <infile> <outfile>");

        if (args[0].equals("decode")) {
            decode(args[1], args[2]);
        }
    }
}
