@SuppressWarnings ({"rawtypes","unchecked"})
public class MergeSort {

	public MergeSort() { } 

	// MergeSort sort
	public static Comparable[] sort(Comparable[] a)
	{
		Comparable[] aux = new Comparable[a.length];
		mergesort (a, aux, 0, a.length-1);
		return a;
	}
	
	private static boolean isSorted(Comparable[] a, int lo, int hi)
	{
		for (int i = lo; i < hi; i++)
		{
			if (less (a[i+1], a[i]))
				return false;
		}
		return true;
	}


	// mergesort sorts a[lo,...,hi], both endpoints included, and returns the result in a
	private static void mergesort(Comparable[] a, Comparable[] aux, int lo, int hi)
	{
		// base case: one-element array to sort
		if (lo >= hi)
			return;
		int mid = (lo + hi) / 2;
		mergesort (a, aux, lo, mid);
		//assert isSorted (a, lo, mid);
		mergesort (a, aux, mid+1, hi);
		//assert isSorted (a, mid+1, hi);
		merge (a, aux, lo, mid, hi);
			
	}

	// merge take two halves, a[lo, ..., mid] and a[mid+1, ..., hi] (all endpoints included)
	// and sorts a in place as a[lo, ..., hi]
	private static void merge(Comparable[] a, Comparable[] aux, int lo, int mid, int hi)
	{
		int i = lo;
		int j = mid+1;
		for (int k = lo; k <= hi; k++)
			aux[k] = a[k];
		System.out.println ("merge " + lo + " (mid=" + mid + ") to " + hi);
		assert isSorted (a, lo, mid);
		assert isSorted (a, mid+1, hi);
		for (int k = lo; k <= hi; k++)
		{
			if (i == mid+1)
				a[k] = aux[j++];
			else if (j == hi+1)	
				a[k] = aux[i++];
			else if (less (aux[i], aux[j]))
				a[k] = aux[i++];
			else
				a[k] = aux[j++];
		}
		assert isSorted(a, lo, hi);
	}

	// is v < w?
	private static boolean less(Comparable v, Comparable w) 
	{
		return (v.compareTo (w) < 0);
	}


	public static void main(String[] args)
	{
		Comparable[] arr;
		try { 
			arr = new Integer[args.length];
			
			for(int i = 0; i < args.length; i++) {
				arr[i] = (Integer) Integer.parseInt(args[i]);
			}

		}
		
		catch(Exception e) {
			System.out.println("Input was not all integers, so comparing as Strings");
			arr = args;
		}
		MergeSort.sort (arr);
		for (int i = 0; i < arr.length; i++)
			System.out.println (arr[i]);

	}
}