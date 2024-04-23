public class Simulator {
    private long cycles;
    private DRAM dram;
    private Cache l1I, l1D, l2;

    public Simulator() {
        dram = new DRAM();
        l1I = new Cache(1, 64, 32_768, 1, 0.5, 0.5e-9, 0);
        l1D = new Cache(1, 64, 32_768, 1, 0.5, 0.5e-9, 0);
        l2  = new Cache(8, 64, 262_144, 2, 0.8,5e-9, 5e-12);
        l1I.setNextLevel(l2);
        l1D.setNextLevel(l2);
        l2.setNextLevel(dram);
        l2.addPrevLevelCache(l1D);
        l2.addPrevLevelCache(l1I);
    }

    /**
     * Simulates the next memory operation passed to it.
     */
    public void acceptTrace(Trace t) {
        cycles++; // 1 cycle to read in trace
        switch(t.OP) {
            case INSN_FETCH -> l1I.read(t.ADDRESS);
            case MEM_READ -> l1D.read(t.ADDRESS);
            case MEM_WRITE -> l1D.write(t.ADDRESS);
        }
    }

    public void printHitRatios() {
        System.out.println("L1I hit/miss ratio: " + l1I.hitRatio());
        System.out.println("L1D hit/miss ratio: " + l1D.hitRatio());
        System.out.println("L2 hit/miss ratio: " + l2.hitRatio());
        System.out.println("L1I accesses: " + l1I.getAccesses());
        System.out.println("L1D accesses: " + l1D.getAccesses());
        System.out.println("L2 accesses: " + l2.getAccesses());
    }

    /**
     * Returns the total energy consumption of the simulator in Joules.
     * @return the energy consumption
     */
    public double energyConsumption() {
        return dram.energyConsumption(cycles);
    }


}
