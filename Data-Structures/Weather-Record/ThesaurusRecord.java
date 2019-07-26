import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
/////////////////////////////////////////////////////////////////////////////
//Semester:         CS367 Spring 2017 
//PROJECT:          p3
//FILE:             ThesaurusRecord.java
//
//Authors: Jiang, Kyle, Pedro Henrique, Tushar, and Zachary.
//Author 1: Jiang Huiyu, hjiang94@wisc.edu, hjiang94, 001
//Author 2: Kyle Malinowski, kmalinowski2@wisc.edu, kmalinowski2, 001
//Author 3: Pedro Henrique Koeler Goulart, koelergoular@wisc.edu, koelergoular, 001
//Author 4: Tushar Narang, tnarang@wisc.edu, tnarang, 001
//Author 5: Zachary Lesavich, zlesavich@wisc.edu, zlesavich, 001
//
//---------------- OTHER ASSISTANCE CREDITS 
//Persons: NA
//
//Online sources: NA
////////////////////////////80 columns wide //////////////////////////////////
/**
/**
 * The ThesaurusRecord class is the child class of Record to be used when merging thesaurus data.
 * The word field is the entry in the thesaurus, syn is the list of all associated synonyms.
 * 
 * @authors Jiang, Kyle, Pedro Henrique, Tushar, and Zachary.
 */

public class ThesaurusRecord extends Record{
    private String word;
    private ArrayList<String> syn;

	/**
	 * Constructs a new ThesaurusRecord by passing the parameter to the parent constructor
	 * and then calling the clear method()
	 */
    public ThesaurusRecord(int numFiles) {
    	super(numFiles);
    	clear();
    }

    /**
	 * This Comparator should simply behave like the default (lexicographic) compareTo() method
	 * for Strings, applied to the portions of the FileLines' Strings up to the ":"
	 * The getComparator() method of the ThesaurusRecord class will simply return an
	 * instance of this class.
	 */
	private class ThesaurusLineComparator implements Comparator<FileLine> {
		public int compare(FileLine l1, FileLine l2) {
			//Words extracted to be compared 
			String sl1 = l1.getString().split(":")[0];
			String sl2 = l2.getString().split(":")[0];
			return sl1.compareTo(sl2);
			
		}
		
		public boolean equals(Object o) {
			return this.equals(o);
		}
    }
    
	/**
	 * This method should simply create and return a new instance of the ThesaurusLineComparator class.
	 */
    public Comparator<FileLine> getComparator() {
		return new ThesaurusLineComparator();
    }
	
	/**
	 * This method should (1) set the word to null and (2) empty the list of synonyms.
	 */
    public void clear() {
		word = null;
		syn = new ArrayList<String>();
    }
	
	/**
	 * This method should parse the list of synonyms contained in the given FileLine and insert any
	 * which are not already found in this ThesaurusRecord's list of synonyms.
	 */
    public void join(FileLine w) {
		if(word == null){
			word = w.getString().split(":")[0];	
		}
		
		if( word.equals(w.getString().split(":")[0]))
		{
			String prior = w.getString();
			String[] temp = prior.split(":")[1].split(",");
			for(int i = 0; i < temp.length; i++)
			{
				if(!syn.contains(temp[i])){
					syn.add(temp[i]);
				}
			}
		}	
    }
	
	/**
	 * See the assignment description and example runs for the exact output format.
	 */
    public String toString() {
    	
    	Collections.sort(syn);
    	String output = "";
    	
    	output += word + ":";
    	
    	for(int i = 0; i < syn.size(); i++)
    	{
    		if(i == 0) output += syn.get(0);
    		else if (i != 0) output += "," + syn.get(i);
    		
    	}
    	output += "\n";
    	return output;
	}
}
