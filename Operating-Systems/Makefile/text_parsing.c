///////////////////////////////////////////////////////////////////////////////
// Main File:         main
// This File:         text_parsing
// Other Files:       build_spec_graph, proc_creation_prog_exe, main,
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
#include <stdlib.h>
#include <unistd.h>
#include <ctype.h>
#include <string.h>
#include "text_parsing.h"
#include "proc_creation_prog_exe.h"

// Static variable for error handling
static int LINE_NUMBER = 1;
static int TARGETS = 0;
static int INDEX_SIZE = 128;
static const int BUF_CHAR_SIZE = 1024;

/*
 * Errror handling. Contains all error messages for this file.
 *
 * Parameters: error_number (error message to be printed), buf (line string)
 * Returns: Nothing. Displays errorm message and exits the program.
 */
void string_error(int error_number, char* string) {
  if (error_number == 1) {
    fprintf(stderr, "%d: Malloc Error: %s allocation error\n", LINE_NUMBER,
      string);
  } else if (error_number == 2) {
    fprintf(stderr, "%d: Invalid line: \"%s\"\n", LINE_NUMBER, string);
  } else if (error_number == 3) {
    fprintf(stderr, "String on line %d exceeds max number of chars (%d)\n",
      LINE_NUMBER, BUF_CHAR_SIZE);
  } else if (error_number == 4) {
    fprintf(stderr, "%s: File not found\n", string);
  } else if (error_number == 5) {
    fprintf(stderr, "%s: No targets specified\n", string);
  } else if (error_number == 6) {
    fprintf(stderr, "%d: Null character found on line\n", LINE_NUMBER);
  }
}

/*
 * Element validator. Checks if the element passed contains invalid characters.
 *
 * Parameters: element string
 * Return: int (0 is false, 1 is true)
 */
int valid_element (char* element) {
  // Checks if element is valid
  if (element == NULL) {
    return 0;
  }

  // Variable
  int i = 0;

  // Searches for illegal characters in element
  while (element[i] != '\0') {
    if (element[i] == ':' || element[i] == ' ' || element[i] == '\t') {
      return 0;
    }
    i++;
  }

  // Returns true if illegal characters not found
  return 1;
}

/*
 * Checks if passed line is empty.
 *
 * Parameters: line string
 * Return: int (0 is false, 1 is true)
 */
int valid_empty_line (char* line) {
  // Checks if line is valid
  if (line == NULL) {
    return 0;
  }

  // Variables
  int i = 0;
  int counter = 0;

  // Searches for illegal characters in line
  while (line[i] != '\0') {
    if (line[i] == ' ') {
      counter++;
    }
    i++;
  }

  int length = strlen(line);

  if (length == counter) {
    return 1;
  } else {
    return 0;
  }
}

/*
 * Command line validator. Checks if passed line is a valid command line.
 *
 * Parameters: line string
 * Return: int (0 is false, 1 is true)
 */
int valid_command_line (char* line) {
  // Checks if line is valid
  if (line == NULL) {
    return 0;
  }

  // Checks if element starts with tab character or has at least one target
  if (line[0] != '\t' || TARGETS == 0) {
    return 0;
  }

  // Copies line to line_split for checking
  char* line_split = NULL;
  line_split = malloc(sizeof(char)*BUF_CHAR_SIZE);
  if (line_split == NULL) {
    fprintf(stderr, "%d: Malloc Error: line split allocation error\n",
      LINE_NUMBER);
    exit(1);
  }
  strncpy(line_split, line, BUF_CHAR_SIZE);

  // Element variable
  char* element = NULL;

  // Gets first element
  element = strtok(line_split, " \t");

  // Checks if line is empty
  if (element == NULL) {
    free(line_split);
    return 0;
  }

  while (element != NULL) {
    // Checks if element is valid
    if (!valid_element(element)) {
      free(line_split);
      return 0;
    }
    // Gets next element
    element = strtok(NULL, " \t");
  }

  // Returns true
  free(line_split);
  return 1;
}

/*
 * Target line validator. Checks if passed line is a valid target line.
 *
 * Parameters: line string
 * Return: int (0 is false, 1 is true)
 */
