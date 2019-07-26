///////////////////////////////////////////////////////////////////////////////
// Main File:         537malloc
// This File:         range_tree
// Other Files:       no other files
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
// Online sources:   GeeksforGeeks "AVL Tree | Set 2 (Deletion)" for AVL tree
//                   insertion and deletion (add_node and remove_node) methods.
//                   Link: https://geeksforgeeks.org/avl-tree-set-2-deletion/
///////////////////////////////////////////////////////////////////////////////

#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include "range_tree.h"

static void** GC_LIST = NULL; // Garbage collect list
static int GC_SIZE = 0; // Garbage collect list size

/*
 * Allocates memory for and initializes node.
 *
 * Parameters: addr (memory address to be added), size_t size (size of memory
 * to be allocated).
 * Return: node struct.
 */
struct Node* initializeNode(void* addr, size_t size) {
  struct Node* node = (struct Node*)malloc(sizeof(struct Node));
  node->addr = addr;
  node->len = size;
  node->height = 1;
  node->is_free = 0;
  node->left = NULL;
  node->right = NULL;

  return node;
}

/*
 * Checks which value is higher and returns it.
 *
 * Parameters: a (first int), b (second int)
 * Return: int with higher value
 */
int checkMax(int a, int b) {
  if (a >= b) {
    return a;
  } else {
    return b;
  }
}

/*
 * Gets height of node checking if node exists.
 *
 * Parameters: node
 * Return: height of node
 */
int get_height(struct Node* node) {
  if (node == NULL) {
    return 0;
  }

  return node->height;
}

/*
 * Gets the balance factor of the nodes by calculating the difference between
 * height of children nodes.
 *
 * Parameters: node.
 * Return: numeric difference in height between children nodes.
 */
int calculate_balance(struct Node *node) {
  if (node == NULL) {
    return 0;
  }

  return get_height(node->left) - get_height(node->right);
}

/*
 * Rotates part of the tree to make it balanced.
 *
 * Parameters: Node to be rotated, int r (0 = left, else = right)
 * Return: New node (node_b) in place of provided node (node_a).
 */
struct Node* rotate_tree(struct Node* node_a, int r) {
  struct Node* node_b = NULL;
  // Checks which rotation should be done
  if (r == 0) {
    // Rotate left
    node_b = node_a->right;
    node_a->right = node_b->left;
    node_b->left = node_a;
  } else {
    // Rotate right
    node_b = node_a->left;
    node_a->left = node_b->right;
    node_b->right = node_a;
  }

  // Update heights
  node_a->height = checkMax(get_height(node_a->left),
      get_height(node_a->right)) + 1;
  node_b->height = checkMax(get_height(node_b->left),
      get_height(node_b->right)) + 1;

  return node_b;
}

/*
 * Checks balance of neighborhood of node and if it needs to be rotated.
 *
 * Parameters: node to be checked, addr (memory address to be added), balance
 * (balance level)
 * Return: node struct.
 */
struct Node* check_balance_tree(struct Node* node, void* addr, int balance) {
  if (balance == 0) {
    return node;
  } else if (balance > 1 && (addr < node->left->addr)) {
    return rotate_tree(node, 1); // Right rotate
  } else if (balance < -1 && (addr > node->right->addr)) {
    return rotate_tree(node, 0); // Left rotate
  } else if (balance > 1 && (addr > node->left->addr)) {
    node->left = rotate_tree(node->left, 0); // Left rotate
    return rotate_tree(node, 1); // Right rotate
  } else if (balance < -1 && (addr < node->right->addr)) {
    node->right = rotate_tree(node->right, 1); // Right rotate
    return rotate_tree(node, 0); // Left rotate
  }

  return node; // If no match, returns same node
}

/*
 * Searches tree and sees if node exists.
 *
 * Parameters: node to be searched for and address to be matched with.
 * Return: node (NULL if no match found).
 */
struct Node* search_tree(struct Node* node, void* addr) {
  if (node == NULL) {
    return node;
  }

  if (addr < node->addr) {
    return search_tree(node->left, addr);
  } else if (addr >= (node->addr + node->len)) {
    return search_tree(node->right, addr);
  } else {
    return node;
  }
}

/*
 * Searches for the left-most node on the tree (node with the smallest addr
 * value).
 *
 * Parameters: Node
 * Return: Smallest node value (addr) on tree.
 */
struct Node* search_minNode(struct Node* node) {
  struct Node* curr = node;

  while(curr->left != NULL) {
    curr = curr->left;
  }

  return curr;
}

/*
 * Creates node and adds it to the tree.
 *
 * Parameters: node, addr (memory address to be added), size_t size (size
 * of memory to be allocated).
 * Return: node created.
 */
struct Node* add_node(struct Node* node, void* addr, size_t size) {
  // Found where to add node
  if (node == NULL) {
    node = initializeNode(addr, size);
    return node;
  }

