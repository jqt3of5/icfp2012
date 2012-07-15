import java.io.File;
import java.util.Queue;
import java.util.Scanner;


public class SkynetTester {

  public static void main(final String[] args) throws Exception {

    Scanner scan = new Scanner(System.in);
    if (args.length > 0) {
      scan = new Scanner(new File(args[0]));
    }
    String map = "";
    while(scan.hasNext()) {
      map += scan.nextLine() + System.getProperty("line.separator");
    }

    final Board b = new Board(map);
    System.out.println(b);
    final Skynet superSky = new Skynet.GreedierSkynet(map);
    final Queue<Point> nearestPoints = superSky.findClosestLambdas();

    System.err.println(nearestPoints);

    /*System.out.println(superSky.plan());
    System.out.println("Score: " + superSky.score());
    System.out.println(superSky.getBoard());*/
  }
}