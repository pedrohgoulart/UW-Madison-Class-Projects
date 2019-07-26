import java.io.*;
import java.util.*;
import java.lang.*;

/////////////////////////////////////////////////////////////////////////////
//Semester:         CS367 Spring 2017 
//PROJECT:          p3
//FILE:             Reducer.java
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
 * Reducer solves the following problem: given a set of sorted input files (each
 * containing the same type of data), merge them into one sorted file. 
 *
 * @authors Jiang, Kyle, Pedro Henrique, Tushar, and Zachary.
 */
public class Reducer {
    // list of files for stocking the PQ
    private List<FileIterator> fileList;
    private String type,dirName,outFile;

    public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("Usage: java Reducer <weather|thesaurus> <dir_name> <output_file>");
			System.exit(1);
		}

		String type = args[0];
		String dirName = args[1];
		String outFile = args[2];

		Reducer r = new Reducer(type, dirName, outFile);
		r.run();
	
    }

    
	/**
	 * Constructs a new instance of Reducer with the given type (a string indicating which type of data is being merged),
	 * the directory which contains the files to be merged, and the name of the output file.
	 */
    public Reducer(String type, String dirName, String outFile) {
		this.type = type;
		this.dirName = dirName;
		this.outFile = outFile;
    }

	/**
	 * Carries out the file merging algorithm described in the assignment description. 
	 */
    public void run() {
		File dir = new File(dirName);
		File[] files = dir.listFiles();

		Record r = null;

		// list of files for stocking the PQ
		fileList = new ArrayList<FileIterator>();

		for(int i = 0; i < files.length; i++) {
			File f = files[i];
			if(f.isFile() && f.getName().endsWith(".txt")) {
				//fileList.add(fif.makeFileIterator(f.getAbsolutePath()));
				fileList.add(new FileIterator(f.getAbsolutePath(), i));
			}
		}

		switch (type) {
		case "weather":
			r = new WeatherRecord(fileList.size());
			break;
		case "thesaurus":
			r = new ThesaurusRecord(fileList.size());
			break;
		default:
			System.out.println("Invalid type of data! " + type);
			System.exit(1);
		}
		
		String output = "";
		//Declare and construct the priority queue.
		FileLinePriorityQueue priorityQueue = new FileLinePriorityQueue(r.getNumFiles(), r.getComparator());

		for(int i = 0; i < fileList.size(); i++)
		{
			//Grab a fileLine from the fileList
			FileLine temp = new FileLine(fileList.get(i).next().getString(), fileList.get(i));
			try {
				priorityQueue.insert(temp); //Attempt to insert it into the priorityQueue.
			} catch (PriorityQueueFullException e) { //If the priority queue happens to be full
				System.out.println("Critical Error");
				System.exit(1); //Exit with error
			}
		}
		//To keep track if new data comes in from a file 
		boolean firstRun = true; 
		
		while (!priorityQueue.isEmpty())
		{
			try {
				//Grab the next minimum item from the priority queue
				FileLine temp = priorityQueue.removeMin(); 
				//if the incoming record's data matches the current record's data
				if(r.getComparator().compare(new FileLine(r.toString(), null), temp) == 0)
				{
					//To merge the record's data
					r.join(temp);
					//Grab the fileIterator of the file the data came from 
					FileIterator fi = temp.getFileIterator();
					//If file has more lines
					if(fi.hasNext()) 
					{
						//Insert file's next record into queue
						priorityQueue.insert(new FileLine(fi.next().getString(), fi));
					}
					firstRun = false;
				}
				//if record's having same data among all files have been exhausted
				//or it is a record with new data 
				else
				{	
					if(!firstRun) {
						output += r.toString();
					}
					
					r.clear();
					r.join(temp);
					//Grab the fileIterator of the file the data came from 
					FileIterator fi = temp.getFileIterator();
					//If file has more lines
					if(fi.hasNext())
					{
						priorityQueue.insert(new FileLine(fi.next().getString(), fi));
					}
				}
			} catch (PriorityQueueEmptyException e) {
				System.out.println("Error in removing. Queue empty");
				System.exit(1);
			} catch (PriorityQueueFullException e) {
				System.out.println("Error in adding to queue. Queue full.");
				System.exit(1);
			}
		}
		
		output += r.toString();
		
		//Begin writing the output file
		try {
			File out = new File(outFile); 
			PrintWriter printFile = new PrintWriter(out); 
			
			printFile.print(output);
			printFile.flush();
			printFile.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("Unable to write file: " + outFile);
		}
		
    }
}
