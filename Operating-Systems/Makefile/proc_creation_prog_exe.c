///////////////////////////////////////////////////////////////////////////////
// Main File:         main
// This File:         proc_creation_prog_exe
// Other Files:       text_parsing, build_spec_repr, main,
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
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/wait.h>
#include "build_spec_graph.h"

/*
 * Function creates an array of strings from a command string and forks
 * to execute the passed command.
 *
 * Parameters: command string
 * Returns: 0 on success.
 */
int create_process (char* command) {
  // Checks if command is valid
  if (command == NULL) {
    fprintf(stderr, "Process Creation Error: null command found\n");
    exit(1);
  }

  // Checks number of commands
  int size = 2;
  int length = strlen(command);
  for (int i = 0; i < length; i++) {
    if (command[i] == ' ' || command[i] == '\t') {
      size++;
    }
  }

  // Command variable array of strings
  int index = 0;
  char** commands = malloc(sizeof(char*)*size);

  if (commands == NULL) {
    fprintf(stderr, "malloc error: allocation failed\n");
    exit(1);
  }

  // Copies line to line_split for checking
  char* element = malloc(sizeof(char)*(strlen(command) + 1));
  char* temp = element;
  if (element == NULL) {
    fprintf(stderr, "malloc error: allocation failed\n");
    exit(1);
  }

  strncpy(element, command, strlen(command));
  element[strlen(command)] = '\0';

  // Gets first element
  element = strtok(element, " \t");

  // Creates string array from command
  while (element != NULL) {
    // Adds command to commands array and increments size
    commands[index] = malloc(sizeof(char)*(strlen(element)+1));
    if (commands[index] == NULL) {
      fprintf(stderr, "malloc error: allocation failed\n");
      free(temp);
      exit(1);
    }
    strncpy(commands[index], element, strlen(element)); // Copies line
    commands[index][strlen(element)] = '\0'; // Adds null terminator at end of line
    index++;
    // Gets next element
    element = strtok(NULL, " \t");
  }

  free(temp);
  commands[index] = '\0'; // Adds null terminator at end of array

  // Prints command being executed
  printf("%s\n", command);

  // Forks and creates processes
  int proc = fork();

  if (proc < 0) {
    // Fork error
    fprintf(stderr, "Fork of '%s' failed\n", command);
    exit(1);
  } else if (proc == 0) {
    // Child process execution
    int execute = -1;
    if (commands[0] != NULL && commands != NULL) {
      execute = execvp(commands[0], commands);

      for(int i = 0; i < index; i++)
      {
        free(commands[i]);
      }
      free(commands); // Free on fail
    }
    if (execute == -1) {
      fprintf(stderr, "Execution of '%s' failed\n", command);
      exit(2); // Signals parent
    }
  } else {
    // Parent process execution (wait)
    int status;
    wait(&status);

    // Checks if process execution was successful
    if (WIFEXITED(status)) {
       // Checks if exit of child process was successful
       if (WEXITSTATUS(status)) {
        fprintf(stderr, "Proccess execution failed\n");
        exit(1);
      }
    } else {
      fprintf(stderr, "Proccess execution failed\n");
      exit(1);
    }
    for(int i = 0; i < index; i++)
    {
      free(commands[i]);
    }
    free(commands);
  }

  return 0;
}

/*
 * Function calls post order traversal on root, which calls createProc for
 * each node.
 *
 * Parameters: graph of type S_grph
 * Returns: Nothing.
 */
void execute(S_grph* graph, int argc, char* target[]) {
  // Checks for cycles in the graph
  int* track = cycle(graph);
  if(track != NULL) {
    fprintf(stderr, "Build Graph Error: a cycle was detected\n");
    fprintf(stderr, "List of nodes causing circular dependency: ");
    for(int i = 0; i < graph->numberofVertices; i++)
    {
      if(track[i])
      {
        printf("%s ", graph->vertices[i]->target);
      }

    }
    printf("\n");
    free(track);
    exit(1);
  }
   commandList * myList = malloc(sizeof(commandList));

   if (myList == NULL) {
     fprintf(stderr, "Malloc Error: Unable to allocate command list\n");
     exit(1);
   }

   myList->commands = NULL;
   myList->numberofCommands = 0;


  // Gets target from string
  char* target_arg = NULL;

  for (int i = 1; i < argc; i++) {
    if (strcmp(target[i], "-f") == 0) {
      target_arg = NULL;
      i += 1;
    } else {
      target_arg = target[i];
    }
  }

  // Saves commands to be executed
  if (target_arg == NULL) {
    // Starts traversal on root (first line)
    post_order_traversal(graph, graph->vertices[0], NULL,myList);
  } else {
    // Starts traversal on target
    S_repr* vertex = getNode (graph,target_arg);
    if (vertex == NULL) {
      fprintf(stderr, "%s: target not found\n", target_arg);
      exit(1);
    }
    post_order_traversal(graph, vertex, NULL, myList);
  }

  // Runs commands
  for (int i=0; i < myList->numberofCommands; i++) {
    create_process(myList->commands[i]);
  }

  // Checks if file is up to date
  if (myList->numberofCommands == 0) {
    if (target_arg == NULL) {
      printf("'%s' is up to date\n", graph->vertices[0]->target);
    } else {
      printf("'%s' is up to date\n", target_arg);
    }
  }

  if(myList != NULL)
  {
    if(myList->commands != NULL)
    {
      for(int i = 0; i < myList->numberofCommands; i++)
      {
        free(myList->commands[i]);
      }

      free(myList->commands);
    }

    free(myList); 
  }

}

