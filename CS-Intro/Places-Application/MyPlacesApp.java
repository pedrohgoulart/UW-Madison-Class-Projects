//
// Title:            Places
// Files:            MyPlacesApp.java, PlaceList.jar, Place.java
// Semester:         CS302 Fall 2016
//
// Author:           Pedro Henrique Koeler Goulart
// Email:            koelergoular@wisc.edu
// CS Login:         koeler-goulart
// Lecturer's Name:  Gary Dahl
// Lab Section:      331
//

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * The MyPlacesApp class is responsible for managing all of the input and data
 * the user enters. The program runs repeatedly until the user enters 'Q', and
 * can add, show and delete a Place to the ArrayList, and can also read and
 * write files on the same program folder.
 * 
 * &lt;p&gt;Bugs: no bugs encountered.
 *  
 * @author Pedro Henrique Koeler Goulart
 */
public class MyPlacesApp {
	
	private static Scanner input;
	private static PlaceList placeList;
	private static final String DATA_PATH = ".";
	private static final String FILE_EXTENSION = ".mp";
	
	/**
	 * This method initializes the variables and runs the program. 
	 * 
	 * @param args is the declaration of main to indicate an array of strings
	 * can be sent to the program to help with program initialization.
	 */
	public static void main(String[] args) {
		//Initialization of variables
		input = new Scanner(System.in); 
		placeList = new PlaceList();
		char menuChosenOption = 'x';
		
		//Do loop that repeats until user enters 'Q'
		do {
			System.out.println("");
			System.out.println("My Places 2016\n--------------------------");
			
			//Display menu options and handles the user input
			menuChosenOption = mainMenuSelection(input, placeList);
			
			if (menuChosenOption == 'A'){
				addPlace(input, placeList);
			}
			
			else if (menuChosenOption == 'S'){
				showPlace(input, placeList);
			}
			
			else if (menuChosenOption == 'D'){
				deletePlace(input, placeList);
			}
			
			else if (menuChosenOption == 'R'){
				readPlace(input, placeList);
			}
			
			else if (menuChosenOption == 'W'){
				writePlace(input, placeList);
			}
		} while (menuChosenOption != 'Q');
		
		//Displays final message and ends program
		System.out.println("Thank you for using My Places 2016!");
		input.close();
	}
	
	/**
	 * This method contains and handles the menu options of the program. It
	 * shows Add, Read and Quit options when there are no places in memory,
	 * and shows 3 other additional options (show, delete and write) when there
	 * are places in memory, while also displaying the name of those places.
	 * When the user enters a letter, this method checks if the letter typed 
	 * matches the menu options available, resets the mainMenuSelection to x 
	 * and displays the unrecognized input the user entered in case it didn't
	 * match the options available or in case the user entered more than 1
	 * character.
	 * 
	 * @param input Scanner variable that reads inputs from the user.
	 * @param placeList ArrayList that contains the Places. 
	 * @return mainMenuSelection the menu option the user entered that was 
	 * converted into a character.
	 */
	private static char mainMenuSelection(Scanner input, PlaceList placeList){
		String menuInput = "";
		char mainMenuSelection = 'x';
		
		//Checks if there are already places on the list
		if (!placeList.hasPlaces()) {
			System.out.println("No places in memory."
					+ "\n--------------------------");
			//Displays menu choices (no places)
			System.out.print("A)dd R)ead Q)uit: ");
		}
		else {
			//Display the places on the list
			for (int i = 0; i < placeList.size(); i++){
				System.out.println((i + 1) + ") "
						+ placeList.get(i).getName());
			}
			System.out.println("--------------------------");
			//Displays menu choices (with places)
			System.out.print("A)dd S)how D)elete R)ead W)rite Q)uit: ");
		}
		
		//Reads the input of the user
		menuInput = input.nextLine();
		
		//Checks if user typed only a letter (size 1)
		if (menuInput.length() == 1){
			//Passes the letter to the char variable and makes it UpperCase
			mainMenuSelection = menuInput.charAt(0);
			mainMenuSelection = Character.toUpperCase(mainMenuSelection);
			//Checks that letter matches menu optionjs available
			if (!placeList.hasPlaces() && mainMenuSelection == 'S' 
					||!placeList.hasPlaces() && mainMenuSelection == 'W'){
				mainMenuSelection = 'x';
				System.out.println("Unrecognized choice: " + menuInput);
				enterToContinue(input);
			}
		}
		else {
			System.out.println("Unrecognized choice: " + menuInput);
			enterToContinue(input);
		}
		
		return mainMenuSelection;
	}
	
