import java.util.ArrayList;

public class Cache implements Buffer {
    private final double ACTIVE_POWER, IDLE_POWER, ACCESS_ENERGY, ACCESS_TIME;
    private int hits, misses;
    private int reads, writes;
    private int idleTime, activeTime;

    private Block[][] data;  // data[1] gives set 1; data[1][2] gives block 2 of set 1
    private AddressParser parser;
    private Buffer nextLevel;
    private ArrayList<Cache> prevLevel;

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
        prevLevel = new ArrayList<>();
    }

    public void setNextLevel(Buffer nextLevel) {
        this.nextLevel = nextLevel;
    }

    public void addPrevLevelCache(Cache prevCache) {
        prevLevel.add(prevCache);
    }

    public void read(long address) {
        int index = parser.getIndex(address);
        Block[] set = data[index];
        int tag = parser.getTag(address);
        Block hit = checkHit(set, tag);
        if(hit != null) {
            // done; return the data
            hits++;
            return;
        }
        misses++;

        // cache miss, first need to read in data from next level
        nextLevel.read(address);
        // now need to find a place to put this line

        // first look for an unoccupied line
        Block unoccupied = checkUnoccupied(set);
        if(unoccupied != null) {
            unoccupied.tag = tag;
            unoccupied.valid = true;
            return;
        }

        // no unoccupied lines, evict a line from this set
        Block evictee = findEvictee(set);
        signalEviction(evictee, index);
        undirty(evictee, address);
        evictee.tag = tag;
    }

    public void write(long address) {
        int index = parser.getIndex(address);
        Block[] set = data[index];
        int tag = parser.getTag(address);
        Block hit = checkHit(set, tag);
        if(hit != null) {
            hits++;
            hit.dirty = true;
            return;
        }
        misses++;
        // cache miss, read in missing line from next level
        nextLevel.read(address);
        // look for an unoccupied block in the set
        Block unoccupied = checkUnoccupied(set);
        if(unoccupied != null) {
            // block is unoccupied, put the retrieved line here
            unoccupied.tag = tag;
            unoccupied.valid = true;
            // write the value to the correct offset
            unoccupied.dirty = true;
            return;
        }

        // no unoccupied blocks, must evict an occupied one
        Block evictee = findEvictee(set);
        signalEviction(evictee, index);
        undirty(evictee, address);
        evictee.tag = tag;
        // write the retrieved line to the evicted one's spot
        evictee.dirty = true;
    }

    public double hitRatio() {
        return (double) hits / (double) (hits + misses);
    }

    public int getHits() {
        return hits;
    }

    public int getMisses() {
        return misses;
    }

    public int getAccesses() {
        return hits + misses;
    }

    private void evict(long address) {
        int index = parser.getIndex(address);
        int tag = parser.getTag(address);
        Block[] set = data[index];
        Block evictee = checkHit(set, tag);
        if(evictee != null) {
            undirty(evictee, address);
            evictee.tag = 0;
            evictee.valid = false;
        }
    }

    private void signalEviction(Block evictee, int index) {
        if(prevLevel.size() > 0) {
            for(Cache c : prevLevel) {
                c.evict(parser.reconstructAddress(evictee.tag, index));
            }
        }
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
                return block;
            }
        }
        // cache miss
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

    private void undirty(Block b, long address) {
        if(b.dirty) {
            // need to write evictee the next level
            nextLevel.write(address);
            b.dirty = false;
        }
    }

    public double energyConsumption() {
        double activeEnergy = activeTime * ACTIVE_POWER + ACCESS_ENERGY * (reads + writes);
        double idleEnergy = idleTime * IDLE_POWER;
        return activeEnergy + idleEnergy;
    }
}
