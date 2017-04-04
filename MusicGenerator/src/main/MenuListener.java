package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class MenuListener implements ActionListener{

	Generator generator;
	
	public MenuListener(Generator in){
		generator = in;
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==FrameGUI.button1){
		    Random random = new Random();
		    String s = "";
	        int x = 5; // words of length 3 through 10. (1 and 2 letter words are boring.)
	        for(int j = 0; j < x; j++){
	            s += (char)('A' + random.nextInt(26));
	        }
			FrameGUI.tf1.setText(s);
		}
		if(e.getSource()==FrameGUI.button2){
			generator.export();
		}
		else if(generator!=null){
			generator.getSong();
		}
	}

}
