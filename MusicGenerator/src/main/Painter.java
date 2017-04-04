package main;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Note;



public class Painter extends JPanel implements ActionListener{
	
	ArrayList<Rectangle> rr = new ArrayList<Rectangle>();
	int time = 0;

	public Painter(Container c) {
		Timer clock = new Timer(1000/60, this);
		clock.start();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		int ww = this.getWidth();
		int hh = this.getHeight();
		g.clearRect(0, 0, ww, hh);
		g.setColor(Color.RED);
		g.fillRect(ww/2 + (int)(ww/2*Math.sin(0.01*time) - 10), hh/2 - 50, 20, 100);
		for(Rectangle i: rr){
			g.drawRect(i.x, i.y, i.width, i.height);
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		time++;
		repaint();
	}
	
	public void drawPattern(Pattern p){
		double timeline = 0;
		Pattern use = new Pattern(p);
		String s = use.toString();
		String[] ss = s.split(" ");
		for(int i=3; i<ss.length; i++){
			System.out.println(ss[i]);
			System.out.println(ss[i]);
			System.out.println(ss[i]);
			for(int j=0; j<Section.timings.length; j++){
				if(ss[i].contains(Section.timings[j])){
					ss[i].replaceAll(Section.timings[j], Section.timings_legacy[j]);
				}
			}
			System.out.println(ss[i]);
			Note n = new Note(ss[i]);
			double d = n.getDuration();
			int val = n.getValue();
			int x1 = (int)(timeline*20);
			int y1 = val;
			int x2 = (int)(d*20);
			int y2 = 2;
			rr.add(new Rectangle(x1,y1,x2,y2));
			timeline+=d;
		}
	}
	
}
