public class Driver{

    public static void main(String[] args){

        MyStringBuilder2 b1, b2, b3, b4, b5;
        b1 = new MyStringBuilder2();
        b2 = null;
        b3 = new MyStringBuilder2("CS54^()AX*X11X");
        b4 = new MyStringBuilder2("C1X^3DX");

        String a, b, c, d;
        a = "X";
        b = "12345";
        c = "^%*()";
        d = "ABCDE";
        String e = a + b + c + d;

        String[] a1 = {c, a, b};
        String[] a2 = {c, a, b, d};
        String[] a3 = {e, a, c};

        testMatch(b3, a1);
        testMatch(b3, a2);
        testMatch(b4, a3);
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
