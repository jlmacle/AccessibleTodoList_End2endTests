	
package jl.project.chrometests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.fail;

import java.awt.AWTException;
import java.awt.Robot;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.log4testng.Logger;

import jl.project.StringExternalization;



/**
 * @author 
 *	Class testing the user requirement 1 of creating and deleting a category
 */
public class UserRequirement1_Test {
	Logger logger = Logger.getLogger(jl.project.chrometests.UserRequirement1_Test.class);
	WebDriver driver;	
	Robot robot;

	
	/**
	 * "The annotated method will be run before the first test method in the current class is invoked."  
	 * https://testng.org/doc/documentation-main.html
	 */
	@BeforeClass	
	public void setup() {	
		logger.info(StringExternalization.TEST_START+StringExternalization.WEBDRIVER_CHROME_VALUE);
		//https://chromedriver.chromium.org/downloads
		System.setProperty(StringExternalization.WEBDRIVER_CHROME_KEY, 
				StringExternalization.WEBDRIVERS_FOLDER+StringExternalization.WEBDRIVER_CHROME_VALUE);
		
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setBrowserName(StringExternalization.BROWSER_NAME_CHROME);
		
		if(StringExternalization.GRID_NOT_USED) {driver = new ChromeDriver();}
		else 
		{	
			try {
				driver = new RemoteWebDriver(new URL(StringExternalization.SELENIUM_HUB), capabilities);
			} catch (MalformedURLException e) {
				logger.error(StringExternalization.EXCEPTION_MALFORMEDURL);
				logger.error(e.getMessage());
				e.printStackTrace();
			}			
		}
		
		driver.manage().window().maximize();
		
		try 
		{
			robot = new Robot();
		} 
		catch (AWTException e) 
		{
			logger.debug(StringExternalization.EXCEPTION_AWT);
			e.printStackTrace();
		}
	}
	
	/**
	 * "The annotated method will be run before each test method"
	 * https://testng.org/doc/documentation-main.html
	 */
	@BeforeMethod	
	public void navigate() {
		driver.get(StringExternalization.ANGULAR_SERVER_URL);
		
	}
	
	/**
	 * Tests a successful creation of category
	 */
	@Test	
    public void createCategory() {
		logger.info(StringExternalization.TEST_START+StringExternalization.TEST_CATEGORY_CREATION);
    	boolean isCategoryFound = false;    	
    	
    	logger.info("1. Category creation");
    	driver.findElement(By.id(StringExternalization.ELEMENT_ID_NEW_CATEGORY_INPUT_FIELD)).sendKeys(StringExternalization.LABEL_TEST_CATEGORY);
    	robot.delay(1000);
    	driver.findElement(By.id(StringExternalization.ELEMENT_ID_ADD_CATEGORY_BUTTON)).click();
    	robot.delay(1000);
    	//The category has been added. The display of the existing categories is being refreshed.
    	logger.debug("At this point, the test category should have been created.");
    	   	
    	logger.info("2. Confirmation of category creation ");		
    	driver.get(StringExternalization.ANGULAR_SERVER_URL);
		robot.delay(2000);
    	List<WebElement> aCategoryElements = driver.findElements(By.name(StringExternalization.ELEMENT_NAME_A_CATEGORY));	  
    	
    	try {
    		logger.debug(StringExternalization.DEBUG_FOUND+aCategoryElements.size()+" aCategory elements");
    		if( aCategoryElements.isEmpty() ){fail(StringExternalization.EXCEPTION_APP_NOT_STARTED);}//for the case where the app wasn't started 
    		for (WebElement aCategoryElement : aCategoryElements) {
	    		String text = aCategoryElement.getText().trim();//A space is in front of all strings
				logger.debug("Found text: *"+text+"*");				
				if (text.contains(StringExternalization.LABEL_TEST_CATEGORY)) {isCategoryFound=true;break;}
				
			}
    	}
    	catch(StaleElementReferenceException e) {
    		logger.error(StringExternalization.EXCEPTION_STALE_ELEMENT_REFERENCE
    				+ "while going through the elements with the name aCategory.");
    		e.printStackTrace();
    	}	    	

    	// Giving time for the item to be displayed
    	// Recurrent failed deletion issues that did not occur with the slowest computer I have.
    	
		robot.delay(3000);    	
    	
    	assertThat(isCategoryFound).isTrue();
    	
    }
	
