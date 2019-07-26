////////////////////////////////////////////////////////////////////////////////
// Main File:        Makefile
// This File:        mem.c
// Other Files:      no other files
// Semester:         CS 354 Spring 2018
//
// Author:           Pedro Henrique Koeler Goulart
// Email:            koelergoular@wisc.edu
// CS Login:         koeler-goulart
//
/////////////////////////// OTHER SOURCES OF HELP //////////////////////////////
//                   fully acknowledge and credit all sources of help,
//                   other than Instructors and TAs.
//
// Persons:          No persons.
//
// Online sources:   No online sources.
////////////////////////////////////////////////////////////////////////////////

#include <stdio.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/mman.h>
#include <string.h>
#include "mem.h"

/*
 * This structure serves as the header for each allocated and free block
 * It also serves as the footer for each free block
 * The blocks are ordered in the increasing order of addresses
 */
typedef struct blk_hdr {
        int size_status;

    /*
    * Size of the block is always a multiple of 8
    * => last two bits are always zero - can be used to store other information
    *
    * LSB -> Least Significant Bit (Last Bit)
    * SLB -> Second Last Bit
    * LSB = 0 => free block
    * LSB = 1 => allocated/busy block
    * SLB = 0 => previous block is free
    * SLB = 1 => previous block is allocated/busy
    *
    * When used as the footer the last two bits should be zero
    */

    /*
    * Examples:
    *
    * For a busy block with a payload of 20 bytes (i.e. 20 bytes data + an additional 4 bytes for header)
    * Header:
    * If the previous block is allocated, size_status should be set to 27
    * If the previous block is free, size_status should be set to 25
    *
    * For a free block of size 24 bytes (including 4 bytes for header + 4 bytes for footer)
    * Header:
    * If the previous block is allocated, size_status should be set to 26
    * If the previous block is free, size_status should be set to 24
    * Footer:
    * size_status should be 24
    *
    */
} blk_hdr;

/* Global variable - This will always point to the first block
 * i.e. the block with the lowest address */
blk_hdr *first_blk = NULL;

/*
 * Note:
 *  The end of the available memory can be determined using end_mark
 *  The size_status of end_mark has a value of 1
 *
 */

/*
 * Function for allocating 'size' bytes
 * Returns address of allocated block on success
 * Returns NULL on failure
 * Here is what this function should accomplish
 * - Check for sanity of size - Return NULL when appropriate
 * - Round up size to a multiple of 8
 * - Traverse the list of blocks and allocate the best free block which can accommodate the requested size
 * - Also, when allocating a block - split it into two blocks
 * Tips: Be careful with pointer arithmetic
 */
void* Mem_Alloc(int size) {
    // Checks if size is valid
    if ((size <=0) || (size > 4088)) {
        return NULL;
    }

    // Converts size to a multiple of 8
    size += 4;

    if ((size % 8) != 0) {
        size += (8 - (size % 8));
    }

    // Variables for keeping track of memory positions
    int hdr_size = (first_blk -> size_status);
    int blk_size = (hdr_size >> 2) << 2;
    blk_hdr* curr_pos = first_blk;
    blk_hdr* saved_pos = NULL;
    int saved_pos_size = 0;

    while (1) {
        // Checks if end block is reached
        if (hdr_size == 1) {
            break;
        }

        // Checks if block is free and size perfectly matches
        if (((hdr_size & 1) == 0) && (blk_size == size)) {
            saved_pos = curr_pos;
            break;
        }

        // Checks if no positions have been saved, and if block is free and fits
        if ((saved_pos == NULL) && ((hdr_size & 1) == 0) && (blk_size > size)) {
            saved_pos = curr_pos;
            saved_pos_size = blk_size;
        } else if ((saved_pos != NULL) && ((hdr_size & 1) == 0) &&
                (blk_size < saved_pos_size)) {
            // Checks if block is free and less than currently saved block
            saved_pos = curr_pos;
            saved_pos_size = blk_size;
        }

        // Updates variables
        curr_pos += (blk_size/sizeof(blk_hdr));
        hdr_size = (curr_pos -> size_status);
        blk_size = (hdr_size >> 2) << 2;
    }

    // Returns NULL if nothing was found
    if (saved_pos == NULL) {
        return NULL;
    }

    // Updates variables to saved position
    hdr_size = (saved_pos -> size_status);
    blk_size = (hdr_size >> 2) << 2;

    // Checks if block needs to be split
    if ((blk_size - size) >= 8) {
        // Updates header of currently allocated block
        blk_hdr* blk_allc = saved_pos;
        (blk_allc -> size_status) = size + (hdr_size & 2) + 1;

        // Creates a header for new free block
        blk_hdr* free_blk = saved_pos + (size/sizeof(blk_hdr));
        int free_blk_size = blk_size - size;
        (free_blk -> size_status) = free_blk_size + 2;

        // Creates a footer for new free block
        blk_hdr* free_blk_f = free_blk + ((free_blk_size - 4)/sizeof(blk_hdr));
        (free_blk_f -> size_status) = free_blk_size;

    } else {
        // Updates header of currently allocated block
        blk_hdr* blk_allc = saved_pos;
        (blk_allc -> size_status) = hdr_size + 1;

        // Updates header of next block
        blk_hdr* next_blk = blk_allc + (blk_size/sizeof(blk_hdr));
        (next_blk -> size_status) += 2;
    }

    return (saved_pos + 1);
}

