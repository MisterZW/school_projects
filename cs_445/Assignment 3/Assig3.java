// CS 0445 Spring 2018
// The first part of this program is identical to Assig2.java.
// Some additional code has been added to test the lastIndexOf() and
// regMatch() methods.

// Some additional comments follow in individual code segments
public class Assig3
{
	public static void main(String [] args)
	{
		System.out.println("Testing constructor methods");
		MyStringBuilder2 b1 = new MyStringBuilder2("this is a string");
		char [] c = {' ','a','n','o','t','h','e','r',' ','s','t','r','i','n','g'};
		MyStringBuilder2 b2 = new MyStringBuilder2(c);
		MyStringBuilder2 b3 = new MyStringBuilder2();

		System.out.println(b1);
		System.out.println(b2);
		System.out.println(b3);  // will show as an empty line

		System.out.println("\nTesting Append methods");
		b1.append(b2);
		System.out.println(b1);
		b1.append(" and another");
		System.out.println(b1);
		b1.append(c);
		System.out.println(b1);
		b1.append('!');  b1.append('!');  // char append
		System.out.println(b1);
		System.out.println();
		
		b2.append(" different strings?");
		System.out.println(b1); // Testing for independence of the StringBuilders
		System.out.println(b2); // after append.  b1 should be unchanged here
		
		// Special case appending to empty object
		b3.append("...appending data");
		System.out.println(b3);

		b2 = new MyStringBuilder2(c);  // reinitialize b2
		System.out.println("\nTesting charAt method");
		for (int i = 0; i < b2.length(); i++)
		{
			System.out.print(b2.charAt(i));
		}
		System.out.println();

		System.out.println("\nTesting delete method");
		b1 = new MyStringBuilder2("we build a string of everything");
		System.out.println(b1);
		b1.delete(9,21);
		System.out.println(b1);
		// Deleting from the front
		b1.delete(0,3);
		System.out.println(b1);
		// Deleting "past" the end just deletes to the end
		b1.delete(5,60);
		System.out.println(b1);

		System.out.println("\nTesting deleteCharAt method");
		b1 = new MyStringBuilder2("Xhere is a funney little stringh");
		System.out.println(b1);
		// Delete from the front
		b1.deleteCharAt(0);
		System.out.println(b1);
		// Delete in middle
		b1.deleteCharAt(14);
		System.out.println(b1);
		// Delete at end
		b1.deleteCharAt(b1.length()-1);
		System.out.println(b1);
		// Delete at location past the end does nothing
		b1.deleteCharAt(40);
		System.out.println(b1);

		System.out.println("\nTesting indexOf method");
		b1 = new MyStringBuilder2("who is whoing over in whoville");
		String s1 = new String("who");
		String s2 = new String("whoing");
		String s3 = new String("whoville");
		String s4 = new String("whoviller");
		String s5 = new String("wacky");
		int i1 = b1.indexOf(s1);
		int i2 = b1.indexOf(s2);
		int i3 = b1.indexOf(s3);
		int i4 = b1.indexOf(s4);
		int i5 = b1.indexOf(s5);
		System.out.println(s1 + " was found at " + i1);
		System.out.println(s2 + " was found at " + i2);
		System.out.println(s3 + " was found at " + i3);
		System.out.println(s4 + " was found at " + i4);
		System.out.println(s5 + " was found at " + i5);

		System.out.println("\nTesting insert and replace methods");
		b1 = new MyStringBuilder2("this is cool");
		b1.insert(8, "very ");
		System.out.println(b1);
		// Insert at the front
		b1.insert(0, "Wow, ");
		System.out.println(b1);
		// Testing replace method
		b1.replace(13, 17, "seriously");
		System.out.println(b1);
		// Replace deletes to end, then inserts to end of string
		b1.replace(23, 40, "challenging");
		System.out.println(b1);

		System.out.println("\nTesting substring method");
		b1 = new MyStringBuilder2("work hard to finish this assignment");
		String sub = b1.substring(25, 31);
		System.out.println(sub);
		sub = b1.substring(5,9);
		System.out.println(sub);
		// Substring at front
		sub = b1.substring(0,4);
		System.out.println(sub);
		
		System.out.println("\nTesting MyStringBuilder2 return types");
		b1 = new MyStringBuilder2("Hello");
		b2 = new MyStringBuilder2("StringBuilder");
		b1.append(" there ").append(b2).append(' ').append("fans!");
		System.out.println(b1);
		b1.delete(12,25).insert(12,"CS0445").append('!');
		System.out.println(b1);
		System.out.println();
		
		System.out.println("Testing Reverse");
		b1 = new MyStringBuilder2("Let's Reverse a String");
		System.out.println("Orig: " + b1);
		b1.reverse();
		System.out.println("Reversed: " + b1);
		b1.reverse();
		System.out.println("Back to orig: " + b1);
		// Reverse should also return a MyStringBuilder2
		b1.reverse().reverse().reverse();
		System.out.println("Should be reversed: " + b1);
		
		// Testing lastIndexOf method.
		System.out.println();
		System.out.println("Testing lastIndexOf method");
		b1 = new MyStringBuilder2("The sixth sick Sheik's sixth sheep's sick but the sixteenth sick Sheik's sixtieth sheep's sicker");
		s1 = new String("sixth");
		s2 = new String("sick");
		s3 = new String("six");
		s4 = new String("The");
		s5 = new String("sixty");
		i1 = b1.lastIndexOf(s1);
		System.out.println(s1 + " found last at " + i1);
		i2 = b1.lastIndexOf(s2);
		System.out.println(s2 + " found last at " + i2);
		i3 = b1.lastIndexOf(s3);
		System.out.println(s3 + " found last at " + i3);
		i4 = b1.lastIndexOf(s4);
		System.out.println(s4 + " found last at " + i4);
		i5 = b1.lastIndexOf(s5);
		System.out.println(s5 + " found last at " + i5);
		System.out.println();
		
		// Testing regMatch method.
		// This first match is discussed in the Assignment 3
		// specifications.
		String patA = "ABC123XYZ";
		String patB = "123";
		String patC = "XYZ";
		String [] patterns = { patA, patB, patC };
		MyStringBuilder2 B = new MyStringBuilder2("BBB222YYYCCC");
		testMatch(B, patterns);
		
		// Below are some additional tests to demonstrate the
		// functionality of the regMatch() method.
		String str1 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		String str2 = "0123456789";
		String str3 = str1 + str2;
		String str4 = "*";
		String [] pat1 = {str3};
		String [] pat2 = {str2};
		String [] pat3 = {str1, str2};
		String [] pat4 = {str3, str2, str1};
		String [] pat5 = {str3, str2};
		String [] pat6 = {str3, str2, str4};
		String [] pat7 = {str3, str2, str1, str3, str2};
	
		b1 = new MyStringBuilder2("1234HelloThere456Friends---");
		b2 = new MyStringBuilder2("R2D2IsAVeryWittyDroid");
		b3 = new MyStringBuilder2("AA22-ABC123abc-ABC123***-BCA321***");
		
		testMatch(b1, pat1);
		testMatch(b1, pat2);
		testMatch(b1, pat3);
		testMatch(b1, pat4);
		testMatch(b2, pat1);
		testMatch(b2, pat5);
		testMatch(b3, pat6);
		testMatch(b2, pat6);
		testMatch(b1, pat7);
	}
	
	public static void testMatch(MyStringBuilder2 target, String [] pat)
	{
		System.out.print("Looking for pattern: ");
		for (String X: pat)
			System.out.print(X + " ");
		System.out.println();
		System.out.println("Looking in string: " + target);
		MyStringBuilder2 [] ans = target.regMatch(pat);
		if (ans != null)
		{
			System.out.print("Match: ");
			for (MyStringBuilder2 bb: ans)
				System.out.print(bb + " ");
			System.out.println();
		}		
		else
			System.out.println("No match found!");
		System.out.println();
	}
}