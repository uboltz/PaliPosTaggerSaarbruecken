import java.util.Comparator;


public class gnarrgh implements Comparator<TaggedToken>{

	@Override
	public int compare(TaggedToken t1, TaggedToken t2) {
		
		return t1.testDataTag.compareTo(t2.testDataTag);
	}

}
