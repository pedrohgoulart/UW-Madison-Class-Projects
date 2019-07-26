///////////////////////////////////////////////////////////////////////////////
// Main File:         main
// This File:         build_spec_repr
// Other Files:       text_parsing, proc_creation_prog_exe, main,
//                    and build_spec_graph
// Semester:          CS 537 Fall 2018
//
// Authors:           Pedro Henrique Koeler Goulart, Varun Sreenivasan
// Emails:            koelergoular@wisc.edu, vsreenivasan@wisc.edu
// CS Logins:         koeler-goulart, sreenivasan
//
/////////////////////////// OTHER SOURCES OF HELP /////////////////////////////
//                   fully acknowledge and credit all sources of help,
//                   other than Instructors and TAs.
//
// Persons:          No persons.
//
// Online sources:   No online sources.
///////////////////////////////////////////////////////////////////////////////

#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include "text_parsing.h"
#include "build_spec_repr.h"


 /* Error handling function to check if malloc allocation was valid.
 *
 * Parameters: char string
 * Returns: Nothing. Display error and exits program if error is found.
 */
void checkMallocErrorType1(char* string) {
  if (string == NULL) {
    fprintf(stderr, "malloc error: allocation failed\n");
    exit(1);
  }
}

 /* Error handling function to check if malloc allocation was valid.
 *
 * Parameters: char string array
 * Returns: Nothing. Display error and exits program if error is found.
 */
void checkMallocErrorType2(char** string) {
  if (string == NULL) {
    fprintf(stderr, "malloc error: allocation failed\n");
    exit(1);
  }
}


/* Initializes and checks if build was successful.
 *
 * Parameters: Graph node of type S_repr
 * Returns: Nothing.
 */
void initializeAndCheckBuild(S_repr* current) {
  if (current != NULL) {
    if (access(current->target, F_OK) != -1) {
      current->finfo = malloc(sizeof(struct stat));

      if(current->finfo == NULL) {
        fprintf(stderr, "malloc error: allocation failed\n");
        exit(1);
      }

      if(stat(current->target, current->finfo) == -1) {
       fprintf(stderr, "%s: Stat error\n", current->target);
       exit(1);
      }
    }
    else {
      current->finfo = NULL;
    }
  }

}

/*
 * Initializes and checks if build was successful.
 *
 * Parameters: S_repr node, string line, number for setting node number.
 * Returns: Nothing.
 */
void splitTargetLine(S_repr * temp, char* line, int number) {
  // Gets target and stores it
  char* tar = strtok(line, ":");
  temp->target = malloc((strlen(tar) + 1)*sizeof(char));
  checkMallocErrorType1(temp->target);

  // Trims whitespace and tabs at the end of string
  int counter = 0;
  for (int i=(strlen(tar)-1); i >= 0; i--) {
    if (tar[i] == ' ' || tar[i] == '\t') {
      counter++;
    } else {
      break;
    }
  }
  tar[strlen(tar) - counter] = '\0';

  strncpy(temp->target, tar, strlen(tar)* sizeof(char));
  temp->target[strlen(tar)] = '\0';
  temp->nodeNumber = number;

  // Allocates memory for dependents
  char* token = strtok(NULL, " \t");
  int memCountFiles = 0;
  temp->num_Files = 0;
  temp->files = NULL;
  if(token != NULL) {
    temp->files = malloc(sizeof(char*));
    checkMallocErrorType2(temp->files);
    memCountFiles++;
  }

  while(token != NULL) {
    if (temp->num_Files == memCountFiles) {
        temp->files = realloc(temp->files, (memCountFiles + 1)* sizeof(char*));
	checkMallocErrorType2(temp->files);
        memCountFiles++;
    }

    temp->files[temp->num_Files++] = token;
    token = strtok(NULL, " \t");
  }
}

/*
 * Creates graph nodes (representation) as a 2D array.
 *
 * Parameters: Line group (which contains all lines and number of lines)
 * Returns: 2D Array of S_repr.
 */
S_repr** createRep(Lines* line_group) {
  //Allocate memory
  S_repr** arr = malloc(sizeof(S_repr*)* line_group->targets);

  if (arr == NULL) {
    fprintf(stderr, "malloc error: allocation failed\n");
    exit(1);
  }

  S_repr* temp = NULL;
  int targetCount = 0;
  int mem_Command = 0;

  for (int i = 0; i < line_group->size; i++) {
     if (line_group->lines[i][0] != '\t') {
      temp = malloc(sizeof(S_repr));

      if (temp == NULL) {
        fprintf(stderr, "malloc error: allocation failed\n");
        exit(1);
      }

      arr[targetCount] = temp;
      splitTargetLine(temp, line_group->lines[i],targetCount++);
      initializeAndCheckBuild(temp);
      temp->commands = malloc(sizeof(char*));
      checkMallocErrorType2(temp->commands);
      mem_Command = 1;
      temp->num_Commands=0;
    } else {
      if (temp == NULL) {
        fprintf(stderr, "malloc error: allocation failed\n");
        exit(1);
      }
      if (mem_Command == temp->num_Commands) {
        temp->commands = realloc(temp->commands, (++mem_Command)* sizeof(char*));
	checkMallocErrorType2(temp->commands);
      }
      char * com = line_group->lines[i];
      com++;
      temp->commands[temp->num_Commands++] = com;
    }
  }

  return arr;
}

/*
 * Frees the nodes.
 *
 * Parameters: S_repr pointer.
 * Returns: nothing.
 */
void freeNode(S_repr* rep) {
  if(rep != NULL) {
    if (rep->target != NULL) {
      free(rep->target);
    }

    if (rep->files != NULL) {
       free(rep->files);
    }

    if (rep->commands != NULL) {
      free(rep->commands);
    }

    if (rep->finfo != NULL) {
      free(rep->finfo);
    }

    free(rep);
  }
}
