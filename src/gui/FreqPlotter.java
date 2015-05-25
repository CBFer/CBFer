package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;

import movie.LMovie;
import movie.ROICollection;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.EnumeratedData;
import de.erichseifert.gral.data.statistics.Histogram1D;
import de.erichseifert.gral.data.statistics.Statistics;
import de.erichseifert.gral.plots.BarPlot;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.Insets2D;
import de.erichseifert.gral.util.MathUtils;
import de.erichseifert.gral.util.Orientation;

/**
 * Window frame for plotting the WIA frequency histogram. Uses Java GRAL for plotting.
 * @author Peter
 *
 */

public class FreqPlotter extends JFrame
{
	private static final long serialVersionUID = 2789137894743714667L;
	private static FreqPlotter fp = null;
	
	public static FreqPlotter get()
	{
		if (fp == null)
		{
			fp = new FreqPlotter();
		}
		return fp;
	}
	
	private FreqPlotter() 
	{
		setBackground(Color.WHITE);
	}
	
	public void newHistogram()
	{
		getContentPane().removeAll();

		//get x-axis range
		ROICollection rc = ROICollection.rc();
		Number [] range = new Number[(int)(Math.ceil(rc.maxFreq-rc.minFreq))+1];
		for (int i=0; i<range.length; i++)
		{
			range[i] = i;
		}
		
		// Create histogram from data
		Histogram1D histogram = new Histogram1D(ROICollection.rc().dt,Orientation.VERTICAL,range);
		
		// Create a second dimension (x axis) for plotting
		DataSource histogram2d = new EnumeratedData(histogram, 0.5, 1.0);

		BarPlot plot = new BarPlot(histogram2d);
		
		//plot formatting
		plot.setInsets(new Insets2D.Double(20.0, 65.0, 50.0, 40.0));
		plot.getTitle().setText("Frequency Distribution");
		plot.setBarWidth(0.78);
		
		// Format x axis
		plot.getAxisRenderer(BarPlot.AXIS_X).setTickAlignment(0.0);
		plot.getAxisRenderer(BarPlot.AXIS_X).setTickSpacing(1.0);
		plot.getAxisRenderer(BarPlot.AXIS_X).setMinorTicksVisible(false);
		plot.getAxisRenderer(BarPlot.AXIS_X).setLabel("Frequency (Hz)");
		// Format y axis
		plot.getAxis(BarPlot.AXIS_Y).setRange(0.0,
				MathUtils.ceil(histogram.getStatistics().get(Statistics.MAX)*1.1, 25.0));
		plot.getAxisRenderer(BarPlot.AXIS_Y).setTickAlignment(0.0);
		plot.getAxisRenderer(BarPlot.AXIS_Y).setMinorTicksVisible(false);
		plot.getAxisRenderer(BarPlot.AXIS_Y).setIntersection(-1);

		// Format bars
		plot.getPointRenderer(histogram2d).setColor(GraphicsUtils.deriveWithAlpha(new Color( 55, 170, 200), 128));
		plot.getPointRenderer(histogram2d).setValueVisible(true);

		// Add plot to Swing component
		InteractivePanel panel = new InteractivePanel(plot);
		panel.setPannable(false);
		panel.setZoomable(false);
		
		setLayout(new BorderLayout());
		getContentPane().add(panel);
		setSize(new Dimension(LMovie.mov().xdim,LMovie.mov().ydim));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	public void showGraph()
	{
		setVisible(true);	
		repaint();
	}
}
