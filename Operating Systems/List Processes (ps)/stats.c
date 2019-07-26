////////////////////////////////////////////////////////////////////////////////
// Main File:         stats
// This File:         stats
// Other Files:       options and processList
// Semester:          CS 537 Fall 2018
//
// Authors:           Pedro Henrique Koeler Goulart, Varun Sreenivasan
// Emails:            koelergoular@wisc.edu, vsreenivasan@wisc.edu
// CS Logins:         koeler-goulart, sreenivasan
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

// Pre-set variable used for buf[] array to stores information for stat file
static const int buf_rows_stat = 15;

/* Function to calculate size (number of characters) in a file. Helps with
 * memory allocation.
 *
 * args - directory (dir) char array
 * return - integer with number of characters in a file
 */
int fileSize(char *dir) {
  // Variables
  int size = 0;
  FILE *fp = fopen(dir, "r");

  // Checks if is file exists
  if(fp != NULL) {
    fgetc(fp);
    // Calculates size of file
    while(!feof(fp)) {
      size++;
      fgetc(fp);
    }
  }

  // Returns size of file
  return size;
}

/* Function to retrieve memory size from /proc/<pid>/statm file.
 *
 * args - directory (dir) char array
 * return - integer with memory size information for process
 */
int parseStatm(char* dir) {
  // Size variable
  int mem_size = 0;

  // Tries to open file
  FILE *inputFile = fopen(dir, "r");
  if (inputFile != NULL) {
    // Checks if item is a string
    if (fscanf(inputFile, "%d", &mem_size)) {
      fclose(inputFile);
    }
  }

  // Returns size variable in STATM
  return mem_size;
}

/* Function to retrieve information from /proc/<pid>/stat file according
 * to selected option. This function retrieves information for:
 * - State (s), located in buf[2]
 * - User time (U), located in buf[13]
 * - System time (S), located in buf[14]
 *
 * args - option char and directory (dir) char array
 * return - char array with chosen information for process
 */
char* parseStat(char option, char* dir) {
  // Variables
  char* string = NULL;
  int file_size = 0;
  FILE *inputFile = fopen(dir, "r");

  // Checks if process exists
  if (inputFile == NULL) {
    perror("Input File Error");
    exit(1);
  }

  file_size = fileSize(dir);
  char buf[buf_rows_stat][file_size];

  // Stores values in buf
  for(int i = 0; i < buf_rows_stat; i++) {
    if(!fscanf(inputFile, "%s", buf[i])) {
      fclose(inputFile);
      return string;
    }
  }

  // Checks for option and stores correct value in string
  if(option == 's') {
    string = buf[2];
  } else if (option == 'U') {
    string = buf[13];
  } else if (option == 'S') {
    string = buf[14];
  }

  // Closes file
  fclose(inputFile);

  // Returns string to be printed
  return string;
}

/* Function to retrieve information on proc/<pid>/cmdline file.
 *
 * args - directory (dir) char array
 * return - cmdline char array.
 */
char* parseCMDLine(char * dir) {
  // Variables
  char* string = NULL;
  int file_size = 0;
  int index = 0;
  char temp;
  FILE *inputFile = fopen(dir, "r");

  // Checks if process exists
  if (inputFile == NULL) {
    perror("Input File Error");
    exit(1);
  }

  file_size = fileSize(dir);
  string = malloc(sizeof(char) * file_size);

  // Checks if memory was allocated
  if (string == NULL) {
    perror("Malloc Error");
    exit(1);
  }

  // Reads CMDLine value and stores it
  while(!feof(inputFile)) {
    temp = fgetc(inputFile);
    if (index != file_size) {
      string[index] = temp;
      index++;
    }
  }

  // Closes file
  fclose(inputFile);

  // Returns string
  return string;
}
