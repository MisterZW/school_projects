import java.util.Random;

public class Substitute implements SymCipher {

	private static final int BIAS = 128;
	private static final int BYTE_RANGE = 256;
	private byte[] key;
	private byte[] inverse;
	
	public Substitute()
	{
		key = new byte[BYTE_RANGE];
		inverse = new byte[BYTE_RANGE];

		for(int i = -128; i < 128; i++)
			key[i + BIAS] = (byte)i;

		shuffle(key);
		
		for(int i = 0; i < key.length; i++)
			inverse[key[i] + BIAS] = (byte)(i - BIAS);
	}

	public Substitute(byte[] seed)
	{
		key = seed.clone();
		inverse = new byte[seed.length];
		for(int i = 0; i < key.length; i++)
			inverse[key[i] + BIAS] = (byte)(i - BIAS);
	}

	// Return an array of bytes that represent the key for the cipher
	public byte[] getKey()
	{
		return key.clone();
	}
	
	// Encode the string using the key and return the result as an array of
	// bytes.  Note that you will need to convert the String to an array of bytes
	// prior to encrypting it.  Also note that String S could have an arbitrary
	// length, so your cipher may have to "wrap" when encrypting.
	public byte[] encode(String S)
	{
		byte[] ciphertext = S.getBytes();
		for(int i = 0; i < ciphertext.length; i++)
		{
			byte value = ciphertext[i];
			ciphertext[i] = key[value];
		}
		return ciphertext;
	}
	
	// Decrypt the array of bytes and generate and return the corresponding String.
	public String decode(byte[] bytes)
	{
		for(int i = 0; i < bytes.length; i++)
		{
			byte value = bytes[i];
			bytes[i] = (byte)(inverse[value + BIAS] + BIAS);
		}
		return new String(bytes);
	}

	// Reorganize the items in the array in a pseudo-random way.
    private void shuffle(byte[] arr)
    {
        Random generator = new Random();
        for(int i = 0; i < arr.length; i++)
        {
            int index = generator.nextInt(arr.length - i);
            swap(arr, index + i, i);
        }
    }

    //helper method for shuffle() -- swaps values
    private void swap(byte[] a, int first, int second)
    {
        byte temp = a[first];
        a[first] = a[second];
        a[second] = temp;
    }
}