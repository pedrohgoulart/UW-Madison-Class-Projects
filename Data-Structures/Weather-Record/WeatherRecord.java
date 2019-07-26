import java.lang.StringBuilder; 
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Comparator;
import java.util.Arrays;
import java.util.Collections;

/////////////////////////////////////////////////////////////////////////////
//Semester:         CS367 Spring 2017 
//PROJECT:          p3
//FILE:             WeatherRecord.java
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
 * The WeatherRecord class is the child class of Record to be used when merging
 * weather data. Station and Date store the station and date associated with 
 * each weather reading that this object stores. l stores the weather readings,
 * in the same order as the files from which they came are indexed.
 * 
 *  @authors Jiang, Kyle, Pedro Henrique, Tushar, and Zachary.
 */
public class WeatherRecord extends Record{
	
	Integer station;
	Integer date;
	ArrayList<Double> data;
	/**
	 * Constructs a new WeatherRecord by passing the parameter to the parent 
	 * constructor and then calling the clear method()
	 */
    public WeatherRecord(int numFiles) {
		super(numFiles);
		clear();
    }
	
	/**
	 * This comparator should first compare the stations associated with the 
	 * given FileLines. If they are the same, then the dates should be 
	 * compared.
	 */
    private class WeatherLineComparator implements Comparator<FileLine> {
		public int compare(FileLine l1, FileLine l2) {
			
			//The two strings to be compared 
			String s1 = l1.getString();
			String s2 = l2.getString();
			
			String[] cmp1 = s1.split(",");
			String[] cmp2 = s2.split(",");
			
			//Arrays 0 and 1 are the ones where stations and dates are located that is used to compare 
			for(int i = 0; i < 2; i++){
				if(Integer.parseInt(cmp1[i]) > Integer.parseInt(cmp2[i])){
					return 1;
				}
				else if(Integer.parseInt(cmp1[i]) < Integer.parseInt(cmp2[i])){
					return -1;
				}
			}
			
			return 0;
		}
		
		public boolean equals(Object o) {
			return this.equals(o);
		}
    }
    
	/**
	 * This method should simply create and return a new instance of the
	 * WeatherLineComparator class.
	 */
    public Comparator<FileLine> getComparator() {
		return new WeatherLineComparator();
    }
	
	/**
	 * This method should fill each entry in the data structurse containing
	 * the readings with Double.MIN_VALUE
	 */
    public void clear() {
		date = 0;
		station = 0;
		 /* This is done to correspond the no data from a particular file to be represented 
		 * as a "-" 
		 */
		data =  new ArrayList<Double>(new ArrayList<Double>(Collections.nCopies(super.getNumFiles(), null)));
    }

	/**
	 * This method should parse the String associated with the given FileLine
	 * to get the station, date, and reading contained therein. Then, in the 
	 * data structure holding each reading, the entry with index equal to the
	 * parameter. FileLine's index should be set to the value of the reading. 
	 * Also, so that this method will handle merging when this WeatherRecord 
	 * is empty, the station and date associated with this WeatherRecord should
	 * be set to the station and date values which were similarly parsed.
	 */
    public void join(FileLine li) {
		//Convert Strings into Integers/Doubles
		Integer stationInteger = Integer.parseInt(li.getString().split(",")[0]);
		Integer dateInteger = Integer.parseInt(li.getString().split(",")[1]);
		Double dataDouble = Double.parseDouble(li.getString().split(",")[2]);
		
		//If a new weather record data set is coming in
		if (station == 0) {
			station = stationInteger;
		}
		if (date == 0) {
			date = dateInteger;
		}
		
		//File Iterator associated with file, the data is coming from 
		FileIterator fi = li.getFileIterator();
		int index = fi.getIndex();
	
		/*
		 *Removes default data in list, and adds data in the 
		 *position corresponding to the file it's coming from.
		 */
		if(!data.contains(dataDouble)){
			data.remove(index);
			data.add(index,dataDouble);
		}
		
    }
	
	/**
	 * See the assignment description and example runs for the exact output format.
	 * @return - output - Weather data for a station and date in exact output format as specified in the assignment.
	 */
    public String toString() {
    	
    	String output = "";
    	
    	output += station + ",";
    	output += date;
    	
    	/*
		 * Checks for null (default value) and changes it to "-" if it finds 
		 * any. If there are no default values, simply includes data to output.
		 */
    	for(int i = 0; i < data.size(); i++) {
    		if (data.get(i) == null) {
    			output += ",-";
    		}
    		else {
    			output += "," + data.get(i);
    		}
    	}
    	
    	output += "\n";
    	return output;
    }
}