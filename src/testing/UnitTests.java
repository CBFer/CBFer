package testing;

import static org.junit.Assert.*;
import gui.GUI;
import gui.GUINotifier;
import gui.HeatMapper;
import gui.PlayThread;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JFrame;

import movie.LMovie;
import movie.MovieLoaderThread;
import movie.ROI;
import movie.ROICollection;

import org.junit.Test;

public class UnitTests 
{
	//test loading a movie
	@Test
	public void testMovieLoad() throws Exception 
	{
		//instantiate GUI
		GUI gui = new GUI();
		gui.jFrame = new JFrame();
        gui.jFrame.getContentPane().add(gui);
        gui.jFrame.setVisible(false);
		
		GUINotifier.init(gui);
		LMovie.mov();
		
		File f = new File("./mov/M-3_c3.MOV");
		Thread t = new Thread(new MovieLoaderThread(f));
		t.start();
		
		t.join();
		
		assertTrue(LMovie.mov().frames.size() > 0);		
	}
	
	@Test
	public void testROIFreq()
	{
		//instantiate GUI
		GUI gui = new GUI();
		gui.jFrame = new JFrame();
        gui.jFrame.getContentPane().add(gui);
        gui.jFrame.setVisible(false);
		
		GUINotifier.init(gui);
		LMovie.mov();
		
		//generate some empty frames
		BufferedImage bf = new BufferedImage(4, 4, BufferedImage.TYPE_3BYTE_BGR);
		
		for (int i=0; i<4; i++)
		{
			for (int j=0; j<4; j++)
			{
				bf.setRGB(i, j, 128);				
			}
		}

		for (int i=0; i<128; i++)
		{
			LMovie.mov().addframe(bf);
		}
		
		ROI roi = new ROI();
		roi.x1 = 0; roi.x2 = 4; roi.y1 = 0; roi.y2 = 4;
		
		roi.calculateFrequency();
		
		assertTrue(roi.freq != -1);
	}
	
	@Test
	public void SingletonTest()
	{
		GUINotifier guin = GUINotifier.notifier();
		assertTrue(guin == GUINotifier.notifier());
		
		HeatMapper hm = HeatMapper.get();
		assertTrue(hm == HeatMapper.get());
		
		LMovie mov = LMovie.mov();
		assertTrue(mov == LMovie.mov());
	}
	
	@Test
	public void ROICollectionTest()
	{
		ROICollection roic = ROICollection.rc();
		
		for (int i=0; i<8; i++)
		{
			ROI r = new ROI();
			r.x1 = i*4; r.y1 = 4*(i+1);
			r.x2 = 2; r.y2 = 2;
			
			roic.add(r);
		}
		
		LMovie.mov().xdim = 100; LMovie.mov().ydim = 100;
		
		roic.saveFreqDist();
		
		assertTrue(roic.dt != null);
		assertTrue(roic.freqMap != null);
	}
	
	@Test(timeout=1000)
	public void PlayThreadKill() throws Exception
	{
		PlayThread pt = PlayThread.pt();
		Thread t = new Thread(pt);
		pt.paused = false;
		pt.kill = true;
		
		t.start();
		t.join();
		
		assertTrue(true);
	}
}
