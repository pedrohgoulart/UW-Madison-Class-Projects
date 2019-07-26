////////////////////////////////////////////////////////////////////////////////
// Main File:        verify_hetero
// This File:        verify_hetero
// Other Files:      no other files
// Semester:         CS 354 Spring 2018
//
// Author:           Pedro Henrique Koeler Goulart
// Email:            koelergoular@wisc.edu
// CS Login:         koeler-goulart
//
/////////////////////////// OTHER SOURCES OF HELP //////////////////////////////
//                   fully acknowledge and credit all sources of help,
//                   other than Instructors and TAs.
//
// Persons:          No persons.
//
// Online sources:   No online sources.
////////////////////////////////////////////////////////////////////////////////

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// Structure representing Square
// size: dimension(number of rows/columns) of the square
// array: 2D array of integers
typedef struct _Square {
    int size;
    int **array;
} Square;

Square * construct_square(char *filename);
int verify_hetero(Square * square);
void insertion_sort(int* arr, int size);

int main(int argc, char *argv[]) {
	if (argc != 2) {
		perror("Usage: ./verify_hetero <filename>\n");
		exit(1);
	}

	//Gets file
	char *filename = *(argv + 1);

	//Constructs square
	Square* hsquare = construct_square(filename);

	// Verifies if it's a heterosquare and print true or false
	int is_hsquare = verify_hetero(hsquare);

	if (is_hsquare == 1) {
		printf("true\n");
	}
	else {
		printf("false\n");
	}

	//Frees allocated memory
        for (int i = 0; i < (hsquare -> size); i++){
                free(*((hsquare -> array) + i));
        }
        free(hsquare -> array);
        free(hsquare);

    return 0;
}

/* construct_square reads the input file to initialize a square struct
 * from the contents of the file and returns the square.
 * The format of the file is defined in the assignment specifications
 */
Square * construct_square(char *filename) {
	FILE *input = fopen(filename, "r");

	//Variable for max string size
	int max_string_size = 101;

	//Temporary String for fgets
	char tempString[max_string_size];

	//Creates a square
        Square* hsquare = malloc(sizeof(Square));
	if ((hsquare) == NULL) {
		perror("Cannot allocate memory.\n");
		exit(1);
        }
	hsquare -> size = 0;

	//Checks if file can be opened
	if (input == 0) {
		perror("Cannot open file for reading.\n");
		exit(1);
	}
	else {
		//Gets size of array (first character) and assigns it to hsquare
		fgets(tempString, max_string_size, input);
		hsquare -> size = atoi(tempString);

		int sqsize = hsquare -> size;

		hsquare -> array = malloc(sizeof(int*) * sqsize);
		if ((hsquare -> array) == NULL) {
			perror("Cannot allocate memory.\n");
                        exit(1);
		}

		//Reads rest of the file and fills out square
		for (int i = 0; i < sqsize; i++) {
			//Initializes array
			*((hsquare -> array) + i) = malloc(sizeof(int*) * sqsize);
			if ((*((hsquare -> array) + i)) == NULL) {
                	        perror("Cannot allocate memory.\n");
        	                exit(1);
	                }

			//Reads line
			fgets(tempString, max_string_size, input);

			//Creates tokens to remove from line
			const char s[2] = ",";
			char *token;
			int number;

   			//Gets the first token
   			token = strtok(tempString, s);

			//Walks through other tokens
			for (int j = 0; token != NULL; j++) {
				number = atoi(token);
				*(*((hsquare -> array) + i) + j) = number;
      				token = strtok(NULL, s);
			}
		}
	}

	//Closes file
        fclose(input);

	return hsquare;
}

/* verify_hetero verifies if the square is a heterosquare
 *
 * returns 1(true) or 0(false)
 */
int verify_hetero(Square * square) {
	int is_hsquare = 1;
	int sqsize = square -> size;
	int * sumarray = malloc((sizeof(int) * (2 * sqsize + 2)));
	if ((sumarray) == NULL) {
		perror("Cannot allocate memory.\n");
		exit(1);
	}

	//Sum of rows
	for (int i = 0; i < sqsize; i++) {
		for(int j = 0; j < sqsize; j++){
			*(sumarray + i) += *(*((square -> array) + i) + j);
		}
	}

	//Sum of columns
	for (int j = 0; j < sqsize; j++) {
                for(int i = 0; i < sqsize; i++){
                        *(sumarray + j + sqsize) += *(*((square -> array) + i) + j);
                }
        }

	//Sum of diagonals
	int m = 0;
	int n = 0;
	while ((m < sqsize)&&(n < sqsize)) {
		*(sumarray + (2 * sqsize)) += *(*((square -> array) + m) + n);
		m++;
		n++;
        }

	m = 0;
	n = sqsize - 1;

	while ((m < sqsize)&&(n >= 0)) {
		*(sumarray + (2 * sqsize + 1)) += *(*((square -> array) + m) + n);
		m++;
		n--;
	}

	//Passes the array to insertion_sort function
	insertion_sort(sumarray, (2 * sqsize + 2));

	//Checks sumarray for duplicates
	for (int i = 1; i < (2 * sqsize + 2); i++) {
		if (*(sumarray + (i - 1)) == *(sumarray + i)) {
			is_hsquare = 0;
			break;
		}
	}

	//Frees allocated memory
        free(sumarray);

	return is_hsquare;
}

/* insertion_sort sorts the arr in ascending order
 *
 */
void insertion_sort(int* arr, int size) {
	int i;
	int j;
	int current_array;

   	for (i = 1; i < size; i++) {
		current_array = *(arr + i);
		j = i - 1;

		while ((j >= 0)&&(*(arr + j) > current_array)) {
			*(arr + j + 1) = *(arr + j);
			j = j - 1;
		}

		*(arr + j + 1) = current_array;
   	}
}
