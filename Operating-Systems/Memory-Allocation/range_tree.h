#ifndef __range_tree_h__

#define __range_tree_h__

// Node structure
struct Node {
  void* addr; // Address of allocated/freed memory
  size_t len; // Size of allocated/freed memory
  int height; // Height of current node
  int is_free; // If node is freed memory or not
  struct Node *left; // Lower address children
  struct Node *right; // Higher address children
};

// Methods
struct Node* search_tree(struct Node* node, void* addr);
struct Node* add_node(struct Node* node, void* addr, size_t size);
struct Node* remove_node(struct Node* node, void* addr);
struct Node* garbage_collect(struct Node* node, void* addr, size_t size);

#endif

