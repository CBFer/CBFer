package mediacontrol;

import gui.GUINotifier;
import gui.PlayThread;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import movie.LMovie;

/**
 * Most media based controllers and listeners
 * @author Peter
 *
 */

public class MovieController implements ActionListener
{

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		int cf = LMovie.mov().current_frame;
		
		switch (e.getActionCommand())
		{
		case "nf":
			//go to the next frame
			LMovie.mov().current_frame = (++cf);
			break;
			
		case "pf":
			//go to the previous frame
			LMovie.mov().current_frame = (--cf);
			break;
			
		case "end":
			//go to end of movie
			LMovie.mov().current_frame = (cf=LMovie.mov().length()-1);
			break;
			
		case "begin":
			//go to end of movie
			LMovie.mov().current_frame = (cf=0);
			break;
			
		case "pause":
			//toggle pause state of movie player
			PlayThread.pt().paused = !PlayThread.pt().paused;
			break;
		
		}
		GUINotifier.n.paintFrame();
	}
	
}