	/**
	 * This method adds places to the ArrayList placeList. It asks the user for
	 * the name and the address of the place and then adds it to the list. This
	 * method also prevent places with the same name from being added.
	 * 
	 * @param input Scanner variable that reads inputs from the user.
	 * @param placeList ArrayList that contains the Places. 
	 */
	private static void addPlace(Scanner input, PlaceList placeList){
		String placeName = "";
		String placeAddress = "";
		
		System.out.print("Enter the name: ");
		placeName = input.nextLine();
		System.out.print("Enter the address: ");
		placeAddress = input.nextLine();
		
		Place newPlace = new Place(placeName, placeAddress);
		
		if (!placeList.contains(newPlace)) {
			System.out.println("Adding: " + placeName);
			placeList.add(newPlace);
		}
		else {
			System.out.println(placeName + " already in list.");
		}
		
		enterToContinue(input);
	}
	
	/**
	 * This method shows a certain place name and address according to its
	 * position in the ArrayList that is displayed in the menu. It also uses
	 * the promptUserForNumber to confirm the number the user entered matches
	 * the ones expected by the program.
	 * 
	 * @param input Scanner variable that reads inputs from the user.
	 * @param placeList ArrayList that contains the Places. 
	 */
	private static void showPlace(Scanner input, PlaceList placeList){
		int placeShowNumber = promptUserForNumber(input, 
				"Enter number of place to Show: ", placeList.size());

		if (placeShowNumber != -1){
			System.out.println(placeList.get(placeShowNumber - 1).getName());
			System.out.println(placeList.get(placeShowNumber - 1).getAddress());
		}
		
		enterToContinue(input);
		input.nextLine();
	}
	
	/**
	 * This method deletes a certain place according to its position in the
	 * ArrayList that is displayed in the menu. It also uses the
	 * promptUserForNumber to confirm the number the user entered matches the
	 * ones expected by the program.
	 * 
	 * @param input Scanner variable that reads inputs from the user.
	 * @param placeList ArrayList that contains the Places. 
	 */
	private static void deletePlace(Scanner input, PlaceList placeList){
		int placeDeleteNumber = promptUserForNumber(input, 
				"Enter number of place to Delete: ", placeList.size());

		if (placeDeleteNumber != -1){
			System.out.println("Deleting: "
					+ placeList.get(placeDeleteNumber - 1).getName());
			placeList.remove(placeDeleteNumber - 1);
		}
		
		enterToContinue(input);
		input.nextLine();
	}
	
