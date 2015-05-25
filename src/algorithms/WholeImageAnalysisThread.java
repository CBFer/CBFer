package algorithms;

import java.util.ArrayList;

import gui.GUINotifier;
import movie.LMovie;
import movie.ROI;
import movie.ROICollection;

/**
 * Parallelisation thread for performing high throughput Whole Image Analysis
 * @author Peter
 *
 */

public class WholeImageAnalysisThread implements Runnable 
{
	private int index, numthreads;
	
	public WholeImageAnalysisThread(int index, int numthreads) 
	{
		this.index = index;
		this.numthreads = numthreads;
	}
	
	@Override
	public void run() 
	{
		final int roix = 4, roiy = 4;
		ROICollection roic = ROICollection.rc();
		
		int width = LMovie.mov().xdim;
		int height = LMovie.mov().ydim;
		
		int start_x = width/numthreads * index, end_x = width/numthreads * (index+1);
		
		//alignment fix
		start_x = start_x - start_x%4;
		end_x = end_x - end_x%4;
		
		//alignment workaround, slightly imbalance last thread
		if (index == 7) end_x = width;
		
		//progress bar will be based on the progress of the 0th thread only,
		//it should be quite reflective of total progress
		int total_progress = (width/roix * height/roiy)/numthreads;
		
		int itr = 0;
		ArrayList<ROI> local_collection = new ArrayList<ROI>();
		for (int x=start_x; x<end_x; x+=roix)
		{
			for (int y=0; y<height; y+=roiy)
			{
				ROI roi = new ROI();
				roi.x1 = x; roi.x2 = roix;
				roi.y1 = y; roi.y2 = roiy;
				
				//perform fft calc
				roi.calculateFrequency();
				
				local_collection.add(roi);
				if (index == 0)
				{
					GUINotifier.n.updateProgress(itr, total_progress);
					itr++;
				}
			}
		}
		
		//merge collections
		roic.list().addAll(local_collection);
	}

}
