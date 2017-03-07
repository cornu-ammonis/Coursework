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
        // TODO: fix .previous links as necessary
        // This is enqueue from LinkedQueue.java
        Node oldlast = last;
        last = new Node();
        last.item = item;
        last.next = null;
        if (isEmpty())
            first = last;
        else
            oldlast.next = last;


        //added previous link for new last element 
        last.previous = oldlast;
        ++N;
    }

    public void addFirst(Item item)
    {
        // TODO: fix .previous links as necessary
        // This is push from LinkedStack.java (also updating last)
        Node oldfirst = first;
        first = new Node();
        first.item = item;
        first.next = oldfirst;
        if (last == null)
            last = first;
        else
            oldfirst.previous = first; // added previous link for oldfirst
        ++N;
    }

    public Item removeFirst()
    {
        // TODO: fix .previous links as necessary
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
        if (last.previous == null) {  //if last.previous is null, we are removing the only item in the list 
            first = null;             //therefore set first and last to null
            last = null;
        }
        else {
            last = last.previous;
            last.next = null;      //eliminates only two references to the item we remove
        }

        N--;                        // deincrements count
        return item;

    }

    // Return a string representation: "[first, second, last]".
    public String toString()
    {
        // TODO: this takes time at least quadratic in N!
        // Modify it to use a StringBuilder, so it takes time
        // that is linear in the length of its output.
        String s = "[", sep = "";
        for (Item it : this) {
	    s += sep + it;
	    sep = ", ";
        }
	return s + "]";
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
        public void remove()
        {

            if (lastcurrent == null) throw new IllegalStateException(); // done: if called a second time will throw IllegalStateException
            // TODO(EC): implement this method.
            // It should be constant time.
            // Note remove() applies to the item last returned by
            // next().  The user should make at most one call to
            // remove() between two calls of next().  If remove()
            // is called a second time (without another next()), it
            // should throw an IllegalStateException. [DONE]

            if(lastcurrent.previous == null) 
                first = lastcurrent.next;
            else
                lastcurrent.previous.next = lastcurrent.next;
            
            if(lastcurrent.next == null)
                last = null;
            else
                lastcurrent.next.previous = lastcurrent.previous;

            lastcurrent = null;
        }
        public Item next()
        {
            if (!hasNext()) throw new NoSuchElementException();
	    lastcurrent = current;
            Item item = current.item;
            current = current.next;
            return item;
        }
    }

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
        throw new UnsupportedOperationException();
    }

    public void reverse()
    {
        // TODO(EC): implement this method.
        // You should reuse the existing Node objects, rather than
        // creating new ones.  You should modify their next/previous
        // links, not their item links.
        throw new UnsupportedOperationException();
    }
}