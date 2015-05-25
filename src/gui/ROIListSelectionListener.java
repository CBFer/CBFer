package gui;

import java.awt.List;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JTextArea;

import movie.ROI;
import movie.ROICollection;

/**
 * Listener for changes to ROI draw conditions
 * @author Peter
 *
 */

public class ROIListSelectionListener implements ItemListener
{
	List l;
	JTextArea details;
	
	public ROIListSelectionListener(List l, JTextArea t) 
	{
		this.l = l;
		details = t;
	}

	@Override
	public void itemStateChanged(ItemEvent e) 
	{
		ROICollection rc = ROICollection.rc();
		int index = l.getSelectedIndex();
		rc.c_roi = index;
		
		ROI roi = rc.get(index);
		String text = String.format("x:%d y:%d\nw:%d h:%d\nFreq: %.2f", roi.x1,roi.y1,roi.x2,roi.y2,roi.freq);
		details.setText(text);
	}

}
