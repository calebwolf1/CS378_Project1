import java.util.Map;

public class Cache {
    private final double ACTIVE_POWER, IDLE_POWER, ACCESS_ENERGY, ACCESS_TIME;
    private int hits, misses;
    private int reads, writes;
    private int idleTime, activeTime;

    private Block[][] data;  // data[1] gives set 1; data[1][2] gives block 2 of set 1
    private AddressParser parser;
    private Cache nextLevel;

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

    public void setNextLevel(Cache nextLevel) {
        this.nextLevel = nextLevel;
    }

    public long read(long address) {
        int index = parser.getIndex(address);
        Block[] set = data[index];
        int tag = parser.getTag(address);
        Block hit = checkHit(set, tag);
        if(hit != null) {
            // done; return the data
            return hit.data;
        }

        // cache miss, first need to read in data from next level
        long newData = nextLevel.read(address);
        // now need to find a place to put this line

        // first look for an unoccupied line
        Block unoccupied = checkUnoccupied(set);
        if(unoccupied != null) {
            unoccupied.data = newData;
            unoccupied.tag = tag;
            unoccupied.valid = true;
            return newData;
        }

        // no unoccupied lines, evict a line from this set
        Block evictee = findEvictee(set);
        // TODO: 4/22/2024 send signal to previous levels that the chosen block is being evicted
        // evict block i
        cleanBlock(evictee, address);
        // perform the eviction and return
        evictee.data = newData;
        return newData;
    }

    public void write(long address, long value) {
        int index = parser.getIndex(address); // TODO: 4/22/2024 index doesn't exist for direct map
        Block[] set = data[index];
        int tag = parser.getTag(address);
        Block hit = checkHit(set, tag);
        if(hit != null) {
            hit.data = value;
            hit.dirty = true;
            return;
        }
        // cache miss, read in missing line from next level
        long newData = nextLevel.read(address);
        // look for an unoccupied block in the set
        Block unoccupied = checkUnoccupied(set);
        if(unoccupied != null) {
            // block is unoccupied, put the retrieved line here
            unoccupied.data = newData;
            unoccupied.tag = tag;
            unoccupied.valid = true;
            // write the value to the correct offset
            unoccupied.data = value;
            unoccupied.dirty = true;
            return;
        }

        // no unoccupied blocks, must evict an occupied one
        Block evictee = findEvictee(set);
        // TODO: 4/22/2024 send signal to previous levels that the chosen block is being evicted
        // evict block i
        cleanBlock(evictee, address);
        // write the retrieved line to the evicted one's spot
        evictee.data = newData;
        // write the value to write to this line and set the dirty bit
        evictee.data = value;
        evictee.dirty = true;
    }

    /**
     * Returns the block in a set that matches a given tag
     * @param set
     * @param tag
     * @return the location of the cache hit or null if a miss.
     */
    private Block checkHit(Block[] set, int tag) {
        for (Block block : set) {
            if (block.valid && block.tag == tag) {
                // cache hit
                // maybe use offset to find the data
                return block;
            }
        }
        return null;
    }

    /**
     * Finds the first unoccupied block in a set, or null if all blocks are occupied
     * @param set
     * @return
     */
    private Block checkUnoccupied(Block[] set) {
        // look for an unoccupied line
        for (Block block : set) {
            if (!block.valid) {
                // line i is unoccupied, bring in a new line from the next level
                return block;
            }
        }
        return null;
    }

    /**
     * Finds a random block to evict from the given set
     * @param set
     * @return
     */
    private Block findEvictee(Block[] set) {
        int i = 0;
        if(set.length > 1) {
            i = (int) (Math.random() * set.length);
        }
        return set[i];
    }

    private void cleanBlock(Block b, long address) {
        if(b.dirty) {
            // need to write evictee the next level
            nextLevel.write(address, b.data);
            b.dirty = false;
        }
    }

    public double energyConsumption() {
        double activeEnergy = activeTime * ACTIVE_POWER + ACCESS_ENERGY * (reads + writes);
        double idleEnergy = idleTime * IDLE_POWER;
        return activeEnergy + idleEnergy;
    }
}
