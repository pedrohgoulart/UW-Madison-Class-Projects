
import java.util.*;
import java.io.*;

public class Chatbot{
    private static String filename = "./WARC201709_wid.txt";
    private static ArrayList<Integer> readCorpus(){
        ArrayList<Integer> corpus = new ArrayList<Integer>();
        try{
            File f = new File(filename);
            Scanner sc = new Scanner(f);
            while(sc.hasNext()){
                if(sc.hasNextInt()){
                    int i = sc.nextInt();
                    corpus.add(i);
                }
                else{
                    sc.next();
                }
            }
            sc.close();
        }
        catch(FileNotFoundException ex){
            System.out.println("File Not Found.");
        }
        return corpus;
    }
    static public void main(String[] args){
        ArrayList<Integer> corpus = readCorpus();
		int flag = Integer.valueOf(args[0]);
        
        if (flag == 100){
        		//Variables
			int w = Integer.valueOf(args[1]);
            int count = 0;
            
            //Counts number of occurrences of W in the corpus
            for (int i = 0; i < corpus.size(); i++) {
            		if (corpus.get(i) == w) count++;
            }
            
            //Output
            System.out.println(count);
            System.out.println(String.format("%.7f",count/(double)corpus.size()));
        }
        
        else if (flag == 200) {
        		//Variables for random number
        		int n1 = Integer.valueOf(args[1]);
            int n2 = Integer.valueOf(args[2]);
            double r = (double)n1/n2;
            
            //Creates Unigram and displays output
            createUnigram(r, corpus, true);
        }
        
        else if(flag == 300) {
        		//Variables
            int h = Integer.valueOf(args[1]);
            int w = Integer.valueOf(args[2]);
            int count = 0;
            
            //List of words that match h
            ArrayList<Integer> words_after_h = new ArrayList<Integer>();
            
            //Populates words_after_h if h is found on corpus and checks 
            //previous word to see if it matches w
            for (int i = 1; i < corpus.size(); i++) {
            		if (corpus.get(i - 1) == h) {
            			words_after_h.add(corpus.get(i));
            			if (corpus.get(i) == w) count++;
            		}
            }
            
            //output 
            System.out.println(count);
            System.out.println(words_after_h.size());
            System.out.println(String.format("%.7f",count/(double)words_after_h.size()));
        }
        
        else if(flag == 400) {
        		//Variables for random value
            int n1 = Integer.valueOf(args[1]);
            int n2 = Integer.valueOf(args[2]);
            double r = (double)n1/n2;
            
            //Word to be used
            int h = Integer.valueOf(args[3]);
            
            //Creates Bigram and displays output
            createBigram(r, h, corpus, true);
        }
        
        else if(flag == 500) {
        		//Variables
            int h1 = Integer.valueOf(args[1]);
            int h2 = Integer.valueOf(args[2]);
            int w = Integer.valueOf(args[3]);
            int count = 0;
            
            //List of words that match h1 and h2
            ArrayList<Integer> words_after_h1h2 = new ArrayList<Integer>();
            
            //Populates words_after_h1h2 if h1 is found on corpus and checks 
            //previous word to see if it matches h2 and w
            for (int i = 2; i < corpus.size(); i++) {
            		if (corpus.get(i - 2) == h1 && corpus.get(i - 1) == h2) {
            			words_after_h1h2.add(corpus.get(i));
            			if (corpus.get(i) == w) count++;
            		}
            }

            //output 
            System.out.println(count);
            System.out.println(words_after_h1h2.size());
            if(words_after_h1h2.size() == 0)
                System.out.println("undefined");
            else
                System.out.println(String.format("%.7f",count/(double)words_after_h1h2.size()));
        }
        
        else if(flag == 600) {
            //Variables for random value
            int n1 = Integer.valueOf(args[1]);
            int n2 = Integer.valueOf(args[2]);
            double r = (double)n1/n2;
            
            //Words to be used
            int h1 = Integer.valueOf(args[3]);
            int h2 = Integer.valueOf(args[4]);
            
            //Creates Trigram and displays output
            createTrigram(r, h1, h2, corpus, true);
        }
        
        else if(flag == 700){
        		//Variables
            int seed = Integer.valueOf(args[1]);
            int t = Integer.valueOf(args[2]);
            int h1=0,h2=0;
            
            //Generates a random number based on seed
            Random rng = new Random();
            if (seed != -1) rng.setSeed(seed);
            
            //INITIAL WORD IS 0
            if(t == 0) {
            		//r to generate first word
                double r = rng.nextDouble();
                
                	//Calculates unigram (like flag 200) and outputs h1
                	h1 = createUnigram(r, corpus, false);
                	System.out.println(h1);
                
                //Returns if h is period, question mark, or exclamation point
                if(h1 == 9 || h1 == 10 || h1 == 12){
                    return;
                }

                //r to generate second word
                r = rng.nextDouble();
                
                //Calculates bigram (like flag 400) and outputs h2
                h2 = createBigram(r, h1, corpus, false);
                System.out.println(h2);
            }
            
            //INITIAL WORD IS 1
            else if(t == 1){
            		//r to generate second word
                double r = rng.nextDouble();
                
                //Word to be found
                h1 = Integer.valueOf(args[3]);
                
                //Calculates bigram (like flag 400) and outputs h2
                h2 = createBigram(r, h1, corpus, false);
                System.out.println(h2);
            }
            
            //INITIAL WORD IS 2
            else if(t == 2){
                h1 = Integer.valueOf(args[3]);
                h2 = Integer.valueOf(args[4]);
            }
            
            //Loops until h is period, question mark, or exclamation point
            while(h2 != 9 && h2 != 10 && h2 != 12){
            		//r to generate new word using h1, h2
                double r = rng.nextDouble();
                
                //Calculates Trigram (like flag 600) and outputs w
                int w  = 0;
                w = createTrigram(r, h1, h2, corpus, false);
                System.out.println(w);
                
                //Updates h1 and h2
                h1 = h2;
                h2 = w;
            }
        }

        return;
    }
    
