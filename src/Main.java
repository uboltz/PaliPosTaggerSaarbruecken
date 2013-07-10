import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

/**
 * This class evaluates a POS-tagger on a txt-file containing tagged data.
 * For every token in the test file, a tag is generated and compared to its correct
 * tag in the file. At the end F-scores for each tag are computed.
 * 
 * @author Uwe Boltz
 *
 */
public class Main {
	
	private static int numberOfTokens;
	private static int numberOfCorrectTags;
	private static HashMap<String, TagStats> tagStats;
	private static HashMap<String, Integer> wordCounts = new HashMap<String, Integer>();
	private static List<TaggedToken> taggedTokens = new ArrayList<TaggedToken>();
	
	public static void main(String[] args) throws IOException{
		
		//access the test data
		FileInputStream input = new FileInputStream("pali-gold-utf-8.txt");
		InputStreamReader streamReader = new InputStreamReader(input, "UTF-8");
		BufferedReader reader = new BufferedReader(streamReader);
		
		//TODO maybe replace the console with an output file eventually
//		FileOutputStream output = new FileOutputStream("outputTest.txt");
//		OutputStreamWriter streamWriter = new OutputStreamWriter(output, "UTF-8");
//		BufferedWriter writer = new BufferedWriter(streamWriter);
		
		//TODO stop hardcoding file names
		Tagger tagger = new Tagger("patterns.txt");
		tagStats = new LinkedHashMap<String, TagStats>();
		String[] chars;
		String currentToken;
		String testDataTag;
		String generatedTag;
		
		
		String line = reader.readLine();
		//TODO I probably need to cut of the first character here, like in the tagger
		
		//process single lines in the test file until none are left
		while(!(line == null)) {
			
			System.out.println();
			System.out.println("Line: " + line);
			
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
				
				System.out.println("Token: " + currentToken);
				System.out.println("Tag(from file): " + testDataTag);
											


				//ignore punctutation etc.
				if (!ignorableTag(testDataTag)) {

					//do the actual work
					generatedTag = tagger.tag(currentToken);
					System.out.println("Tag(from tagger): " + generatedTag);
					taggedTokens.add(new TaggedToken(currentToken, testDataTag, generatedTag));
					
					//put every tag in a hashmap the first time we see it
					if (!tagStats.containsKey(testDataTag)) {						
						tagStats.put(testDataTag, new TagStats());
					}
					if (!tagStats.containsKey(generatedTag)){
						tagStats.put(tagger.tag(currentToken), new TagStats());
					}

					
					//see if we generated the right tag, keep track of the stats
					if (testDataTag.equals(generatedTag)){
						numberOfCorrectTags++;
						tagStats.get(testDataTag).truePositives++;

					}
					else {
						tagStats.get(testDataTag).falseNegatives++;
						tagStats.get(generatedTag).falsePositives++;
					}
					
					tagStats.get(testDataTag).overallCount++;
					numberOfTokens++;					

				}


			}

			//proceed to the next line
			line = reader.readLine();
		}


		System.out.println();
		System.out.println();
		
		//print results by tag
		printTagResults();
		System.out.println();

		//print overall results
		System.out.println("Tokens: " + numberOfTokens
				+ "   Correct: " + numberOfCorrectTags
				+ " " + ((float) numberOfCorrectTags / ( numberOfTokens)* 100) + "%");

		
		printWordCounts();
	
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
	
	
	
	/**
	 * Computes precision, recall and f-score for each tag and prints it to the
	 * console.
	 */
	private static void printTagResults() {
		
		int truePositives;
		int falsePositives;
		int falseNegatives;		
		int trueNegatives;	
		float precision;
		float recall;
		float fScore;
		
		for(Map.Entry<String, TagStats> tag: tagStats.entrySet()) {
			
			truePositives = tag.getValue().truePositives;
			falsePositives = tag.getValue().falsePositives;
			falseNegatives = tag.getValue().falseNegatives;
			
			trueNegatives = numberOfTokens 
							- truePositives
							- falsePositives
							- falseNegatives;
			
			//beware the divide by zero
			if(truePositives + falsePositives != 0) {
				precision = (float) truePositives / (truePositives + falsePositives);
			}
			else {
				precision = 0;
			}
			if(truePositives + falseNegatives != 0) {
				recall = (float) truePositives / (truePositives + falseNegatives);
			}
			else {
				recall = 0;
			}
			if(precision + recall != 0) {
				fScore = precision * recall / (2 * (precision + recall));
			}
			else {
				fScore = 0;
			}
			
			System.out.println();
			System.out.println(tag.getKey());
			System.out.println(
					  " Overall: " + tag.getValue().overallCount
					+ "    true pos: " + truePositives
					+ "    false pos: " + falsePositives
					+ "    true neg: " + trueNegatives 
					+ "    false neg: " + falseNegatives
					+ "    prec: " + precision
					+ "    rec: " + recall
					+ "    f1: " + fScore);
		}
		
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
	
	
	//TODO 
	private static void printWordCounts() throws IOException {
		
		FileOutputStream output = new FileOutputStream("outputTest.txt");
		OutputStreamWriter streamWriter = new OutputStreamWriter(output, "UTF-8");
		BufferedWriter writer = new BufferedWriter(streamWriter);
		
		Collections.sort(taggedTokens, new gnarrgh());
		
		for (TaggedToken token: taggedTokens) {

			if((!token.testDataTag.equals(token.generatedTag))
					/*&& (!token.generatedTag.equals("unknown"))*/) {
				writer.write(token.token
						+ " " + token.testDataTag
						+ " " + token.generatedTag); 
				writer.newLine();
			}

			
		}

		writer.close();
	}

}