int valid_target_line (char* line) {
  // Checks if line is valid
  if (line == NULL) {
    return 0;
  }

  // Checks if there is a single column (:) on the line
  int counter = 0;
  for (int i = 0; i < BUF_CHAR_SIZE; i++) {
    if (line[i] == '\0') {
      break;
    } else if (line[i] == ':') {
      counter++;
    }
  }

  if (counter < 1) {
    return 0;
  } else if (counter > 1) {
    string_error(2, line); // Prints string error and exit
    exit(1);
  }

  // Copies line to line_split for checking
  char* line_split = NULL;
  line_split = malloc(sizeof(char)*BUF_CHAR_SIZE);
  if (line_split == NULL) {
    fprintf(stderr, "%d: Malloc Error: line split allocation error\n",
      LINE_NUMBER);
    exit(1);
  }
  strncpy(line_split, line, BUF_CHAR_SIZE);
  char* temp = line_split;
  // Gets first part of line
  line_split = strtok(line_split, ":");

  if (line_split == NULL) {
    free(temp);
    return 0;
  }

  // Trims whitespace and tabs at the end of string
  counter = 0;
  for (int i=(strlen(line_split)-1); i >= 0; i--) {
    if (line_split[i] == ' ' || line_split[i] == '\t') {
      counter++;
    } else {
      break;
    }
  }
  line_split[strlen(line_split) - counter] = '\0';

  // Checks if first part of line is valid
  if (!valid_element(line_split)) {
    free(temp);
    return 0;
  }

  // Gets second part of line (if it exists), and checks if it is valid
  line_split = strtok(NULL, ":");

  if (line_split == NULL) {
    free(temp);
    return 1; // No files (second part of line)
  } else {
    // Gets elemenets
    line_split = strtok(line_split, " \t");

    while (line_split != NULL) {
      // Checks if element is valid
      if (!valid_element(line_split)) {
        free(temp);
        return 0;
      }
      // Gets next element
      line_split = strtok(NULL, " \t");
    }
  }

  // Returns true
  free(temp);
  return 1;
}

/*
 * Line reader function. Reads line and calls validation methods.
 *
 * Parameters: file
 * Return: char*
 */
char* line_reader(FILE *file) {
  // Variables
  char c;
  int buf_size = 0;
  char* line = NULL;

  // Allocates memory for line
  line = malloc(sizeof(char)*BUF_CHAR_SIZE);
  if (line == NULL) {
    string_error(1, "Line"); // Prints error and exits
    exit(1);
  }

  // Gets first character and adds it to line variable
  c = fgetc(file);

  if (c == EOF) {
    free(line);
    return NULL;
  }

  // Reads rest of line
  while (c != '\n') {
    // Checks if line size is within bounds
    if (buf_size >= BUF_CHAR_SIZE) {
      free(line);
      string_error(3, NULL); // Print string error and exit
      exit(1);
    } else if (c == '\0') {
      free(line);
      string_error(6, NULL); // Print string error and exit
      exit(1);
    }

    // Stores character
    if (line != NULL) {
      line[buf_size] = c;
    }

    // Gets next character
    buf_size++;
    c = fgetc(file);

    // Checks for inline EOF
    if (c == EOF) {
      break;
    }
  }

  if (line != NULL) {
    // Adds null character to end of string
    line[buf_size] = '\0';

    // Skip line if comment line
    if (line[0] == '#') {
      free(line);
      char* nLine = malloc(sizeof(char)*2);
      nLine[0] = '\n';
      nLine[1] = '\0';
      return nLine;

    }
  }

  // Checks if line and elements are valid
  if (valid_target_line(line)) {
    TARGETS++; // Increments targets
    return line;
  } else if (valid_command_line(line)) {
    return line;
  } else if (valid_empty_line(line)) {
    free(line);
    char* nLine = malloc(sizeof(char)*2);
    nLine[0] = '\n';
    nLine[1] = '\0';
    return nLine;

  } else {
    string_error(2, line); // Prints string error and exit
    exit(1);
  }

  return NULL;
}


/*
 * Initializes Line struct.
 *
 * Parameters: lines array of strings, total_size (size of lines)
 * Returns: Pointer to Lines structure
 */
