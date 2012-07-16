import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {

  public static boolean gotSIGINT = false;

  public static void main(final String[] args) throws IOException {

    if (args.length == 0) {
      System.err.println("Usage: java Main <greedy | greedier> [<file>]");
    }

    // Add hook for
    Runtime.getRuntime().addShutdownHook(new Thread() {
        @Override
        public void run() {
	  gotSIGINT = true;
          System.out.println("Shutdown hook ran!");
	  try {
	    Thread.sleep(10000);
	  } catch(InterruptedException e) { System.err.println("interrupted"); }
        }
      });

    Scanner scan = new Scanner(System.in);
    if (args.length > 1) {
      scan = new Scanner(new File(args[1]));
    }
    String map = "";
    while(scan.hasNext()) {
      map += scan.nextLine() + System.getProperty("line.separator");
    }

    Board b = new Board(map);
    System.out.println(b);
    Skynet superSky = null;
    if (args[0].equalsIgnoreCase("greedy")) {
      superSky = new Skynet.GreedySkynet(map);
    } else if (args[0].equalsIgnoreCase("greedier")) {
      superSky = new Skynet.GreedierSkynet(map);
    } else {
      System.err.println("invalid option.");
      System.exit(0);
    }
    System.out.println(superSky.plan());
    System.out.println("Score: " + superSky.score());
    System.out.println(superSky.getBoard());

    /*while (true) {
      try {
      Thread.sleep(1000);
      } catch (InterruptedException e) {
      System.out.println("interrupted: " + e);
      }
      }*/


  }
}
