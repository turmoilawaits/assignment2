package com.userreview;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.opencsv.CSVReader;
/**
 * 
 * @author AnSrivastava
 * Main class for simulating user review and asserting that user's review has been posted successfully.
 */
public class UserAccount {	
	WebDriver driver;
	String workingDir = System.getProperty("user.dir");
	String driverpath = workingDir;
	String CSV_PATH = workingDir;
	
	public void invokeBrowser() {
		/**
		 * This method launches firefox browser ,deletes cookies, maximizes the window and navigates to test_insurance_company profile page.
		 * Reads geckodriver.exe form ./files in the package.
		 */
		try {
			System.setProperty("webdriver.gecko.driver",driverpath+"\\files\\geckodriver.exe");
			driver = new FirefoxDriver();
			driver.manage().deleteAllCookies();
			driver.manage().window().maximize();
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
			driver.manage().timeouts().pageLoadTimeout(30,TimeUnit.SECONDS);								
			driver.get("https://wallethub.com/profile/test_insurance_company/");
		} catch (Exception e) {		
			e.printStackTrace();
		}
	}
	
	public void loginToLightUser() {
		/**
		 * This method logs in the user once the writeReview() has written the review in the profile page , as the review can be posted only when the registered user account is authenticated
		 * Reads registered user's credentials from csv file at ./files 
		 */
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(CSV_PATH+"\\files\\credentials.csv"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String [] csvCell;
		try {
			while ((csvCell = reader.readNext()) != null) {
				String email = csvCell[0];
				String password = csvCell[1];
				driver.findElement(By.xpath("//a[contains(text(),'Login')]")).click();
				driver.findElement(By.name("em")).click();
				driver.findElement(By.name("em")).sendKeys(email);
				driver.findElement(By.name("pw1")).click();
				driver.findElement(By.name("pw1")).sendKeys(password);							
				driver.findElement(By.xpath("//button[@type='button']")).click();			
			}
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	
	public void writeReview() {
		/**
		 * The method used to write review based on the condition mentioned in problem statement
		 */
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
		
		WebElement we = driver.findElement(By.className("wh-rating-notes"));
		Actions action = new Actions(driver);
		//hover-over review link so that review starts appears to be in focus .
		action.moveToElement(we).moveToElement(driver.findElement(By.xpath("//a[contains(text(),'4')]"))).click().build().perform();
		
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {		
			e.printStackTrace();
		}
		//clicks on the drop down list for selecting Health.
		WebElement dropdown = driver.findElement(By.cssSelector("span.val"));
		dropdown.click();
		WebDriverWait wait = new WebDriverWait(driver, 20);		

		//presence in DOM
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(text(),'Health')]")));		

		//clickable
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Health')]")));
		List<WebElement> options = dropdown.findElements(By.xpath("//a[contains(text(),'Health')]"));
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		options.get(0).click();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
		//clicks on the Write Review text box.
		driver.findElement(By.id("review-content")).click();
		driver.findElement(By.id("review-content")).sendKeys("Some random automated review Some random automated review Some random automated review Some random automated review Some random automated review Some random automated review Some random automated review Some random automated review");
		
		//rate 5-star for Health ,enter a minimum 200 character review and then hit Submit.
		driver.findElement(By.xpath("(//a[@onclick='try{gRatingsClick(this)}catch(e){};return false'])[5]")).click();
		
		//click submit button.
		driver.findElement(By.cssSelector("input.btn.blue")).click();		
		
	}	
	
	public boolean verifyReviewMessage() {
		/**
		 * this method verifies the review message posted by registered user.
		 */
		//navigate to registered user's profile review link.This was mentioned as a requirement in the assignment.
		driver.get("https://wallethub.com/profile/anksriv291/reviews/");
		
		//assuming that the latest review will always be at top and its xpath will be constant.
		String actualString = driver.findElement(By.xpath("/html/body/div[3]/div/div[1]/div[1]/div[3]/div[3]/div/div[1]/p")).getText();		
		actualString.contains("Some random automated review Some random automated review Some random automated review Some random automated review Some random automated review Some random automated review Some random automated review Some random automated review");
		boolean result = actualString.contains("Some random automated review Some random automated review Some random automated review Some random automated review Some random automated review Some random automated review Some random automated review Some random automated review");
		return result;
	}
	
	public static void main(String[] args) {
		/**
		 * Creates object of main class and accesses its methods.
		 */		
		UserAccount userAcc = new UserAccount();
		userAcc.invokeBrowser();
		userAcc.writeReview();
		userAcc.loginToLightUser();	
		if (userAcc.verifyReviewMessage()) {
			System.out.println("Message verified!");		
		}
		else {
			System.err.println("Failed to verify");
		}
		
		
	}

}
