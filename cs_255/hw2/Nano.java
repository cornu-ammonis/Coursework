
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

public class Nano
{
   public static char[] digit 
            = {'#', '!', '%', '@', '(', ')', '[', ']', '$'};


   


   /* ==========================================================
      Return the 2's complement binary representation for the
      Nano number given in String s
      ========================================================== */
   public static int parseNano(String s)
   {
      /* ------------------------------------------------------------------
         This loop checks if the input contains an illegal (non-Nano) digit
         ------------------------------------------------------------------ */
      for (int i = 0 ; i < s.length(); i++)
      {
         int j = 0;
         while ( j < digit.length )
         {
            if ( s.charAt(i) == digit[j] || s.charAt(i) == '-' )
            {
               break;
            }

            j++;
         }

         if ( j >= digit.length )
         {
            System.out.println("Illegal nano digit found in input: " 
					+ s.charAt(i) );
            System.out.println("A Nano digit must be one of these: " 
				+ Arrays.toString (digit) );
            System.exit(1);
         }
      }


      // this map associates each nano digit with its equivalent integer (2s complement) representation
      // this weird double bracket notation is essentially the equivalent of literal array construction

      HashMap<String, Integer> map = new HashMap<String, Integer>() {{
            put("#", 0);
            put("!", 1);
            put("%", 2);
            put("@", 3);
            put("(", 4);
            put(")", 5);
            put("[", 6);
            put("]", 7);
            put("$", 8);

      }};

      // value of the xth digit (from right to left) is digit*(9^(x-1))
      int currentPlaceValue = (int) Math.pow(9, s.length() - 1);

      //flips to true if we encounter 
      boolean neg = false; 

      int ans = 0;
      for (int i = 0; i < s.length(); i++) {
         System.out.println(s.charAt(i));

         // perhaps this should include a check that i is 0 (because the '-'' doesnt make sense anywhere else)
         // but the provided code doesn't check, so we'll assume '-' will only appear at position 0
         if (s.charAt(i) == '-') 
         {
            neg = true;
            currentPlaceValue = currentPlaceValue/9; //have to decrease place value because '-' included s.length()
         }

         else // character is anything but '-'
         { 
            // converts s.charAt(i) to a string, gets its corresponding int value, mulitiplies by 
            // place value and adds to the total
            int digitValue = map.get("" + s.charAt(i));
            ans = ans + (currentPlaceValue * digitValue);

            currentPlaceValue = currentPlaceValue/9; // for next loop, place value is 1/9th of this one
         }
         
      }

      // convert to negative value if we encountered a '-'
      if (neg)
         ans = ans * -1;

      return ans;
   }



   /* ==========================================================
      Return the String of Nano digit that represent the value
      of the 2's complement binary number given in 
      the input parameter 'value'
      ========================================================== */
   public static String toString(int value)
   {
      String ans = "";
      int r; // stores remainders during loop

      // preprocess sign
      boolean neg = (value < 0);
      if (neg) 
         value *= -1;
      
      // execute at least once for case where value = 0
      do 
      {
         
         // remainder value/9 is value for digit to prepend to ans ('reverse order')
         r = value % 9;
         ans = digit[r] + ans;

         value /= 9;
         
      } while (value > 0);
      
      
      // insert sign if value was negative
      if (neg)
         ans = "-" + ans;

      return ans;  
   }

}

