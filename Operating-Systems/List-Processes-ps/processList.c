////////////////////////////////////////////////////////////////////////////////
// Main File:         options
// This File:         processList
// Other Files:       options and stats
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
#include <unistd.h>
#include <string.h>
#include <dirent.h>
#include "processList.h"
#include "stats.h"

// Global variable for total number of processes
int totalProcesses = 0;

// Pre-set variable used for buf[] array to check uid_match
static const int buf_rows_uid = 19;

/* This function stores the directory of the status file that is used to
 * compare UIDs.
 *
 * args - directory (dir) char array and filename
 */
void directory(char* dir, char * fileName) {
  strcpy(dir, "/proc/");
  strcat(dir, fileName);
  strcat(dir, "/status");
}

/* This function is used to help allocate a 2D array
 *
 * returns a pointer to char pointers
 * prints error message and exits program if allocation fails
 * args - size integer: Size of first dimension (rows)
 */
char ** allocate2D(int size) {
  //Tries to allocate memory for 2D Array
  char** arr = malloc(sizeof(char*) * size);

  // Checks for errors
  if (arr == NULL) {
    perror("Malloc Error");
    exit(1);
  }

  // Returns allocated array
  return arr;
}

/* This function returns the total number of processes to
 * help initially allocate the array that stores the the list of
 * processes.
 */
int numberOfProcs() {
  // Variables
  DIR *dir;
  struct dirent *entry;
  int size = 0;

  // Checks if directory is valid
  if((dir = opendir("/proc")) == NULL) {
    perror("Opendir Error");
    exit(1);
  }

  // Reads files from directory (numbered folders)
  while ((entry = readdir(dir)) != NULL) {
    if (atoi(entry->d_name)) {
      // Adding pid length and "/proc//status" string length (14)
      char statDir[strlen(entry->d_name) + 14];
      directory(statDir, entry->d_name);
      FILE *inputFile = fopen(statDir, "r");

      // Checks if file is readable and if UID matches
      if (inputFile != NULL) {
        if (uid_match(entry->d_name) == 1) {
          size++;
        }
      }

      // Closes file
      fclose(inputFile);
    }
  }

  // Closes the directory
  closedir(dir);

  // Returns number of processes
  return size;
}

/* This function returns the pointer to the 2D array
 * that stores the name of the files that will be parsed
 */
char ** userProcesses() {
  // Variables
  DIR *dir;
  struct dirent *entry;
  int index = 0;
  int size = numberOfProcs();
  char** arr = allocate2D(size);

  // Checks if directory is valid
  if ((dir = opendir("/proc")) == NULL) {
    perror("Opendir Error");
    exit(1);
  }

  // Reads files from directory (numbered folders)
  while ((entry = readdir(dir)) != NULL) {
    if (atoi(entry->d_name)) {
      // Adding pid length and "/proc//status" string length (14)
      char statDir[strlen(entry->d_name) + 14];
      directory(statDir, entry->d_name);
      FILE *inputFile = fopen(statDir, "r");

      // Checks if file is readable
      if (inputFile == NULL) {
        perror("Opendir Error");
        exit(1);
      }

      // Checks if array needs to be reallocated
      if (index >= size) {
        arr = realloc(arr, (size + 1) * sizeof(char*) );
        if (arr == NULL) {
          perror("Realloc Error");
          exit(1);
        }
        size++;
      }

      // Allocates memory for new array item
      arr[index] = malloc(sizeof(char) * (strlen(entry->d_name) + 1));

      // Checks if array item is valid
      if (arr[index] == NULL) {
        perror("Malloc Error");
        exit(1);
      }

      // Checks if current process matches UID
      if (uid_match(entry->d_name) == 1) {
        // Fills array item and updates index
        strcpy(arr[index], entry->d_name);
        index++;
      }

      // Closes file
      fclose(inputFile);
    }
  }
  // Updates total processes
  totalProcesses = index;

  // Closes directory
  closedir(dir);

  // Returns user processes array
  return arr;
}

/* This function checks if user id and the process UID are the same
 * return 1 if UIDs are the same and 0 if not.
 *
 * args - process id (pid) char array.
 * returns - an integer (2 = does not match, 1 = match, 0 = error)
 */
int uid_match(char* pid) {
  // Variables
  // Adding pid length and "/proc//status" string length (14)
  char statDir[strlen(pid) + 14];
  directory(statDir, pid);
  FILE *inputFile = fopen(statDir, "r");

  // Checks if file is readable
  if (inputFile == NULL) {
    return 0;
  }

  int file_size = fileSize(statDir);
  int uid = getuid(); // UID for current user
  char buf[buf_rows_uid][file_size]; // UID located in buf[18]

  // Stores values in buf
  for(int i = 0; i < buf_rows_uid; i++) {
    if(!fscanf(inputFile, "%s", buf[i])) {
      fclose(inputFile);
      return 0;
     }
  }

  // Closes file
  fclose(inputFile);

  // Checks if current process matches UID
  if (atoi(buf[18]) == uid) {
    return 1;
  } else {
    return -1;
  }
}
