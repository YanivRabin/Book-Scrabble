package model.logic;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class BloomFilter {

    int bitSize;
    private final ArrayList<MessageDigest> messageDigests = new ArrayList<>();
    private final BitSet bitSet;

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

    @Override
    public String toString() {

        StringBuilder str = new StringBuilder(bitSet.length());
        for (int i = 0; i < bitSet.length(); i++)
            str.append(bitSet.get(i) ? "1" : "0");

        return str.toString();
    }

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
