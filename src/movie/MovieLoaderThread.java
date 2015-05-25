package movie;

import gui.GUINotifier;

import java.io.File;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.Utils;

/**
 * Single thread for frame and metadata extraction from a selected movie file
 * @author Peter
 *
 */
public class MovieLoaderThread implements Runnable
{
	File mov;
	public MovieLoaderThread(File p) 
	{
		mov = p;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void run() 
	{
		try 
		{
			LMovie m = LMovie.mov();
			long maxprogress = 0;
			
			//open xuggler container for the movie file
			IContainer container = IContainer.make();
			if (container.open(mov.getAbsolutePath(), IContainer.Type.READ, null) < 0)
			{
				throw new IllegalArgumentException("could not open file: " + mov.getName());
			}
			
			//get number of streams, we only want to video stream
			int numStreams = container.getNumStreams();
			int videoStreamId = -1;
			IStreamCoder videoCoder = null;
			for(int i = 0; i < numStreams; i++)
			{
				//find the stream object
				IStream stream = container.getStream(i);
				maxprogress = stream.getNumFrames();
				//get the pre-configured decoder that can decode this stream;
				IStreamCoder coder = stream.getStreamCoder();

				if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO)
				{
					m.xdim = coder.getWidth();
					m.ydim = coder.getHeight();
					m.framerate = coder.getFrameRate().getValue();
					m.fn = mov.getAbsolutePath();
					
					videoStreamId = i;
					videoCoder = coder;
					break;
				}
			}
			
			if (videoStreamId == -1)
			{
				throw new RuntimeException("could not find video stream in container: "+mov.getName());				
			}
			if (videoCoder.open() < 0)
			{
				throw new RuntimeException("could not open video decoder for container: " + mov.getName());				
			}
			
			//convert stream to BGR24 if not already
			IVideoResampler resampler = null;
		    if (videoCoder.getPixelType() != IPixelFormat.Type.BGR24)
		    {
		        resampler = IVideoResampler.make(m.xdim, m.ydim, IPixelFormat.Type.BGR24,
		        		m.xdim, m.ydim, videoCoder.getPixelType());
			    if (resampler == null)
			    	throw new RuntimeException("could not create color space resampler for: " + mov.getName());
		    }
		    
		    //start walking through the container looking at each packet.
		    IPacket packet = IPacket.make();
		    int frame_count = 0;
		    while(container.readNextPacket(packet) >= 0)
		    {
		    	if (packet.getStreamIndex() == videoStreamId)
		        {
		    		// We allocate a new picture to get the data out of Xuggle
		            IVideoPicture picture = IVideoPicture.make(videoCoder.getPixelType(),m.xdim,m.ydim);
		    		
		    		int offset = 0;
		            while(offset < packet.getSize())
		            {
		            	int bytesDecoded = videoCoder.decodeVideo(picture, packet, offset);
		            	if (bytesDecoded < 0)
		            		throw new RuntimeException("got error decoding video in: " + mov.getName());
		            	offset += bytesDecoded;
		            	
		            	if (picture.isComplete())
		                {
		            		IVideoPicture newPic = picture;
		            		
		            		newPic = IVideoPicture.make(resampler.getOutputPixelFormat(),m.xdim,m.ydim);
		            		if (resampler.resample(newPic, picture) < 0)
		            			throw new RuntimeException("could not resample video from: " + mov.getName());
		                
		            		//convert frame to bufferedimage
		            		m.addframe(Utils.videoPictureToImage(newPic));
		            		
		            		GUINotifier.n.updateProgress(frame_count++, (int)Math.min(maxprogress,m.max_frames));
		                }
		            }
		            
		            //Low memory mode fix
		            if (m.length() >= m.max_frames)
		            {
		            	break;
		            }
		        }
		    	else
		    	{
		    		//do nothing, this is not a video frame
		    	}
		    }
		    
		    //clean up
		    if (videoCoder != null)
		    {
			    videoCoder.close();
			    videoCoder = null;
		    }
		    if (container !=null)
		    {
			    container.close();
			    container = null;
		    }
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		GUINotifier.notifier().movieLoaded();
	}
}
