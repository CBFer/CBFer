package algorithms;

import movie.ROICollection;
import gui.GUINotifier;

/**
 * Master WIA analysis thread. Listens for the completion of all worker threads and informs the main user interface thread
 * @author Peter
 *
 */

public class WholeImageAnalysisThreadMaster implements Runnable 
{	
	Thread[] children;
	int cmd;
	
	public WholeImageAnalysisThreadMaster(Thread[] c, int cmd) 
	{
		children = c;
		this.cmd = cmd;
	}
	
	@Override
	public void run() 
	{
		for (Thread c : children)
		{
			try {
				c.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//process ROIs 
		ROICollection.rc().saveFreqDist();
		
		//reset progress bar
		GUINotifier.n.updateProgress(0, 1);
		GUINotifier.n.WOIComplete(cmd);
		GUINotifier.n.paintFrame();
	}

}
