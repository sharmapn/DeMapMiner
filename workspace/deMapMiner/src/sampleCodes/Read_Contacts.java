package sampleCodes;

import javax.swing.*; //Uses this for Swing commands/classes

import javax.swing.JPanel; //JPanel

import java.awt.event.*; //Event handler

import java.awt.*; //Incorporates AWT commands

import java.text.DecimalFormat; //Decimal Formatter

import java.io.FileInputStream; //IO Importer

import java.io.FileOutputStream; //IO Exporter

import java.io.IOException; //Catches errors with IO

import java.io.*; //IO wildcard

public class Read_Contacts extends JFrame

{
	private JPanel buttonpanel; // Panel for buttons
	private JPanel textpanel; // Panel for Text Area
	private JButton openbutton; // Open button
	private JButton exitbutton; // Exit button
	private JTextArea textarea1; // New Text Area
	private final int WINDOW_WIDTH = 800; // Window Width
	private final int WINDOW_HEIGHT = 600; // Window Height
	String file_contents; // File Contents

	public static void main(String[] args)
	{
		Read_Contacts kc = new Read_Contacts();
	}

	private Read_Contacts()
	{
		// Put title on window
		super("Read Contacts");
		// Sets size of window using values defined earlier
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		// Sets default behaivor when close (X) button is clicked
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Build panel
		ButtonPanel();
		TextPanel();
		add(buttonpanel);
    	add(textpanel);
		// Layout Manager (grid)
		setLayout(new GridLayout(10, 15));
		// Dispaly's the window
		setVisible(true);
	}

	private void ButtonPanel()
	{
		// Build panel to hold buttons

		buttonpanel = new JPanel();
		// Create Submit and Exit Button
		openbutton = new JButton("Open");
		exitbutton = new JButton("Exit");
		// Registers actionlistener to buttons
		openbutton.addActionListener(new Read_Contacts.OpenButtonListener());
		exitbutton.addActionListener(new Read_Contacts.ExitButtonListener());
		buttonpanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		// Add buttons to panel
		buttonpanel.add(openbutton);
		buttonpanel.add(exitbutton);

	}

	private void TextPanel()
	{
		// Create Text Area
		textpanel = new JPanel();
		textarea1 = new JTextArea(100,100);
		textpanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		textpanel.add(textarea1);
	}

	private class OpenButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			BufferedReader in;
			String contents;
			// StringBuilder sb = new StringBuilder();
			try
			{
				// Buffered Reader opens contacts.txt
				in = new BufferedReader(new FileReader("C:\\scripts\\data.txt"));
				// Reads information from file
				//contents = in.readLine();
				while ((contents = in.readLine()) != null)
				{
					// Display contents
					file_contents = contents;
					// textarea1.setText(file_contents);
					textarea1.read(in, null);
				}

				// Close the file
				in.close();
			}

			catch (IOException f)
			{
				System.out.println("There was an issue:" + f);

			}
		}
	}

	private class ExitButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			System.exit(0); // When the Exit button is clicked, the applet just
						// closes

		}
	}
}
