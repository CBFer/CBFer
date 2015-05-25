package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import movie.ROICollection;
import algorithms.WholeImageAnalysisThread;
import algorithms.WholeImageAnalysisThreadMaster;

/**
 * Action menu listeners
 * @author Peter
 *
 */

public class MenuListener implements ActionListener
{
	GUI inst;
	public MenuListener(GUI inst) 
	{
		this.inst = inst;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		switch(e.getActionCommand())
		{
		case "0":
		case "1":
			if (inst.movieLoaded)
			{
				if (ROICollection.rc().freqMap == null && ROICollection.rc().dt == null)
				{
					//clear any existing rois, all operations are performed from scratch
					inst.displayROINumerals.setSelected(false);
					PlayThread.pt().paused = true;
					ROICollection.rc().removeAll();
					ROICollection.rc().drawFrequencyOnCanvas = false;
					
					Thread[] workers = new Thread[GUI.NUM_WORKERS];
					for (int i=0; i<GUI.NUM_WORKERS; i++)
					{
						workers[i] = new Thread(new WholeImageAnalysisThread(i,GUI.NUM_WORKERS));
						workers[i].start();
					}
					
					//master will inform this thread when all operations are done and
					//pass back the cmd to the main Frame thread
					Thread master = new Thread(new WholeImageAnalysisThreadMaster(workers,
							Integer.parseInt(e.getActionCommand())));
					master.start();
				}
				else
				{
					inst.WOIComplete(Integer.parseInt(e.getActionCommand()));
				}
			}			
			break;
			
		case "help":
			JOptionPane.showMessageDialog(inst,
					"CBFer by Peter Szot - 2015\n\n"
					+ "1. Load a movie via 'File' > 'Load Movie'.\n"
					+ "2. Click on the movie pane to create an ROI. \n"
					+ "The CBF is calculated automatically and displayed next to it.\n"
					+ "3. Delete ROIs by right-clicking on them, or \n"
					+ "by selecting 'Actions' > 'Clear ROIs' from the menu.\n"
					+ "4. Alternately, browse through the video using the provided controls. \n"
					+ "Pause, rewind, scan at will. Change playback speed via setting a \n"
					+ "new 'Frame Rate' on the left.",
					"About",JOptionPane.QUESTION_MESSAGE);
			break;
			
		case "ClearROIs":
			ROICollection.rc().removeAll();
			inst.updateROIList();
			GUINotifier.n.paintFrame();
			break;
			
		case "ToggleDrawROIFreq":
			ROICollection.rc().drawFrequencyOnCanvas = !ROICollection.rc().drawFrequencyOnCanvas;
			GUINotifier.n.paintFrame();
			break;
			
		default:
			break;
		}
	}
}
