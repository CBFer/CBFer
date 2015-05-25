package gui;

import javax.imageio.ImageIO;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.Color;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Scrollbar;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.border.LineBorder;

import mediacontrol.*; 
import movie.*;

import java.awt.List;
import java.io.File;
import java.io.IOException;

import javax.swing.SwingConstants;
import javax.swing.JProgressBar;

/**
 * Main thread for controlling primary user interface.
 * @author Peter
 *
 */

public class GUI extends JPanel 
{
	public static final int NUM_WORKERS = 8;
	public static final int CULL_UPPER_FREQUENCY = 15;
	
	public GUI() {
		setLayout(null);
		
		JLabel lblMovieDetails = new JLabel("Movie Details:");
		lblMovieDetails.setBounds(10, 11, 86, 14);
		add(lblMovieDetails);
		
		JLabel lblFrameCount = new JLabel("Frames");
		lblFrameCount.setHorizontalAlignment(SwingConstants.RIGHT);
		lblFrameCount.setBounds(20, 36, 53, 14);
		add(lblFrameCount);
		
		JLabel lblFrameRate = new JLabel("Frame Rate");
		lblFrameRate.setHorizontalAlignment(SwingConstants.RIGHT);
		lblFrameRate.setBounds(5, 206, 68, 14);
		add(lblFrameRate);
		
		tfFrameCount = new JTextField();
		tfFrameCount.setEditable(false);
		tfFrameCount.setBounds(80, 33, 86, 20);
		add(tfFrameCount);
		tfFrameCount.setColumns(10);
		
		JLabel lblResolution = new JLabel("Resolution");
		lblResolution.setHorizontalAlignment(SwingConstants.RIGHT);
		lblResolution.setBounds(10, 64, 63, 14);
		add(lblResolution);
		
		tfResolution = new JTextField();
		tfResolution.setBackground(SystemColor.menu);
		tfResolution.setEditable(false);
		tfResolution.setBounds(80, 61, 86, 20);
		add(tfResolution);
		tfResolution.setColumns(10);
		
		movieCanvas = new MovieCanvas();
		movieCanvas.setBackground(Color.BLACK);
		movieCanvas.setBounds(172, 33, 359, 359);
		add(movieCanvas);
		
		frame_extract_menu = new PopupMenu();
		MenuItem frame_extract = new MenuItem("Extract Frame");
		frame_extract.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				try {
					//pause the video and get frame data
					if (movieCanvas.frameBuf != null)
					{
						PlayThread.pt().paused = true;
						File f = new File((LMovie.mov().current_frame+1)+".bmp");
						ImageIO.write(movieCanvas.frameBuf, "bmp", f);
						JOptionPane.showMessageDialog(jFrame,
								"Frame written to:\n\n"+f.getAbsolutePath(),
								"Exported!",JOptionPane.INFORMATION_MESSAGE);
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		frame_extract_menu.add(frame_extract);
		movieCanvas.add(frame_extract_menu);
		
		lblMovieStatus = new JLabel("No Movie Loaded.");
		lblMovieStatus.setBounds(172, 11, 786, 14);
		add(lblMovieStatus);
		
		btnBegin = new Button("|<");
		btnBegin.setBounds(172, 417, 22, 22);
		btnBegin.setActionCommand("begin");
		add(btnBegin);
		
		scrProgress = new Scrollbar();
		scrProgress.setMaximum(1);
		scrProgress.setBackground(Color.WHITE);
		scrProgress.setBlockIncrement(30);
		scrProgress.setOrientation(Scrollbar.HORIZONTAL);
		scrProgress.setBounds(172, 398, 359, 14);
		scrProgress.setVisibleAmount(1);
		add(scrProgress);
		
		btnEnd = new Button(">|");
		btnEnd.setBounds(323, 417, 22, 22);
		btnEnd.setActionCommand("end");
		add(btnEnd);
		
		btnPlayPause = new Button("Play/Pause");
		btnPlayPause.setBounds(351, 417, 70, 22);
		btnPlayPause.setActionCommand("pause");
		add(btnPlayPause);
		
		JLabel lblFilename = new JLabel("Filename");
		lblFilename.setHorizontalAlignment(SwingConstants.RIGHT);
		lblFilename.setBounds(10, 113, 63, 14);
		add(lblFilename);
		
		tfFrameRate = new JTextField();
		tfFrameRate.setEditable(true);
		tfFrameRate.setBounds(80, 206, 86, 20);
		add(tfFrameRate);
		tfFrameRate.setColumns(10);
		
		tfFilename = new JTextArea();
		tfFilename.setBackground(SystemColor.menu);
		tfFilename.setEditable(false);
		tfFilename.setBounds(80, 114, 86, 86);
		add(tfFilename);
		tfFilename.setColumns(10);
		tfFilename.setLineWrap(true);
		tfFilename.setBorder(new LineBorder(SystemColor.inactiveCaption));
		
		btnPrev = new Button("<");
		btnPrev.setBounds(199, 417, 22, 22);
		btnPrev.setActionCommand("pf");
		add(btnPrev);
		
		btnNext = new Button(">");
		btnNext.setBounds(295, 417, 22, 22);
		btnNext.setActionCommand("nf");
		add(btnNext);
		
		tfFramePos = new JTextField();
		tfFramePos.setText("0/0");
		tfFramePos.setEditable(false);
		tfFramePos.setBounds(227, 417, 62, 22);
		add(tfFramePos);
		tfFramePos.setColumns(10);

		//MenuBar controls
		menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 449, 20);
        JMenu menu = new JMenu("File");
        
        loadMovie = new JMenuItem("Load Movie");
        loadMovie.addActionListener(new MovieLoader(this));
        menu.add(loadMovie);
        
        JMenuItem exitmenu = new JMenuItem("Exit");
        exitmenu.addActionListener(new ActionListener()
        {
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				jFrame.dispose();
			}
		});
        menu.add(exitmenu);

        //listener for most action menu events
        MenuListener ml = new MenuListener(this);
        
        JMenu actionsmenu = new JMenu("Actions");
        JMenuItem FFT = new JMenuItem("Clear ROIs");
        FFT.setActionCommand("ClearROIs");
        FFT.addActionListener(ml);
        actionsmenu.add(FFT);
        
        displayROINumerals = new JCheckBoxMenuItem("Draw ROI freq",true);
        displayROINumerals.setActionCommand("ToggleDrawROIFreq");
        displayROINumerals.addActionListener(ml);
        actionsmenu.add(displayROINumerals);        
        
        JMenuItem WIA = new JMenuItem("Whole Image Analysis");
        WIA.setActionCommand("0");
        WIA.addActionListener(ml);
        actionsmenu.add(WIA);
        
        JMenuItem HM = new JMenuItem("Frequency Heat Map");
        HM.setActionCommand("1");
        HM.addActionListener(ml);
        actionsmenu.add(HM);
        
        JMenu helpmenu = new JMenu("Help");
        JMenuItem aboutmenu = new JMenuItem("About");
        aboutmenu.setActionCommand("help");
        aboutmenu.addActionListener(ml);
        helpmenu.add(aboutmenu);
        
        menuBar.add(menu);
        menuBar.add(actionsmenu);
        menuBar.add(helpmenu);
        
        //length of the vid in seconds
        JLabel lblLength = new JLabel("Length");
        lblLength.setHorizontalAlignment(SwingConstants.RIGHT);
        lblLength.setBounds(10, 89, 63, 14);
        add(lblLength);
        
        tfLength = new JTextField();
        tfLength.setEditable(false);
        tfLength.setBackground(SystemColor.menu);
        tfLength.setBounds(80, 88, 86, 20);
        add(tfLength);
        tfLength.setColumns(10);
        
        btnSetRate = new Button("Set Rate");
        btnSetRate.setBounds(80, 231, 86, 23);
        btnSetRate.setActionCommand("setrate");
        add(btnSetRate);
        
        JLabel lblROIs = new JLabel("ROI List");
        lblROIs.setHorizontalAlignment(SwingConstants.RIGHT);
        lblROIs.setBounds(17, 261, 57, 14);
        add(lblROIs);
        
        tfROIDetails = new JTextArea();
        tfROIDetails.setLineWrap(true);
        tfROIDetails.setEditable(false);
        tfROIDetails.setColumns(10);
        tfROIDetails.setBorder(new LineBorder(SystemColor.inactiveCaption));
        tfROIDetails.setBackground(SystemColor.menu);
        tfROIDetails.setBounds(80, 353, 86, 86);
        add(tfROIDetails);

        listROI = new List();
        listROI.addItemListener(new ROIListSelectionListener(listROI,tfROIDetails));
        listROI.setBounds(80, 261, 86, 86);
        add(listROI);
        
        JLabel lblROIDetails = new JLabel("ROI Details");
        lblROIDetails.setBounds(10, 353, 68, 14);
        add(lblROIDetails);
        
        tfMovieTimer = new JTextField();
        tfMovieTimer.setText("0/0 sec");
        tfMovieTimer.setEditable(false);
        tfMovieTimer.setColumns(10);
        tfMovieTimer.setBounds(427, 417, 104, 22);
        add(tfMovieTimer);
        
        progressBar = new JProgressBar();
        progressBar.setBounds(0, 445, 542, 14);
        add(progressBar);
	}

