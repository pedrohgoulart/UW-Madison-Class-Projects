////////////////////////////////////////////////////////////////////////////////
// Main File:        generate_magic
// This File:        generate_magic
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

// Structure representing Square
// size: dimension(number of rows/columns) of the square
// array: 2D array of integers
typedef struct _Square {
    int size;
    int **array;
} Square;

int get_square_size();
Square * generate_magic(int size);
void write_to_file(char *filename, Square * square);

int main(int argc, char *argv[]){
	if (argc != 2) {
                perror("Usage: ./generate_magic <filename>\n");
                exit(1);
        }

        //Gets file
        char *filename = *(argv + 1);

	//Gets size from user and generates magic square
	int sqsize = get_square_size();
	Square* msquare = generate_magic(sqsize);

	// Writes the square to the output file
	write_to_file(filename, msquare);

	//Frees allocated memory
        for (int i = 0; i < (msquare -> size); i++){
                free(*((msquare -> array) + i));
        }
        free(msquare -> array);
        free(msquare);

	return 0;
}

/* get_square_size prompts the user for the magic square size
 * checks if it is an odd number >= 3 and returns the number
 */
int get_square_size(){
    	printf("Enter size of magic square, must be odd\n");

	int sqsize = 0;
    	scanf("%d", &sqsize);

     	if ((sqsize <= 2)||((sqsize % 2) != 1)) {
     		printf("Size must be an odd number >= 3.\n");
     		exit(1);
	}

	return sqsize;
}

/* generate_magic constructs a magic square of size n
 * using the Siamese algorithm and returns the Square struct
 */
Square * generate_magic(int n){
	//Creates a square
        Square* msquare = malloc(sizeof(Square));
        if ((msquare) == NULL) {
                perror("Cannot allocate memory.\n");
                exit(1);
        }

        msquare -> size = n;

	msquare -> array = malloc(sizeof(int*) * n);
        if ((msquare -> array) == NULL) {
                perror("Cannot allocate memory.\n");
                exit(1);
        }

        for (int i = 0; i < n; i++) {
                //Initializes array
                *((msquare -> array) + i) = malloc(sizeof(int*) * n);
                if ((*((msquare -> array) + i)) == NULL) {
                        perror("Cannot allocate memory.\n");
                        exit(1);
                }

		//Fills array row with 0
		for (int j = 0; j < n; j++) {
			*(*(msquare -> array + i) + j) = 0;
		}
	}

	//Fills out square
	int i = n/2; //Row
	int j = n - 1; //Column
	int prev_i;
	int prev_j;

	for (int currnumber = 1; currnumber <= (n * n); currnumber++) {
		*(*(msquare -> array + i) + j) = currnumber;

		//Saves previous i and j
		prev_i = i;
		prev_j = j;

		//Updates i (row) position
		if (i == n - 1){
                        i = 0;
                }
                else {
                        i += 1;
                }

		//Updates j (column) position
		if (j == n - 1) {
                        j = 0;
                }
                else {
                        j += 1;
                }

		//Checks if currnumber is on the edge of square
		while (((*(*(msquare -> array + i) + j)) != 0)&&(currnumber != (n*n))) {
			if (j == 0) {
				j = n - 1;
			}
			else {
				j = prev_j - 1;
			}
			i = prev_i;
		}
	}

	return msquare;
}

/* write_to_file opens up a new file(or overwrites the existing file)
 * and writes out the square in the format expected by verify_hetero.c
 */
void write_to_file(char *filename, Square * square){
	//Creates file
	FILE * writesquare = fopen (filename,"w");

	//Writes square size
	int sqsize = square -> size;
	fprintf(writesquare, "%d\n", sqsize);

	//Writes square
	for (int i = 0; i < sqsize; i++) {
		for (int j = 0; j < sqsize; j++) {
			if (j < (sqsize - 1)) {
				fprintf(writesquare, "%d,", (*(*(square -> array + i) + j)));
			}
			else {
				fprintf(writesquare, "%d\n", (*(*(square -> array + i) + j)));
			}
		}
	}

	//Closes file
	fclose(writesquare);
}