/*
 * Function for freeing up a previously allocated block
 * Argument - ptr: Address of the block to be freed up
 * Returns 0 on success
 * Returns -1 on failure
 * Here is what this function should accomplish
 * - Return -1 if ptr is NULL
 * - Return -1 if ptr is not 8 byte aligned or if the block is already freed
 * - Mark the block as free
 * - Coalesce if one or both of the immediate neighbours are free
 */
int Mem_Free(void *ptr) {
    // Checks if the block is NULL
    if (ptr == NULL) {
        return -1;
    }

    // Block to be freed
    blk_hdr* blk_addr = ptr - 4;

    int hdr_size = (blk_addr -> size_status);
    int blk_size = (hdr_size >> 2) << 2;

    // Checks if block is not 8-bit aligned or already freed
    if ((blk_size % 8 != 0) || ((hdr_size & 1) == 0)) {
        return -1;
    }

    // Marks block as free
    (blk_addr -> size_status) = blk_addr -> size_status - 1;

    // Variables for coalescing
    blk_hdr* next_blk_addr = blk_addr + blk_size/sizeof(blk_hdr);

    int next_hdr_size = (next_blk_addr -> size_status);
    int next_blk_size = (next_hdr_size >> 2) << 2;

    // Checks if NEXT block can be coalesced
    if ((next_hdr_size != 1) && ((next_hdr_size & 1) == 0)) {
        // Update footer of next block
        blk_hdr* next_blk_f = next_blk_addr +
            ((next_blk_size - 4)/sizeof(blk_hdr));
        (next_blk_f -> size_status) = blk_size + next_blk_size;

        // Update header and size
        (blk_addr -> size_status) += next_blk_size;
        blk_size += next_blk_size;
    } else {
        // Creates footer on current block
        blk_hdr* blk_f1 = blk_addr + ((blk_size - 4)/sizeof(blk_hdr));
        (blk_f1 -> size_status) = blk_size;

        (next_blk_addr -> size_status) = next_hdr_size - 2;
    }

    // Checks if PREVIOUS block can be coalesced
    if (((hdr_size & 2) == 0) && (blk_addr != first_blk)) {
        int prev_blk_size = ((blk_addr - 1) -> size_status);
        blk_hdr* prev_blk_addr = blk_addr - (prev_blk_size/sizeof(blk_hdr));

        // Updates previous block header
        int prev_hdr_size = (prev_blk_addr -> size_status);
        (prev_blk_addr -> size_status) = blk_size + prev_hdr_size;

        // Creates footer
        blk_hdr* blk_f2 = blk_addr + ((blk_size - 4)/sizeof(blk_hdr));
        (blk_f2 -> size_status) = prev_blk_size + blk_size;
    } else {
        // Creates footer on next block
        blk_hdr* blk_f2 = blk_addr + ((blk_size - 4)/sizeof(blk_hdr));
        (blk_f2 -> size_status) = blk_size;

        (next_blk_addr -> size_status) = next_hdr_size - 2;
    }

    return 0;
}

/*
 * Function used to initialize the memory allocator
 * Not intended to be called more than once by a program
 * Argument - sizeOfRegion: Specifies the size of the chunk which needs to be allocated
 * Returns 0 on success and -1 on failure
 */
