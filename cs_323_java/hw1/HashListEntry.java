public class HashListEntry<K, V>  implements Entry<K, V> {
   public  K key; 
   public  V value; 

   public  HashListEntry<K,V> prev;
   public  HashListEntry<K,V> next;

   // ***************************************************
   // Constructor: 
   //    Initial the key and value variable using k and v
   //    and set prev and next to null
   // ***************************************************
   public HashListEntry(K k, V v) {

      this.key = k;
      this.value = v;
      this.prev = null;
      this.next = null;

   }

   // ***************************************************
   // getKey(): return the key in the entry
   // ***************************************************
   public K getKey() {

      return this.key;

   }

   // ***************************************************
   // getKey(): return the value in the entry
   // ***************************************************
   public V getValue() {

      return this.value;

   }

   public V setValue(V val) {
      V oldValue = value;

      value = val;                // Update value
      return oldValue;            // Return old value
   }

   public String toString() {
      return( "(" + key + "," + value + ")" );
   }
}