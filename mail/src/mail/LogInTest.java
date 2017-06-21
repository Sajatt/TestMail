package mail;

import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

import dataprovider.Data;
import pages.Draft;
import pages.MailPage;
import pages.MainPage;
import pages.Sent;

public class LogInTest {

	// public LogInTest(WebDriver driver) {
	// super(driver);
	//
	// }

	static WebDriver driver;
	String address;
	String subject;
	String body;
	String[] line;
	private static final String FILE_URL = "C:\\Users\\Anna_Ivanova\\workspace\\mail\\src\\resourse\\mail_template.txt";
	private static final String MAIL_RU_URL = "https://mail.ru";
	public static final By LOGOUT_LINK_LOCATOR = By.id("PH_logoutLink");

	@BeforeClass
	public void setUp() {

		// initWebdriver and open start url
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setBrowserName("firefox");
		capabilities.setPlatform(Platform.WINDOWS);
		try {
			driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), capabilities);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		driver.get(MAIL_RU_URL);

		// driver = new FirefoxDriver();
		/// driver.get(MAIL_RU_URL);
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);

	}

	@SuppressWarnings("static-access")
	@Test
	public void loginTest() {

		MainPage page = PageFactory.initElements(driver, MainPage.class);
		page.logIn();
		Assert.assertTrue(page.isElementPresent(LOGOUT_LINK_LOCATOR));
		System.out.println("Sucsessfully loged in");
	}

	@Test(dataProvider = "mailData", dependsOnMethods = { "loginTest" })
	public void writeLetterTest(Data data) throws InterruptedException {

		MailPage mail = PageFactory.initElements(driver, MailPage.class);
		mail.writeLetter();
		mail.writeAddress(data);
		mail.writeSubject(data);
		mail.writeBody(data);
		mail.saveLetter();
		System.out.println("The letter was written &saved sucsessfully");
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		Draft draft = PageFactory.initElements(driver, Draft.class);
		driver.get(draft.DRAFTS_URL);
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		String subj = data.getSubject();
		String address = data.getAddress();
		String body = data.getBody();

		Assert.assertTrue(!(driver.findElements(By.xpath("//div[contains(text(),'" + subj + "')]")).isEmpty()),
				"Can`t find subject");
		Assert.assertTrue(!(driver.findElements(By.xpath("//div[contains(text(),'" + address + "')]")).isEmpty()),
				"Can`t find address");
		Assert.assertTrue(
				!(driver.findElements(By.xpath("//span[contains(text(),'" + body.substring(0, 20) + "')]")).isEmpty()),
				"Can`t find inner text");
		System.out.println("The letter is in the draft box");

	}

	@Test(dataProvider = "mailData", dependsOnMethods = { "writeLetterTest" })
	public void sendLetterTest(Data data) {
		String subj = data.getSubject();
		String address = data.getAddress();
		String body = data.getBody();
		Draft draft = PageFactory.initElements(driver, Draft.class);
		draft.clickDraft(data);
		MailPage mail = PageFactory.initElements(driver, MailPage.class);
		mail.sendLetter();
		Sent sent = PageFactory.initElements(driver, Sent.class);
		driver.get(sent.URL_SENT);
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

		Assert.assertTrue(!(driver.findElements(By.xpath("//div[contains(text(),'" + subj + "')]")).isEmpty()),
				"Can`t find subject");
		Assert.assertTrue(!(driver.findElements(By.xpath("//div[contains(text(),'" + address + "')]")).isEmpty()),
				"Can`t find address");
		Assert.assertTrue(
				!(driver.findElements(By.xpath("//span[contains(text(),'" + body.substring(0, 20) + "')]")).isEmpty()),
				"Can`t find inner text");
		System.out.println("The letter was sucsessfuly sent");

	}

	@AfterClass
	public void cleanUpAndLogOut() {
		System.out.println("Cleaning sent box...");
		Sent sent = PageFactory.initElements(driver, Sent.class);
		sent.cleanUpSent();
		System.out.println("Sent box is empty");
		System.out.println("Cleaning draft box...");
		Draft draft = PageFactory.initElements(driver, Draft.class);
		draft.cleanUpDraft();
		System.out.println("Draft box is empty");
		driver.findElement(By.xpath("//a[@id='PH_logoutLink']")).click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

		Assert.assertTrue(Draft.isElementPresent(By.xpath("//a[@id= 'PH_regLink' ]")), "Wrong page");
		System.out.println("Sucsessfully loged out");
		driver.close();

	}

	@SuppressWarnings("finally")
	@DataProvider(name = "mailData")
	public Object[][] readFromFile() {

		List<Data> dataList = new ArrayList<Data>();
		BufferedReader readFromFile = null;
		try {
			readFromFile = new BufferedReader(new FileReader(FILE_URL));
			while ((line = readFromFile.readLine().split(";")) != null) {

				Data newData = new Data(line[0], line[1], line[2]);
				dataList.add(newData);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (readFromFile != null)
				try {
					readFromFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			Object[][] dataArray = new Object[dataList.size()][1];

			for (int i = 0; i < dataList.size(); i++) {
				dataArray[i][0] = dataList.get(i);

				dataArray.toString();

			}
			return dataArray;
		}
	}
}
