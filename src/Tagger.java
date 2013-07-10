import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
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
	
	private Map<String, String> Patterns;
	
	
	public Tagger(String patternFile) throws IOException {
		
		System.out.println("Initializing tagger from " + patternFile);
		
		//get ready to read from file
		FileInputStream input = new FileInputStream(patternFile);
		InputStreamReader streamReader = new InputStreamReader(input, "UTF-8");
		BufferedReader reader = new BufferedReader(streamReader);
		
		Patterns = new HashMap<String, String>();
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

				System.out.println("Found a rule");
				System.out.println("Tag: " + tag);
				System.out.println("Pattern: " + pattern);

				Patterns.put(pattern, tag);

			}
			
			else {
				System.out.println("ignoring: " + line);
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
		
		for(Entry<String, String> entry : Patterns.entrySet()) {
			
			//first tag will be considered the right one, if there is ambiguity
			//we conveniently ignore it for now
			if(token.matches(entry.getKey())) {
				return entry.getValue();
			}

		}

		//TODO this is ugly
		if(token.matches("(e)(.*)")) {

			for(Entry<String, String> entry : Patterns.entrySet()) {

				if(token.substring(1).matches(entry.getKey())) {
					if (entry.getValue().contains("personal_pronoun")) {
						return "demonstrative_pronoun";
					}
				}

			}
		}
		
		
		//baseline: return most frequent tag
		return "unknown";
	}

}
