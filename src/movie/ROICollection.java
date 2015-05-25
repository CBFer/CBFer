package movie;

import gui.GUI;

import java.util.ArrayList;

import de.erichseifert.gral.data.DataTable;

/**
 * Singleton ROI collector, managing multiple ROIs
 * @author Peter
 *
 */

public class ROICollection 
{
	private ArrayList<ROI> rois;
	private static ROICollection rc;
	public int c_roi;
	public boolean drawFrequencyOnCanvas = true;
	public boolean drawROIsOnCanvas = true;
	
	public double[][] freqMap = null;
	public double minFreq=-1, maxFreq=-1;
	
	public DataTable dt = null;
	
	@SuppressWarnings("unchecked")
	public void saveFreqDist()
	{
		dt = new DataTable(Double.class);
		
		for (ROI roi : rois)
		{
			//add all data to table, order doesn't matter here
			dt.add(roi.freq);
		}
		
		int w = LMovie.mov().xdim, h = LMovie.mov().xdim;
		
		freqMap = new double[w/4][h/4];
		minFreq = Double.MAX_VALUE;
		maxFreq = -1;
		for (int i=0; i<rois.size(); i++)
		{
			ROI r = rois.get(i);
			//autocull erroneous data
			if (r.freq > GUI.CULL_UPPER_FREQUENCY)
			{
				r.freq = 0;
			}
			freqMap[r.x1/4][r.y1/4] = r.freq;
			if (minFreq > freqMap[r.x1/4][r.y1/4]) minFreq = freqMap[r.x1/4][r.y1/4];
			if (maxFreq < freqMap[r.x1/4][r.y1/4]) maxFreq = freqMap[r.x1/4][r.y1/4];
		}
		
		//erase rois from memory
		removeAll();
	}
	
	private ROICollection()
	{
		rois = new ArrayList<ROI>();
		c_roi = -1;
	}
	
	public static ROICollection rc()
	{
		if (rc == null)
			rc = new ROICollection();
		
		return rc;
	}
	
	public ArrayList<ROI> list()
	{
		return rois;
	}
	
	public ROI get(int w)
	{
		if (w >= rois.size() || w < 0)
			return null;
		
		return rois.get(w);
	}
	
	public int size()
	{
		return rois.size();
	}
	
	public void add(ROI roi)
	{
		rois.add(roi);
		c_roi++;
	}
	
	public void remove(int w)
	{
		if (w >= rois.size())
			return;
		
		rois.remove(w);
		c_roi--;
	}
	
	public void removeAll()
	{
		rois.clear();
		c_roi = -1;
	}

}
