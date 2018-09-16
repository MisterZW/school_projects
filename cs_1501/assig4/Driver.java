import java.util.Arrays;

public class Driver 
{

	public static void main(String[] args)
	{

		byte[] seed = new byte[256];
		for(int i = 0; i < 256; i++)
		{
			seed[i] = (byte)(127 - i);
		}
		

		SymCipher cipher = new Substitution();
		String plaintext = "This is the plaintext to encode. Let's hope all of my stuff is working " +
			"correctly so that I don't have to rewrite my entire stupid cipher all over again. That would " +
			"be sort of a drag. Let's keep typing so that we hit the 256 character mark for the string. " +
			"I think this String is probably long enough now.\n";

		System.out.println("Here is the plaintext:\n\n" + plaintext);

		System.out.println("ENCRYPTING THE PLAINTEXT\n\n");

		byte[] ciphertext = cipher.encode(plaintext);

		//System.out.println("Here is the ciphertext:\n\n" + new String(ciphertext));

		System.out.println("DECRYPTING THE CIPHERTEXT\n\n");

		plaintext = cipher.decode(ciphertext);

		System.out.println("Here is the plaintext:\n\n" + plaintext);
	}
}