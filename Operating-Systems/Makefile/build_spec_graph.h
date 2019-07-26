#ifndef __build_spec_graph_h__

#define __build_spec_graph_h__

#include "build_spec_repr.h"

typedef struct graph{
  S_repr** vertices;
  int numberofVertices;
} S_grph;

typedef struct list {
  char ** commands;
  int numberofCommands;
} commandList;

S_repr* getNode (S_grph* graph, char* file);
S_grph* graph_create (S_repr** vertexList, int targets);
int* cycle(S_grph * graph);
void post_order_traversal (S_grph * graph, S_repr* current, S_repr* parent, commandList* myList);
void run_process_creation (S_repr* current);
void freeGraph(S_grph* graph);

#endif
