public class AddressParser {
    private static final int ADDRESS_LENGTH = 32;
    private int offsetBits;
    private int indexBits;
    private int tagBits;

    /**
     * Create a parsing configuration based on block size and number of sets
     * @param B block size
     * @param S number of sets
     */
    public AddressParser(int B, int S) {
        offsetBits = log2(B);
        indexBits = log2(S);
        tagBits = ADDRESS_LENGTH - (offsetBits + indexBits);
    }

    public int getTag(long address) {
        return (int) bitExtracted(address, tagBits, offsetBits + indexBits);
    }

    public int getIndex(long address) {
        return (int) bitExtracted(address, indexBits, offsetBits);
    }

    public int getOffset(long address) {
        return (int) bitExtracted(address, offsetBits, 0);
    }

    private static int log2(int N) {
        return (int)(Math.log(N) / Math.log(2));
    }

    private static long bitExtracted(long number, long k, long p) {
        return ((1L << k) - 1) & (number >> p);
    }
}
