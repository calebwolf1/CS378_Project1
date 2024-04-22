import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DineroReader {
    private Scanner scanner;

    public DineroReader(String fileName) {
        try {
            scanner = new Scanner(new File(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean hasNextTrace() {
        return scanner.hasNextLine();
    }

    public Trace nextTrace() {
        return new Trace(scanner.nextLine());
    }

    public void close() {
        scanner.close();
    }
}
