package dev.dvinnik.applitools.testzeus;


import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import testzeus.base.HTTPClientWrapper;
import testzeus.base.SFPageBase;

public class AccountCreationViaUI {
    @Test
    public void createAccount() throws Exception {
        // Credentials for using the Connected app and accessing data via REST API
        final String SFAPIUSERNAME_UAT = "dvinnik@applitools.com";

        final String SFAPITOKEN_UAT = "bm0h8hSyTuSbr0JLT5rytH23U";

        final String SFAPIPASSWORDSTRING_UAT = "applitools1";

        // password needs to be appended with token as per : //
        // https://stackoverflow.com/questions/38334027/salesforce-oauth-authentication-bad-request-error

        final String SFAPIPASSWORD_UAT = SFAPIPASSWORDSTRING_UAT + SFAPITOKEN_UAT;

        final String SFAPILOGINURL_UAT = "https://applitools2-dev-ed.develop.my.salesforce.com";

        final String SFAPIGRANTSERVICE = "/services/oauth2/token?grant_type=password";
        // Client id is the consumerkey for the connected app
        final String SFAPICLIENTID_UAT = "3MVG9ux34Ig8G5eo.ZPLJ57b4ck5hdKoJm8ROb7J5smZX_Q_xSBSnPQe9xoUtqyX6zF7wENoN2SSRlF5BZ9QX";

        // Client secret is the consumer secret protected static final String
        final String SFAPICLIENTSECRET_UAT = "2A63F873DEDB7646A334FD8D504ABFD4D81DF204844D3D530491CE1603B76C6B";

        // Setting up Login for SF API requests
        HTTPClientWrapper.SFLogin_API(SFAPILOGINURL_UAT, SFAPIGRANTSERVICE, SFAPICLIENTID_UAT, SFAPICLIENTSECRET_UAT,
                SFAPIUSERNAME_UAT, SFAPIPASSWORD_UAT);

        //Sample usage of BoniGarcia's webdriver manager
        WebDriverManager.chromedriver().setup();

        ChromeDriver driver = new ChromeDriver();

        //Create a new instance of the SFPageBase class
        SFPageBase pb = new SFPageBase(driver);

        // Use methods from TestZeus as below for Navigation to login page
        pb.openHomepage(SFAPILOGINURL_UAT);
        pb.maximize();

        // Or Use the webdriver implementations: Example for Submitting user id,
        // password and logging in
        driver.findElement(By.id("username")).sendKeys(SFAPIUSERNAME_UAT);
        driver.findElement(By.id("password")).sendKeys(SFAPIPASSWORDSTRING_UAT);
        WebElement loginbutton = driver.findElement(By.id("Login"));

        pb.safeClick(loginbutton);
        pb.waitForSFPagetoLoad();
        pb.appLauncher("Account");

        WebElement newbutton = driver.findElement(By.xpath("//a[@title='New']"));

        pb.safeClick(newbutton);

//        // We fetch all the labels and datatype from UI API here for a certain record
        String recordid = "001Dn00000Bu7RZIAZ";
        pb.uiApiParser(recordid);
        // Form data can be passed directly on the new sObject creation screen
        pb.formValueFiller("Ac count Name", "AccountCreatedOn : " + pb.getCurrentDateTimeStamp());
        WebElement savebutton = driver.findElement(By.xpath("//button[@name='SaveEdit']"));

        pb.safeClick(savebutton);
        HTTPClientWrapper.SFLogout_API();

        driver.close();
        driver.quit();
        // Setting driver to null for stopping persistent use of driver
        // session across browsers
        driver = null;

    }
}
