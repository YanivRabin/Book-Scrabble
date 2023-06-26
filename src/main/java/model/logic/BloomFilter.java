package model.logic;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class BloomFilter {

    int bitSize;
    private final ArrayList<MessageDigest> messageDigests = new ArrayList<>();
    private final BitSet bitSet;

    /**
     * The BloomFilter function takes in a string and hashes it using the hash functions
     * that were passed into the constructor. It then sets those bits to 1.

     *
     * @param args size Set the size of the bitset
     * @param args args Pass in a variable number of arguments
     *
     * @return A new bloomfilter object
     *
     * @docauthor Trelent
     */
    public BloomFilter(int size, String... args) {

        bitSize = size;
        bitSet = new BitSet(size);
        for (String hashAlgo : args) {
            try {
                messageDigests.add(MessageDigest.getInstance(hashAlgo));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * The add function takes a string and hashes it using the hash functions
     * in messageDigests. The resulting hash values are then used to set bits
     * in bitSet.

     *
     * @param s s Get the bytes array from the hash function
     *
     * @return The bitset
     *
     * @docauthor Trelent
     */
    public void add(String s)  {

        for (MessageDigest md : messageDigests) {

            //return bytes array for s from the hash func
            byte[] bytes = md.digest(s.getBytes());
            //get bytes value
            BigInteger bigInt = new BigInteger(bytes);
            //byte value mod bitSize give index to turn on
            bitSet.set(Math.abs(bigInt.intValue() % bitSize));
        }
    }

    /**
     * The toString function returns a string representation of the BitSet.
     *
     *
     *
     * @return A string of bits
     *
     * @docauthor Trelent
     */
    @Override
    public String toString() {

        StringBuilder str = new StringBuilder(bitSet.length());
        for (int i = 0; i < bitSet.length(); i++)
            str.append(bitSet.get(i) ? "1" : "0");

        return str.toString();
    }

    /**
     * The contains function takes a string as input and returns true if the BloomFilter contains that string.
     * It does this by hashing the input with each of the hash functions in messageDigests, then checking to see if
     * bitSet has been set at those indices. If any of these bits are not set, it returns false; otherwise it returns true.

     *
     * @param s s Get the byte array of the string
     *
     * @return True if the bloom filter contains the given string
     *
     * @docauthor Trelent
     */
    public boolean contains(String s) {

        for (MessageDigest md : messageDigests) {

            byte[] bytes = md.digest(s.getBytes());
            BigInteger bigInt = new BigInteger(bytes);

            if (!bitSet.get(Math.abs(bigInt.intValue() % bitSize)))
                return false;
        }
        return true;
    }
}
