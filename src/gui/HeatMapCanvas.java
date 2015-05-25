package gui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import movie.LMovie;
import movie.ROICollection;

/**
 * Canvas extension for drawing custom frequency-based heatmaps
 * @author Peter
 *
 */

public class HeatMapCanvas extends Canvas 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2410277894548721727L;
	ROICollection roic;
	
	public int xpos = -1;
	public int ypos = -1;
	
	public static final int BLACKRED = 1;
	public static final int GREYSCALE = 0;
	
	public BufferedImage heatmapBuf;
	
	int colormode = 0;
	
	public HeatMapCanvas() 
	{
		roic = ROICollection.rc();
	}
	
	@Override
	public void paint (Graphics g) 
	{
		if (roic.freqMap == null)
			return;
		
		LMovie mov = LMovie.mov();
		
		heatmapBuf = new BufferedImage(mov.xdim, mov.ydim, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D gheatmapBuf = heatmapBuf.createGraphics();
		
		for (int x=0, i=0; x<mov.xdim && i < roic.freqMap.length; x+=4, i++)
		{
			for (int y=0, j=0; y<mov.ydim && j< roic.freqMap[i].length; y+=4, j++)
			{
				if (colormode == BLACKRED)
				{
					gheatmapBuf.setColor(Color.getHSBColor(0, 1.0f, 
							(float)(((roic.freqMap[i][j]-roic.minFreq)/(roic.maxFreq-roic.minFreq))*1.0))
					);					
				}
				else if (colormode == GREYSCALE)
				{
					int col = (int)(((roic.freqMap[i][j]-roic.minFreq)/(roic.maxFreq-roic.minFreq))*255);
					gheatmapBuf.setColor(new Color(col,col,col));					
				}
				gheatmapBuf.fillRect(x, y, 4, 4);
			}
		}
		
		g.drawImage(heatmapBuf, 0,0, null);
		
		//draws the roi data over the graph
		if (xpos > 0 && ypos > 0)
		{
			int x = (xpos - xpos%4)/4, y = (ypos - ypos%4)/4;
			g.setColor(Color.yellow);
			String s = String.format("%.2f", roic.freqMap[x][y]);
			g.drawChars(s.toCharArray(), 0, s.length(), xpos, ypos);
		}
	}
}
