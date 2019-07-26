///////////////////////////////////////////////////////////////////////////////
// Main File:         537malloc
// This File:         537malloc
// Other Files:       range_tree
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
#include "range_tree.h"

static struct Node* MAIN_TREE = NULL; // Pointer to tree root

/*
 * Main functionality method (type 4).
 * Memory allocation method. Calls malloc and record a tuple (addr, len) for
 * memory allocated in the heap. Gets return value from malloc for address
 * (addr) and size parameter for length (len).
 *
 * Method will skip allocation if size is 0.
 *
 * Parameters: size_t size (size of memory to be allocated)
 * Return: address (void*) allocated.
 */
void *malloc537(size_t size) {
  // Allocates memory
  void *ptr = malloc(size);

  if (ptr == NULL) {
    fprintf(stderr, "Malloc537 error: memory allocation failed\n");
    exit(-1);
  }

  // Checks if size is 0 and shows warning to user
  if (size == 0) {
    fprintf(stderr, "Malloc537 warning: memory of size 0 was allocated\n");
  }

  // Creates an allocated node and adds it to the tree
  MAIN_TREE = add_node(MAIN_TREE, ptr, size);

  // Checks for nodes that should be deleted
  MAIN_TREE = garbage_collect(MAIN_TREE, ptr, size);

  return ptr;
}

/*
 * Main functionality method (type 4).
 * Memory free method. Checks if pointer can be freed and then calls free. This
 * method will check for and fail if:
 *  1. Attempts to free memory not allocated with malloc537
 *  2. Attempts to free memory that is not first byte range of allocated memory
 *  3. Attempts to free memory that has already been freed.
 *
 * Parameters: ptr (pointer to memory that should be freed)
 * Return: nothing, error (with exit code -1) if failed.
 *
*/
void free537(void *ptr) {
  if (ptr == NULL) {
    fprintf(stderr, "Free537 error: NULL memory pointer\n");
    exit(-1);
  }

  // Checks status of pointer
  struct Node* temp_node = search_tree(MAIN_TREE, ptr);

  if (temp_node == NULL) {
     fprintf(stderr, "Free537 error: not allocated by malloc537\n");
     exit(-1);
  } else if(temp_node->addr != ptr) {
    fprintf(stderr, "Free537 error: no element with provided starting address\n");
    exit(-1);
  } else if (temp_node->is_free) {
    fprintf(stderr, "Free537 error: double free detected\n");
    exit(-1);
  } else {
    temp_node->is_free = 1;
    // Frees pointer
    free(ptr);
  }
}

/*
 * Main functionality method (type 4).
 * Memory realocation method. Changes memory allocation by calling realloc and
 * updates tuple if it exists (deletes old one and adds new).
 *
 * This method will call malloc537 if ptr is NULL and call free537 if size is
 * zero but pointer is not NULL.
 *
 * Parameters: ptr (pointer to memory), size_t size (size of memory to be
 * reallocated).
 * Return: address (void*) reallocated.
 *
 */
void *realloc537(void *ptr, size_t size) {
  if (ptr == NULL) {
    ptr = malloc537(size);
    return ptr;
  } else if (size == 0) {
    free537(ptr);
    return NULL;
  }

  // Checks status of pointer
  struct Node* temp_node = search_tree(MAIN_TREE, ptr);

  // Reallocates memory
  ptr = realloc(ptr, size);

  if (ptr == NULL) {
    fprintf(stderr, "Realloc537 error: memory reallocation failed\n");
    exit(1);
  }

  if (temp_node->addr == ptr) {
    // Updates node in tree
    temp_node->len = size;
  } else {
    // Sets previous node as freed
    temp_node->is_free = 1;

    // Adds newly allocated node to the tree
    MAIN_TREE = add_node(MAIN_TREE, ptr, size);

    // Checks for nodes that should be deleted
    MAIN_TREE = garbage_collect(MAIN_TREE, ptr, size);
  }

  return ptr;
}

/*
 * Main functionality method (type 4).
 * Checks address range specified by the pointer and length allocated by
 * malloc537 and not yet freed by free537.
 *
 * Parameters: ptr (pointer to memory), size_t size (size of memory to be
 * allocated).
 * Return: nothing, error (with exit code -1) if failed.
 *
*/
void memcheck537(void *ptr, size_t size) {
  struct Node* temp_node = search_tree(MAIN_TREE, ptr);

  if (temp_node == NULL) {
    fprintf(stderr, "Memcheck537 error: memory not allocated with malloc537. Invalid address\n");
    exit(-1);
  }

  if((ptr < temp_node->addr) || (ptr >= temp_node->addr + temp_node->len)) {
    fprintf(stderr, "Memcheck537 error: malloc537 pointer address outside of allocated bounds\n");
    exit(-1);
  }

  if((ptr + size) > (temp_node->addr + temp_node->len)) {
    fprintf(stderr, "Memcheck537 error: malloc537 pointer address allocated size does not match\n");
    exit(-1);
  }

}
