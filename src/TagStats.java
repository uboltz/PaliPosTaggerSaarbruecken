/**
 * Contains all the counts necessary to compute the F-scores at the end of 
 * evaluation for each tag. Meant to be used in a HashMap.
 * 
 * @author Uwe Boltz
 *
 */
public class TagStats {
	
	public int inTestData = 0;
	
	public int truePositives = 0;
	public int falsePositives = 0;
	public int falseNegatives = 0;
    public int trueNegatives = 0;

}
