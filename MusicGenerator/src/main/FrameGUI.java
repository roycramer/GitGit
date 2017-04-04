package main;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class FrameGUI extends JFrame{
	

	Font font = new Font(Font.SERIF, Font.BOLD, 14);
	Font smaller = new Font(Font.MONOSPACED, 0, 12);
	
	JFrame frame = new JFrame("Music Program");
	JPanel panel = new JPanel();
	static JButton button1 = new JButton("Randomize");
	static JButton button2 = new JButton("Export MIDI");
	static JCheckBox cMaj = new JCheckBox();
	static JTextArea taVerse, taInstruction, taChorus, taDebug;
	static JTextField tf1;
	static Painter painter;
	JTextArea ta;
	Generator generator;
	private static final long serialVersionUID = -5803280250200342310L;
	
	
	public FrameGUI(){
		
		generator = new Generator();
		generator.loadFont();
		
		tf1 = new JTextField("", 20);
		tf1.setFont(font);
		MenuListener listener1 = new MenuListener(generator);
		tf1.addActionListener(listener1);
		cMaj.setText("C Maj Lock");
		
		taVerse = new JTextArea("");
		taVerse.setFont(smaller);
		
		taInstruction = new JTextArea(" Enter a seed number/text in the box above, this can be a name or number."
				+ " Press 'enter' key on the text box to generate the song,"
				+ " or click the 'randomize' button to start with a random four-digit seed."
				+ "\n\nA \"-1\" symbol indicates a copy from the previous measure"
				+ "\nA \"-2\" indicates a copy from two measures ago, and so on.");
		taInstruction.setLineWrap(true);
		taInstruction.setWrapStyleWord(true);
		taInstruction.setEditable(false);
		taInstruction.setSize(1000, 1000);
		taInstruction.setFont(font);
		
		taChorus = new JTextArea("");
		taChorus.setFont(smaller);
		
		taDebug = new JTextArea("");
		taDebug.setFont(smaller);
		
		button1.setFont(font);
		button2.setFont(font);
		button1.addActionListener(listener1);
		button2.addActionListener(listener1);
		painter = new Painter(this);
		
		
		
		JPanel side2 = new JPanel();
//		BoxLayout l2 = new BoxLayout(side2, BoxLayout.PAGE_AXIS);
		side2.add(new JTextArea("HarpGen Music Generator"));
		side2.add(cMaj);
		side2.add(tf1);
		side2.add(button1);
		side2.add(button2);
		
		JPanel side = new JPanel();
		side.setLayout(new BorderLayout());
		side.add(side2, BorderLayout.NORTH);
		
		JPanel bottom = new JPanel();
		bottom.setLayout(new GridLayout(1,3));
		bottom.add(new JScrollPane(taInstruction));
		bottom.add(new JScrollPane(taDebug));
		bottom.add(painter);
		
		JPanel center = new JPanel();
		center.setLayout(new GridLayout(1,2));
		center.add(new JScrollPane(taVerse));
		center.add(new JScrollPane(taChorus));
		
		
		BorderLayout l1 = new BorderLayout();
		panel.setLayout(l1);
		panel.add(center, BorderLayout.CENTER);
		panel.add(bottom, BorderLayout.SOUTH);
		panel.add(side, BorderLayout.NORTH);
		
		frame.add(panel);
		frame.setSize(1080,720);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
