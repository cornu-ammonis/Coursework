
/*
THIS CODE WAS MY OWN WORK, IT WAS WRITTEN WITHOUT CONSULTING
CODE WRITTEN BY OTHER STUDENTS OR SOURCES OUTSIDE OF THOSE
PROVIDED BY THE INSTRUCTOR.  _Andrew_Christopher_Jones_
*/



@SuppressWarnings("unchecked")
public class HashListMap<K, V> implements Map<K, V> {
  public HashListEntry<K,V>[] bucket;   // The hash table (buckets)
  public int capacity;                  // capacity == bucket.length
  public int NItems;                    // NItems = # entries in hash table

  public int MAD_p;
  public int MAD_a;
  public int MAD_b;

  /* ===============================================================
  General Constructor:  initialize prime number p
    Create an array (bucket) of given size
  =============================================================== */
  public HashListMap(int p, int MapArraySize) {
    capacity = MapArraySize;
    bucket = new HashListEntry[ capacity ]; // Java can't create gen arrays
    NItems = 0;

    java.util.Random rand = new java.util.Random(1234);

    MAD_p = p;                              // Prime number in MAD
    MAD_a = rand.nextInt(MAD_p-1) + 1;      // Multiplier in MAD
    MAD_b = rand.nextInt(MAD_p);            // Shift amount in MAD
  }

  /* ===============================================================
  Common Constructor:  pick the default prime number 109345121
   Create an array (bucket) of given size
  =============================================================== */
  public HashListMap(int MapArraySize) {
    this( 109345121, MapArraySize );   // Calls general constructor
  }

  public HashListMap() {
    this( 109345121, 1000 );   // Default array size = 1000
  }


  public int size() {
    return NItems;
  }

  public boolean isEmpty() {
    return NItems==0;
  }

  /* ===============================================================
  Hash function:

  1. uses the Hash Code inside the K class
  2. uses the MAD compression function

  hash index = [ (a*HashCode + b) % p ] % N
  =============================================================== */
  public int hashValue(K key) {
    int x = key.hashCode();      // K has a built-in hashCode() !!!

    return (int) ((Math.abs(x*MAD_a + MAD_b) % MAD_p) % capacity);
  }

  // ****************************************************************
  // Todo: implement these 3 methods
  //
  //     1. insert()
  //     2. delete()
  //     3. findEntry()
  //
  //  See project description for details
  // ****************************************************************


  // ****************************************************************
  // insert(bucketID, e): insert entry e in bucket[bucketID]
  //
  // Write this method
  // ****************************************************************
  public void insert(int bucketID, HashListEntry<K, V> e) {

    if (bucket[bucketID] == null) 
      bucket[bucketID] = e;
    
    
    // already an entry; insert at end of linked list 
    else {

      HashListEntry<K, V> crawler = bucket[bucketID];

      // find end of linked list 
      while (crawler.next != null) 
        crawler = crawler.next;
      

      // insert
      crawler.next = e;
      e.prev = crawler;

    }

    NItems++;
  }


  // *****************************************************************
  // delete(bucketID, e): delete entry e from bucket[bucketID]
  //
  // Write this method
  // *****************************************************************
  public void delete(int bucketID, HashListEntry<K, V> p) {

    if (bucket[bucketID] == null) 
      throw new UnsupportedOperationException("attempted to delete an entry which does not exist.");
    

    HashListEntry<K, V> crawler = bucket[bucketID];

    while (crawler.next != null && !crawler.key.equals(p.key)) 
      crawler = crawler.next;
    

    // check that the entry on which we stopped is actually p 
    // I think that .equals() rather than == is correct here becasue K could be a reference type
    if ( !crawler.key.equals(p.key) ) 
      throw new UnsupportedOperationException("attempted to delete an entry which does not exist.");
    

    // deincrement count 
    NItems--;

    // was first element in list 
    if (crawler.prev == null) {
      bucket[bucketID] = crawler.next;
      
      if (crawler.next != null) 
        crawler.next.prev = null;
      

      return;
    }


    crawler.prev.next = crawler.next;

    // else it was last element in the list
    if (crawler.next != null) 
      crawler.next.prev = crawler.prev;
    

    return;
  }


  // *********************************************************************
  // findEntry(key): find entry for given key
  //
  // Return value:
  //
  //       The reference to entry that contains the given key
  //       If key is NOT found, return null
  //
  // Write this method
  // ********************************************************************* 
  public HashListEntry<K,V> findEntry(K key) {

    int bucketID = hashValue(key);

    // no list case 
    if (bucket[bucketID] == null) 
      return null;
    


    // some list case 
    HashListEntry<K, V> crawler = bucket[bucketID];

    // traverse until either found or end of list 
    while ( crawler != null && !crawler.key.equals(key) )
      crawler = crawler.next;
    
    // determine whether found or reached end of list 
    if ( crawler == null || !crawler.key.equals(key) ) 
      return null;
    else 
      return crawler;
    

  }



  /* ===========================================================
  get(), put() and remove() are very easily implemented
  using 

  findEntry()
  insert()
  delete()
  =========================================================== */

  public V get(K k){
    HashListEntry<K, V> e;

    e = findEntry(k);  // Find entry for the key k

    if ( e != null ) {
      return e.getValue();       // return value
    }
    else {
      return null;               // Key not found
    }
  }


  public V put (K k, V v) {
    HashListEntry<K, V> e;

    e = findEntry(k);  // Find entry for the key k

    if ( e != null ) {
      V oldValue = e.setValue(v);// set new value
      return ( oldValue );  // Return old value
    }
    else {
      int bucketID = hashValue( k );

      insert( bucketID, new HashListEntry<K,V>(k, v) );

      return null;
    }
  }


  public V remove(K k) {
    HashListEntry<K, V> e = findEntry(k);  // Find entry for the key k

    if ( e != null ) {
      V oldValue = e.getValue();

      int bucketID = hashValue( k );
      delete ( bucketID, e );

      return oldValue;
    }
    else {
      return null;               // Key not found
    }
  }

  public String toString() {
    boolean more;

    String output = "{";

    for (int i = 0; i < bucket.length; i++) {
      HashListEntry<K,V> e;

      e = bucket[i];

      if ( e != null && e.next != null ) {
        more = true;
        output += "[";
      }
      else
        more = false;

      while ((e != null) ) { 
        output = output + e + " ";
        e = e.next;
      }

      if ( more )
        output += "] ";
    }

    output += "} (" + NItems + " entries)";
    return output;
  }
}