package dev.dvinnik.applitools.testzeus.base;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


import dev.dvinnik.applitools.testzeus.pageobjects.AccountListPage;
import dev.dvinnik.applitools.testzeus.pageobjects.LightningLoginPage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;


import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import testzeus.base.GetSFApps;
import testzeus.base.HTTPClientWrapper;
import testzeus.base.PageBase;


public class BaseTest {

	public static final Logger logger = LogManager.getLogger(BaseTest.class);

	protected static WebDriver driver;

	protected static Actions action;
	protected LightningLoginPage lightningloginpage;
	protected AccountListPage accountlistpage;

	public static String SFBaseURL;

	protected static PageFactory pageFactory = null;
	protected URL huburl = null;// Setup GRID hub URL here or from properties file

	public static String env;
	public static String SFUserId;
	public static String SFPassword;
	// Credentials for using the Connected app and accessing data via REST API
	final String SFAPIUSERNAME_UAT = "test10zeus@gmail.com";

	final String SFAPITOKEN_UAT = "yourtoken";

	final String SFAPIPASSWORDSTRING_UAT = "yourpassword";

	// password needs to be appended with token as per : //
	// https://stackoverflow.com/questions/38334027/salesforce-oauth-authentication-bad-request-error

	final String SFAPIPASSWORD_UAT = SFAPIPASSWORDSTRING_UAT + SFAPITOKEN_UAT;

	final String SFAPILOGINURL_UAT = "https://testzeus2-dev-ed.my.salesforce.com";

	final String SFAPIGRANTSERVICE = "/services/oauth2/token?grant_type=password";
	// Client id is the consumerkey for the connected app
	final String SFAPICLIENTID_UAT = "yourclientID";

	// Client secret is the consumer secret protected static final String
	final String SFAPICLIENTSECRET_UAT = "yourclientsecret";

	@BeforeSuite(alwaysRun = true)
	@Parameters({ "browserType" })
	public void setupWebDriver(@Optional("chrome") String browserType) throws IOException {
		// Fetch all the test data like URL, UserID and Passwords from config.json file
		readConfigJsonFile();

		if ((driver == null)) {
			logger.info("setupWebDriver()");
			driver = WebDriverFactory.createInstance(huburl, browserType);
			action = new Actions(driver);
			pageFactory = new PageFactory(driver);

			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			driver.manage().window().maximize();
		}
	}

	private void readConfigJsonFile() {
		{ // Here the commonly used Test data is read from the config.json file
			// UAT stands for User Acceptance Testing and is a short hand for the
			// environment name. Similarly it can be PROD, Sandbox etc

			try {

				String sPath = new File(".").getCanonicalPath();
//				Log.info("Path: " + sPath);
				File jsonFile = new File(sPath + File.separator + "src" + File.separator + "main" + File.separator
						+ "resources" + File.separator + "config.json");
				String salesforce_Lighteningenv = "Salesforce_Lightening";

//				Log.info("Reading Environment variables from json file");

				env = (env == null) ? salesforce_Lighteningenv : env;

				SFBaseURL = (String) JsonPath.read(jsonFile, "$.environments." + env + ".UAT.homePage");
				SFUserId = (String) JsonPath.read(jsonFile, "$.environments." + env + ".UAT.userId");
				SFPassword = (String) JsonPath.read(jsonFile, "$.environments." + env + ".UAT.passwd");

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	@BeforeTest(alwaysRun = true)
	public void cleanTestSetup() {
		driver.manage().deleteAllCookies();
	}

	@BeforeClass(alwaysRun = true)
	protected void setUp() throws Exception {

		// Setting up email utils object
//EmailUtils emu = new EmailUtils();
		// Setting up Login for SF API requests
		HTTPClientWrapper.SFLogin_API(SFAPILOGINURL_UAT, SFAPIGRANTSERVICE, SFAPICLIENTID_UAT, SFAPICLIENTSECRET_UAT,
				SFAPIUSERNAME_UAT, SFAPIPASSWORD_UAT);
		// Set up the common page objects and fetch the data to be used in most
		// of the tests using Reflections concept

		accountlistpage = (AccountListPage) pageFactory.getPageObject(AccountListPage.class.getName());

		// Below is commented code as reference for reading data from properties file
		// SFUserId = (String) getStaticData().get("SFLightning.userid");
		// SFPassword = (String) getStaticData().get("SFLightning.password");

	}

	@AfterMethod(alwaysRun = true)
	public void tearDownandCaptureScreenShot(Method method, ITestResult result) { // Method for taking screenshots on
																					// failure of the test case
		if (ITestResult.FAILURE == result.getStatus()) {
			try {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
				String currentdatetime = simpleDateFormat.format(new Date());
				File source = captureScreenShot();
				FileUtils.copyFile(source, new File(System.getProperty("user.dir")
						+ "/target/surefire-reports/FailedScreenShots/" + result.getName() + currentdatetime + ".png"));
				Reporter.log("Screenshot taken");
			} catch (Exception e) {

				Reporter.log("Exception while taking screenshot " + e.getMessage());
			}
		}
		logger.info("*************");
		logger.info("Ending Test  ---->" + method.getName());

	}

	public File captureScreenShot() {
		return new PageBase(driver).takeScreenshot();
	}

	@AfterClass(alwaysRun = true)
	public void deleteAllCookies() {
		// Logging out of the Salesforce APIs
		HTTPClientWrapper.SFLogout_API();

		// Handling windows after executing each class from Suite
		try {

			String originalHandle = driver.getWindowHandle();

			for (String handle : driver.getWindowHandles()) {
				if (!handle.equals(originalHandle)) {
					driver.switchTo().window(handle);
					driver.close();
				}
			}

			driver.switchTo().window(originalHandle);

		} catch (Exception e) {

			Reporter.log("Error while closing child windows" + e.getMessage());

		}

		logger.info("Clearing all browser cookies...");
		driver.manage().deleteAllCookies();

	}

	@AfterSuite(alwaysRun = true)
	public void quitWebDrivers() {
		logger.info("terminateWebDrivers()");
		try {
			driver.close();
			driver.quit();
			// Setting driver to null for stopping persistent use of driver
			// session across browsers
			driver = null;
		} catch (Exception e) {
			// Sometime driver.quit() causes exception and not nullifying the
			// driver obj. Which stops next successful browser launch
			driver = null;
			logger.error("Error quitting driver");
			e.printStackTrace();
		}
	}


}