  // Searches for node
  if (addr < node->addr) {
    node->left = add_node(node->left, addr, size);
  } else if (addr >= (node->addr + node->len)) {
    node->right = add_node(node->right, addr, size);
  } else if (node->is_free) {
    // Handles case where address in within freed node range
    if (node->addr != addr) {
      // Updates freed node
      node->len = (addr - node->addr);
      // Add newly alloc'd node to right child
      node->right = add_node(node->right, addr, size);
    } else {
      // Update malloc'd node
      node->len = size;
      node->is_free = 0;
      return node;
    }
  } else {
    // Prints error message if address is already allocated
    fprintf(stderr, "Add node error: memory already allocated\n");
    exit(1);
  }

  // Update height
  node->height = checkMax(get_height(node->left), get_height(node->right))+1;

  // Checks if tree is balanced
  int check_balance = 0;

  if (node != NULL) {
    check_balance = (get_height(node->left) - get_height(node->right));
  }

  // Fixes balance of tree if needed
  return check_balance_tree(node, addr, check_balance);
}

/*
 * Deletes node from the tree and re-structures it.
 *
 * Parameters: node, addr (memory address to be deleted).
 * Return: node removed.
 */
struct Node* remove_node (struct Node* node, void* addr) {
  if (node == NULL) {
    return node;
  }

  if (addr < node->addr) {
    node->left = remove_node(node->left, addr);
  } else if(addr > node->addr) {
    node->right = remove_node(node->right, addr);
  } else {
    //node with no children
    if (node->left == NULL && node->right == NULL) {
      node = NULL;
    } else if (node->left == NULL && node->right != NULL) {
      struct Node* rep = node->right;
      free(node);
      node = rep;
    } else if (node->left != NULL && node->right == NULL) {
      struct Node* rep = node->left;
      free(node);
      node = rep;
    } else {
      struct Node* successor = search_minNode(node->right);
      node->addr = successor->addr;
      node->len = successor->len;
      node->is_free = successor->is_free;
      node->right = remove_node(node->right, successor->addr);
    }
  }

  if (node == NULL) {
    return node;
  }

  node->height = checkMax(get_height(node->left), get_height(node->right)) + 1;

  int check_balance = 0;

  if (node != NULL) {
    check_balance = calculate_balance(node);
  }

  if (check_balance > 1 && calculate_balance(node->left) >= 0) {
    return rotate_tree(node,1); // Right rotate
  } else if (check_balance > 1 && calculate_balance(node->left) < 0) {
    node->left = rotate_tree(node->left,0); // Left rotate
    return rotate_tree(node,1); // Right rotate
  } else if (check_balance < -1 && calculate_balance(node->right) <= 0) {
    return rotate_tree(node,0); // Left rotate
  } else if (check_balance < -1 && calculate_balance(node->right) > 0) {
     node->right = rotate_tree(node->right,1); // Right rotate
     return rotate_tree(node,0); // Left rotate
  }

  return node;
}

/*
 * Searches tree and adds nodes to GC_LIST (garbage collect list) if needed.
 *
 * Parameters: node to be searched for, address and sizes to be matched with.
 * Return: nothing.
 */
void garbage_search(struct Node* node, void* addr, size_t size) {
  if (node == NULL) {
    return;
  }

  if (node->is_free) {
    if ((node->addr >= addr) && (node->addr < (addr + size))) {
      if (GC_SIZE != 0) {
        // Realocate size of list
        GC_LIST = realloc(GC_LIST, sizeof(void*)*(GC_SIZE+1));
      } else {
        // Initialize list
        GC_LIST = malloc(sizeof(void*)*1);
      }
      // Add node to list and increment list size
      GC_LIST[GC_SIZE] = node->addr;
      GC_SIZE++;
    }
  }

  if (node->addr >= addr) {
    garbage_search(node->left, addr, size);
  }

  if (node->addr < (addr + size)) {
    garbage_search(node->right, addr, size);
  }
}

/*
 * Checks if freed nodes should be "garbage collected" (deleted) from the tree.
 * This happens if newly allocated node overlaps with existing freed nodes on
 * the tree. This method adds nodes to GC_LIST (garbage collect list) and then calls
 * remove_node on a loop.
 *
 * Parameters: node to be searched for, address and sizes to be matched with.
 * Return: nothing.
 */
struct Node* garbage_collect(struct Node* node, void* addr, size_t size) {
  garbage_search(node, addr, size);

  for(int i = 0; i < GC_SIZE; i++) {
    node = remove_node(node, GC_LIST[i]);
  }

  if (GC_SIZE != 0) {
    free(GC_LIST);
    GC_SIZE = 0;
    GC_LIST = NULL;
  }

  return node;
}
