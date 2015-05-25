package movie;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

import algorithms.Complex;
import algorithms.FastFourierTransform;
import algorithms.FreqPeaks;

/**
 * Region of Interest object, allows for setting, modifying, and performing signal processing
 * @author Peter
 *
 */

public class ROI 
{

	//start x, start y, width, height
	public int x1,y1,x2,y2;
	
	public double freq = -1;
	
	public ROI()
	{
		this.invalidate();
	}
	
	public void invalidate()
	{
		x1=x2=y1=y2=-1;
	}
	
	public boolean valid()
	{
		if (x1 < 0 || y1 < 0 || x2 < 0 || y2 < 0)
		{
			return false;
		}
		return true;
	}
	
	/**
	 * For an ROI w*h, compute the average of all elements in w*h for
	 * each frame in the movie. Returns a numframes size complex array
	 * @return
	 */
	public Complex[] getROIIntensityPerFrame(int numpoints)
	{
		if (!valid())
		{
			return null;
		}
		
		ArrayList<BufferedImage> m = LMovie.mov().frames;
		if (numpoints > m.size())
		{
			return null;
		}
		
		//init double buffer to zeros
		double r[] = new double[numpoints]; for (int i=0; i<r.length; i++) r[i] = 0;
		
		int itr = 0;
		for (int div=0; div < numpoints; div++)
		{
			BufferedImage sub = m.get(div).getSubimage(x1, y1, x2, y2);
			for (int x=0; x<sub.getWidth(); x++)
			{
				for (int y=0; y<sub.getHeight(); y++)
				{
					Color c = new Color(sub.getRGB(x, y));
					r[itr] += 0.2989*(c.getRed()*1.0) + 
							0.5870*(c.getGreen()*1.0) + 
							0.1140*(c.getBlue()*1.0);
				}
			}
			//compute average intensity of the entire ROI per frame
			r[itr] /= (x2*y2);
			itr++;
		}
		
		//calculate intensity average
		double s = 0;
		for (int i=0; i<r.length; i++)
		{
			s += r[i];
		}
		s=s/(1.0*r.length);
		
		//normalise by subtracting average
		Complex c[] = new Complex[r.length];
		for (int i=0; i<r.length; i++)
		{
			c[i] = new Complex(r[i]-s,0);
		}
		
		return c;
	}
	
	public void calculateFrequency()
	{	
		//calculate the number of sample points we can use
		int numpoints = 0;
		//System.out.println(LMovie.mov().length());
		for (int i=2; i<=LMovie.mov().length(); i*=2)
		{
			if (i*2 > LMovie.mov().length())
			{
				break;
			}
			else numpoints = i*2;
		}
		//System.out.println("Samples: "+numpoints);
		
		double samplerate = LMovie.mov().framerate;
		
		//read ROI data
		Complex x[] = getROIIntensityPerFrame(numpoints);					
		x = FastFourierTransform.fft(x);
		
		//find peak frequencies, search up to the Nyquist point
		ArrayList<FreqPeaks> peaks = new ArrayList<FreqPeaks>();
		final int comp = (int) (samplerate/4);
		for (int i=0; i<x.length/2+1; i++)
		{
			boolean localmax = true;
			double p = x[i].abs();
			//System.err.println(p);
			//define a local maximum to be larger than the 'comp' points on either side of it
			for (int j=-comp; j<=comp; j++)
			{
				//bounds checking
				if (i+j < 0 || i+j >= x.length/2+1 || j==0)
					continue;
				
				if (p <= x[i+j].abs())
				{
					localmax = false;
					break;
				}
			}
			
			if (localmax)
				peaks.add(new FreqPeaks(i, p));
		}
		
		Collections.sort(peaks);
//		System.err.println("Frequency peaks by amplitude:");
//		for (int i=0; i<peaks.size(); i++)
//		{			
//			System.err.println("ind:"+i+" "+peaks.get(i).amplitude+" f: "+peaks.get(i).getFrequency(numpoints, samplerate));
//		}
		
		//workaround for heavy noise data, the real value should be in the first 2 peaks
		freq = peaks.get(0).getFrequency(numpoints, samplerate);
		if (peaks.size() > 1)
		{
			if (peaks.get(1).getFrequency(numpoints, samplerate) > 10*freq)
			{
				if (Math.abs(peaks.get(0).amplitude) > 250)
					freq = peaks.get(1).getFrequency(numpoints, samplerate);
				
			}
		}
	}
}
