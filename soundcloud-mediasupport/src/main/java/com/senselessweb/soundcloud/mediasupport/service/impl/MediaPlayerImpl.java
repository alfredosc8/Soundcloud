package com.senselessweb.soundcloud.mediasupport.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gstreamer.Pipeline;

import com.senselessweb.soundcloud.mediasupport.domain.DefaultPlaylist;
import com.senselessweb.soundcloud.mediasupport.domain.MediaSource;
import com.senselessweb.soundcloud.mediasupport.gstreamer.GstreamerSupport;
import com.senselessweb.soundcloud.mediasupport.gstreamer.MessageListener;
import com.senselessweb.soundcloud.mediasupport.gstreamer.PipelineBridge;
import com.senselessweb.soundcloud.mediasupport.gstreamer.elements.EqualizerBridge;
import com.senselessweb.soundcloud.mediasupport.gstreamer.elements.VolumeBridge;
import com.senselessweb.soundcloud.mediasupport.gstreamer.pipeline.PipelineBuilder;
import com.senselessweb.soundcloud.mediasupport.service.Equalizer;
import com.senselessweb.soundcloud.mediasupport.service.MediaPlayer;
import com.senselessweb.soundcloud.mediasupport.service.Playlist;
import com.senselessweb.soundcloud.mediasupport.service.VolumeControl;

/**
 * {@link MediaPlayer} implementation based on a gstreamer {@link Pipeline}.
 * 
 * @author thomas
 */
public class MediaPlayerImpl implements MediaPlayer, MessageListener
{

	/**
	 * Logger
	 */
	private static final Log log = LogFactory.getLog(MediaPlayerImpl.class);
	
	/**
	 * The current playlist
	 */
	private Playlist playlist = new DefaultPlaylist();
	
	/**
	 * The currently used pipeline. Is rebuilt everytime a new song is played.  
	 */
	private PipelineBridge pipeline; 
	
	/**
	 * The current mediasource
	 */
	private MediaSource current;
	
	/**
	 * The volume brigde. Is reused for every build pipeline.
	 */
	private final VolumeBridge volume = new VolumeBridge();
	
	/**
	 * The equalizer brigde. Is reused for every build pipeline.
	 */
	private final EqualizerBridge equalizer = new EqualizerBridge();
	
	/**
	 * The preconfigured pipeline builder. 
	 */
	private final PipelineBuilder pipelineBuilder = new PipelineBuilder().
		withEqualizer(this.equalizer).
		withMessageListener(this).
		withVolume(this.volume);
	
	
	/**
	 * Constructor
	 */
	public MediaPlayerImpl()
	{
		GstreamerSupport.initGst();
	}
	
	/**
	 * @see com.senselessweb.soundcloud.mediasupport.service.MediaPlayer#play()
	 */
	@Override
	public synchronized void play()
	{
		if (this.pipeline == null && this.playlist.getCurrent() != null)
			this.pipeline = this.pipelineBuilder.createPipeline(this.playlist.getCurrent());
		
		if (this.pipeline != null) this.pipeline.play();
	}
	
	
	/**
	 * @see com.senselessweb.soundcloud.mediasupport.service.MediaPlayer#stop()
	 */
	@Override
	public synchronized void stop()
	{
		if (this.pipeline != null) 
		{
			this.pipeline.stop();
			this.pipeline = null;
		}
	}
	
	
	/**
	 * @see com.senselessweb.soundcloud.mediasupport.service.MediaPlayer#pause()
	 */
	@Override
	public synchronized void pause()
	{
		if (this.pipeline != null) this.pipeline.pause();
	}
	

	/**
	 * @see com.senselessweb.soundcloud.mediasupport.service.MediaPlayer#next()
	 */
	@Override
	public void next()
	{
		if (this.playlist.next())
		{
			this.stop();
			this.play();
		}
	}	
	

	/**
	 * @see com.senselessweb.soundcloud.mediasupport.service.MediaPlayer#previous()
	 */
	@Override
	public void previous()
	{
		if (this.playlist.previous())
		{
			this.stop();
			this.play();
		}
	}	
	

	/**
	 * @see com.senselessweb.soundcloud.mediasupport.gstreamer.MessageListener#endofStream()
	 */
	@Override
	public void endofStream()
	{
		this.pipeline = null;
		this.next();
	}	

	
	/**
	 * @see com.senselessweb.soundcloud.mediasupport.service.MediaPlayer#getCurrentPlaylist()
	 */
	@Override
	public Playlist getCurrentPlaylist()
	{
		return this.playlist;
	}

	
	/**
	 * @see com.senselessweb.soundcloud.mediasupport.service.MediaPlayer#getVolumeControl()
	 */
	@Override
	public VolumeControl getVolumeControl()
	{
		return this.volume;
	}
	
	
	/**
	 * @see com.senselessweb.soundcloud.mediasupport.service.MediaPlayer#getEqualizer()
	 */
	@Override
	public Equalizer getEqualizer()
	{
		return this.equalizer;
	}

	
	/**
	 * @see com.senselessweb.soundcloud.mediasupport.gstreamer.MessageListener#error(int, java.lang.String)
	 */
	@Override
	public void error(final int errorcode, final String message)
	{
		if (this.pipeline.resetInErrorCase())
		{
			log.debug("Trying to restart the pipeline");
			this.pipeline = this.pipelineBuilder.createPipeline(this.current);
			this.pipeline.play();
		}
		else
		{
			this.stop();
			// TODO Delegate error to calling application
		}
	}
	
	
	/**
	 * @see com.senselessweb.soundcloud.mediasupport.service.MediaPlayer#shutdown()
	 */
	@Override
	public synchronized void shutdown()
	{
		log.debug("Shutdown called");
		if (this.pipeline != null) this.pipeline.stop();
		GstreamerSupport.shutdown(false);
	}

	
	/**
	 * The deinit() Method may not be called from inside junit.
	 * (No idea why...)
	 * This method is used as destroy method by spring.  
	 */
	public synchronized void deinitAndShutdown()
	{
		log.debug("Shutdown called");
		if (this.pipeline != null) this.pipeline.stop();
		GstreamerSupport.shutdown(true);
	}

}
