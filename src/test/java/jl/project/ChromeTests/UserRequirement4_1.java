package jl.project.ChromeTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.fail;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import jl.project.StringExternalization;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

/**
 * @author
 * Class testing the user requirement of physical impairment web accessibility 
 * using the keyboard only - Enter key used.
 */
public class UserRequirement4_1 {
	/* Note: delaying or not the sending of the keys impact the success of the tests */
	ChromeDriver driver;
	String testCategoryLabel= "Protractor test category"; 
	String testItemLabel = "Protractor test";//The word item is not always well detected by the ocr.
	
	@BeforeClass
	public void setup() {
		System.setProperty(StringExternalization.webdriver_chrome_key, 
				StringExternalization.webdrivers_folder+StringExternalization.webdriver_chrome_value);
	driver = new ChromeDriver();	
	driver.manage().window().maximize();
	}
	
	@BeforeMethod
	public void navigate() {
		driver.get(StringExternalization.front_end_url);
		
	}
	
	// For reasons of Tesseract library issue this test needs to be ignored on Ubuntu
	@Ignore
	@Test(groups = {"creation_deletion_Chrome_1"})		
	public void createAndDeleteACategoryWithKeyboardOnly_EnterKey() {
		
		boolean isCategoryCreated = false;		
		
		System.out.println("1. Creation of a category with the keyboard only.");		
		//Tabbing until finding the input field to add the new category label
		Robot robot;
		Actions  action = new Actions(driver);
		try {
			robot = new Robot();
			robot.keyPress(KeyEvent.VK_TAB);//nav bar
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//nav bar
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//nav bar
			robot.delay(1000);			
			robot.keyPress(KeyEvent.VK_TAB);//to form label
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//to category input
			robot.delay(1000);
			action.sendKeys(testCategoryLabel).build().perform();//to new category entry
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//to submit button
			robot.delay(1000);
			//robot.keyPress(KeyEvent.VK_ENTER);// Previous test failures with the enter event being ignored
			action.sendKeys("\n").build().perform();//submit
			robot.delay(1000);		
			
		} catch (AWTException e) {
			System.err.println("AWTException when using the robot class");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		finally 
		{
		
			//Verifying that the category has been created		
			System.out.println("2. Confirming creation of the category");
			driver.get(StringExternalization.front_end_url);
			
			List<WebElement> aCategoryElements = driver.findElements(By.name("aCategory"));
			System.out.println("Found "+aCategoryElements.size()+" elements named aCategory");	
			for(WebElement aCategoryElement: aCategoryElements ) {
				String text = aCategoryElement.getText();
				if(text.contains(testCategoryLabel)) 
				{
					System.out.println("The text *"+text+"* was found. The category was successfully "
							+ "created using the keyboard only. ");
					isCategoryCreated=true;
				}
			};		
			
			assertThat(isCategoryCreated).isEqualTo(true);
		}
		
		
		
		System.out.println("3. Deletion of a category with the keyboard only.");
		//Assuming the category location
		boolean isCategoryFound;
		try {
			robot = new Robot();
			robot.keyPress(KeyEvent.VK_TAB);//nav bar
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//nav bar
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//nav bar
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//Form label
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//new category text
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//submit category button
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//Form label
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//category selection
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//new item text
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//submit item button
			robot.delay(1000);	
			robot.keyPress(KeyEvent.VK_TAB);//hyperlink
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//trash can icon: category "Protractor test category"
			robot.delay(1000);
			//robot.keyPress(KeyEvent.VK_ENTER);//Click to delete the test category
			action.sendKeys("\n").build().perform();//Click to delete the test category
			robot.delay(2000);
			
		} catch (AWTException e) {
			System.err.println("AWTException while using the instance of the class ");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			//Verifying that the category has been deleted
			System.out.println("4. Confirming that the category has been deleted.");
			driver.get(StringExternalization.front_end_url);
			
			List<WebElement>aCategoryElements = driver.findElements(By.name("aCategory"));
			System.out.println("Found "+aCategoryElements.size()+" elements in aCategoryElements after deletion.");
			try {
				for(WebElement aCategoryElement : aCategoryElements) {
					String text = aCategoryElement.getText();
					System.out.println(text);
					if (text.contains(testCategoryLabel)) {
						//if the created category can be found the test is failed    					
						fail("Found "+testCategoryLabel+" when the test category should have been deleted."
								+ "The test is failed.");
					}
					   				
				}
				//otherwise the test is successful
				isCategoryFound = false;
				
				assertThat(isCategoryFound).isEqualTo(false);
			}
			catch(StaleElementReferenceException e) {
				System.err.println("Caught a StaleElementReferenceException"
						+ "while going through the elements related to a trash can icon before a category.");
				System.err.println(e.getMessage());
				e.printStackTrace();    			
			}  
			
		}
		
	}
	
	// For reasons of Tesseract library issue this test needs to be ignored on Ubuntu
	@Ignore
	@Test(groups = {"creation_deletion_Chrome_1"})		
	public void createAndDeleteItemWithKeyboardOnly_EnterKey() {
		System.out.println("1. Creation of an item with the keyboard only.");
		Robot robot;
		Actions actions;
		try {
			robot = new Robot();			
			actions = new Actions(driver);
			robot.keyPress(KeyEvent.VK_TAB);//nav bar
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//nav bar
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//nav bar
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//Form label
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//new category text
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//submit category button
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//Form label
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//category selection
			actions.sendKeys("Uncategorized").build().perform();
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//new item text
			robot.delay(1000);
			actions.sendKeys(testItemLabel).build().perform();
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//submit item button
			robot.delay(2000);
			//robot.keyPress(KeyEvent.VK_ENTER);
			actions.sendKeys("\n").build().perform();
			robot.delay(2000);
			
				
			
		} catch (AWTException e) {
			System.err.println("AWTException while using the instance of the class ");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		System.out.println("2. Confirmation of creation.");
		//Checking that the new item creation was successful		
		List<WebElement> anItemElements = driver.findElements(By.name("anItem"));
		boolean isItemCreated=false;
		try {
			System.out.println("Found "+anItemElements.size()+" element named 'anItem'");
			for(WebElement anItemElement: anItemElements) {
				String text = anItemElement.getText();				
				if (text.contains(testItemLabel)) {
					System.out.println("Found "+text+" as text.");
					isItemCreated = true;
					}
			}
			
		}
		catch(StaleElementReferenceException e) {
			System.err.println("A StaleElementReferenceException has been caught while searching"
					+ "the elements named 'anItem' after creation of the element.");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}		
		assertThat(isItemCreated).isEqualTo(true);
		
		System.out.println("3. Deletion of the test item using the keyboard only.");
		
		try {
			robot = new Robot();	
			actions = new Actions(driver);
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//trash can icon: category "Uncategorized"
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//plus sign icon: category "Uncategorized"
			robot.delay(1000);		
			robot.keyPress(KeyEvent.VK_TAB);//Category "Uncategorized"
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//hyperlink
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//trash can icon: category "Protractor test category"
			actions.sendKeys("\n").build().perform(); //Click to delete the test category
			//robot.keyPress(KeyEvent.VK_ENTER);//Click to delete the test category
		} catch (AWTException e) {
			System.err.println("AWTException while using the instance of the class ");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		System.out.println("4. Confirmation of deletion");
		driver.get(StringExternalization.front_end_url);
		
		List<WebElement> anIconToDeleteAnItemElements = driver.findElements(By.name("anItem"));
		try {
			
			System.out.println("Found "+anIconToDeleteAnItemElements.size()+" element named 'anItem'");
			for(WebElement anItemElement: anIconToDeleteAnItemElements) {
				String text = anItemElement.getText();
				System.out.println("Found *"+text+"* as text.");
				if (text.equals(testItemLabel)) 
				{
					fail("Error: the test item label has been found. The test is failed.");
				}
				
			}
			
		}
		catch(StaleElementReferenceException e) 
		{
			System.err.println("A StaleElementReferenceException has been caught while searching"
					+ "the elements named 'anItem' ");
			e.getMessage();
			e.printStackTrace();
		}
	}
	
	// For reasons of Tesseract library issue this test needs to be ignored on Ubuntu
	@Ignore
	@Test(dependsOnGroups = {"creation_deletion_Chrome_1"})		
	public void HideAndDisplayItemsWithKeyboardOnly_EnterKey() 
	{
		System.out.println("1. Creation of an item with the keyboard only.");
		driver.get(StringExternalization.front_end_url);
		
		Robot robot;
		Actions actions;
		try {
			robot = new Robot();
			actions = new Actions(driver);
			robot.keyPress(KeyEvent.VK_TAB);//nav bar
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//nav bar
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//nav bar
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//Form label
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//new category text
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//submit category button
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//Form label
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//category selection
			actions.sendKeys("Uncategorized").build().perform();
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//new item text
			robot.delay(1000);
			actions.sendKeys(testItemLabel).build().perform();
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//submit item button
			robot.delay(1000);
			//robot.keyPress(KeyEvent.VK_ENTER);
			actions.sendKeys("\n").build().perform();
			robot.delay(5000);
							
			
		} catch (AWTException e) {
			System.err.println("AWTException while using the instance of the class ");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		System.out.println("2. Confirmation of creation.");
		//Checking that the new item creation was successful		
		List<WebElement> anItemElements = driver.findElements(By.name("anItem"));
		boolean isItemCreated=false;
		try {
			System.out.println("Found "+anItemElements.size()+" element named 'anItem'");
			for(WebElement anItemElement: anItemElements) {
				String text = anItemElement.getText();				
				if (text.contains(testItemLabel)) {
					System.out.println("Success. Found "+text+" as text.");
					isItemCreated = true;
					}
			}
			assertThat(isItemCreated).isEqualTo(true);
			
		}
		catch(StaleElementReferenceException e) {
			System.err.println("A StaleElementReferenceException has been caught while searching"
					+ "the elements named 'anItem' after creation of the element.");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}		

		
		System.out.println("3. Verification that the item is displayed");
		File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);	
		File screenshotFile_copy = new File("./screenshots/newItemScreenshot.png");
		try {
			FileUtils.copyFile(screenshotFile, screenshotFile_copy);
		} catch (IOException e) {
			System.err.println("IOException while copy and saving the screenshot");
			e.printStackTrace();
		}
		// code to extract the text from the picture
		Tesseract ocr = new Tesseract();
		String result = null;
		//https://github.com/tesseract-ocr/tessdata
		ocr.setDatapath("./tessdata");
		ocr.setLanguage("eng");
		try {
			result = ocr.doOCR(screenshotFile_copy);
		} catch (TesseractException e) {
			System.err.println("Exception while doing the OCR.");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		if(result.contains(testItemLabel)) 
		{
			System.out.println("Success. The test label has been found on the screen.");
		}
		else{fail("The item label seems to be absent from the screenshot: "+result);};
		
		//clicking to hide the item		
		System.out.println("4. Verification that the item can be hidden.");
		//Using the keyboard to hide the item. Only one category (Uncategorized) means only one element named foldUnfoldArea.
		driver.get(StringExternalization.front_end_url);
		
		try {
			robot = new Robot();
			actions = new Actions(driver);
			robot.keyPress(KeyEvent.VK_TAB);//nav bar
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//nav bar
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//nav bar
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//Form label
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//new category text
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//submit category button
			robot.delay(1000);robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//Form label
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//category selection
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//new item text
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//submit item button
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//hyperlink
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//trash can icon: category "Uncategorized"
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_TAB);//plus sign icon: category "Uncategorized"
			robot.delay(1000);
			//robot.keyPress(KeyEvent.VK_ENTER);//Click to hide the item
			actions.sendKeys("\n").build().perform();//Click to hide the item
			robot.delay(5000);
		} catch (AWTException e) {
			System.err.println("AWTException while using the instance of the class ");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		// Verification that the item is hidden
		screenshotFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		File screenshot_AfterClickToHide_copy = new File("./screenshots/AfterClickToHideScreenshot.png");
		try {
			FileUtils.copyFile(screenshotFile, screenshot_AfterClickToHide_copy);
			result = ocr.doOCR(screenshot_AfterClickToHide_copy);
			
			if(!result.contains(testItemLabel)) 
			{ 
				
				System.out.println("Success: the label couldn't be found in the screenshot: "+result);
			}
			else 
			{fail("The label was found on the screenshot when the item should have been hidden: "+result);
			}
			
		} catch (IOException e) {
			System.err.println("An IOExeption occured while copying the screenshot taken after the click"				
					+ "(Hiding of the item).");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		 catch (TesseractException e) {
			System.err.println("TesseractException while reading the screenshot taken after the click."
					+ "(Hiding of the item)");			
			System.err.println(e.getMessage());
			e.printStackTrace();
		}		
		
		//Verification that the item can be displayed by clicking a second time.
		System.out.println("5. Verification that the item can be displayed");
		try {
			
			robot = new Robot();
			actions = new Actions(driver);
			robot.delay(1000);
			actions.sendKeys("\n").build().perform();//Click to hide the item
			//robot.keyPress(KeyEvent.VK_ENTER);//Click to hide the item
			
		} catch (AWTException e) {
			System.err.println("AWTException while using the instance of the class ");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		screenshotFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		File screenshot_AfterClickToDisplay_copy = new File("./screenshots/AfterClickToDisplayScreenshot.png");
		
		try {			
			FileUtils.copyFile(screenshotFile, screenshot_AfterClickToDisplay_copy);
			ocr.setDatapath("./tessdata");
			ocr.setLanguage("eng");
			result = ocr.doOCR(screenshot_AfterClickToDisplay_copy);
			
			if(result.contains(testItemLabel)) 
			{
				System.out.println("Sucess: the label was found after clicking to display the item: "+result);
			}
			else {fail("The label: "+testItemLabel+" could not be in the ocr result: "+result
					+" when the item should have been displayed.");}
		} catch (IOException e) {
			System.err.println("An IOException occured while copying the screenshot taken after the click"
					+ "(Display of the item)");
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (TesseractException e) {
			System.err.println("TesseractException while reading the screenshot taken after the click"
					+ "(Display of the item)");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		//Cleaning up for a potential next test. Using a click for the task
		System.out.println("6. Suppression of the item.");
		List<WebElement> anIconToDeleteAnItemElements = driver.findElements(By.name("anIconToDeleteAnItem"));
		for(WebElement anIconToDeleteAnItemElement: anIconToDeleteAnItemElements) {//only one item in the test
			anIconToDeleteAnItemElement.click();
		}
		
		System.out.println("7. Testing the deletion of the test item");
		driver.get(StringExternalization.front_end_url);
		
		anIconToDeleteAnItemElements = driver.findElements(By.name("anIconToDeleteAnItem"));
		if(!(anIconToDeleteAnItemElements.size() == 0)) { fail("The test item was not deleted. "+anIconToDeleteAnItemElements.size()+" element has been found with the name anIconToDeleteAnItem");}
		else {System.out.println("Page cleaned from test item.");}
			
	}
	
	
	@AfterClass
	public void releseResources() 
	{
		driver.close();
		driver.quit();
	}
	
}
