package dev.dvinnik.applitools.testzeus.base;

import java.util.concurrent.TimeUnit;


import dev.dvinnik.applitools.testzeus.utils.PropertyKey;
import dev.dvinnik.applitools.testzeus.utils.TestPropertiesUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import testzeus.base.HTTPClientWrapper;

public class BaseTest {
	private static final String TOKEN_GRANT_URL = "/services/oauth2/token?grant_type=password";

	// Test Properties
	protected static final String USERNAME = TestPropertiesUtil.getInstance().getProperty(PropertyKey.USERNAME);
	protected static final String PASSWORD = TestPropertiesUtil.getInstance().getProperty(PropertyKey.PASSWORD);
	protected static final String SECURITY_TOKEN = TestPropertiesUtil.getInstance().getProperty(PropertyKey.SECURITY_TOKEN);

	protected static final String HOME_URL = TestPropertiesUtil.getInstance().getProperty(PropertyKey.HOME_URL);

	private static final String CONSUMER_KEY = TestPropertiesUtil.getInstance().getProperty(PropertyKey.CONSUMER_KEY);
	private static final String CONSUMER_SECRET = TestPropertiesUtil.getInstance().getProperty(PropertyKey.CONSUMER_SECRET);


	protected static WebDriver driver;

	@BeforeEach
	public void setup() {
		setupDriver();
		authenticateUser();
	}

	private void setupDriver(){
		if (driver == null) {
			driver = WebDriverManager.chromedriver().create();
			driver.manage().deleteAllCookies();
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			driver.manage().window().maximize();
		}
	}

	private void authenticateUser() {
		HTTPClientWrapper.SFLogin_API(HOME_URL, TOKEN_GRANT_URL, CONSUMER_KEY, CONSUMER_SECRET,
				USERNAME, PASSWORD + SECURITY_TOKEN);
	}

	@AfterEach
	public void tearDown() {
		try {
			driver.close();
			driver.quit();
			driver = null;
		} catch (Exception e) {
			driver = null;
		}
	}


}
