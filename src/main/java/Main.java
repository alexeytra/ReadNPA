import classes.Reader;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        File file = new File("NPA.xls");
        if (file.exists() && file.delete()) {
            System.out.println("Удален");
        } else {
            System.out.println("Не удален");
        }
        String path = "npa.txt";
        Reader reader = new Reader(path);
        reader.ReadFile();
    }
}
