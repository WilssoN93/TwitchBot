package TwitchScraper;

public class Main {

    private String chromeDriver;
    private String username;
    private String password;

    public Main(String chromeDriver,String username,String password){
        this.chromeDriver = chromeDriver;
        this.password = password;
        this.username = username;
    }

    public static void main(String[] args) {

        String chromeDriver = args[0];
        String username = args[1];
        String password = args[2];

        Main main = new Main(chromeDriver, username, password);
        main.run();

    }


    private void run(){
        System.setProperty("webdriver.chrome.driver", chromeDriver);
        Twitch twitch = new Twitch(username,password);
        twitch.run();
    }

}
