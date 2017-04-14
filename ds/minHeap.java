

class MinHeap<K extends Comparable<K>>
{
	
	// heap defaults to a size of 20, it will double its size as needed
	private static final int defaultCapcity = 20;
	

	// count always points to the first empty slot in the heap array,
	// so it starts at 0
	private int count = 0;
	

	// heap is stored in an array. an item at index i has 
	// two children, found at 2*i + 1 and 2*i + 2
	// each element has one parent, found at (i-1)/2 (rounded down)
	// except for position 0, which has no parent. the thing at position 0
	// will be the smallest thing in the array. each parent will be smaller 
	// than both its children. 
	// when we add or remove something, we have to rearrnge elements 
	// to restablish the heap property, this is done in methods swim and sink so make
	// sure you understand those 


	// an array of K - so if its initialized as MinHeap<Integer> myHeap = new MinHeap<Integer>, 
	// itll be an array of ints 
	private K[] heap;


	//constructors cast Comparable[] as K[] so we supress warnings so that compiler doesnt complain
	// ;)
	@SuppressWarnings("unchecked")
	public MinHeap(int capacity)
	{
		heap = (K[]) new Comparable[capacity];
	}

	@SuppressWarnings("unchecked")
	public MinHeap()
	{
		heap = (K[]) new Comparable[defaultCapcity];
	}

	// add an item to the heap. we first add it to the first open position
	// in the array (at index count)
	// but this may violate the heap property, so we call swim 
	// which will swap the new item with its parent if it is smaller with its parent
	// until it is either no longer smaller than its parents or it is at the top of the heap 
	// (which will only happen if it is the smallest thing in the heap )
	public void add (K item)
	{
		if (count > heap.length)
			throw new IllegalStateException("count pointer error");
		if(count == heap.length)
			doubleHeapSize();

		heap[count] = item;
		swim(count++);
	}

	// remove the smallest item from the heap. we store it in a temporary value, then we move
	// the last item in the heap to position 0. this would only be valid if teh last item were somehow 
	// the smallest remaining item (it wont be unless its a heap of size 2)
	// so we then "sink" that item down until it is in the proper position 
	// (i.e. until it is smaller than all its children)
	public K removeMin()
	{
		K toReturn = heap[0];
		heap[0] = heap[--count];
		heap[count] = null;

		if (count > 0)
			sinkFirstElement();

		return toReturn;
	}

	// exactly what it sounds like
	public boolean isEmpty()
	{
		return count == 0;
	}


	//swaps element at i with element at j
	private void swap (int i, int j)
	{
		K tmp = heap[i];
		heap[i] = heap[j];
		heap[j] = tmp;
	}



	// the while loop will check if teh item at position i is smaller than its parent. 
	// if its smaller than its parent, it will swap that item with its parent, update the index
    // to the new position (one "level" up), and then once again check if its smalelr than its new parent
    // it will keep doing this until we either get to index 0 (the top of the heap)
    // or until the items parent is smaller than it  
	private void swim(int i)
	{

		// while we arent at the top of the heap
		while(i > 0)
		{	

			//index of my parent
			int parentIndex = (i-1)/2;

			// if im smaller tahn my parent, swap myself iwth my parent and update 
			// the index to my new position
			if (heap[i].compareTo(heap[parentIndex]) < 0)
			{
				swap(i, parentIndex);
				i = parentIndex;
			}

			//otherwise, we are done, leave the loop
			else
				break;

		}
	}

	// the while loop will check if the first is larger than either of its children;
	// if it is, it will swap it with the smallst child (which will become the smallest item
	// of the heap, at the top of the heap). 
	// the loop then updates the index and checks if that item is larger than any of its children 
	// again (think of this as one layer deeper)
	// it keeps doing that until there are no more children (we're at the bottom of the heap)
	// or until the item which was in the first position is now in a position where its smaller than 
	// both children (re established the heap property)
	private void sinkFirstElement()
	{
		int i = 0;

		// i.e., while "index of my children" 
		// is within the bounds of the heap
		while ((2*i) + 1 < count)
		{

			// j = "index of my first child"
			int j = 2*i + 1;

			//if the second child exists and is smaller select it instead
			if (j + 1 < count && heap[j+1].compareTo(heap[j]) < 0)
				j++; //make j point to second child


			//if current element is smaller than both its children we are finished
			if (heap[i].compareTo(heap[j]) <= 0) break;

			//otherwise swap it with its smallest child
			swap(i, j);

			//repeat with the original element now in its child's position
			i = j;
		}
	}





	@SuppressWarnings("unchecked")
	private void doubleHeapSize()
	{
		K[] newHeap = (K[]) new Comparable[heap.length * 2];

		for(int i = 0; i < count; i++)
			newHeap[i] = heap[i];

		heap = newHeap;
	}

	
}