public class TestSortedQueue {
	
	public static void main(String[] args)
	{
		SortedPriorityQueue<Integer, String> sq = new SortedPriorityQueue<Integer, String>();

		sq.insert(1, "second");
		sq.insert(0, "first");
		sq.insert(2, "third");
		System.out.println(sq.removeMin());
		sq.insert(10, "fifth");
		sq.insert(9, "fourth");
		sq.insert(10, "test");
		sq.insert(11, "seventh");

		System.out.println(sq.toString());
		System.out.println(sq.removeMin());
		System.out.println(sq.removeMin());
		System.out.println(sq.removeMin());
		sq.insert(1, "second");
		sq.insert(0, "first");
		sq.insert(2, "third");
		System.out.println(sq.toString());
		System.out.println(sq.removeMin());
		System.out.println(sq.removeMin());
		System.out.println(sq.removeMin());
		System.out.println(sq.removeMin());
		System.out.println(sq.removeMin());
		System.out.println(sq.removeMin());
		System.out.println(sq.removeMin());
	}
}