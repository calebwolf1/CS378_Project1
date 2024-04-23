public class Tests {
    public static void main(String[] args) {
        int A = 1;
        int B = 64;
        int C = 32_000;
        int S = C / (A * B);
        long n = 0b1011_0111_0111_1011_1110_1111_1101_1111L;
        AddressParser parser = new AddressParser(64, S);
        String ans = Long.toBinaryString(parser.getTag(n));
        System.out.println(ans);
    }
}
