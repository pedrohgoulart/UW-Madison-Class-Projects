import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class tokenizer {
	
	private static final String DATA_PATH = ".";
	private static int filesNumber;
	private static int filesScanned;
	private static int wordCounter;
	private static ArrayList<String> uniqueWord = new ArrayList<String>();
	private static ArrayList<Integer> frequency = new ArrayList<Integer>();
	private static ArrayList<Integer> frequencySorted = new ArrayList<Integer>();
	
	
	private static void readPlace() throws IOException {
		Scanner input = new Scanner(System.in); 
		
		File folder = new File(DATA_PATH);
		
		for (File file : folder.listFiles()) {
			if (file.getName().endsWith(".txt")) {
				filesNumber++;
			}
		}
		
		System.out.println("Number of files found: " + filesNumber);
		
		try {
			for (int i = 1; i <= filesNumber; i++) {
				String tempFileName = "";
				//Checks for correct filename
				if (i < 10) {
					tempFileName = "00" + String.valueOf(i) + ".txt";
				}
				
				else if (i >= 10 && i < 100) {
					tempFileName = "0" + String.valueOf(i) + ".txt";
				}
				
				else {
					tempFileName = String.valueOf(i) + ".txt";
				}
				
				//Reads file
				File file = new File(tempFileName);
				Scanner scanFile = new Scanner(file);
				int temp = 0;
				
				
				
				while (scanFile.hasNext()){
										
					//Adds word to list
					String readWord = scanFile.next();
					if (temp == 0) {
						filesScanned++;
						temp++;
					}

					//Variables for the deleted characters
					String tempStringOne = null;
					String tempStringTwo = null;
					
					//Checks for special characters
					if (readWord.endsWith(".") || readWord.endsWith(",") || readWord.endsWith(":") || readWord.endsWith(";") || 
							readWord.endsWith("\u201D") || readWord.endsWith("\u201C") || readWord.endsWith("\"") || 
							readWord.endsWith("\u2018") || readWord.endsWith("\u2019") || readWord.endsWith("'") ||
							readWord.endsWith("?") || readWord.endsWith("!") ||
							readWord.endsWith("(") || readWord.endsWith(")")) {
						tempStringOne = readWord.substring(readWord.length() - 1);
						readWord = readWord.substring(0, readWord.length() - 1);
					}
					
					if (readWord.startsWith(".") || readWord.startsWith(",") || readWord.startsWith(":") || readWord.startsWith(";") || 
							readWord.startsWith("\u201D") || readWord.startsWith("\u201C") || readWord.startsWith("\"") || 
							readWord.startsWith("\u2018") || readWord.startsWith("\u2019") || readWord.startsWith("'") ||
							readWord.startsWith("?") || readWord.startsWith("!") ||
							readWord.startsWith("(") || readWord.startsWith(")")) {
						tempStringTwo = readWord.substring(0, 1);
						readWord = readWord.substring(1);
					}
					
					//Checks if the first special character was removed
					if (tempStringTwo != null) {
						uniquenessChecker(tempStringTwo);
						tempStringOne = null;
					}
					
					//Checks for unique word
					uniquenessChecker(readWord);
					
					//Checks if the final special character was removed
					if (tempStringOne != null) {
						uniquenessChecker(tempStringOne);
						tempStringOne = null;
					}
				}
				scanFile.close();
			}
			
			input.close();
			
			//Sort the frequency list
			for (int i = 0; i < frequency.size(); i++) {
				frequencySorted.add(frequency.get(i));
			}
			
			Collections.sort(frequencySorted);
			
		} catch (FileNotFoundException e) {
			System.out.println("Unable to read from file");
		}
	}
	
	
	private static void uniquenessChecker(String readWord) {
		wordCounter++;
		
		if (!uniqueWord.contains(readWord)) {
			uniqueWord.add(readWord);
			frequency.add(1);
		}
		else {
			frequency.add(index(uniqueWord, readWord), frequency.remove(index(uniqueWord, readWord)) + 1);
		}
	}
	
	
	private static int index(ArrayList<String> temp, String word) {
		for(int i = 0; i < temp.size(); i++) {
			if(temp.get(i).equals(word)) {
				return i;
			}
		}
		
		return 0;
	}
	
	
	public static void main(String args[]) throws IOException {
		readPlace();
		System.out.println("Files scanned: " + filesScanned);
		System.out.println("Number of words: " + wordCounter);
		System.out.println("Number of unique words: " + uniqueWord.size());
		
		int listSize = 0;
		
		System.out.println("\n\nMost used words: ");
		
		for (int i = frequencySorted.size() - 1; i > 0; i--) {
			for (int j = 0; j < frequencySorted.size(); j++) {
				if (listSize > 24) {
					i = 0;
					break;
				}
				
				if (frequencySorted.get(i).equals(frequency.get(j))) {
					System.out.println(uniqueWord.get(j) + "\t" + frequency.get(j));
					listSize++;
					break;
				}
			}
		}
		
		ArrayList<String> containsL = new ArrayList<String>();
		System.out.println("\n\nLeast used words: ");
		
		for (int i = 0; i < 25; i++) {
			for (int j = 0; j < frequencySorted.size(); j++) {
				if (frequencySorted.get(i).equals(frequency.get(j))) {
					if(!containsL.contains(uniqueWord.get(j))) {
						System.out.println(uniqueWord.get(j) + "\t" + frequency.get(j));
						containsL.add(uniqueWord.get(j));
						break;
					}
				}
			}
		}
		
		System.out.println("\n\nSaving list of unique words...");
		
		try {
			File file = new File("wordslist.txt");
			PrintWriter printFile = new PrintWriter(file);;
		
			for (int i = frequencySorted.size() - 1; i > 0; i--) {
				printFile.print(frequencySorted.get(i) + "\n");
			}
			
			System.out.println("Finished.");
			printFile.flush();
			printFile.close();
		}catch (FileNotFoundException e){
			System.out.println("Unable to write to file");
		}
	}
}
