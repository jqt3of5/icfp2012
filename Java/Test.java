import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class Test<E extends Skynet> {
  private ArrayList<String> maps;
  private ArrayList<Integer> scores;
  
  public Test() {
    maps = new ArrayList<String>();
    scores = new ArrayList<Integer>();
  }
  
  public void readMaps() throws IOException {
    Scanner cmd = new Scanner(new InputStreamReader(Runtime.getRuntime().exec("ls ../maps").getInputStream()));
    while(cmd.hasNext()) {
      File fileName = new File("../maps/" + cmd.next());
      Scanner inFile = new Scanner(fileName);
      String newMap = "";
      while(inFile.hasNext()) {
        newMap += inFile.nextLine() + System.getProperty("line.separator");
      }
      addMap(newMap);
    }
   for(String map: maps) {
    System.out.print(map);
   }
  }
  
  public void addMap(String map) {
    maps.add(map);
  }
  
  public void runTests(Class SkynetClass) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    for(String map: maps) {
      Timer timer = new Timer();
      long delay = 150*1000;
      
      Skynet planner = (Skynet) SkynetClass.getDeclaredConstructor(Map.class).newInstance(map);

      timer.schedule(new Reminder(), delay);
      
      
      planner.plan();
      
      scores.add(planner.score());
    }
    
    int overall = 0;
    for(int score: scores) {
      overall += score;
    }
    
    System.out.println("Average score: " + overall/scores.size());
  }
  
  class Reminder extends TimerTask {
    @Override
    public void run() {
      Main.gotSIGINT = true;
    } 
  }
  
  public static void main(String args[]) throws IOException {
    Test<Skynet> t = new Test<Skynet>();
    
    t.readMaps();
  }
  
}
