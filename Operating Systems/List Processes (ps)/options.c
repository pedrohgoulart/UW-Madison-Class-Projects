///////////////////////////////////////////////////////////////////////////////
// Main File:         options
// This File:         options
// Other Files:       processList and stats
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
#include "stats.h"
#include "processList.h"

/* Function to store directory information in preset variable.
 *
 * args - directory char array variable, pid char array, subdirecoty
 * char array.
 */
void getDir(char * directory, char * pid, char * subDir) {
  strcpy(directory,"/proc/");
  strcat(directory, pid);
  strcat(directory, subDir);
}

/* Output function. Prints process information variables or error messages
 * according to display options.
 *
 * args - pid char array and integer display options (s, U, S, v, c).
 */
void printPS(char* pid, int s, int U, int S,int v, int c) {
  // Variable to store directory location
  char dir[80];

  // Gets and prints value if display option is enabled
  getDir(dir, pid, "/stat");
  printf("%s: ", pid);

  if (s) {
    printf("%s ", parseStat('s',dir)); // State
  }

  if (U) {
    printf("utime=%s ", parseStat('U', dir)); // User time
  }

  if (S) {
    printf("stime=%s ", parseStat('S', dir)); // System time
  }

  if (v) {
    getDir(dir, pid, "/statm");
    printf("vmemory=%d ", parseStatm(dir)); // Virtual memory
  }

  if (c) {
    getDir(dir, pid, "/cmdline");
    printf("[%s] ", parseCMDLine(dir)); // Command-line
  }
  printf("\n");
}

/* Main method. Reads input and calls functions for the program.
 *
 * args - number of arguments (argc) integer and arguments char array.
 */
int main(int argc, char *argv[]) {
  // Variables
  char* pid = NULL;
  int has_pid = 0;
  int arguments;
  char prevChar;

  // Default display options (0 = do not display)
  int d_s = 0; // Single character state (s)
  int d_U = 1; // Ammount of user time (U)
  int d_S = 0; // System time consumed (S)
  int d_v = 0; // Virtual memory (v)
  int d_c = 1; // Command-line that started program (c)

  // Checks user input and updates display options
  while ((arguments = getopt(argc, argv, "p:sUSvc-")) != -1) {
    switch (arguments) {
      case 'p':
        pid = optarg;
        has_pid = 1;
        break;
      case 's':
        d_s = 1;
        break;
      case 'U':
        d_U = 1;
        break;
      case 'S':
        d_S = 1;
        break;
      case 'v':
        d_v = 1;
        break;
      case 'c':
        d_c = 1;
        break;
      case '-':
        prevChar = argv[optind - 1][1];
        if (prevChar == 's') {
          d_s = 0;
        } else if (prevChar == 'U') {
          d_U = 0;
        } else if (prevChar == 'S') {
          d_S = 0;
        } else if (prevChar == 'v') {
          d_v = 0;
        } else if (prevChar == 'c') {
          d_c = 0;
        }
        break;
      default:
        printf("Check README for valid options \n");
        exit(1); // Exits program (getopt shows invalid char message)
    }
  }

  // Checks if pid was provided
  if (has_pid) {
    // Checks if selected process exists and prints it
    // uid_match may return 1 or -1 if the process exists
    if (uid_match(pid)) {
      printPS(pid, d_s,  d_U,  d_S, d_v, d_c);
    } else {
      printf("%s: Proccess could not be found \n", pid);
      printf("Check README for valid options \n");
      exit(1);
    }
  } else {
    // Retrieves list of processes and prints them
    char** arr = userProcesses();

    // Prints processes
    for(int i = 0; i < totalProcesses; i++) {
      printPS(arr[i], d_s, d_U, d_S, d_v, d_c);
    }

    // Frees allocated variables
    for(int j = 0; j < totalProcesses; j++) {
      free(arr[j]);
    }

    free(arr);
  }
}
