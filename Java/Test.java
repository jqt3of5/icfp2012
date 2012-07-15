import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Test<E extends Skynet> {
  private ArrayList<String> maps;
  private ArrayList<Integer> scores;
  
  public Test(ArrayList<String> testingMaps) {
    maps = testingMaps;
    scores = new ArrayList<Integer>();
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
  
  public void main(String args[]) {
    
  }
  
}
