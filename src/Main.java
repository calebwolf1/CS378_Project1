public class Main {
    public static void main(String[] args) {
        assert args.length == 1;
        System.out.println("TRACE FILE: " + args[0]);
        for(int i = 2; i <= 8; i *= 2) {
            DineroReader scanner = new DineroReader("traces/" + args[0]);
            Simulator sim = new Simulator(i);

            while(scanner.hasNextTrace()) {
                Trace t = scanner.nextTrace();
                sim.acceptTrace(t);
            }

            scanner.close();

//            sim.printHitRatios();
//            sim.printEnergyConsumption();

            sim.printResult();
        }
        System.out.println();
        System.out.println();
    }
}
