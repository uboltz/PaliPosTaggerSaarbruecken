import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * This class evaluates a POS-tagger on a txt-file containing tagged data.
 * For every token in the test file, a tag is generated and compared to its correct
 * tag in the file. At the end F-scores for each tag are computed.
 * 
 * @author Uwe Boltz
 *
 */
public class Main {
	
	
	private static HashMap<String, Integer> wordCounts = new HashMap<String, Integer>();
	private static List<TaggedToken> taggedTokens = new ArrayList<TaggedToken>();

	
	public static void main(String[] args) throws IOException{
		
		//access the test data
		FileInputStream input = new FileInputStream("pali-gold-utf-8.txt");
		InputStreamReader streamReader = new InputStreamReader(input, "UTF-8");
		BufferedReader reader = new BufferedReader(streamReader);
		
		
		//TODO stop hardcoding file names
		Tagger tagger = new Tagger("patterns.txt");
		Evaluator evaluator = new Evaluator("output.txt");
		String[] chars;
		String currentToken;
		String testDataTag;
		String generatedTag;
		
		
		String line = reader.readLine();
		//TODO I probably need to cut of the first character here, like in the tagger
		
		//process single lines in the test file until none are left
		while(!(line == null)) {
			
			//TODO move the output functionality away from evaluator class
			evaluator.println();
			evaluator.println("Line: " + line);
			
			chars = line.split(" ");
			
			//unless there are at least 2 groups of chars (token + tag), we can
			//ignore the line
			if(chars.length > 1) {							
				
				/*
				 * First word on the line is the word token, if there is anything
				 * else, it will be treated as one single big tag.
				 * Implementation might be considered a tad ugly.
				 */			
				for(int i = 0; i<chars.length; i++) {
					if(i>1) {
						chars[1] += chars[i];
					}
				}
				
				currentToken = chars[0];
				testDataTag = chars[1];
				addCount(currentToken);
				
				evaluator.println("Token: " + currentToken);
				evaluator.println("Tag(from file): " + testDataTag);
											


				//ignore punctutation etc.
				if (ignorableTag(testDataTag)) {
					
					evaluator.println("Tagger ignores this token.");
				}
				
				else {

					//do the actual work
					generatedTag = tagger.tag(currentToken);
					evaluator.println("Tag(from tagger): " + generatedTag);
					taggedTokens.add(new TaggedToken(currentToken, testDataTag, generatedTag));
													
				}


			}

			//proceed to the next line
			line = reader.readLine();
		}


		evaluator.println();
		evaluator.println();
		
		//print results
		evaluator.printTagResults(taggedTokens);
		evaluator.printOverallResults(taggedTokens);
		
	
	}
	
	
	
	/**
	 * We don't care for punctuation, so we need to filter out the respective tags
	 * 
	 * @param tag the tag in question
	 * @return true if the tag should be ignored
	 */
	private static boolean ignorableTag(String tag) {
		return (!tag.equals("@sandhi@") && tag.contains("@")); 
	}
	
	
	
	
	
	private static void addCount(String word){
		if (!wordCounts.containsKey(word)){
			wordCounts.put(word, new Integer(1));
		}
		else {
			int i = wordCounts.get(word).intValue() + 1;
			wordCounts.remove(word);
			wordCounts.put(word, new Integer(i));
		}
	}

}
