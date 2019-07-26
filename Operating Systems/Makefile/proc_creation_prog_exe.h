#ifndef __proc_creation_prog_exe_h__

#define __proc_creation_prog_exe_h__

#include "build_spec_graph.h"

int create_process(char* command);
int execute(S_grph* graph, int argc, char* target[]);

#endif
