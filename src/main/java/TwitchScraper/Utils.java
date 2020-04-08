package TwitchScraper;

public class Utils {

    public static void wait(int waitTime){
        try {
            Thread.sleep(waitTime);
        }catch (InterruptedException e){
        }
    }


}
