package algorithms;

/**
 * Sortable helper class to sort frequency peaks in FFT analysis
 * @author Peter
 *
 */

public class FreqPeaks implements Comparable<FreqPeaks>
{

	public int index;
	public double amplitude;
	
	public FreqPeaks(int index, double amplitude) 
	{
		this.index = index;
		this.amplitude = amplitude;
	}
	
	@Override
	public int compareTo(FreqPeaks o) 
	{
		if (this.amplitude >= o.amplitude)
		{
			return -1;
		}
		else
		{
			return 1;
		}
	}
	
	public double getFrequency(int numpoints, double samplerate)
	{
		return index*(samplerate/(1.0*numpoints));
	}

}
