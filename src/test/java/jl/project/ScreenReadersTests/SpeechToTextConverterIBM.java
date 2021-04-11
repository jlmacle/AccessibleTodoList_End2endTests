// https://www.ibm.com/cloud/watson-speech-to-textError stream curl: (1) Protocol ""https" not supported or disabled in libcurl
// https://cloud.ibm.com/services/speech-to-text/crn%3Av1%3Abluemix%3Apublic%3Aspeech-to-text%3Aeu-gb%3Aa%2F2e4a017664644b6c865cf4e532dd9056%3A08dcb5ed-d57f-410f-98ef-11ae645b5a04%3A%3A?paneId=manage&new=true
// https://cloud.ibm.com/docs/speech-to-text?topic=speech-to-text-gettingStarted#getting-started-tutorial

package jl.project.screenreaderstests;

import java.awt.Robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpeechToTextConverterIBM 
{
	Logger logger = LoggerFactory.getLogger(SpeechToTextConverterIBM.class);
	Robot robot;
	ProcessBuilder processBuilder = new ProcessBuilder();	
	String apikeyParam="\"apikey:"+System.getenv("IBM_SpeechToText")+"\"";	
	
	
	
	public String convertAudioToText(String pathToAudioFile) throws Exception
	{
		//Needed to fix an new line issue in the environment variable value in Windows.
		apikeyParam = apikeyParam.replace("\n", "");
		
		String informationReturned  = null;			
				
		String os_name = System.getProperty("os.name");
		ProcessBuilder processBuilder = new ProcessBuilder();
		Process process = null;
		
		robot = new Robot();
		
		logger.debug(String.format("OS: %s",os_name));		
		if(os_name.contains("Windows"))
		{
			processBuilder.command("src/test/java/jl/project/ScreenReadersTests/scripts/script_IBM_STT_Windows.bat");
		}
		else if (os_name.contains("Mac"))
		{
			processBuilder.command("src/test/java/jl/project/ScreenReadersTests/scripts/script_IBM_STT_macOS.zsh");
		}
		else if (os_name.contains("Linux"))
		{
			processBuilder.command("src/test/java/jl/project/ScreenReadersTests/scripts/script_IBM_STT_Ubuntu.sh");

		}
		else {throw new Exception(String.format("Unrecognized OS : %s",os_name));}
		
		process = processBuilder.start();
		logger.debug("Process started");
		
		logger.debug("Waiting for results.");
		robot.delay(15000);
		
		//logger.debug(String.format("Error stream %s",new String(process.getErrorStream().readAllBytes())));		
		//Stream that gives the output 		
		//logger.debug(String.format("Input stream %s",new String(process.getInputStream().readAllBytes())));
		
		
		// readAllBytes() since Java 1.9
		// https://docs.oracle.com/javase/9/docs/api/java/io/InputStream.html#readAllBytes--
		informationReturned = new String(process.getInputStream().readAllBytes());
		
		// supportsNormalTermination() since Java 1.9
		// https://docs.oracle.com/javase/9/docs/api/java/lang/Process.html#supportsNormalTermination--
		if (process.supportsNormalTermination()) process.destroy(); else process.destroyForcibly();
		logger.debug(String.format("Process is alive : %b", process.isAlive()));				
		
		return informationReturned;
	}
}
