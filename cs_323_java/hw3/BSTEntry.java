
/* =================================================================
Entry structure

Added: height to determine if the BST is balanced
================================================================= */

public class BSTEntry {
   public String  key;
   public Integer value;
   public int     height;

   public BSTEntry parent;
   public BSTEntry left;
   public BSTEntry right;

   public BSTEntry(String k, Integer v) {
      key = k;
      value = v;

      parent = null;
      left = null;
      right = null;
   }

   public String toString() {
      return "(" + key + "," + value + ")";
   }
}
