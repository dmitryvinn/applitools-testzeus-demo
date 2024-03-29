package dev.dvinnik.applitools.testzeus;


import dev.dvinnik.applitools.testzeus.base.BaseTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import testzeus.base.SFPageBase;

public class AccountsTest extends BaseTest {

    private static final String TEST_ACCOUNT_NAME = "Test Account";

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
        driver.findElement(By.xpath("//p[text()='Account Owner']"));
    }
}
