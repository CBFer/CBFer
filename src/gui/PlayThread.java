package gui;

import movie.LMovie;

/**
 * Primary timing control thread, causes frame updates according to the set framerate
 * @author Peter
 *
 */

public class PlayThread implements Runnable
{
	//default framerate = 30
	double delayms = 0;
	long sleepms = 100;
	
	public boolean paused = false;
	
	public boolean kill = false;
	
	private PlayThread() 
	{
		setFrameRate(30);
	}
	
	private static PlayThread pt = null;
	
	public static PlayThread pt()
	{
		if (pt == null)
			pt = new PlayThread();
		
		return pt;
	}
	
	public void setFrameRate(double fr)
	{
		delayms = 1000.0/fr;
	}

	@Override
	public void run()
	{
		LMovie m = LMovie.mov();
		do
		{
			while (!paused && !kill)
			{
				//advance current frame by 1
				int cf = (m.current_frame+=1);
				
				//loop around if at end
				if (cf == m.length())
				{
					cf = (m.current_frame=0);
				}
				
				//do drawing
				GUINotifier.n.paintFrame();
				
				//wait before drawing next frame
				try{Thread.sleep((long)delayms);}catch (Exception e){}
			}
			
			try{Thread.sleep((long)sleepms);}catch (Exception e){}
		}
		while (paused || !kill);
		
	}
	
}
