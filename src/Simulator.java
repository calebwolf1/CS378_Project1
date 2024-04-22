public class Simulator {
    private long cycles;
    private DRAM dram;
    private Cache l1I, l1D, l2;

    public Simulator() {
        dram = new DRAM();
        l1I = new Cache(1, 64, 32_000, 1, 0.5, 0.5e-9, 0);
        l1D = new Cache(1, 64, 32_000, 1, 0.5, 0.5e-9, 0);
        l2 = new Cache(4, 64, 256_000, 2, 0.8,5e-9, 5e-12);
    }

    /**
     * Simulates the next memory operation passed to it.
     */
    public void acceptTrace(Trace t) {
        cycles++; // 1 cycle to read in trace

//        l1I.read();
//        l2.read();
//        dram.read();
//        cycles += 100;
    }

    /**
     * Returns the total energy consumption of the simulator in Joules.
     * @return the energy consumption
     */
    public double energyConsumption() {
        return dram.energyConsumption(cycles);
    }


}
