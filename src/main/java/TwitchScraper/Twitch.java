package TwitchScraper;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class Twitch {

    private ChromeDriver driver;
    private String username;
    private String password;
    private final String LOGIN_BUTTON = "//div[@class='tw-mg-t-2']/button";
    private final String USERNAME_INPUT = "//input[@id='login-username']";
    private final String PASSWORD_INPUT = "//input[@id='password-input']";
    private final String LOGIN_FORM = "//button[@class='tw-align-items-center tw-align-middle tw-border-bottom-left-radius-medium tw-border-bottom-right-radius-medium tw-border-top-left-radius-medium tw-border-top-right-radius-medium tw-core-button tw-core-button--secondary tw-inline-flex tw-interactive tw-justify-content-center tw-overflow-hidden tw-relative']";
    private final String STREAMS = "//div[@class='tw-relative']/div[@class='tw-aspect tw-aspect--align-top']";
    private final String VIEW_COUNT = "//div[@class='channel-info-bar__viewers-wrapper tw-c-text-live tw-inline-flex']//div[@class='tw-mg-l-05 tw-stat__value']";
    private final String FILTERS = "//div[@class='tw-align-items-center tw-flex tw-pd-05 tw-relative']/div[@class='tw-flex-grow-1']/div[@class='tw-flex']/div[@class='tw-flex-grow-1']";
    private final String FILTER_DROPDOWN = "//button[@class='tw-align-items-center tw-align-middle tw-border-bottom-left-radius-medium tw-border-bottom-right-radius-medium tw-border-top-left-radius-medium tw-border-top-right-radius-medium tw-core-button tw-inline-flex tw-interactive tw-justify-content-center tw-overflow-hidden tw-relative tw-select-button']";

    public Twitch(String username, String password) {
        this.driver = new ChromeDriver(getDesiredCapabilitiesConfiguration(getChromeOptionsConfiguration()));
        this.password = password;
        this.username = username;
    }


    public void run() {
        driver.get("https://www.twitch.tv/");
        this.login();
        Utils.wait(8000);
        this.getNewStream();
        while (true) {
            boolean live = true;
            try {
                driver.findElementByXPath(VIEW_COUNT);
            } catch (NoSuchElementException e) {
                live = false;
            }

            if (!live) {
                this.getNewStream();
            }else {
                System.out.println("Still Live!!");
            }
            Utils.wait(60000);
        }


    }

    private void getNewStream() {
        driver.get("https://www.twitch.tv/directory/game/VALORANT");
        Utils.wait(4000);

        WebElement dropdown = driver.findElementByXPath(FILTER_DROPDOWN);
        dropdown.click();

        Utils.wait(2000);
        List<WebElement> filters = driver.findElementsByXPath(FILTERS);
        for (WebElement filter:filters) {
            String filterName = filter.getAttribute("innerText");
            if (filterName.equals("Tittare (flest först)")){
                filter.click();
                break;
            }
        }
        Utils.wait(4000);

        List<WebElement> streams = driver.findElementsByXPath(STREAMS);
        streams.get(5).click();
    }


    private boolean login() {

       boolean cookieExists = readCookie();

        Utils.wait(2000);

        WebElement loginForm = driver.findElementByXPath(LOGIN_FORM);
        loginForm.click();

        Utils.wait(1000);


        WebElement userNameInput = driver.findElementByXPath(USERNAME_INPUT);
        userNameInput.sendKeys(username);
        WebElement passwordInput = driver.findElementByXPath(PASSWORD_INPUT);
        passwordInput.sendKeys(password);

        Utils.wait(2000);

        WebElement loginButton = driver.findElementByXPath(LOGIN_BUTTON);
        loginButton.click();

        if (!cookieExists){
            Utils.wait(60000);
        }

        writeCookie();

        return true;
    }

    private void writeCookie() {
        File file = new File("Cookie.data");
        try {
            FileWriter fileWrite = new FileWriter(file);
            BufferedWriter Bwrite = new BufferedWriter(fileWrite);

            file.delete();
            file.createNewFile();
            for (Cookie ck : driver.manage().getCookies()) {
                Bwrite.write((ck.getName() + ";" + ck.getValue() + ";" + ck.getDomain() + ";" + ck.getPath() + ";" + ck.getExpiry() + ";" + ck.isSecure()));
                Bwrite.newLine();
            }
            Bwrite.close();
            fileWrite.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean readCookie() {
        try {
            File file = new File("Cookie.data");
            FileReader fileReader = new FileReader(file);
            BufferedReader Buffreader = new BufferedReader(fileReader);
            String strline;
            while ((strline = Buffreader.readLine()) != null) {
                StringTokenizer token = new StringTokenizer(strline, ";");
                while (token.hasMoreTokens()) {
                    String name = token.nextToken();
                    String value = token.nextToken();
                    String domain = token.nextToken();
                    String path = token.nextToken();
                    Date expiry = null;


                    String val;
                    if (!(val = token.nextToken()).equals("null")) {
                        Locale locale = new Locale("sv_SE");
                        DateFormat dateFormat = new SimpleDateFormat(
                                "EEE MMM dd HH:mm:ss zzz yyyy", locale);
                        expiry = dateFormat.parse(val);
                    }
                    Boolean isSecure = Boolean.valueOf(token.nextToken());
                    Cookie ck = new Cookie(name, value, domain, path, expiry, isSecure);


                    driver.manage().addCookie(ck); // This will add the stored cookie to your current session
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private ChromeOptions getChromeOptionsConfiguration() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        // options.addArguments("--headless");
        options.addArguments("--start-maximized");
        return options;

    }

    private DesiredCapabilities getDesiredCapabilitiesConfiguration(ChromeOptions options) {
        DesiredCapabilities cap = DesiredCapabilities.chrome();
        cap.setCapability(ChromeOptions.CAPABILITY, options);
        return cap;
    }


}
