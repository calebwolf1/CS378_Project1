public class DRAM implements Buffer {
    public int accessEnergy;

    private final double ACTIVE_POWER = 4;  // J/s
    private final double IDLE_POWER = 0.8;
    private long accesses;

    public DRAM() {

    }

    public int getAccessEnergy() {
        return accessEnergy;
    }

    public double read(long address, int indicator) {
        accesses++;
        this.accessEnergy += 200640;
        return 50;
    }

    public double write(long address, int indicator) {
        accesses++;
        this.accessEnergy += 200000;
        return 5;
    }

    public long getAccesses() {
        return accesses;
    }

    public void addAccessEnergy(int increment) {
        accessEnergy += increment;
    }

    /**
     * Calculates the energy consumption of the DRAM
     * @return DRAM energy consumption in Joules
     */
    public double energyConsumption(long totalCycles) {
        double accessOverheadEnergy = 640e-9;  // const, Joules
        double secPerNano = 1e-9; // const
        double timePerAccess = 50 * secPerNano; // const, time per access in seconds
        double timeActive = timePerAccess * accesses;
        double timePerCycle = 0.5 * secPerNano;  // const
        double totalRuntime = totalCycles * timePerCycle;

        double timeIdle = totalRuntime - timeActive;
        double totalOverheadEnergy = accessOverheadEnergy * accesses;
        double activeEnergy = timeActive * ACTIVE_POWER + totalOverheadEnergy;
        double idleEnergy = timeIdle * IDLE_POWER;
        return activeEnergy + idleEnergy;
    }
}
