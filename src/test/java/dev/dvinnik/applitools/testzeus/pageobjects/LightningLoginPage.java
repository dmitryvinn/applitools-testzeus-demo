package dev.dvinnik.applitools.testzeus.pageobjects;

import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LightningLoginPage extends testzeus.base.SFPageBase {

	@FindBy(id = "username")
	@CacheLookup
	private WebElement username;

	@FindBy(id = "password")
	@CacheLookup
	private WebElement password;

	@FindBy(id = "Login")
	@CacheLookup
	private WebElement login_button;

	@FindBy(xpath = "")
	private List<WebElement> sessionErrorMessage;

	public LightningLoginPage(WebDriver webDriver) {
		super(webDriver);
		PageFactory.initElements(driver, this);// Creates instance for all web elements
	}

	/**
	 *
	 * @author Robin 28-9-2021
	 * @return the SF Lightning page class instance.
	 * @throws InterruptedException
	 */
	public void login(String userid, String passwordtext) throws InterruptedException {

		Thread.sleep(8000);

		explicitWait(username, 40);

		username.sendKeys(userid);
		password.sendKeys(passwordtext);
		safeClick(login_button);

		Thread.sleep(5000);
		try {

			Alert alert = driver.switchTo().alert();
			String alertText = alert.getText();
			System.out.println("Alert data: " + alertText);
			alert.accept();

		} catch (NoAlertPresentException e) {

		}

		waitForSFPagetoLoad();

	}

	public void applauncher(String appname) throws InterruptedException {
		Thread.sleep(5000);
		String accountappurl = getURL(appname);
		System.out.println("account URL is" + accountappurl);
		String cleanurl = accountappurl.replace("[\"", "").replace("\"]", "");
		System.out.println("Navigating to App URL as : " + cleanurl);
		openHomepage(cleanurl + "?eptVisible=1");

		waitForSFPagetoLoad();

	}

}