Lines* create_line_group(char** buf, int total_size) {
  Lines* line_group = malloc(sizeof(Lines));
  if (line_group == NULL) {
    string_error(1, "Lines"); // Prints error and exits
    exit(1);
  }

  if (buf == NULL) {
    string_error(1, "Buf"); // Prints error and exits
    exit(1);
  }

  line_group->targets = TARGETS;
  line_group->size = total_size;
  line_group->lines = malloc(sizeof(char*)*total_size);

  if (line_group->lines == NULL) {
    string_error(1, "Lines"); // Prints error and exits
    exit(1);
  }

  // Fills lines and size variables
  for (int i=0; i < total_size; i++) {
    if (buf[i] != NULL) {
      line_group->lines[i] = malloc(sizeof(char)*BUF_CHAR_SIZE);
      if (line_group->lines[i] == NULL) {
        string_error(1, "Lines"); // Prints error and exits
        exit(1);
      }
      strncpy(line_group->lines[i],buf[i],BUF_CHAR_SIZE);
    }
  }

  return line_group;
}

/*
 * Opens makefile file, calls line_reader to read and validate lines, and
 * returns struct.
 *
 * Parameters: None.
 * Returns: Lines struct (lines array of strings and size of lines)
 */
Lines* get_lines(int argc, char *argv[]) {
  char* file_name;
  int arguments;
  int has_file_name = 0;

  // Checks user input and updates display options
  while ((arguments = getopt(argc, argv, "f:")) != -1) {
    switch (arguments) {
      case 'f':
        file_name = optarg;
        has_file_name = 1;
        break;
      default:
        printf("Check README for valid options \n");
        exit(1); // Exits program (getopt shows invalid char message)
    }
  }

  // Checks if file name was provided, if not, tries to open makefile
  if (!has_file_name) {
    if (access("makefile", F_OK) != -1) {
      file_name = "makefile";
    } else if (access("Makefile", F_OK) != -1) {
      file_name = "Makefile";
    } else {
      string_error(4, "Makefile"); // Prints error and exits
      exit(1);
    }
  }

  // Open file
  FILE *file = fopen(file_name, "r");

  if (file == NULL){
    string_error(4, file_name); // Prints error and exits
    exit(1);
  }

  // Allocates memory for buf
  char** buf = malloc(sizeof(char*)*INDEX_SIZE);
  if(buf == NULL) {
    string_error(1, "Buf"); // Prints error and exits
    exit(1);
  }

  // Adds lines to buf
  int index = 0;
  char* check_line = line_reader(file);

  while (check_line != NULL) {
    // Checks size and allocates more memory if needed
    if (index >= INDEX_SIZE) {
      buf = realloc(buf, (++INDEX_SIZE)*sizeof(char*));
      if (buf == NULL) {
	free(check_line);
	check_line = NULL;
        string_error(1, "realloc Lines"); // Prints error and exits
        exit(1);
      }
    }
    // Checks if line is empty and adds it to buf
    if (strcmp(check_line,"\n")) {
      if (buf != NULL) {
        buf[index] = malloc(sizeof(char)*(strlen(check_line)+1));
	strncpy(buf[index], check_line, strlen(check_line));
	buf[index][strlen(check_line)] = '\0';
	free(check_line);
	check_line = NULL;
        index++;
      }
    }
    // Gets next line
    LINE_NUMBER++;

    if(check_line != NULL)
    {
      free(check_line);
    }

    check_line = line_reader(file);
  }

  // Checks if there are targets in the file
  if (!TARGETS) {
    string_error(5, file_name); // Prints error and exits
    exit(1);
  }

  // Creates line_group
  Lines* line_group = create_line_group(buf, index);

  // Frees buf
  for (int i=0; i < index; i++) {
    if (buf[i] != NULL) {
      free(buf[i]);
    }
  }
  free(buf);

  return line_group;
}

/*
 * Frees Lines.
 *
 * Parameters: Lines* line group.
 * Returns: nothing.
 */
void free_lines(Lines* line_group) {
  // Frees line group
  if (line_group != NULL) {
    for( int i = 0; i < line_group->size; i++) {
      free(line_group->lines[i]);
    }

    free(line_group->lines);
    free(line_group);
  }
}
