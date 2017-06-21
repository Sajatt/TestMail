package pages;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import org.openqa.selenium.JavascriptExecutor;

import dataprovider.Data;

public class MailPage {
	private static String URL_MATCH = "compose";
	private static WebDriver driver;
	private static final String COMPOSE_URL = "https://e.mail.ru/compose/";
	private static final String ADDRESS_LOCATOR = "//textarea[@data-original-name='To']";
	private static final String SUBJECT_LOCATOR = "Subject";
	private static final String SEND_BUTTON_LOCATOR = "//*[@data-name='send']";
	private static final String IS_SAVED_MESSAGE = "//*[@class='b-toolbar__message']";
	private static final String SAVE_DRAFT_BUTTON_LOCATOR = "//*[@data-name='saveDraft']";
	private static final String TEXT_BOX_LOCATOR = "//*[@id='tinymce']";
	private static final String IFRAME_TEXT_FRAME_LOCATOR = "//iframe[contains(@id, 'composeEditor_ifr')]";
	protected static final String LOGOUT_LINK = "PH_logoutLink";
	@FindBy(xpath = ADDRESS_LOCATOR)
	private WebElement address;

	@FindBy(xpath = SUBJECT_LOCATOR)
	private WebElement subject;

	@FindBy(xpath = TEXT_BOX_LOCATOR)
	private WebElement textBox;

	@FindBy(xpath = IFRAME_TEXT_FRAME_LOCATOR)
	private WebElement iframeTextBox;

	@FindBy(xpath = IS_SAVED_MESSAGE)
	private WebElement savedLetterNotification;

	@FindBy(xpath = SAVE_DRAFT_BUTTON_LOCATOR)
	private WebElement saveDraftButton;

	@FindBy(xpath = SEND_BUTTON_LOCATOR)
	private WebElement sendButton;

	@FindBy(id = LOGOUT_LINK)
	private WebElement logout;

	public MailPage(WebDriver driver) {

		PageFactory.initElements(driver, this);
		this.driver = driver;
	}

	public void writeLetter() {
		driver.get(COMPOSE_URL);

		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(ADDRESS_LOCATOR)));
	}

	public void writeAddress(Data data) {
		Actions actions = new Actions(driver);

		driver.findElement(By.xpath(ADDRESS_LOCATOR)).click();
		driver.findElement(By.xpath(ADDRESS_LOCATOR)).sendKeys(data.getAddress());
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
	}

	public void writeSubject(Data data) {
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		driver.findElement(By.name(SUBJECT_LOCATOR)).click();
		driver.findElement(By.name(SUBJECT_LOCATOR)).sendKeys(data.getSubject());
	}

	public void writeBody(Data data) {
		Actions actions = new Actions(driver);
		WebElement textFrame = driver.findElement(By.xpath(IFRAME_TEXT_FRAME_LOCATOR));
		driver.switchTo().frame(textFrame);
		WebElement textBox = driver.findElement(By.xpath(TEXT_BOX_LOCATOR));
		actions.click(textBox).sendKeys(data.getBody()).build().perform();
		driver.switchTo().defaultContent();
		driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
	}

	public void saveLetter() {
		driver.findElement(By.xpath("//*[@data-name='saveDraft']")).click();
		(new WebDriverWait(driver, 10))
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(IS_SAVED_MESSAGE)));
		driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);

	}

	public void sendLetter() {
		driver.getCurrentUrl();
		driver.findElement(By.xpath(SEND_BUTTON_LOCATOR)).click();
		// driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
	}

}
