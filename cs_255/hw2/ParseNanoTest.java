// Test program for cs255 hw2
// DO NOT make any changes to this program
//
// For hw2, you must write another program in file "Nano.java"
// that contains the static methods:
//
//         public static int parseNano(String s)
//         public static String toString(int a)
//
// Compile the program (after you have written "Nano.java")
// with:
//         javac ParseNanoTest.java
//
// and run it with the command:
//         java ParseNanoTest
//

import java.util.Scanner;


public class ParseNanoTest
{
   public static char[] digit
            = {'#', '!', '%', '@', '(', ')', '[', ']', '$'};

   public static void main (String[] args) 
   {
      int i, j;
      String s;
      int val;

      System.out.println("Single digit nano number tests....");
      for ( i = 0; i < 9; i ++ )
      {
         s = "" + digit[i];
         val = Nano.parseNano( s );

         if ( val != i )
         {
           System.out.println(" ---- FAILED for Nano number: " + s);
           System.exit(1);
         }
      }

      for ( i = 1; i < 9; i ++ )
      {
         s = "-" + digit[i];
         val = Nano.parseNano( s );

         if ( val != -i )
         {
           System.out.println(" ---- FAILED for Nano number: " + s);
           System.exit(1);
         }
      }
      System.out.println("Single digit nano number tests....  PASSED !");

      System.out.println("Double digits nano number tests....");
      for ( i = 1; i < 9; i ++ )
         for ( j = 0; j < 9; j ++ )
         {
            s = "" + digit[i] + digit[j];
            val = Nano.parseNano( s );

            if ( val != i*9 + j )
            {
               System.out.println(" ---- FAILED for Nano number: " + s);
               System.exit(1);
            }
         }

      for ( i = 1; i < 9; i ++ )
         for ( j = 0; j < 9; j ++ )
         {
            s = "-" + digit[i] + digit[j];
            val = Nano.parseNano( s );

            if ( val != -(i*9 + j) )
            {
               System.out.println(" ---- FAILED for Nano number: " + s);
               System.exit(1);
            }
         }
      System.out.println("Double digits nano number tests.... PASSED !!");

      System.out.println("Large nano number tests....");
      s = "" + digit[7] + digit[6] + digit[4] + digit[8] + digit[3];
      val = Nano.parseNano( s );

      if ( val != (7*9*9*9*9 + 6*9*9*9 + 4*9*9 + 8*9 + 3 ) )
      {
         System.out.println(" ---- FAILED for Nano number: " + s);
         System.exit(1);
      }

      s = "-" + digit[8] + digit[0] + digit[1] + digit[2] + digit[5];
      val = Nano.parseNano( s );

      if ( val != -(8*9*9*9*9 + 0*9*9*9 + 1*9*9 + 2*9 + 5 ) )
      {
         System.out.println(" ---- FAILED for Nano number: " + s);
         System.exit(1);
      }
      System.out.println("Large nano number tests.... PASSED !!\n");

      System.out.println(" ---- All parseNano tests passed !");

   }
}

