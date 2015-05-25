package movie;

import gui.FreqPlotter;
import gui.PlayThread;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;

/**
 * Listener for initiating movie loading and unloading of previous data
 * @author Peter
 *
 */

public class MovieLoader implements ActionListener
{
	
	Component parent;
	
	public MovieLoader(Component parent) 
	{
		this.parent = parent;
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		JFileChooser open = new JFileChooser(".");
		open.showOpenDialog(parent);
		
		File mov = open.getSelectedFile();
		if (mov != null)
		{
			LMovie.mov().unload();
			ROICollection.rc().removeAll();
			ROICollection.rc().freqMap = null;
			ROICollection.rc().dt = null;
			FreqPlotter.get().dispose();
			
			PlayThread.pt().paused = true;
			
			//start a separate thread to load the movie file
			Thread t = new Thread(new MovieLoaderThread(mov));
			t.start();
		}
	}

}
