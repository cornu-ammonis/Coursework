/*
THIS CODE WAS MY OWN WORK, IT WAS WRITTEN WITHOUT CONSULTING
CODE WRITTEN BY OTHER STUDENTS OR SOURCES OUTSIDE OF THOSE
PROVIDED BY THE INSTRUCTOR.  Andrew Christopher Jones
*/



/* ================================================================
   This is BST with "height" awareness

   Each node has a height to determine if the BST is balanced
   ================================================================ */

  public class BST {
  public BSTEntry root;	// References the root node of the BST

  public BST() {
    root = null;
  }

  public BSTEntry firstEntry(){
    BSTEntry current = root;
    while (current.left != null)
      current = current.left;

    return current;
  }

  public BSTEntry lastEntry(){
    BSTEntry current = root;

    while (current.right != null)
      current = current.right;

    return current;
  }

  //return the entry with the largest key value that is ≤ k
  public BSTEntry floorEntry(String key){

    BSTEntry current = root;
    BSTEntry ans = root;

    while (current != null) {

      // current is larger than the key so we must go left
      if ( key.compareTo( current.key ) < 0 ) {
        //ans = current; // remember previous 
        current = current.left; // go left
      }

      // current is smaller than key so go right
      else if ( key.compareTo( current.key ) > 0 )  {
        ans = current;
        current = current.right;
      }

      else // exact match
        return current;

    }
    
    return ans;
  }

  // return the entry with the smallest key value that is ≥ k
  public BSTEntry ceilingEntry(String key){

    BSTEntry current = root;
    BSTEntry ans = null;

    while (current != null) {

      // current is larger than key so it is the best answer we've seen
      // so far and we must go left
      if ( key.compareTo( current.key ) < 0 ) {

        ans = current; // save current since its best answer seen so far
        current = current.left; // go left
      }

      // current is smaller than the key so it is not a possible answer and
      // we must go right
      else if ( key.compareTo( current.key ) > 0 ) 
        current = current.right;
      
      else // exact match; no other answer is possible at this point so retrn
        return current;

    }

    return ans;    
  }

  // return the entry with the largest key value that is < k
  public BSTEntry lowerEntry(String key){
    
    BSTEntry current = root;
    BSTEntry ans = null;

    while (current != null) {
      
      // current is larger than or equal to key so it is not a possible 
      // answer and we must go left
      if ( key.compareTo( current.key ) <= 0 ) 
        current = current.left;

      // current is smaller than the key so it is best candidate
      // so far and we should go right
      else  {
        ans = current; // save current as it is potential answer
        current = current.right;
      }

    }

    return ans;
  }

  // return the entry with the smallest key value that is > k
  public BSTEntry upperEntry(String key){
    
    BSTEntry current = root;
    BSTEntry ans = null;

    while (current != null) {

      // current is larger than key so it is best answer 
      // seen so far and we should go left
      if ( key.compareTo( current.key ) < 0 ) {
        ans = current;
        current = current.left;
      }

      // current is less than or equal to key so it is not a 
      // potential answer and we should go right
      else 
        current = current.right;
    }

    return ans;
  }


  /* ================================================================
  findEntry(k): find entry with key k 

  Return:  reference to (k,v) IF k is in BST
   reference to parent(k,v) IF k is NOT in BST (for put)
  ================================================================ */
  public BSTEntry findEntry(String k) {
    BSTEntry curr_node;   // Help variable
    BSTEntry prev_node;   // Help variable

    /* --------------------------------------------
    Find the node with key == "k" in the BST
    -------------------------------------------- */
    curr_node = root;  // Always start at the root node
    prev_node = root;  // Remember the previous node for insertion

    while ( curr_node != null ) {
      if ( k.compareTo( curr_node.key ) < 0 ) {
        prev_node = curr_node;       // Remember prev. node
        curr_node = curr_node.left;  // Continue search in left subtree
      } 
      else if ( k.compareTo( curr_node.key ) > 0 ) {
        prev_node = curr_node;       // Remember prev. node
        curr_node = curr_node.right; // Continue search in right subtree
      } 
      else 
        // Found key in BST 
        return curr_node;
  
    }

    /* ======================================
    When we reach here, k is NOT in BST
    ====================================== */
    return prev_node;		// Return the previous (parent) node
  }

  /* ================================================================
  get(k): find key k and return assoc. value
  ================================================================ */
  public Integer get(String k) {
    BSTEntry p;   // Help variable

    /* --------------------------------------------
    Find the node with key == "key" in the BST
    -------------------------------------------- */
    p = findEntry(k);

    if ( k.equals( p.key ) )
      return p.value;
    else
      return null;
  }

  /* ================================================================
  put(k, v): store the (k,v) pair into the BST

  1. if the key "k" is found in the BST, we replace the val
  that is associated with the key "k"
  1. if the key "k" is NOT found in the BST, we insert
  a new node containing (k, v)
  ================================================================ */
  public void put(String k, Integer v) {
    BSTEntry p;   // Help variable

    /* ----------------------------------------------------------
    Just like linked list, insert in an EMPTY BST
    must be taken care off separately by an if-statement
    ---------------------------------------------------------- */
    if ( root == null ) {  
      // Insert into an empty BST
      root = new BSTEntry( k, v );
      root.height = 1;
      return;
    }

    /* --------------------------------------------
    Find the node with key == "key" in the BST
    -------------------------------------------- */
    p = findEntry(k);

    if ( k.equals( p.key ) ) {
      p.value = v;			// Update value
      return;
    }

    /* --------------------------------------------
    Insert a new entry (k,v) under p !!!
    -------------------------------------------- */
    BSTEntry q = new BSTEntry( k, v );
    q.height = 1;

    q.parent = p;

    if ( k.compareTo( p.key ) < 0 )
      p.left = q;            	// Add q as left child
    else 
      p.right = q;           	// Add q as right child

    /* --------------------------------------------
    Recompute the height of all parent nodes...
    -------------------------------------------- */
    recompHeight(p);
  }


  /* =======================================================
  remove(k): delete entry containg key k
  ======================================================= */
  public void remove(String k) {
    BSTEntry p, q;     // Help variables
    BSTEntry parent;   // parent node
    BSTEntry succ;     // successor node

    /* --------------------------------------------
    Find the node with key == "key" in the BST
    -------------------------------------------- */
    p = findEntry(k);

    if ( ! k.equals( p.key ) )
      return;			// Not found ==> nothing to delete....


    /* ========================================================
    Hibbard's Algorithm
    ======================================================== */

    if ( p.left == null && p.right == null ) {
      // Case 0: p has no children
      parent = p.parent;

      /* --------------------------------
      Delete p from p's parent
      -------------------------------- */
      if ( parent.left == p )
        parent.left = null;
      else
        parent.right = null;

      /* --------------------------------------------
      Recompute the height of all parent nodes...
      -------------------------------------------- */
      recompHeight(parent);

      return;
    }

    if ( p.left == null ){
      // Case 1a: p has 1 (right) child
      
      parent = p.parent;

      /* ----------------------------------------------
      Link p's right child as p's parent child
      ---------------------------------------------- */
      if ( parent.left == p )
        parent.left = p.right;
      else
        parent.right = p.right;

      /* --------------------------------------------
      Recompute the height of all parent nodes...
      -------------------------------------------- */
      recompHeight(parent);

      return;
    }

    if ( p.right== null ) {
      // Case 1b: p has 1 (left) child
      parent = p.parent;

      /* ----------------------------------------------
      Link p's left child as p's parent child
      ---------------------------------------------- */
      if ( parent.left == p )
        parent.left = p.left;
      else
        parent.right = p.left;

      /* --------------------------------------------
      Recompute the height of all parent nodes...
      -------------------------------------------- */
      recompHeight(parent);

      return;
    }

    /* ================================================================
    Tough case: node has 2 children - find successor of p

    succ(p) is as as follows:  1 step right, all the way left

    Note: succ(p) has NOT left child !
    ================================================================ */
    succ = p.right;			// p has 2 children....

    while ( succ.left != null )
      succ = succ.left;

    p.key = succ.key;		// Replace p with successor
    p.value = succ.value;


    /* --------------------------------
    Delete succ from succ's parent
    -------------------------------- */
    parent = succ.parent;

    if ( parent.left == succ )
      parent.left = succ.right;	// parent skip over succ ...
    else
      parent.right = succ.right;    // ... and point to succ's right child

    /* --------------------------------------------
    Recompute the height of all parent nodes...
    -------------------------------------------- */
    recompHeight(parent);

    return;

  }


  /* =======================================================
  Show what the BST look like....
  ======================================================= */

  public void printnode(BSTEntry x, int h) {
    for (int i = 0; i < h; i++)
      System.out.print("               ");

    System.out.print("[" + x.key + "," + x.value + "](h=" + x.height + ")");

    if ( diffHeight( x.left, x.right) > 1 )
      System.out.println("*");
    else
      System.out.println();
  }

  void printBST() {
    showR( root, 0 );
    System.out.println("================================");
  }

  public void showR(BSTEntry t, int h) {
    if (t == null)
      return;

    showR(t.right, h+1);
    printnode(t, h);
    showR(t.left, h+1);
  }


  /* ================================================================
  recompHeight(x): recompute height starting at x (and up)
  ================================================================ */
  public static void recompHeight( BSTEntry x ) {
    while ( x != null ) {
      x.height = maxHeight( x.left, x.right ) + 1;
      x = x.parent;
    }
  }


  /* ================================================================
  maxHeight(t1,t2): compute max height of 2 (sub)trees
  ================================================================ */
  public static int maxHeight( BSTEntry t1, BSTEntry t2 ) {
    int h1, h2;

    if ( t1 == null )
      h1 = 0;
    else
      h1 = t1.height;

    if ( t2 == null )
      h2 = 0;
    else
      h2 = t2.height;

    return (h1 >= h2) ? h1 : h2 ;
  }

  /* ================================================================
  diffHeight(t1,t2): compute difference in height of 2 (sub)trees
  ================================================================ */
  public static int diffHeight( BSTEntry t1, BSTEntry t2 ) {
    int h1, h2;

    if ( t1 == null )
      h1 = 0;
    else
      h1 = t1.height;

    if ( t2 == null )
      h2 = 0;
    else
      h2 = t2.height;

    return ((h1 >= h2) ? (h1-h2) : (h2-h1)) ;
  }
}