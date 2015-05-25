package mediacontrol;

import gui.GUINotifier;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import movie.LMovie;

/**
 * Listener for scroll based movie jumping
 * @author Peter
 *
 */

public class MovScroll implements AdjustmentListener
{

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) 
	{
		LMovie.mov().current_frame = e.getValue();
	    GUINotifier.n.paintFrame();
	}
	
}
