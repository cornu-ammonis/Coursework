package ds;

class minHeap<K extends Comparable<K>>
{
	private static final int defaultCapcity = 20;
	private int count;
	private K[] heap;

	@SuppressWarnings("unchecked")
	public minHeap(int capacity)
	{
		heap = (K[]) new Comparable[capacity];
	}

	@SuppressWarnings("unchecked")
	public minHeap()
	{
		heap = (K[]) new Comparable[defaultCapcity];
	}
	
	private void swap (int i, int j)
	{
		K tmp = heap[i];
		heap[i] = heap[j];
		heap[j] = tmp;
	}
}