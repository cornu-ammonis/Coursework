public class TestAutomata {
	
	public static void main(String[] args) {

		String pattern1 = "aba;alksdjfcccabefagasdf;lkj";
		String test1 = "aba;alksdjfcccabesfagasdf;lkjaba;alksdjfccccaba;alksdjfcccabefagasdf;lkjabefagasdf;lkjaba;alksdjfcccsabefagsasdf;lkj";

		Automata testAutomata = new Automata(pattern1);

		int current = 0;
		int i = 0;

		while (i < test1.length() ) {
			current = testAutomata.transition(current, test1.charAt(i) );
			i++;
		}

		System.out.println("Current = " + current + testAutomata.isFinal(current));

		if (testAutomata.isFinal(current)) {
			boolean works = true;
			while (i < test1.length()) {
				if (current != testAutomata.transition(current, test1.charAt(i))) {
					System.out.println("isFinal is broken! changed anyway.");
					works = false;
				}
			
				i++;
			}

			System.out.println("isfinal works: " + works);
		}
	}
}