    /*
     * Private methods to make code easier to read
     */
    private static int createUnigram (double r, ArrayList<Integer> c, boolean output) {
    		//Array with vocabulary list values. List has number of elements repeated
        double[] vocabularyList = new double[4700];
        
        	for (int i = 0; i < c.size(); i++) {
            	vocabularyList[c.get(i)] = vocabularyList[c.get(i)] + 1;
        	}
        	
        	//Calculates the probability (Pi) for every item in vocabularyList
        	for (int j = 0; j < vocabularyList.length; j++) {
        		vocabularyList[j] = vocabularyList[j]/(double)c.size();
        	}
        	
        	//List of intervals
        	ArrayList<Interval> intervalList = new ArrayList<Interval>();
        	intervalList = createIntervalList(vocabularyList);
        	
        	//Searches for correct index
        	int index = indexForIntervalList(intervalList, r);
        	
        	//Outputs (if true) wordTypeIndex, leftIndex, and rightIndex
        	if (output) {
            	System.out.println(intervalList.get(index).wordTypeIndex);
        		System.out.println(String.format("%.7f",intervalList.get(index).leftIndex));
        		System.out.println(String.format("%.7f",intervalList.get(index).rightIndex));
        	}
    		
    		return intervalList.get(index).wordTypeIndex;
    }
    
    private static int createBigram (double r, int h, ArrayList<Integer> c, boolean output) {
    		//Array with vocabulary list values. List has repeated number of 
        //elements after h
        double[] vocabularyList = new double[4700];
        
        //Counts how many occurrences of h happens and adds repeated number
        //of occurrences of words following h
        int counter = 0;
        
        	for (int i = 1; i < c.size(); i++) {
        		if (c.get(i - 1) == h) {
        			vocabularyList[c.get(i)] = vocabularyList[c.get(i)] + 1;
        			counter++;
        		}
        	}
        	
        	if (counter == 0) {
        		//Outputs (if true) undefined for counter = 0
            	if (output) {
            		System.out.println("undefined");
            		return 0; //Ends method
            	}
        		
            	//Creates unigram (used for flag = 700)
        		return createUnigram(r, c, false);
        	}
        	else {
        		//Calculates probability of each item in vocabularyList
        		for (int j = 0; j < vocabularyList.length; j++) {
        			vocabularyList[j] = vocabularyList[j]/counter;
        		}
        		
        		//List of intervals
        		ArrayList<Interval> intervalList = new ArrayList<Interval>();
            	intervalList = createIntervalList(vocabularyList);
            	
            	//Searches for correct index
            	int index = indexForIntervalList(intervalList, r);
            	
            	//Outputs (if true) wordTypeIndex, leftIndex, and rightIndex
            	if (output) {
            		System.out.println(intervalList.get(index).wordTypeIndex);
            		System.out.println(String.format("%.7f",intervalList.get(index).leftIndex));
            		System.out.println(String.format("%.7f",intervalList.get(index).rightIndex));
            	}
            	
        		return intervalList.get(index).wordTypeIndex;
        	}
    }
    