	/**
	 * This method displays file names and reads files that are on the the 
	 * indicated folder of DATA_PATH. When reading the file, the method takes
	 * the name of the place (before the ;) and the address (after the ;) and 
	 * adds it to the list, preventing places with the same name from being 
	 * added. Additionally, this method displays a custom error message if it 
	 * can not read the file.
	 * 
	 * @param input Scanner variable that reads inputs from the user.
	 * @param placeList ArrayList that contains the Places. 
	 */
	private static void readPlace(Scanner input, PlaceList placeList){
			String fileName = "";
			
			readFilesInDirectory();

			System.out.print("Enter filename: ");
			fileName = input.nextLine();
			System.out.println("Reading file: " + fileName);
			
			try {
				File file = new File(fileName);
				Scanner scanFile = new Scanner(file);
				
				while (scanFile.hasNextLine()){
					String placeName = "";
					String placeAddress = "";
					
					String readLine = scanFile.nextLine();
					
					for (int i = 0; i < readLine.length(); i++){
						if (readLine.charAt(i) == ';'){
							placeName = readLine.substring(0,i);
							placeName = placeName.trim();
							placeAddress = readLine.substring((i+1), 
								readLine.length());
							break;
						}
					}
					
					Place newPlace = new Place(placeName, placeAddress);
				
					if (!placeList.contains(newPlace)) {
						placeList.add(newPlace);
					}
					else {
						System.out.println(placeName + " already in list.");
					}
				}
				
				scanFile.close();
		}catch (FileNotFoundException e) {
			System.out.println("Unable to read from file: " + fileName);
		}
		
		enterToContinue(input);
	}
	
	/**
	 * This method writes (creates) a new document in the DATA_PATH specified.
	 * When writing the file, the method adds the name of the place, then adds
	 * a semicolon (;) and then the address of the place. Additionally, this
	 * method displays a custom error message if it can not read the file.
	 * 
	 * @param input Scanner variable that reads inputs from the user.
	 * @param placeList ArrayList that contains the Places. 
	 */
	private static void writePlace(Scanner input, PlaceList placeList){
		String fileName = "";
		
		readFilesInDirectory();
		
		System.out.print("Enter filename: ");
		fileName = input.nextLine();
		System.out.println("Writing file: " + fileName);
		
		try {
			File file = new File(fileName);
			PrintWriter printFile = new PrintWriter(file);;
		
			for (int i = 0; i < placeList.size(); i++){
				printFile.print(placeList.get(i).getName());
				printFile.print(";");
				printFile.println(placeList.get(i).getAddress());
			}
				
			printFile.flush();
			printFile.close();
		}catch (FileNotFoundException e){
			System.out.println("Unable to write to file: " + fileName);
		}
		
		enterToContinue(input);
	}
	
	/**
	 * This method displays all files available at the DATA_PATH folder that
	 * match the FILE_EXTENSION.
	 */
	private static void readFilesInDirectory(){
		File folder = new File(DATA_PATH);
		
		System.out.println("My Places Files: ");
		
		for (File file : folder.listFiles()) {
			if (file.getName().endsWith(FILE_EXTENSION)) {
				System.out.println("    " + file.getName());
			}
		}
		
		System.out.println("");
	}
	
	/**
	 * This method simply contains the "Press Enter to continue." message that
	 * comes before the program runs the loop again.
	 */
	private static void enterToContinue (Scanner input){
		System.out.print("Press Enter to continue.");
		input.nextLine();
	}
	
	/**
	 * This method checks for the input of the user when choosing the place from
	 * a numbered list displayed on the menu. If the number is between 1 and max
	 * the program returns the value entered, otherwise, it returns -1 and
	 * warns the user that the numbers expected were between 1 and max.
	 * 
	 * @param input Scanner variable that reads inputs from the user.
	 * @param placeList ArrayList that contains the Places. 
	 * @param max is the max number that can be entered by the user.
	 * @return promptUserForNumber the number the user entered (if it matches
	 * the expectations) and -1 if it doesn't.
	 */
	private static int promptUserForNumber(Scanner input, String prompt, 
			int max){
		int promptUserForNumber = 0;

		System.out.print(prompt);
		//If user types an int value
		if (input.hasNextInt()) {
			promptUserForNumber = input.nextInt();
			if (promptUserForNumber < 1 || promptUserForNumber > max) {
				System.out.println("Expected a number between 1 and "
						+ max + ".");
				return -1;
			}
			else {
				return promptUserForNumber;
			}
		} 
		//If user types anything other than an int value
		else {
			input.nextLine();
			System.out.println("Expected a number between 1 and " + max + ".");
			return -1;
		} 
	}

}