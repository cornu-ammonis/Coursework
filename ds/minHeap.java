package ds;

class MinHeap<K extends Comparable<K>>
{
	private static final int defaultCapcity = 20;
	private int count;
	private K[] heap;

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

	public void add (K item)
	{
		if (count > heap.length)
			throw new IllegalStateException("count pointer error");
		if(count == heap.length)
			doubleHeapSize();

		heap[count] = item;
		swim(count++);
	}

	public K removeMin()
	{
		K toReturn = heap[0];
		heap[0] = heap[--count];
		heap[count] = null;

		if (count > 0)
			sinkFirstElement();

		return toReturn;
	}

	public boolean isEmpty()
	{
		return count == 0;
	}

	private void sinkFirstElement()
	{
		int i = 0;
		while ((2*i) + 1 < count)
		{
			int j = 2*i + 1;

			//if the other child exists and is smaller select it instead
			if (j + 1 < count && heap[j+1].compareTo(heap[j]) < 0)
				j++;


			//if current element is smaller than both its children we are finished
			if (heap[i].compareTo(heap[j]) <= 0) break;

			//otherwise swap it with its smallest child
			swap(i, j);

			//repeat with the original element now in its child's position
			i = j;
		}
	}

	private void swim(int i)
	{
		while(i > 0)
		{	
			int parentIndex = (i-1)/2;
			if (heap[i].compareTo(heap[parentIndex]) < 0)
			{
				swap(i, parentIndex);
				i = parentIndex;
			}
			else
				break;

		}
	}

	private void swap (int i, int j)
	{
		K tmp = heap[i];
		heap[i] = heap[j];
		heap[j] = tmp;
	}

	@SuppressWarnings("unchecked")
	private void doubleHeapSize()
	{
		K[] newHeap = (K[]) new Comparable[heap.length * 2];

		for(int i = 0; i < count; i++)
			newHeap[i] = heap[i];

		heap = newHeap;
	}

	//for testing
	public static void main(String[] args)
	{
		String test1 = "aasdfhajsdhfgxvhbaweryawegrybsfnzzyqx";
		MinHeap testHeap = new MinHeap();

		for (char c : test1.toCharArray())
			testHeap.add(c);

		while(!testHeap.isEmpty())
			System.out.println(testHeap.removeMin());
	}
}