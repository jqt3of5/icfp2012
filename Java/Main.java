import java.util.*;
import java.io.*;

public class Main {

  public static void main(String[] args) {
    // Add hook for
    Runtime.getRuntime().addShutdownHook(new Thread() {
	@Override
	public void run() {
	  // TODO(jack): replace
	  System.out.println("Shutdown hook ran!");
	}
      });

    /*while (true) {
      try {
	Thread.sleep(1000);
      } catch (InterruptedException e) {
	System.out.println("interrupted: " + e);
      }
      }*/


  }
}