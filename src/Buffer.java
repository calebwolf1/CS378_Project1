public interface Buffer {
    double read(long address, int indicator);
    double write(long address, int indicator);
    void addAccessEnergy(int increment);
}
