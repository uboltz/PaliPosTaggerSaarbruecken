

/**
 * Stores a token and its tags, both from the tagger and the file containing the
 * test data.
 * 
 * 
 * @author Uwe Boltz
 *
 */
public class TaggedToken{
	
	
	public String token, testDataTag, generatedTag;
	
	public TaggedToken(String token, String testDataTag, String generatedTag) {
		this.token = token;
		this.testDataTag = testDataTag;
		this.generatedTag = generatedTag;

	}
	
	public boolean correctTag(){
		return testDataTag.equals(generatedTag);
	}
	

}