	private static final long serialVersionUID = -5151041547543472432L;
	private JTextField tfFrameCount;
	private JTextField tfResolution;
	private JTextField tfFrameRate;
	private JTextArea tfFilename;
	private JTextField tfFramePos;
	private JTextField tfLength;
	private JTextArea tfROIDetails;
	
	private Button btnEnd;
	private Button btnPlayPause;
	private Button btnBegin;
	private Button btnPrev;
	private Button btnNext;
	private Button btnSetRate;
	
	private JMenuItem loadMovie;
	private JMenuBar menuBar;
	
	private JLabel lblMovieStatus;
	private Scrollbar scrProgress;
	private MovieCanvas movieCanvas;
	private List listROI;
	private JProgressBar progressBar;
	private PopupMenu frame_extract_menu;
	
	public JFrame jFrame;
	
	public boolean movieLoaded = false;
	private JTextField tfMovieTimer;
	
	public JMenuItem displayROINumerals;
	
	public static void main(String[] args)
    {
		if (args.length == 1)
		{
			if (args[0].equals("-lowmem"))
			{
				//should take about 1 GB mem, no more.
				LMovie.mov().max_frames = 256;
			}
		}
		
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() 
        {
            public void run() 
            {
                GUI gui = new GUI();
                gui.jFrame = new JFrame();
                gui.jFrame.getContentPane().add(gui);
                gui.jFrame.setJMenuBar(gui.menuBar);
                gui.jFrame.setMinimumSize(new Dimension(558,521));
                gui.jFrame.setVisible(true);
                gui.jFrame.setTitle("CBFer");
                
                //init the movie storage
                LMovie.mov();
                
                //init ROI selection
                ROICollection.rc();
                
                //closing JFrame closes program
                gui.jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                //init notifier
                GUINotifier.init(gui);
                
                //init movie player thread
                PlayThread.pt();
            }
        });
    }
	
	public void updateProgress(int current, int max)
	{
		progressBar.setMaximum(max);
		progressBar.setValue(current);
	}
	
	/**
	 * update interface when movie is loaded
	 */
	public void movieLoaded()
	{	
		LMovie m = LMovie.mov();
		PlayThread pt = PlayThread.pt();
		pt.setFrameRate(m.framerate);
		pt.paused = false;
		m.current_frame = 0;
		progressBar.setValue(0);
		
		//fill in movie parameters
		tfFrameCount.setText(""+m.length());
		tfResolution.setText(m.xdim+" * "+m.ydim);
		tfFrameRate.setText(String.format("%.3f", m.framerate));
		tfFilename.setText(m.fn);
		tfFramePos.setText("1/"+m.length());
		tfLength.setText(String.format("%.2f sec", m.getTime()));
		lblMovieStatus.setText("Navigate movie with controls below. Left click to create a ROI, right click to remove one. Middle click to bring up context menu.");
		
		//scrollbar
		scrProgress.setMaximum(m.length());
		scrProgress.setValue(0);
		
		//load the first frame and resize the window if necessary
		if (movieCanvas.getHeight() != m.ydim || movieCanvas.getWidth() != m.xdim)
		{
			movieCanvas.setSize(m.xdim, m.ydim);
			Dimension size = new Dimension(172+m.xdim+25,33+m.ydim+130);
			jFrame.setMinimumSize(size);
			jFrame.setMaximumSize(size);
			jFrame.setSize(size);

			scrProgress.setBounds(172, 249+(m.ydim-210), m.xdim, 14);
			btnBegin.setBounds(172, 268+(m.ydim-210), 22, 22);
			btnEnd.setBounds(323, 268+(m.ydim-210), 22, 22);
			btnPlayPause.setBounds(351, 268+(m.ydim-210), 70, 22);
			btnPrev.setBounds(199, 268+(m.ydim-210), 22, 22);
			btnNext.setBounds(295, 268+(m.ydim-210), 22, 22);
			tfFramePos.setBounds(227, 268+(m.ydim-210), 62, 22);
			tfMovieTimer.setBounds(427, 268+(m.ydim-210), 104, 22);
			progressBar.setBounds(0, jFrame.getHeight()-76, jFrame.getWidth(), 14);
			//249, 210
		}
		movieCanvas.paintFrame(0);
		
		//only do this once
		if (!movieLoaded)
		{
			//init the action listeners for media controls
			MovieController ltn = new MovieController();
			btnPrev.addActionListener(ltn);
			btnNext.addActionListener(ltn);
			btnBegin.addActionListener(ltn);
			btnEnd.addActionListener(ltn);
			btnPlayPause.addActionListener(ltn);
			scrProgress.addAdjustmentListener(new MovScroll());
			movieCanvas.addMouseListener(new CanvasROIListener(frame_extract_menu));
			
			btnSetRate.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					try
					{
						String newrate = tfFrameRate.getText();
						double fr = m.framerate;;
						if (newrate.length() == 0)
						{
							//no string provided, reset to movie default
							tfFrameRate.setText(String.format("%.3f", fr));
						}
						else
						{
							fr = Double.parseDouble(newrate);
						}
						PlayThread.pt().setFrameRate(fr);		
						//m.framerate = fr;
						//tfLength.setText(String.format("%.2f sec", m.getTime()));
					} 
					catch(Exception ex) 
					{
						//fail silently if there is a format error
					}
				}
			});
			
			//init the frame timer			
			Thread playTimer = new Thread(pt);
			playTimer.start();
			
			movieLoaded = true;
		}
	}
	
	/**
	 * Draws current frame of the movie in the movie canvas
	 */
	public void toFrame()
	{
		LMovie m = LMovie.mov();
		
		int cframe = m.current_frame;
		if (cframe < 0) cframe = (m.current_frame=0);
		if (cframe >= m.length()) cframe = (m.current_frame=m.length()-1);
		
		scrProgress.setValue(cframe);		
		
		movieCanvas.paintFrame(cframe);
		tfFramePos.setText((cframe+1)+"/"+LMovie.mov().length());
		
		double ctime = (cframe+1)*(1.0/m.framerate);
		tfMovieTimer.setText(String.format("%.2f/%.2f sec", ctime,m.getTime()));
	}
	
	/**
	 * Invoke to redraw ROI list from collection
	 */
	public void updateROIList()
	{
		ROICollection roic = ROICollection.rc();
		listROI.removeAll();
		for (ROI roi : roic.list())
		{
			listROI.add("x:"+roi.x1+" y:"+roi.y1);
		}
		listROI.select(roic.c_roi);
				
		//TODO: get freq dynamically on creation
		String text = "";
		if (roic.size() > 0)
		{
			ROI roi = roic.get(roic.c_roi);
			text = String.format("x:%d y:%d\nw:%d h:%d\nFreq: %.2f", roi.x1,roi.y1,roi.x2,roi.y2,roi.freq);
		}
		tfROIDetails.setText(text);
	}
	
	public void WOIComplete(int cmd)
	{
		//frequency distribution graph
		if (cmd == 0)
		{
			FreqPlotter.get().newHistogram();
			FreqPlotter.get().showGraph();
		}
		//heatmap
		else if (cmd == 1)
		{
			HeatMapper.get().newHeatMap();
			HeatMapper.get().showGraph();
		}
	}
}
