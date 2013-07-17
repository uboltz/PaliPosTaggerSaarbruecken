import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Rule-based POS-Tagger. Takes a word and tries to match it to a number of
 * regular expressions, then returns a tag accordingly.
 *  
 * @author Uwe Boltz
 *
 */
public class Tagger {
	
	private List<String> patterns = new ArrayList<String>();
	private List<String> tags = new ArrayList<String>();
	private OutputWriter debug;
	
	
	public Tagger(String patternFile, OutputWriter debug) throws IOException {
		
		this.debug = debug;
		
		debug.println("Initializing tagger from " + patternFile);
		
		//get ready to read from file
		FileInputStream input = new FileInputStream(patternFile);
		InputStreamReader streamReader = new InputStreamReader(input, "UTF-8");
		BufferedReader reader = new BufferedReader(streamReader);
		
		int index;
		String tag, pattern;
		String line = reader.readLine();
		
		//This is weird. I have to cut off an invisible character in the first line.
		//Byte order mark?
		line = line.substring(1);		
		
		//read pattern file line by line and store tags+patterns
		while(line != null) {
			
			//lines starting with ## will be considered comments and ignored
			if(!line.matches("(##)(.*)") && (!line.equals(""))) {
				
				index = line.indexOf(" ");
				tag = line.substring(0, index);
				pattern = line.substring(index + 1);

				debug.println("Found a rule");
				debug.println("Tag: " + tag);
				debug.println("Pattern: " + pattern);

				patterns.add(pattern);
				tags.add(tag);

			}
			
			else {
				debug.println("ignoring: " + line);
			}

			line = reader.readLine();

		}
	}
	
	
	/**
	 * @param word the word to be tagged
	 * @return the tag
	 */
	public String tag(String token){
		
		token = token.toLowerCase();
		
		for(String pattern : patterns) {
			
			//first tag will be considered the right one, if there is ambiguity
			//we conveniently ignore it for now
			if(token.matches(pattern)) {
				return tags.get(patterns.indexOf(pattern));
			}

		}

		//TODO this is ugly
		if(token.matches("(e)(.*)")) {
			
			int index = 0;

			for(String tag : tags){
				if(tag.equals("personal_pronoun")){
					if(token.substring(1).matches(patterns.get(index))){
						
						return "demonstrative_pronoun";
					}
				}
				
				index++;
				
			}
			
		}
		
		
		//baseline: return most frequent tag
		return "unknown";
	}

}
