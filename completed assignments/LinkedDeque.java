/*
THIS CODE IS MY OWN WORK, IT WAS WRITTEN WITHOUT CONSULTING
A TUTOR OR CODE WRITTEN BY OTHER STUDENTS.  ___ANDREW C JONES 3/6/17____
*/


// HEY!
// DID YOU FILL IN YOUR NAME ABOVE?

// Homework: revise this file, see TODO items below.

// This LinkedDeque class is a linked list, and a partial
// implementation of the java.util.Deque interface.  It is already
// sufficient for its use by PathFinder.  You should improve it, by
// fixing the TODO items.

// TODO: make this a doubly-linked list.  We already declared a
// "previous" link in the Node class, but you need to properly
// maintain those links.

// All methods here (except toString() and reverse()) should run in
// constant time.   Iterators do not need to be fail-fast.

// As given, this is nearly a copy of LinkedQueue.java from our
// textbook.  We added the second link in Node, We removed check() and
// main().  We removed the "private" declarations, to allow
// testing your code from external classes (like Test1.java,
// which should still be able to compile when you are done).

// WARNING! Again, did you fill in your name at the top?


import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedDeque<Item> implements Deque<Item>
{
    // Not private, just to allow testing.
    int N;         // number of elements on deque
    Node first;    // beginning of deque
    Node last;     // end of deque

    // Linked list node.
    class Node
    {
        Item item;
        Node next;
        Node previous;          // new!
    }

    // Initialize empty deque
    public LinkedDeque()
    {
        first = null;
        last  = null;
        N = 0;
    }

    public boolean isEmpty() { return first == null; }

    public int size() { return N; }

    public void addLast(Item item)
    {
        // DONE: fix .previous links as necessary
        // This is enqueue from LinkedQueue.java
        Node oldlast = last;
        last = new Node();
        last.item = item;
        last.next = null;
        if (isEmpty())                 // queue was empty, first = last 
            first = last;
        else
            oldlast.next = last;


        //added previous link for new last element 
        last.previous = oldlast;
        ++N;
    }

    public void addFirst(Item item)
    {
        // DONE: fix .previous links as necessary
        // This is push from LinkedStack.java (also updating last)
        Node oldfirst = first;
        first = new Node();
        first.item = item;
        first.next = oldfirst;
        if (last == null)              // queue was empty, last = first
            last = first;
        else
            oldfirst.previous = first; // added previous link for oldfirst
        ++N;
    }

    public Item removeFirst()
    {
        // DONE: fix .previous links as necessary
        // This is dequeue from LinkedQueue.java
        if (isEmpty()) throw new NoSuchElementException();
        Item item = first.item;
        first = first.next;
        N--;
        if (isEmpty()) 
            last = null;
        else
            first.previous = null;   //sets first.previous to null if first exists 
        return item;
    }

    public Item removeLast()
    {
        // DONE: method implemented 
        if (isEmpty()) throw new NoSuchElementException();
        Item item = last.item;
        if (last.previous == null) {  // if last.previous is null, we are removing the only item
            first = null;             // in the list therefore set first and last to null
            last = null;
        }
        else {
            last = last.previous;
            last.next = null;        // eliminates only two references to the item we remove
        }

        N--;                         // deincrements count
        return item;

    }

    // Return a string representation: "[first, second, last]".
   public String toString()
    {
        // DONE: this takes time at least quadratic in N!
        // Modify it to use a StringBuilder, so it takes time
        // that is linear in the length of its output.
        String sep = "";
        StringBuilder str = new StringBuilder("[");
        for (Item it : this) {
	    str.append(sep + it);
	    sep = ", ";
        }
        str.append("]");
	return str.toString();
    }

    // A standard iterator (visits items from first to last).
    public Iterator<Item> iterator() { return new Iter(); }

    private class Iter implements Iterator<Item>
    {
        protected Node current = first;
        protected Node lastcurrent = null;
        public boolean hasNext()
        {
            return current != null;
        }
        
        // removes the element most recently returned by the next() method. 
        // if there is no such element, e.g. if the list is empty or there 
        // was more than one remove() call between next() calls, the method 
        // throws an IllegalStateException. 

        // removes element by setting its previous element's next point to 
        // its own next pointer, and its next element's previous pointer to 
        // its own previous pointer (if they exist, updating first and last
        // pointers as required)in effect removing all references to the item 
        // (so it is deleted by garbage collection)
        public void remove()
        {
            // done: if called a second time will throw IllegalStateException
            if (lastcurrent == null) throw new IllegalStateException(); 
            else
                N -= 1;
            // [DONE] (EC): implement this method.
            // It should be constant time.
            // Note remove() applies to the item last returned by
            // next().  The user should make at most one call to
            // remove() between two calls of next().  If remove()
            // is called a second time (without another next()), it
            // should throw an IllegalStateException. [DONE]

            // true if we are removing first element, so must 
            // update first pointer
            if(lastcurrent.previous == null) 
                first = lastcurrent.next;
            else 
                lastcurrent.previous.next = lastcurrent.next;
            
            // true if we are removing the last element
            if(lastcurrent.next == null)
                if (lastcurrent.previous == null) // if removing only element
                    last = null;
                else
                    last = lastcurrent.previous;
            
            // otherwise we arent removing last element so update next 
            // element's previous pointer
            else
                lastcurrent.next.previous = lastcurrent.previous;

            // finally remove temp pointer reference to avoid loitering
            // and correctly throw IllegalStateException if called again
            // before next()
            lastcurrent = null;
            
        }

        // returns next item for iterator
        public Item next()
        {   
            // next should never be called once iterator reaches end
            if (!hasNext()) throw new NoSuchElementException();
	        
            // we are returning current and also updating current, 
            // track what we returned in lastcurrent in case client
            // needs to remove it subsequently
            lastcurrent = current;
            Item item = current.item;
            current = current.next;   // current steps forward
            return item;
        }
    }

    //same as iterator but goes in reverse order 
    private class RevIter extends Iter
    {
        public RevIter() { this.current = last; }
        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
	    lastcurrent = current;
            Item item = current.item;
            current = current.previous;
            return item;
        }
          
    }

    // A reverse iterator (visits items from last to first).
    public Iterator<Item> descendingIterator()
    {
        // TODO: implement this method.
        return new RevIter();
    }

    public void reverse()
    {
        // DONE(EC): implement this method.
        // You should reuse the existing Node objects, rather than
        // creating new ones.  You should modify their next/previous
        // links, not their item links.
        if (first == null || last == null || first.equals(last))
            return;


        // switches next and previous pointers, beginning with the 
        // last element and continuing in the loop if there are more 
        Node oldfirst = first;
        Node i = last;
        first = last;
        i.next = i.previous;
        i.previous = null; //always null because this is the last element becoming the first element, means temp is not required
        i = i.next;
        

        while(i!= null && i.next != null){
            Node temp;
            temp = i.next;
            i.next = i.previous;
            i.previous = temp;
            i = i.next;

        }
        //makes sure what was previously "first" is set to "last"
        last = oldfirst;
    }


    //testing 
    public static void main(String[] args){
        LinkedDeque<String> testq = new LinkedDeque<String>();
        

        
        testq.addFirst("hi");
        
        testq.addLast("there");
        testq.addLast("sup?");
        Iterator<String> testiter = testq.iterator();
         System.out.println(testiter.next());

        System.out.println(testq.toString());
        System.out.println(testiter.next());
        testq.reverse();
        System.out.println(testiter.next());
        System.out.println(testq.toString());
        testiter.remove();
        System.out.println(testq.toString());
        System.out.println(testiter.next());
        testiter.remove();
        System.out.println(testq.toString());

         System.out.println(testiter.next());
        testiter.remove();
        System.out.println(testq.toString());
        /*
        Iterator<String> reviter = testq.descendingIterator();
        System.out.println(reviter.next());
        reviter.remove();
        System.out.println(testq.toString());*/



       /*
        System.out.println(testiter.next());
        testiter.remove();

        System.out.println(testiter.next());
        testiter.remove();
        System.out.println(testiter.next());
        testiter.remove();
        System.out.println(testiter.next()); */
    }
}