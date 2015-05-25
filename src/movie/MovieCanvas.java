package movie;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Customised canvas for rendering the movie frames and drawing ROIs/metadata
 * @author Peter
 *
 */

public class MovieCanvas extends Canvas 
{

	private ROICollection roic;
	
	public BufferedImage frameBuf;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1760605716825307375L;
	int frame = 0;
	
	public MovieCanvas() 
	{
		super();
		
		roic = ROICollection.rc();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void paint (Graphics g) 
	{
		LMovie m = LMovie.mov();
		if (m.length() == 0 || g == null)
		{
			return;
		}
		frameBuf = new BufferedImage(m.xdim, m.ydim, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D gframeBuf = frameBuf.createGraphics();
		
		//draw movie buffered frame into framebuffer
		gframeBuf.drawImage(m.frames.get(frame), 0,0, null);
		
	    //specify whether to draw ROIs in real time
	    if (!roic.drawROIsOnCanvas)
	    {
	    	return;
	    }
	    else
	    {
		    //if there is are defined ROIs
		    int itr = 0;
		    for (ROI roi : (ArrayList<ROI>)roic.list().clone())
		    {
		    	if (roi.valid())
		    	{
		    		if (itr == roic.c_roi)
		    		{
		    			gframeBuf.setColor(Color.white);	    			
		    		}
		    		else
		    		{
		    			gframeBuf.setColor(Color.red);
		    		}
		    		gframeBuf.drawRect(roi.x1, roi.y1, roi.x2, roi.y2);
		    		
		    		if (roic.drawFrequencyOnCanvas)
		    		{
		    			char[] freq = String.format("%.2f", roi.freq).toCharArray();
		    			gframeBuf.drawChars(freq, 0, freq.length, roi.x1+6, roi.y1+2);	    			
		    		}
		    	}
		    	itr++;
		    }
	    }

	    g.drawImage(frameBuf, 0,0, null);
	}
	
	public void paintFrame(int f)
	{
		frame = f;
		this.paint(this.getGraphics());
	}

}
