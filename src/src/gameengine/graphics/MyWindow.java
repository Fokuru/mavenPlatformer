/*
 * 
 */
package gameengine.graphics;

import java.awt.image.BufferStrategy;
import java.awt.Insets;
import java.awt.Graphics;

import javax.swing.JFrame;

/**
 * 
 * @author Paul Kappmeyer & Daniel Lucarz
 * 
 */


@SuppressWarnings("serial")
public class MyWindow extends JFrame {
	private static int insetX;
	private static int insetY;
	private BufferStrategy strat;
	
	public MyWindow(String title, int width, int height) {
		this.setTitle(title);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setUndecorated(false);
		this.setVisible(true);
		
		
		setVisible(true);
		createBufferStrategy(3);
		strat = null;
		
		Insets i = getInsets();
		insetX = i.left;
		insetY = i.top;
		this.setResizable(false);
		this.setSize(width + insetX + i.right, height + insetY + i.bottom);
		

	}
	
	public BufferStrategy beginDrawing() {
		strat = getBufferStrategy();
		while(strat == null){
			strat = getBufferStrategy();
		}
	
		return strat;
	}
	
	// public void endDrawing(Graphics g){
	// 	g.dispose();
	// 	strat.show();

	// }
	
	//---------------------------------------------Getters
	public static int getInsetX() {
		return insetX;
	}
	
	public static int getInsetY() {
		return insetY;
	}
}
