///////////////////////////////////////////////////////////////////////////////
// Main File:         main
// This File:         build_spec_graph
// Other Files:       text_parsing, proc_creation_prog_exe, main,
//                    and build_spec_repr
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

#include <sys/types.h>
#include <stdio.h>
#include <unistd.h>
#include <time.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include "build_spec_repr.h"
#include "build_spec_graph.h"
#include "proc_creation_prog_exe.h"

/*
 * Searches graph and returns node which corresponds to searched target.
 *
 * Parameters: S_grph graph and file string (target) to be searched for.
 * Returns: S_repr node or NULL (if nothing found).
 */
S_repr* getNode (S_grph* graph, char* file) {
  for (int i = 0; i < graph->numberofVertices; i++) {
    if (strcmp(file, graph->vertices[i]->target) == 0) {
      return graph->vertices[i];
    }
  }

  return NULL;
}

/*
 * Adds leaf nodes to the graph.
 *
 * Parameters: S_grph graph and file string (target).
 * Returns: nothing.
 */
void addNodeToGraph(S_grph* graph, char* file) {
  if (file != NULL) {
    // Malloc for node
    S_repr* temp = malloc(sizeof(S_repr));

    if(temp == NULL)
    {
      fprintf(stderr, "malloc error: allocation failed\n");
      exit(1);
    }

    // Adds target info
    temp->target = malloc(sizeof(char)* (strlen(file)+1));

    if(temp->target == NULL)
    {
      fprintf(stderr, "malloc error: allocation failed\n");
      exit(1);
    }

    strncpy(temp->target,file, strlen(file));
    temp->target[strlen(file)] = '\0';

    // Adds number of nodes
    temp->nodeNumber = graph->numberofVertices;

    // Adds files information
    temp->num_Files = 0;
    temp->files = NULL;

    // Adds commands information
    temp->num_Commands = 0;
    temp->commands = NULL;

    // Adds finfo
    temp->finfo = malloc(sizeof(struct stat));

    if (temp->finfo == NULL) {
      fprintf(stderr, "malloc error: allocation failed\n");
      exit(1);
    }

    graph->vertices = realloc(graph->vertices,(++graph->numberofVertices)*
        sizeof(S_repr*));

    if (graph->vertices == NULL) {
      fprintf(stderr, "malloc error: allocation failed\n");
      exit(1);
    }

    graph->vertices[temp->nodeNumber] = temp;
  }
}

/*
 * Creates the graph and connects nodes.
 *
 * Parameters: S_repr array and number of targets.
 * Returns: S_grph graph.
 */
S_grph* graph_create (S_repr** vertexList, int targets) {
  // Allocates memory for graph
  S_grph* graph = malloc(sizeof(S_grph));

  if(graph == NULL) {
    fprintf(stderr, "malloc error: allocation failed\n");
    exit(1);
  }

  // Initializes vertices
  graph->vertices = vertexList;
  graph->numberofVertices = targets;


  char* file = NULL;

  //Add nodes that are not targets
  for (int i = 0; i < targets; i++) {
    for (int j = 0; j < graph->vertices[i]->num_Files; j++) {
      file =  graph->vertices[i]->files[j];
      addNodeToGraph(graph, file);
    }
  }

  for (int i = 0; i < graph->numberofVertices; i++) {
    graph->vertices[i]->needsBuild = 0;
  }

  return graph;
}

/*
 * Function to detect graph cycles. Recursive function.
 *
 * Parameters: S_grph graph, node number, array of visited nodes, and array
 * to keep track of nodes.
 * Returns: 0 if no cycles and 1 if there are cycles
 */
int detectCycle (S_grph* graph,int vertexNumber, int visited[], int track[]) {
  visited[vertexNumber] = 1;
  track[vertexNumber] = 1;

  for (int i = 0; i < graph->vertices[vertexNumber]->num_Files; i++) {
    S_repr* neighbor = getNode(graph, graph->vertices[vertexNumber]->files[i]);
    if (neighbor != NULL) {
      if (visited[neighbor->nodeNumber] == 0) {
        if (detectCycle(graph, neighbor->nodeNumber,visited, track)) {
	  return 1;
	}
      }

      if(track[neighbor->nodeNumber]) {
	 return 1;
      }
    }
  }

  track[vertexNumber] = 0;
  return 0;
}

/*
 * Function to call the recursive function detectCycle.
 *
 * Parameters: S_grph graph
 * Returns: 0 if no cycles and 1 if there are cycles
 */
int* cycle (S_grph * graph) {
  int* visited = calloc(graph->numberofVertices, sizeof(int));
  int* track = calloc(graph->numberofVertices, sizeof(int));
 
  for(int i = 0;i < graph->numberofVertices; i++) {
    if(detectCycle(graph,i,visited, track)) {
      free(visited);
      return track;
    }
  }

  free(track);
  free(visited);
  return NULL;
}

/*
 * Checks if root needs to be run (updated).
 *
 * Parameters: S_grph graph, current node
 * Returns: 0 if does not need to run and 1 if it needs to
 */
