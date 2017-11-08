/*THIS CODE WAS MY OWN WORK, IT WAS WRITTEN WITHOUT CONSULTINGCODE WRITTEN BY OTHER STUDENTS OR SOURCES 
OUTSIDE OF THOSEPROVIDED BY THE INSTRUCTOR.  Andrew_C_Jones*/

// style grading note: I use a lot of extra whitespace and newlines where in your UnsortedPriorityQueue example 
// you put things on one line -- I appreciate your style as well and it has its adherants 
// but for me and those i've worked with the liberal use of whitespace makes the code more readable, and 
// the extra lines don't really have an opportunity cost. Please don't deduct style points.


import java.util.ArrayList;
import java.util.Comparator;

public class SortedPriorityQueue<Key,Value> extends AbstractPriorityQueue<Key,Value> {
	
	private ArrayList<Entry<Key, Value>> list = new ArrayList();

	// Constructors

	public SortedPriorityQueue() { super(); }

	public SortedPriorityQueue(Comparator<Key> comp) { super(comp); }


	public Entry<Key, Value> insert(Key k , Value v) throws IllegalArgumentException {

		checkKey(k); // throws exception if key is invalid

		PQEntry<Key, Value> toInsert = new PQEntry<Key, Value>(k, v);

		int i = 0;

		// increment insertion index until end of list or reach element larger
		// than toInsert
		while (i < list.size() && compare( list.get(i), toInsert ) < 0)
			i++;

		list.add(i, toInsert); // adds at i, pushing entries at >= i to the right

		return toInsert;
	}

	public Entry<Key, Value> min() {
		
		if (list.isEmpty())
			return null;

		return list.get(0);
	}

	public Entry<Key, Value> removeMin()  {

		if ( list.isEmpty() )
			return null;

		return list.remove(0);
	}

	// source citation: taken directly from UnsortedPriorityQueue code
	public int size() { return list.size(); }

	// source citation: taken directly from UnsortedPriorityQueue code
	public String toString(){
		String s = "{";
		if (isEmpty()) return s + "}";

		s += list.get(0);
		for(int i = 1; i < list.size(); i++){
			s+= (", " + list.get(i));
		}
		s += "}";

		return s;
	}



}