package jl.project.pa11yTest;

import static org.junit.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.log4testng.Logger;

/**
 * @author 
 *
 */
public class UserRequirement5_Pa11yTest {
	
	Logger logger = Logger.getLogger(UserRequirement5_Pa11yTest.class);
	String fileFolder = "pa11yTestFiles/";
	String frontEndPath = "..\\AccessibleTodoList_FrontEnd";
	String backEndPath =  "..\\AccessibleTodoList_Backend";
	String endToEndPath = "..\\AccessibleTodoList_End2endTests";
	String osName = System.getProperty("os.name");	
	Process process_Angular;
	Process process_Pa11y;
	Process process_Backend;
	boolean UncategorizedFound = false;
	boolean pa11yTestPassed = false; 
	int process_exit_code = -1;
	
	
	@BeforeClass
	public void setup()
	{
		logger.debug(String.format("OS: %s",osName));
		String[] command_Angular = null;
		String[] command_Backend = null;
		String[] command_Pa11y = null;
		
		if (this.osName.contains("Windows"))
		{			
			command_Angular = new String[]{"cmd.exe","/c","ng","serve","-o", "&"};
			command_Backend = new String[]{"cmd.exe", "/c", "mvn","spring-boot:run"};
			command_Pa11y = new String[]{"cmd.exe","/c","pa11y","http://localhost:4200"};
			
		}
		else if (this.osName.contains("Mac"))
		{
			command_Angular = new String[]{"ng","serve","-o", "&"};
			command_Backend = new String[]{"mvn","spring-boot:run"};
			command_Pa11y = new String[]{"pa11y","http://localhost:4200"};
		}		
		else {fail("OS name not recognized");}
		
		try 
		{		
			//https://docs.oracle.com/javase/7/docs/api/java/lang/Runtime.html
			logger.debug("Starting back-end server");			
			process_Backend = Runtime.getRuntime().exec(command_Backend, null, new File(backEndPath));		
			process_Backend.getInputStream();		
			logger.debug("Waiting for the back-end startup.");
			Thread.sleep(15000);			
			
			
			logger.debug("Todo: Testing that the 'Uncategorized' category can be found."); 	
				 
									
			logger.debug("Starting Angular server");
			process_Angular = Runtime.getRuntime().exec(command_Angular, null, new File(this.frontEndPath));		
			
			try {
				// Note: starting the angular server may take a while
				logger.debug("Putting thread to sleep.");
				Thread.sleep(30000);	
				
				logger.debug("Building and starting the pa11y command."); 				
				process_Pa11y =	Runtime.getRuntime().exec(command_Pa11y); 
				process_exit_code = process_Pa11y.waitFor();				
				pa11yTestPassed = this.print_stream(process_Pa11y.getInputStream(), "Pa11y", "No issues found!" );				
				logger.debug(String.format("Pa11y process exit code: %s",process_exit_code));
				
			} 
			catch (InterruptedException e1) 
			{
				logger.debug("Caught an InterruptedException while pausing the execution.");
				e1.printStackTrace();
			}			
			
		} 
		catch (IOException e) 
		{
			logger.debug("IOException caught while using Runtime.getRuntime().exec");
			logger.debug(e.getMessage());
			e.printStackTrace();
		}	
		
		catch (InterruptedException e1) 
		{
		  logger.debug("Caught an InterruptedException while pausing the execution.");
		  e1.printStackTrace(); 
		}
		 
	}
	
	
	@Test
	public void pa11yTest()
	{		
		assertTrue(pa11yTestPassed);		
	}
	
	
	/**
	 * A method that takes an input stream (for example, an output stream or an error stream) and logs its content.
	 * The method can also test if a String is in the stream.
	 * @param stream: the InputStream
	 * @param streamType: the stream type: (regular)output or error
	 * @param stringToFind: potentially a String to find in the output
	 * @return returns true if stringToFind
	 */
	private boolean print_stream(InputStream stream, String streamType, String stringToFind)	
	{
		boolean stringFound =  false;
		try {
			logger.debug(String.format("%s stream available %d",streamType,stream.available()));
			File stream_File = new File("log_"+streamType+".txt");		
			logger.debug("Before copyInputStreamToFile.");
			FileUtils.copyToFile(stream, stream_File);//The source stream is not closed with copyToFile.
			
			List<String> log_lines = FileUtils.readLines(stream_File,"UTF-8");
			//https://en.wikipedia.org/wiki/UTF-8 
			for(String line:log_lines) 
			{
			  logger.debug(line); if(line.contains(stringToFind)) {stringFound = true; }
			  
			} 
			logger.debug("All lines read."); 
			 
		} 
		catch (IOException e) 
		{
			logger.debug("");
			e.printStackTrace();
		}		
		
		return stringFound;
	}
	

	@AfterClass
	void release()
	{
		if(process_Angular.supportsNormalTermination()) {process_Angular.destroy();} else {process_Angular.destroyForcibly();}
		if(process_Pa11y.supportsNormalTermination()) {process_Pa11y.destroy();} else {process_Pa11y.destroyForcibly();}
		if(process_Backend.supportsNormalTermination()) {process_Backend.destroy();} else {process_Backend.destroyForcibly();}
	}

}
