package dev.dvinnik.applitools.testzeus;


import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import dev.dvinnik.applitools.testzeus.base.BaseTest;
import dev.dvinnik.applitools.testzeus.utils.PropertyKey;
import dev.dvinnik.applitools.testzeus.utils.TestPropertiesUtil;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import testzeus.base.SFPageBase;

public class AccountsApplitoolsTest extends BaseTest {

    private static final String TEST_ACCOUNT_NAME = "Test Account";
    private static final String APPLITOOLS_API_KEY = TestPropertiesUtil.getInstance().getProperty(PropertyKey.APPLITOOLS_API_KEY);

    private static Configuration config;
    private static VisualGridRunner runner;

    private Eyes eyes;


    @BeforeAll
    public static void setUpConfigAndRunner() {
        runner = new VisualGridRunner(new RunnerOptions().testConcurrency(1));

        config = new Configuration();
        config.setApiKey(APPLITOOLS_API_KEY);

        final BatchInfo batch = new BatchInfo("Salesforce Accounts Page Tests with TestZeus");
        config.setBatch(batch);



        config.addBrowser(1024, 768, BrowserType.CHROME);
    }


    @BeforeEach
    public void openBrowserAndEyes(TestInfo testInfo) {
        eyes = new Eyes(runner);
        eyes.setConfiguration(config);

        eyes.open(
                driver,
                "TestZeus Demo",
                testInfo.getDisplayName());
    }

    @Test
    public void findTestAccount() throws Exception {

        // Arrange
        final SFPageBase salesforcePage = new SFPageBase(driver);

        salesforcePage.openHomepage(HOME_URL);
        salesforcePage.maximize();

        driver.findElement(By.id("username")).sendKeys(USERNAME);
        driver.findElement(By.id("password")).sendKeys(PASSWORD);

        final WebElement loginButton = driver.findElement(By.id("Login"));
        salesforcePage.safeClick(loginButton);

        // Act
        salesforcePage.appLauncher("Account");

        final WebElement testAccountItem = driver.findElement(By.xpath(String.format("//a[@title='%s']", TEST_ACCOUNT_NAME)));
        salesforcePage.safeClick(testAccountItem);

        // Assert
        eyes.check(Target.window().fully().withName("Test Account").layout());
}

    @AfterEach
    public void cleanUpTest() {
        // Close Eyes to tell the server it should display the results.
        eyes.closeAsync();
    }
}
