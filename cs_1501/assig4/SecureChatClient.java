//  Zachary Whitney  zdw9  ID: 3320178
//  CS 1501 Summer 2018 (enrolled in W section)
//  Assignment #4 -- This is a primitive chat client

//  This client connects to a server so that messages can be typed and forwarded
//  to all other clients. It is a modified version of ImprovedChatServer.java.
//  This version sends its key to the server via RSA and encrypts other messages
//  with either a substitution cipher or a vigenere cipher. The encrytion scheme
//  is determied by the server at runtime.

import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.math.BigInteger;

public class SecureChatClient extends JFrame implements Runnable, ActionListener {

    public static final int PORT = 8765;

    BigInteger E, N, key;
    String cipherType, plaintext, ciphertext;
    byte[] byte_array;
    SymCipher cipher;
    ObjectOutputStream oos;
    ObjectInputStream ois;
    JTextArea outputArea;
    JLabel prompt;
    JTextField inputField;
    String myName, serverName;
	Socket connection;

    public SecureChatClient ()
    {
        try {

        serverName = JOptionPane.showInputDialog(this, "Enter the server name: ");
        InetAddress addr = InetAddress.getByName(serverName);
        connection = new Socket(addr, PORT);   // Connect to server with new
                                               // Socket

        oos = new ObjectOutputStream(connection.getOutputStream());
        oos.flush();
        ois = new ObjectInputStream(connection.getInputStream());

        System.out.println("Getting E from the server...");
        E = (BigInteger)ois.readObject();
        System.out.println("E:\t" + E + "\n");

        System.out.println("Getting N from the server...");
        N = (BigInteger)ois.readObject();
        System.out.println("N:\t" + N + "\n");

        System.out.println("Getting cipher type from the server...");
        cipherType = (String)ois.readObject();
        System.out.println("Cipher type: " + cipherType + "\n");

        if(cipherType.equals("Add"))
            cipher = new Add128();
        else
            cipher = new Substitute();

        System.out.println("Getting client-side symmetric key...");
        byte[] rawKey = cipher.getKey();
        System.out.println("Here is the symmetric key as a byte[]");
        printByteArray(rawKey);

        System.out.println("\nEncrypting the key with E & N as a BigInteger");
        key = new BigInteger(1, rawKey);
        key = key.modPow(E, N);
        System.out.println("Here is the encryped key the client is sending the server\n");
        System.out.println(key + "\n");

        oos.writeObject(key);

        myName = JOptionPane.showInputDialog(this, "Enter your user name: ");
        this.setTitle("Chatting on " + serverName + " as " + myName);      // Set title to identify chatter

        System.out.println("User's unencryped name: " + myName);
        System.out.println("Here is the user's unencrypted name as a byte[]");
        printByteArray(myName);

        byte[] encryptedName = cipher.encode(myName);
        System.out.println("Here is the user's encrypted name as a byte[]");
        printByteArray(encryptedName);

        oos.writeObject(encryptedName);

        /* END HANDSHAKING PORTION */

        Box b = Box.createHorizontalBox();  // Set up graphical environment for
        outputArea = new JTextArea(15, 30);  // user
        outputArea.setFont(new Font("Ariel", Font.BOLD, 16));
        outputArea.setBackground(Color.BLACK);
        outputArea.setForeground(Color.WHITE);
        outputArea.setEditable(false);
        b.add(new JScrollPane(outputArea));

        outputArea.append("Welcome to the Chat Group, " + myName + "\n\n");

        inputField = new JTextField("");  // This is where user will type input
        inputField.setFont(new Font("Ariel", Font.PLAIN, 24));
        inputField.addActionListener(this);

        prompt = new JLabel("  Type your messages below:");
        prompt.setFont(new Font("Helvetica", Font.PLAIN, 14));
        prompt.setSize(new Dimension(150, 400));
        Container c = getContentPane();

        c.add(b, BorderLayout.NORTH);
        c.add(prompt, BorderLayout.CENTER);
        c.add(inputField, BorderLayout.SOUTH);

        Thread outputThread = new Thread(this);  // Thread is to receive strings
        outputThread.start();                    // from Server

		addWindowListener(
                new WindowAdapter()
                {
                    public void windowClosing(WindowEvent e)
                    {
                        String endSession = "CLIENT CLOSING";
                        System.out.println("Sending end session sentinel "+ endSession);
                        System.out.println("As a plaintext byte[], the sentinel is ");
                        printByteArray(endSession);
                        byte_array = cipher.encode(endSession);
                        System.out.println("As a ciphertext byte[], the sentinel is ");
                        printByteArray(byte_array);
                        try
                        {
                            oos.writeObject(byte_array);
                        }
                        catch (IOException g)
                        {
                            System.out.println("Error closing window");
                        }
                        System.exit(0);
                    }
                }
            );

        setSize(600, 400);
        setVisible(true);

        }
        catch (Exception e)
        {
            System.out.println("Problem starting client!");
        }
    }

    public void run()
    {
        while (true)
        {
             try {
                byte_array = (byte[])ois.readObject();
                System.out.println("GOT DATA FROM THE SERVER\n");
                System.out.println("The encrypted message as a byte[] is: ");
                printByteArray(byte_array);
                plaintext = cipher.decode(byte_array);
                System.out.println("The plaintext message as a byte[] is: ");
                printByteArray(plaintext);
                System.out.println("The plaintext message is " + plaintext + "\n");

			    outputArea.append(plaintext + "\n");
             }
             catch (EOFException f)
             {
                System.out.println("Chat session complete -- exiting client");
                break;
             }
             catch (Exception e)
             {
                System.err.println(e +  ", closing client!");
                break;
             }
        }

        System.exit(0);
    }

    public void actionPerformed(ActionEvent e)
    {

        String currMsg = e.getActionCommand();      // Get input value
        plaintext = myName + ": " + currMsg;
        inputField.setText("");
        System.out.println("The plaintext message is " + plaintext + "\n");
        System.out.println("The plaintext message as a byte[] is: ");
        printByteArray(plaintext);
        byte_array = cipher.encode(plaintext);
        System.out.println("The encrypted message as a byte[] is: ");
        printByteArray(byte_array);
        try
        {
            System.out.println("WRITING TO THE SERVER");
            oos.writeObject(byte_array);
        }
        catch (IOException f)
        {
            System.err.println(f +  ", closing client!");
            System.exit(0);
        }
    }

    public static void main(String [] args)
    {
         SecureChatClient JR = new SecureChatClient();
         JR.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    private void printByteArray(String s)
    {
        for(int i = 0; i < s.length(); i++)
        {
            System.out.print((byte)(s.charAt(i)- 128)+ " ");
            if((i+1) % 20 == 0)
                System.out.println();
        }
        System.out.println("\n");
    }

    private void printByteArray(byte[] array)
    {
        for(int i = 0; i < array.length; i++)
        {
            System.out.print(array[i] + " ");
            if((i+1) % 20 == 0)
                System.out.println();
        }
        System.out.println("\n");
    }
}