    private static int createTrigram (double r, int h1, int h2, ArrayList<Integer> c, boolean output) {
    		//Array with vocabulary list values. List has repeated number of 
        //elements after h
        double[] vocabularyList = new double[4700];
        
        //Counts how many occurrences of h happens and adds repeated number
        //of occurrences of words following h
        int counter = 0;
        
        for (int i = 2; i < c.size(); i++) {
        		if (c.get(i - 2) == h1 && c.get(i - 1) == h2) {
        			vocabularyList[c.get(i)] = vocabularyList[c.get(i)] + 1;
        			counter++;
        		}
    		}
        
        	if (counter == 0) {
        		//Outputs (if true) undefined for counter = 0
            	if (output) {
            		System.out.println("undefined");
            		return 0; //Ends method
            	}
            	
            	//Creates bigram (used for flag = 700)
        		return createBigram(r, h1, c, false);
        	}
        	else {
        		//Calculates probability of each item in vocabularyList
        		for (int j = 0; j < vocabularyList.length; j++) {
        			vocabularyList[j] = vocabularyList[j]/counter;
        		}
        		
        		//List of intervals
        		ArrayList<Interval> intervalList = new ArrayList<Interval>();
            	intervalList = createIntervalList(vocabularyList);
            	
            	//Searches for correct index
            	int index = indexForIntervalList(intervalList, r);
            	
            	//Outputs (if true) wordTypeIndex, leftIndex, and rightIndex
            	if (output) {
            		System.out.println(intervalList.get(index).wordTypeIndex);
            		System.out.println(String.format("%.7f",intervalList.get(index).leftIndex));
            		System.out.println(String.format("%.7f",intervalList.get(index).rightIndex));
            	}
        		
        		return intervalList.get(index).wordTypeIndex;
        	}
    }
    
    private static ArrayList<Interval> createIntervalList(double[] v) {
    		//Interval list to keep track of wordTypeIndex, leftIndex, and rightIndex
    		ArrayList<Interval> intervalList = new ArrayList<Interval>();
    
    		//Adds intervals to Interval list according to order of index
    		double sumOfLi = 0; //Sum of leftIndex
    		double sumOfRi = 0; //Sum of rightIndex
    		Interval tempInterval = null;
    	
    		for (int i = 0; i < v.length; i++) {
    			if(v[i] != 0) {
    				sumOfLi = sumOfRi;
    				sumOfRi = sumOfRi + v[i];
    				tempInterval = new Interval(i, sumOfLi, sumOfRi);
    				intervalList.add(tempInterval);
    			}
    		}
    		
    		return intervalList;
    }
    
    private static int indexForIntervalList(ArrayList<Interval> l, double r) {
		//Applies (leftIndex,rightIndex] rule and searches for correct intervalList index
		for(int i = 0; i < l.size(); i++) {
			if(i == 0 && r >= l.get(i).leftIndex && r <= l.get(i).rightIndex) {
				return i;
			}
			else if (i != 0 && r > l.get(i).leftIndex && r <= l.get(i).rightIndex) {
				return i;
			}
		}
		
		return 0;
	}
}

class Interval {
	public int wordTypeIndex;
	public double leftIndex;
	public double rightIndex;
	
	public Interval(int i, double li, double ri) {
		wordTypeIndex = i;
		leftIndex = li;
		rightIndex = ri;
	}
}