import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {

  public static void main(final String[] args) throws IOException {

    // Add hook for
    Runtime.getRuntime().addShutdownHook(new Thread() {
        @Override
        public void run() {
          // TODO(jack): replace
          System.out.println("Shutdown hook ran!");
        }
      });

    Scanner scan = new Scanner(System.in);
    if (args.length > 0) {
      scan = new Scanner(new File(args[0]));
    }
    String map = "";
    while(scan.hasNext()) {
      map += scan.nextLine() + System.getProperty("line.separator");
    }

    // Board b = new Board(map);
    // System.out.println(b);
    final Skynet superSky = new Skynet.GreedySkynet(map);
    System.out.println(superSky.plan());

    /*while (true) {
      try {
      Thread.sleep(1000);
      } catch (InterruptedException e) {
      System.out.println("interrupted: " + e);
      }
      }*/


  }
}
