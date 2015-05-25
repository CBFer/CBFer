package gui;

/**
 * Singleton notifier class for informing the GUI of a variety of events
 * @author Peter
 *
 */

public class GUINotifier 
{
	public static GUINotifier n = null;
	GUI gui;
	
	private GUINotifier(GUI gui) 
	{
		this.gui = gui;
	}

	public static void init(GUI gui)
	{
		n = new GUINotifier(gui);
	}
	
	public static GUINotifier notifier()
	{
		return n;
	}
	
	public void movieLoaded()
	{
		gui.movieLoaded();
	}
	
	public void paintFrame()
	{
		gui.toFrame();
	}
	
	public void updateROIList()
	{
		gui.updateROIList();
	}
	
	public void updateProgress(int current, int max)
	{
		gui.updateProgress(current, max);
	}
	
	public void WOIComplete(int cmd)
	{
		gui.WOIComplete(cmd);
	}
}