int Mem_Init(int sizeOfRegion) {
    int pagesize;
    int padsize;
    int fd;
    int alloc_size;
    void* space_ptr;
    blk_hdr* end_mark;
    static int allocated_once = 0;

    if (0 != allocated_once) {
        fprintf(stderr,
        "Error:mem.c: Mem_Init has allocated space during a previous call\n");
        return -1;
    }
    if (sizeOfRegion <= 0) {
        fprintf(stderr, "Error:mem.c: Requested block size is not positive\n");
        return -1;
    }

    // Get the pagesize
    pagesize = getpagesize();

    // Calculate padsize as the padding required to round up sizeOfRegion
    // to a multiple of pagesize
    padsize = sizeOfRegion % pagesize;
    padsize = (pagesize - padsize) % pagesize;

    alloc_size = sizeOfRegion + padsize;

    // Using mmap to allocate memory
    fd = open("/dev/zero", O_RDWR);
    if (-1 == fd) {
        fprintf(stderr, "Error:mem.c: Cannot open /dev/zero\n");
        return -1;
    }
    space_ptr = mmap(NULL, alloc_size, PROT_READ | PROT_WRITE, MAP_PRIVATE,
                    fd, 0);
    if (MAP_FAILED == space_ptr) {
        fprintf(stderr, "Error:mem.c: mmap cannot allocate space\n");
        allocated_once = 0;
        return -1;
    }

     allocated_once = 1;

    // for double word alignement and end mark
    alloc_size -= 8;

    // To begin with there is only one big free block
    // initialize heap so that first block meets
    // double word alignement requirement
    first_blk = (blk_hdr*) space_ptr + 1;
    end_mark = (blk_hdr*)((void*)first_blk + alloc_size);

    // Setting up the header
    first_blk->size_status = alloc_size;

    // Marking the previous block as busy
    first_blk->size_status += 2;

    // Setting up the end mark and marking it as busy
    end_mark->size_status = 1;

    // Setting up the footer
    blk_hdr *footer = (blk_hdr*) ((char*)first_blk + alloc_size - 4);
    footer->size_status = alloc_size;

    return 0;
}

/*
 * Function to be used for debugging
 * Prints out a list of all the blocks along with the following information i
 * for each block
 * No.      : serial number of the block
 * Status   : free/busy
 * Prev     : status of previous block free/busy
 * t_Begin  : address of the first byte in the block (this is where the header starts)
 * t_End    : address of the last byte in the block
 * t_Size   : size of the block (as stored in the block header) (including the header/footer)
 */
void Mem_Dump() {
    int counter;
    char status[5];
    char p_status[5];
    char *t_begin = NULL;
    char *t_end = NULL;
    int t_size;

    blk_hdr *current = first_blk;
    counter = 1;

    int busy_size = 0;
    int free_size = 0;
    int is_busy = -1;

    fprintf(stdout, "************************************Block list***\
                    ********************************\n");
    fprintf(stdout, "No.\tStatus\tPrev\tt_Begin\t\tt_End\t\tt_Size\n");
    fprintf(stdout, "-------------------------------------------------\
                    --------------------------------\n");

    while (current->size_status != 1) {
        t_begin = (char*)current;
        t_size = current->size_status;

        if (t_size & 1) {
            // LSB = 1 => busy block
            strcpy(status, "Busy");
            is_busy = 1;
            t_size = t_size - 1;
        } else {
            strcpy(status, "Free");
            is_busy = 0;
        }

        if (t_size & 2) {
            strcpy(p_status, "Busy");
            t_size = t_size - 2;
        } else {
            strcpy(p_status, "Free");
        }

        if (is_busy)
            busy_size += t_size;
        else
            free_size += t_size;

        t_end = t_begin + t_size - 1;

        fprintf(stdout, "%d\t%s\t%s\t0x%08lx\t0x%08lx\t%d\n", counter, status,
        p_status, (unsigned long int)t_begin, (unsigned long int)t_end, t_size);

        current = (blk_hdr*)((char*)current + t_size);
        counter = counter + 1;
    }

    fprintf(stdout, "---------------------------------------------------\
                    ------------------------------\n");
    fprintf(stdout, "***************************************************\
                    ******************************\n");
    fprintf(stdout, "Total busy size = %d\n", busy_size);
    fprintf(stdout, "Total free size = %d\n", free_size);
    fprintf(stdout, "Total size = %d\n", busy_size + free_size);
    fprintf(stdout, "***************************************************\
                    ******************************\n");
    fflush(stdout);

    return;
}
