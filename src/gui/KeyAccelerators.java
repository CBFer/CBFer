package gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

import movie.LMovie;

/**
 * Unimplemented accelerator listeners
 * @author Peter
 *
 */

public class KeyAccelerators implements KeyListener
{
	JFrame parent;
	
	public KeyAccelerators(JFrame jf)
	{
		parent = jf;
	}

	@Override
	public void keyTyped(KeyEvent e){}

	@Override
	public void keyPressed(KeyEvent e) 
	{
		System.out.println(e.getKeyCode());
		//only act if the window is the main focus
//		if (parent.isActive())
//		{	
			switch (e.getKeyCode())
			{
			//space - pause movie
			case 32:
				PlayThread.pt().paused = !PlayThread.pt().paused;
				break;
				
			//left arrow - right frame
			case 37:
				LMovie.mov().current_frame++;
				GUINotifier.n.paintFrame();
				break;
				
			//right arrow - left frame
			case 39:
				LMovie.mov().current_frame--;
				GUINotifier.n.paintFrame();
				break;
			}
//		}
	
	}

	@Override
	public void keyReleased(KeyEvent e) {}
	

}
