

class TestHeap
{
	//for testing
	public static void main(String[] args)
	{
		String test1 = "aasdfhajsdhfgxvhbaweryawegrybsfnzzyqx";
		MinHeap<Character> testHeap = new MinHeap<Character>();

		for (Character c : test1.toCharArray())
			testHeap.add(c);

		while(!testHeap.isEmpty())
			System.out.println(testHeap.removeMin());
	}
}