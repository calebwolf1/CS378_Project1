public class Trace {
    public enum Operation {
        MEM_READ,
        MEM_WRITE,
        INSN_FETCH,
        IGNORE
        // no cache flush
    }

    public final Operation OP;
    public final long ADDRESS;
    public final long VALUE;

    /**
     * Parses a given String in the Dinero trace format into an instance of Trace.
     * @param dinString a Dinero trace string.
     */
    public Trace(String dinString) {
        String[] tokens = dinString.split(" ");
        assert tokens.length == 3;
        // read the memory operation
        int opCode = Integer.parseInt(tokens[0]);
        OP = numToOp(opCode);
        // read address
        ADDRESS = Long.parseLong(tokens[1], 16);
        // read value (probably unnecessary)
        VALUE = Long.parseLong(tokens[2], 16);

    }

    public String toString() {
        return "Operation: " + OP.toString() + "\n" +
                "Address: " + Long.toHexString(ADDRESS);
    }

    /**
     * Returns an Operation given a Dinero memory op code
     * @param code
     * @return
     */
    private Operation numToOp(int code) {
        switch(code) {
            case 0:
                return Operation.MEM_READ;
            case 1:
                return Operation.MEM_WRITE;
            case 2:
                return Operation.INSN_FETCH;
            default:
                return Operation.IGNORE;
        }
    }
}