	/**
	 * Tests a successful deletion of category	 
	 */
	@Test	
	public void deleteCategory() {
		logger.info(StringExternalization.TEST_START+StringExternalization.TEST_CATEGORY_DELETION);
		int testCategoryPositionIntheList = 0;
		int currentCategoryPosition = 0;
		boolean isCategoryFound = false;
		
		//1. Confirmation that the category was created; registration of its position in the list of elements named aCategory    	
    	logger.info("1. Category existence confirmation");
    	driver.get(StringExternalization.ANGULAR_SERVER_URL);
		robot.delay(2000);
		List<WebElement> aCategoryElements = driver.findElements(By.name(StringExternalization.ELEMENT_NAME_A_CATEGORY));	
		
		logger.debug(StringExternalization.DEBUG_FOUND+aCategoryElements.size()+" elements named aCategory");
		if( aCategoryElements.isEmpty() ){fail(StringExternalization.EXCEPTION_APP_NOT_STARTED);}//for the case where the app wasn't started 
    	try {    		
    		for (WebElement aCategoryElement : aCategoryElements) {
    			currentCategoryPosition++;
	    		String text = aCategoryElement.getText().trim();
	    		logger.debug("Found text: *"+text+"*");
				if (text.equals(StringExternalization.LABEL_TEST_CATEGORY)) {
					testCategoryPositionIntheList = currentCategoryPosition;
					logger.debug("Found the text:"+text+" in position: "+testCategoryPositionIntheList);					
					isCategoryFound=true;
					break;
				}
			}
    	}
    	catch(StaleElementReferenceException e) {
    		logger.error(StringExternalization.EXCEPTION_STALE_ELEMENT_REFERENCE
    				+ "while going through the anItem elements.");
    		e.getMessage();
    		e.printStackTrace();
    	}	 
		    	
    	if(isCategoryFound ) {
    		logger.debug("The new category has been successfuly created.");    		
    		//2. Deletion of the category created
    			// finding the elements with the name StringExternalization.ELEMENT_NAME_AN_ICON_TO_DELETE_A_CATEGORY
    		logger.info("2. Category deletion");
    		driver.get(StringExternalization.ANGULAR_SERVER_URL);
    		robot.delay(2000);
    		List<WebElement> trashIconElementsInFrontOfCategories = driver.findElements(By.name(StringExternalization.ELEMENT_NAME_AN_ICON_TO_DELETE_A_CATEGORY));
    		logger.debug(StringExternalization.DEBUG_FOUND+trashIconElementsInFrontOfCategories.size()+" elements with name anIconToDeleteACategory.");    		
    		try {
    			currentCategoryPosition=0;    			
    			for(WebElement trashCanIconElementInFrontOfCategory : trashIconElementsInFrontOfCategories) {
    				currentCategoryPosition++;    				    				
    				if (currentCategoryPosition == testCategoryPositionIntheList) {
    					logger.debug("Clicking the trash can icon in position: "+currentCategoryPosition);
    					trashCanIconElementInFrontOfCategory.click();
    					//Issue with undeleted category
    		    		robot.delay(2000);
    					break;
    				}
    				else {logger.debug("Skipping this trash can icon.");}
    			}
    			
    		}
    		catch(StaleElementReferenceException e) {
    			logger.error(StringExternalization.EXCEPTION_STALE_ELEMENT_REFERENCE
    					+ "while going through the elements related to a trash can icon in front of a category.");
    			e.getMessage();
    			e.printStackTrace();    			
    		}    	
    		
    		
    		//3. confirmation of deletion
    		logger.info("3. Confirmation of category deletion");
    		driver.get(StringExternalization.ANGULAR_SERVER_URL);
    		robot.delay(2000);
    		
    		aCategoryElements = driver.findElements(By.name(StringExternalization.ELEMENT_NAME_A_CATEGORY));
    		logger.debug(StringExternalization.DEBUG_FOUND+aCategoryElements.size()+" elements in aCategoryElements after deletion.");
    		try {
    			for(WebElement aCategoryElement : aCategoryElements) {
    				String text = aCategoryElement.getText();
    				logger.debug(text);
    				if (text.equals(StringExternalization.LABEL_TEST_CATEGORY)) {
    					//if the created category can be found the test is failed    					
    					fail(StringExternalization.DEBUG_FOUND+StringExternalization.LABEL_TEST_CATEGORY+" when the test category should have been deleted."
    							+ "The test is failed.");
    				}
    				   				
    			}
    			//otherwise the test is successful
    			isCategoryFound = false;
    			assertThat(isCategoryFound).isFalse();
    		}
    		catch(StaleElementReferenceException e) {
    			logger.error(StringExternalization.EXCEPTION_STALE_ELEMENT_REFERENCE
    					+ "while going through the elements related to a trash can icon before a category.");
    			e.getMessage();
    			e.printStackTrace();    			
    		}    
    		
    	}
    	
    	else {
    		logger.error("The test category was not found.");
    		fail("Test of category creation failed.");
    	}		
		
	}	
	
	
	/**
	 * The annotated method will be run after all the test methods in the current class have been run.
	 * https://testng.org/doc/documentation-main.html 
	 */
	@AfterClass	
	public void releaseResources() {
		driver.quit();
	}

}
