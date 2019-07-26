///////////////////////////////////////////////////////////////////////////////
// Main File:         main
// This File:         main
// Other Files:       build_spec_graph, proc_creation_prog_exe, text_parsing,
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

#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include "text_parsing.h"
#include "build_spec_repr.h"
#include "build_spec_graph.h"
#include "proc_creation_prog_exe.h"

/*
 * Main method. Calls methods to read file, create the graph, and execute the
 * commands.
 *
 * Parameters: int argc (Number of arguments), char* argv[] (arguments array)
 * Return: 0 if successful
 */
int main(int argc, char *argv[]) {
  // Reads file and creates line group
  Lines* line_group = get_lines(argc, argv);

  // Turns lines into representation for graph
  S_repr** rep = createRep(line_group);

  // Builds graph using representation
  S_grph* graph = graph_create(rep, line_group->targets);

  // Executes commands according to graph
  execute(graph, argc, argv);

  // Frees the graph
  freeGraph(graph);

  // Frees line group
  free_lines(line_group);

  return 0;
}
