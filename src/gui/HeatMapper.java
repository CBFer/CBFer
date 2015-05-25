package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;

import movie.LMovie;
import movie.ROICollection;

/**
 * Window frame containing the Heatmap drawing based of WIA analysis.
 * @author Peter
 *
 */

public class HeatMapper extends JFrame
{
	private static final long serialVersionUID = 1448666561623089639L;
	private static HeatMapper fp = null;
	
	public static HeatMapper get()
	{
		if (fp == null)
		{
			fp = new HeatMapper();
		}
		return fp;
	}
	
	private HeatMapper() 
	{
		setBackground(Color.WHITE);
	}
	
	public void newHeatMap()
	{
		PopupMenu exportMenu = new PopupMenu();
		HeatMapCanvas hmc = new HeatMapCanvas();
		hmc.addMouseListener(new MouseListener() 
		{
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) 
			{
				//left click queries ROIs
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					hmc.xpos = e.getX();
					hmc.ypos = e.getY();
					hmc.repaint();
				}
				//right click export menu
				else if (e.getButton() == MouseEvent.BUTTON3)
				{
					exportMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseClicked(MouseEvent e) {}
		});
		
		//allows saving of the heatmap to file
		MenuItem frame_extract = new MenuItem("Export Graph");
		frame_extract.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				try
				{
					if (hmc.heatmapBuf != null)
					{
						File f = new File(LMovie.mov().fn+".heatmap.bmp");
						ImageIO.write(hmc.heatmapBuf, "bmp", f);
						JOptionPane.showMessageDialog(HeatMapper.get(),
								"HeatMap written to:\n\n"+f.getAbsolutePath(),
								"Exported!",JOptionPane.INFORMATION_MESSAGE);
					}
				}
				catch(Exception e1)
				{ e1.printStackTrace(); }
				
			}
		});
		exportMenu.add(frame_extract);
		hmc.add(exportMenu);
		
		setLayout(new BorderLayout());
		getContentPane().removeAll();
		getContentPane().add(hmc);
		setSize(new Dimension(LMovie.mov().xdim+10,LMovie.mov().ydim+54));
		setMinimumSize(new Dimension(LMovie.mov().xdim+10,LMovie.mov().ydim+54));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		ROICollection r = ROICollection.rc();
		setTitle(String.format("Frequency Heatmap : %.2f-%.2f Hz", r.minFreq, r.maxFreq));
		
		//color map options
		JMenuBar menu = new JMenuBar();
		JMenu options = new JMenu("Options");
		JRadioButtonMenuItem col1 = new JRadioButtonMenuItem("Black -> Red");
		JRadioButtonMenuItem col2 = new JRadioButtonMenuItem("Black -> White");

		col1.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				hmc.colormode = HeatMapCanvas.BLACKRED;
				hmc.repaint();
				col1.setSelected(true);
				col2.setSelected(false);
			}
		});
		col1.setSelected(false);
		options.add(col1);
		
		col2.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				hmc.colormode = HeatMapCanvas.GREYSCALE;
				hmc.repaint();
				col1.setSelected(false);
				col2.setSelected(true);
			}
		});
		col2.setSelected(true);
		options.add(col2);
		
		menu.add(options);
		
		setJMenuBar(menu);
	}
	
	public void showGraph()
	{
		setVisible(true);
		repaint();
	}
}
