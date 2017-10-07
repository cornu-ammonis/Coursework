
public class TestSkipList {

      public static void main(String[] args) {

            SkipList S = new SkipList();
            Integer x = null;

            S.put("ABC", 123);
            S.put("DEF", 345);
            S.put("KLM", 987);
            S.put("HAT", 777);
            S.put("ZIJ", 957);
            S.put("HIJ", 769);
            S.put("XYZ", 444);
            S.put("AAA", 123);

            System.out.println("The skip list is:");
            System.out.println("*******************************");
            S.printHorizontal();
            System.out.println("*******************************");

            System.out.println();
            System.out.println("=====================================");
            System.out.println("Testing the value-added methods:");
            System.out.println("=====================================");
            System.out.println();
            System.out.println("firstEntry() = " + S.firstEntry() ); 
            System.out.println("lastEntry() = " + S.lastEntry() );
            System.out.println(); 
            System.out.println("ceilingEntry(XYZ) = " + S.ceilingEntry("XYZ") ); /*
            System.out.println("floorEntry(XYZ) = " + S.floorEntry("XYZ") );
            System.out.println("upperEntry(XYZ) = " + S.upperEntry("XYZ") );
            System.out.println("lowerEntry(XYZ) = " + S.lowerEntry("XYZ") );
            System.out.println(); */
            System.out.println("ceilingEntry(WWW) = " + S.ceilingEntry("WWW") ); 
            System.out.println("ceilingEntry(BCD) = " + S.ceilingEntry("BCD") ); /*/*
            System.out.println("floorEntry(WWW) = " + S.floorEntry("WWW") );
            System.out.println("upperEntry(WWW) = " + S.upperEntry("WWW") );
            System.out.println("lowerEntry(WWW) = " + S.lowerEntry("WWW") ); */

            System.out.println();
            System.out.println("=====================================");
            System.out.println("Testing the remove() implementaion:");
            System.out.println("=====================================");
            System.out.println();

            x = null;

            x= S.remove("XXX");
            System.out.println("remove XXX returns: " + x + " (correct: null)");
            System.out.println("*******************************");
            S.printHorizontal();
            System.out.println("*******************************");

            x= S.remove("XYZ");
            System.out.println("remove XYZ returns: " + x + " (correct: 444)");
            System.out.println("*******************************");
            S.printHorizontal();
            System.out.println("*******************************");

            x= S.remove("HAT");
            System.out.println("remove HAT returns: " + x + " (correct: 777)");
            System.out.println("*******************************");
            S.printHorizontal();
            System.out.println("*******************************");

            System.out.println();
            System.out.println("************ DONE ************");
      }
}