int checkRootRun (S_grph* graph, S_repr* current) {
  if (current->num_Commands == 0) {
    return 0;
  } else if (current->finfo == NULL) {
    current->needsBuild = 1;
    return 1;
  }

  S_repr* successor = NULL;

  for (int i = 0; i < current->num_Files;i++) {
    successor = getNode(graph, current->files[i]);
    if (successor->needsBuild) {
      current->needsBuild = 1;
      return 1;
    }
    if (successor != NULL) {
      double diff_t = difftime(successor->finfo->st_mtime,
        current->finfo->st_mtime);

      if (diff_t > 0) {
        current->needsBuild = 1;
        return 1;
      }
    }
  }

  return 0;
}

/*
 * Checks if nodes (not root or leaf) needs to be run (updated).
 *
 * Parameters: S_grph graph, current node
 * Returns: 0 if does not need to run and 1 if it needs to
 */
int checkMid (S_grph* graph,S_repr* current){
  if (current->finfo == NULL) {
      current->needsBuild = 1;
      return 1;
  }

  S_repr* successor = NULL;

  for (int i = 0; i < current->num_Files;i++) {
    successor = getNode(graph, current->files[i]);

    if (successor != NULL) {
      if (successor->needsBuild) {
	current->needsBuild = 1;
        return 1;
      }
      double diff_t = difftime(successor->finfo->st_mtime,
        current->finfo->st_mtime);

      if (diff_t > 0) {
        current->needsBuild = 1;
        return 1;
      }
    }
  }

  return 0;
}

/*
 * Check if there are any problems with leaf nodes.
 *
 * Parameters: current node, parent node
 * Returns: nothing. Exists if there are any errors.
 */
int checkLeaf (S_repr* current, S_repr* parent) {

   if (access(current->target, F_OK) == -1) {
     if (current->num_Commands == 0) {
       fprintf(stderr, "%s: File not found\n", current->target);
       if (parent != NULL) {
         fprintf(stderr, "Recipe for target '%s' failed\n", parent->target);
       }else
       {
         printf("Nothing to be done for '%s'\n", current->target);
       }

       exit(1);
     } else {
       return 1;
     }
   }

   if(stat(current->target, current->finfo) == -1) {
      fprintf(stderr, "%s: Stat error\n", current->target);
      exit(1);
   }

   return 0;
}

/*
 * Check if there are any problems with leaf nodes.
 *
 * Parameters: current node, parent node
 * Returns: nothing. Exists if there are any errors.
 */
void addCommands (char* command, commandList* myList) {
  int size = myList->numberofCommands;

  if (size == 1) {
    myList->commands = malloc(sizeof(char*));
  } else {
    myList->commands = realloc(myList->commands,sizeof(char*)*(size));
  }

  if (myList->commands == NULL) {
    fprintf(stderr, "Malloc Error\n");
    exit(1);
  }

  myList->commands[size-1] = malloc((strlen(command)+1) * sizeof(char));
  if (myList->commands[size-1] == NULL) {
    fprintf(stderr, "Malloc Error\n");
    exit(1);
  }

  strncpy(myList->commands[size-1], command, strlen(command));
  myList->commands[size-1][strlen(command)]= '\0';
}

/*
 * Traverses the graph on post order. Recursive method.
 *
 * Parameters: S_grph graph, current node, parent node.
 * Returns: nothing.
 */
void post_order_traversal (S_grph* graph, S_repr* current, S_repr* parent,
      commandList* myList) {
  // Checks if node exists
  if (current == NULL) {
    return;
  }

  // Checks leaf Node
  if (current->num_Files == 0) {
    if (checkLeaf(current, parent)) {
      for (int i = 0; i < current->num_Commands; i++) {
        (myList->numberofCommands)++;
        addCommands(current->commands[i], myList);
      }

    }
    return;
  }

  S_repr* successor = NULL;

  // Calls recursive
  for (int i = 0; i < current->num_Files; i++) {
     successor = getNode(graph, current->files[i]);
     post_order_traversal(graph, successor, current, myList);
  }

  // Checks root Node
  if (parent == NULL) {
    if (checkRootRun(graph, current)) {
      for (int i = 0; i < current->num_Commands; i++) {
        (myList->numberofCommands)++;
        addCommands(current->commands[i], myList);
      }
    }
  } else {
    if (checkMid (graph,current)) {
       for (int i = 0; i < current->num_Commands; i++) {
        (myList->numberofCommands)++;
        addCommands(current->commands[i], myList);
      }
    }
  }
}

/*
 * Frees graph.
 *
 * Parameters: S_grph graph.
 * Returns: nothing.
 */
void freeGraph(S_grph* graph) {
  if (graph != NULL) {
    if (graph->vertices != NULL) {
      for (int i = 0; i < graph->numberofVertices; i++) {
        if (graph->vertices[i] != NULL) {
	  freeNode(graph->vertices[i]);
	}
      }
      free(graph->vertices);
    }

    free(graph);
  }
}
