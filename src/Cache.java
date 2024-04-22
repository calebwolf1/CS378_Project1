import java.util.Map;

public class Cache {
    private final double ACTIVE_POWER, IDLE_POWER, ACCESS_ENERGY, ACCESS_TIME;
    private int hits, misses;
    private int reads, writes;
    private int idleTime, activeTime;

    private Block[][] data;  // data[1] gives set 1; data[1][2] gives block 2 of set 1
    private AddressParser parser;

    /**
     * Creates an empty cache. See SE lab notes pt. 2 for more info about params.
     * @param A Associativity, number of ways, and number of lines per set
     * @param B Block/line size, number of bytes in a line/block
     * @param C Cache capacity, total size in bytes of cache
     */
    public Cache(int A, int B, int C, double activePower, double idlePower,
                 double accessTime, double accessEnergy) {
        ACTIVE_POWER = activePower;
        IDLE_POWER = idlePower;
        ACCESS_TIME = accessTime;
        ACCESS_ENERGY = accessEnergy;

        // create cache
        int S = C / (A * B);
        data = new Block[S][A];
        for(int i = 0; i < S; i++) {
            for(int j = 0; j < A; j++) {
                data[i][j] = new Block();
            }
        }

        parser = new AddressParser(B, S);
    }

    public void read() {
        misses++;
    }

    public double energyConsumption() {
        double activeEnergy = activeTime * ACTIVE_POWER + ACCESS_ENERGY * (reads + writes);
        double idleEnergy = idleTime * IDLE_POWER;
        return activeEnergy + idleEnergy;
    }
}
