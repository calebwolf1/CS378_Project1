public class Simulator {
    private long cycles;
    private DRAM dram;
    private Cache l1I, l1D, l2;
    private double time;
    private final int associativity;

    public Simulator(int associativity) {
        this.associativity = associativity;
        dram = new DRAM();
        l1I = new Cache(1, 64, 32_768, 1, 0.5, 0.5e-9, 0);
        l1D = new Cache(1, 64, 32_768, 1, 0.5, 0.5e-9, 0);
        l2  = new Cache(associativity, 64, 262_144, 2, 0.8,5e-9, 5e-12);
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
            case INSN_FETCH -> time += l1I.read(t.ADDRESS, 0);
            case MEM_READ -> time += l1D.read(t.ADDRESS, 1);
            case MEM_WRITE -> time += l1D.write(t.ADDRESS, 2);
        }
    }

    public void printHitRatios() {
//        System.out.println("L1I hit/miss ratio: " + l1I.hitRatio());
//        System.out.println("L1D hit/miss ratio: " + l1D.hitRatio());
//        System.out.println("L2 hit/miss ratio: " + l2.hitRatio());

        System.out.println("L1I accesses: " + l1I.getAccesses());
        System.out.println("L1I misses: " + l1I.getMisses());
        System.out.println("L1D accesses: " + l1D.getAccesses());
        System.out.println("L1D misses: " + l1D.getMisses());
        System.out.println("L2 accesses: " + l2.getAccesses());
        System.out.println("L2 misses: " + l2.getMisses());
        System.out.println("DRAM accesses: " + dram.getAccesses());
    }

    public double energyConsumption(int indicator) {
        double energy = 0;
        double idle = 0;
        double activeTime = 0;
        if (indicator == 0) {
            energy = l1I.accessEnergy;
            activeTime =
                    0.5 * (l1I.hits + l1I.misses - l1I.dirtyEvict + l2.hits * 2 + l2.misses * 2 - l2.evict) + 4.5 * (l1I.misses + l1I.dirtyEvict);
            idle = 500 * (time - activeTime);
        } else if (indicator == 1) {
            energy = l1D.accessEnergy;
            activeTime =
                    0.5 * (l1D.hits + l1D.misses * 2 - l1I.dirtyEvict + l2.hits * 2+ + l2.misses * 2 - l2.evict) + 4.5 * (l1D.misses + l1D.dirtyEvict);
            idle = 500 * (time - activeTime);
        } else if (indicator == 2) {
            energy = l2.accessEnergy;
            activeTime = 5 * (l1D.misses + l1D.dirtyEvict * 2 + l2.misses * 4 - l2.dirtyEvict) + 4.5 * l2.hits + 45 * l2.dirtyEvict;
            idle = 800 * (time - activeTime);
        } else if (indicator == 3) {
            energy = dram.accessEnergy;
            activeTime = 50 * (l2.hits + l2.misses * 2 - l2.dirtyEvict) + 45 * l2.misses;
            idle = 800 * (time - activeTime);
        }
        energy += idle;
        return energy;
    }

    public void printEnergyConsumption() {
        System.out.println("Total energy consumption: " + totalEnergyConsumption() + " pJ");
        System.out.println("Energy consumption for L1 Instruction: " + energyConsumption(0) + " pJ");
        System.out.println("Energy consumption for L1 Data: " + energyConsumption(1) + " pJ");
        System.out.println("Energy consumption for L2: " + energyConsumption(2) + " pJ");
        System.out.println("Energy consumption for DRAM: " + energyConsumption(3) + " pJ");
    }

    public double totalEnergyConsumption() {
        return energyConsumption(0) + energyConsumption(1) +
                energyConsumption(2) + energyConsumption(3);
    }

    public void printResult() {
        System.out.println("Using associativity " + associativity);
        System.out.println("Total time: " + time + " ns");
        printEnergyConsumption();
        printHitRatios();
        System.out.println();
    }
}