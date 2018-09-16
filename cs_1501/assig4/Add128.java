//  Zachary Whitney  zdw9  ID: 3320178
//  CS 1501 Summer 2018 (enrolled in W section)
//  Assignment #4 -- This is a simple Vigenere cipher

import java.util.Random;

public class Add128 implements SymCipher {

	private static final int DEFAULT_SIZE = 128;
	private byte[] key;

	public Add128()
	{
		Random r = new Random();
		key = new byte[DEFAULT_SIZE];
		r.nextBytes(key);
	}

	public Add128(byte[] seed)
	{
		if(seed == null)
			throw new IllegalArgumentException();
		key = seed.clone();
	}

	// Return an array of bytes that represent the key for the cipher
	public byte[] getKey()
	{
		return key.clone();
	}

	// Encode the string using the key and return the result as an array of
	// bytes.
	public byte[] encode(String S)
	{
		if(S == null)
			throw new IllegalArgumentException();
		byte[] ciphertext = S.getBytes();
		int j = 0;
		for(int i = 0; i < ciphertext.length; i++)
		{
			ciphertext[i] += key[j++];
			j %= key.length;
		}
		return ciphertext;
	}

	// Decrypt the array of bytes and generate and return the corresponding String.
	public String decode(byte[] bytes)
	{
		if(bytes == null)
			throw new IllegalArgumentException();
		int j = 0;
		for(int i = 0; i < bytes.length; i++)
		{
			bytes[i] -= key[j++];
			j %= key.length;
		}
		return new String(bytes);
	}
}
