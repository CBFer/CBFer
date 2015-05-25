package movie;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Singleton movie class holding currently loaded movie and its parameters
 * @author Peter
 *
 */

public class LMovie 
{
	public int xdim = 0;
	public int ydim = 0;
	public double framerate = 0;
	public String fn = "";
	
	//low memory fixer, load only up to max_frames
	public int max_frames = Integer.MAX_VALUE;
	
	public int current_frame = 0;
	
	public ArrayList<BufferedImage> frames;
	
	private static LMovie mov = null;
	
	public static LMovie mov()
	{
		if (mov == null)
			mov = new LMovie();
		
		return mov;
	}
	
	private LMovie()
	{
		frames = new ArrayList<BufferedImage>();		
	}

	public void addframe(BufferedImage b)
	{
		frames.add(b);
	}
	
	public double getTime()
	{
		return (1.0/(double)framerate) * (length()*1.0);
	}
	
	public int length()
	{
		return frames.size();
	}
	
	void unload()
	{
		frames.clear();
	}
	
}
