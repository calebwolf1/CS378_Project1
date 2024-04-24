public class Main {
    public static void main(String[] args) {
        assert args.length == 1;
        DineroReader scanner = new DineroReader("traces/" + args[0]);
        Simulator sim = new Simulator();

        while(scanner.hasNextTrace()) {
            Trace t = scanner.nextTrace();
            sim.acceptTrace(t);
        }

        sim.printHitRatios();
        sim.printEnergyConsumption();
    }
}
