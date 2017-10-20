
import java.util.Scanner;

public class TestBST
{
   public static void main(String[] args)
   {
      Scanner in = new Scanner(System.in);
      BST x = new BST();  
                  // Key = String, Value = String

      x.put("lion", 9999);
      x.put("dog",  1000);
      x.put("tiger", 8888);
      x.put("cat", 500);
      x.put("horse", 2000);
      x.put("ape", 1500);
      x.put("cow", 700);
      x.put("donkey", 1900);
      x.put("man", 5000);
      x.put("jackal", 4000);
      x.put("owl", 2000);
      x.put("zebra", 1600);
      x.printBST();

      BSTEntry p, q;

      p = x.firstEntry();
      System.out.println("Smallest Key Entry: " + p);
      System.out.println("Correct Answer: ape");

      p = x.lastEntry();
      System.out.println("Largest Key Entry: " + p);
      System.out.println("Correct Answer: zebra");

      System.out.print("Key: ");
      String input = in.next();
      while (!input.equals("exit")) {
         System.out.println("For " + input + ":");
         p = x.floorEntry(input);
         System.out.println("Floor Entry: " + p);
         p = x.ceilingEntry(input);
         System.out.println("Ceiling Entry: " + p);
         p = x.lowerEntry(input);
         System.out.println("Lower Entry: " + p);
         p = x.upperEntry(input);
         System.out.println("Upper Entry: " + p);

         System.out.print("Key: ");
         input = in.next();
      }




     
   }
}