package mediacontrol;

import gui.GUINotifier;
import gui.PlayThread;

import java.awt.PopupMenu;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import movie.ROI;
import movie.ROICollection;

/**
 * Interactive ROI 'getter' and 'setter' for placing and removing ROIs on the movie view
 * @author Peter
 *
 */

public class CanvasROIListener implements MouseListener
{
	
	ROICollection roic;
	PopupMenu ref;
	
	public CanvasROIListener(PopupMenu p) 
	{
		roic = ROICollection.rc();
		ref = p;
	}
	
	@Override
	public void mousePressed(MouseEvent e) 
	{
        int x = e.getX(), y = e.getY();

		//right click removes ROIs
		if (e.getButton() == MouseEvent.BUTTON3)
		{
			//search all ROIs for one that is selected
			int itr = 0;
			for (ROI r : roic.list())
			{
				if (x >= r.x1 && x <= r.x1 + r.x2)
				{
					if (y >= r.y1 && y <= r.y1 + r.y2)
					{
						roic.remove(itr);
						break;
					}
				}
				itr++;
			}
			GUINotifier.n.updateROIList();
			if (PlayThread.pt().paused)
			{
				GUINotifier.n.paintFrame();
			}
		}
		
		
		if (e.getButton() == MouseEvent.BUTTON2)
		{	
			ref.show(e.getComponent(), x, y);
			return;
		}
		
		//otherwise we create a new one
		if (e.getButton() != MouseEvent.BUTTON1)
			return;
		
		ROI roi = new ROI();
		
		roi.x1 = x; roi.y1 = y;
		roi.x2 = 0; roi.y2 = 0;
		
		roic.add(roi);
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		if (e.getButton() != MouseEvent.BUTTON1)
			return;
		
		ROI roi = roic.get(roic.c_roi);
		
		int x = e.getX(), y = e.getY();
		
		if (x < roi.x1)
		{
			int t=roi.x1;
			roi.x1 = x;
			roi.x2 = t-x;
		}
		else roi.x2 = x-roi.x1; 
		if (y < roi.y1)
		{
			int t=roi.y1;
			roi.y1 = y;
			roi.y2 = t-y;
		}
		else roi.y2 = y-roi.y1;
		
		//XXX: temporary auto select 4x4 pixels
		roi.x2 = 4; roi.y2 = 4;

		//invoke FFT frequency calculation
		roi.calculateFrequency();

		//if movie is paused, force a refresh
		if (PlayThread.pt().paused)
		{
			GUINotifier.n.paintFrame();
		}
		GUINotifier.n.updateROIList();
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}	

}
