import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;


public class OutputWriter {
	
	private BufferedWriter writer;

	public OutputWriter(String outputFile){

		try {				
			FileOutputStream output = new FileOutputStream(outputFile);
			OutputStreamWriter streamWriter = new OutputStreamWriter(output, "UTF-8");

			this.writer = new BufferedWriter(streamWriter);
		}

		catch(IOException e) {
			e.printStackTrace();
		}

	}
	
	public void println() {	
		try {
			System.out.println();
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			
			e.printStackTrace();		
		}
		
	}
	
	public void println(String message){
		try {
			System.out.println(message);
			writer.write(message);
			writer.newLine();
			writer.flush();
		} catch(IOException e) {

			e.printStackTrace();
		}
		
	}



}
