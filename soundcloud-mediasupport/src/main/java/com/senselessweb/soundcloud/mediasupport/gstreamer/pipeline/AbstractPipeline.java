package com.senselessweb.soundcloud.mediasupport.gstreamer.pipeline;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gstreamer.Bus;
import org.gstreamer.Element;
import org.gstreamer.GstObject;
import org.gstreamer.Pipeline;
import org.gstreamer.Tag;
import org.gstreamer.TagList;

import com.google.common.collect.Sets;
import com.senselessweb.soundcloud.mediasupport.gstreamer.PipelineBridge;
import com.senselessweb.soundcloud.mediasupport.gstreamer.elements.EqualizerBridge;
import com.senselessweb.soundcloud.mediasupport.gstreamer.elements.PanoramaBridge;
import com.senselessweb.soundcloud.mediasupport.gstreamer.elements.VolumeBridge;
import com.senselessweb.soundcloud.mediasupport.service.MediaPlayer.State;
import com.senselessweb.soundcloud.mediasupport.service.MessageListenerService;
import com.senselessweb.soundcloud.mediasupport.service.VolumeControl;

/**
 * Bridge that contains the gstreamer {@link Pipeline} and contains references 
 * to some pipeline {@link Element}s.
 * 
 * @author thomas
 *
 */
public abstract class AbstractPipeline implements PipelineBridge
{

	/**
	 * Logger
	 */
	static final Log log = LogFactory.getLog(AbstractPipeline.class);
	
	/**
	 * The actual pipeline
	 */
	protected final Pipeline pipeline;
	
	/**
	 * The volume control
	 */
	private final VolumeBridge volume;
	
	/**
	 * The equalizer control
	 */
	private final EqualizerBridge equalizer;
	
	/**
	 * The audiopanorama control
	 */
	private final PanoramaBridge panoramaBridge;
	
	/**
	 * Constructor
	 * 
	 * @param pipeline The {@link Pipeline} to use.
	 * @param volume The {@link VolumeControl}.
	 * @param equalizer The {@link EqualizerBridge}.
	 * @param panoramaBridge The {@link PanoramaBridge}. 
	 * @param messageListener The {@link MessageListenerService}.
	 */
	public AbstractPipeline(final Pipeline pipeline, final VolumeBridge volume, final EqualizerBridge equalizer, 
			final PanoramaBridge panoramaBridge, final MessageListenerService messageListener)
	{
		this.pipeline = pipeline;
		
		this.volume = volume;
		this.volume.initElement(this.pipeline.getElementByName("volume"));
		
		this.equalizer = equalizer;
		this.equalizer.initElement(this.pipeline.getElementByName("equalizer"));
		
		this.panoramaBridge = panoramaBridge;
		this.panoramaBridge.initElement(this.pipeline.getElementByName("audiopanorama"));
		
		final BusMessageListener busMessageListener = new BusMessageListener(messageListener);
		this.pipeline.getBus().connect(busMessageListener.errorMessageListener);
		this.pipeline.getBus().connect(busMessageListener.warnMessageListener);
		this.pipeline.getBus().connect(busMessageListener.infoMessageListener);
		this.pipeline.getBus().connect(busMessageListener.tagMessageListener);
		this.pipeline.getBus().connect(busMessageListener.eosMessageListener);		
	}
	
	/**
	 * Creates a pipeline containing the default pipeline elements.
	 * 
	 * @param src The name of the source {@link Element} to use. Read the dokumentation about 
	 * gstreamer plugins to find out what sources are supported. The source element is added 
	 * to the pipeline under the name "src".   
	 * 
	 * @return The created pipeline.
	 */
	protected static Pipeline createDefaultPipeline(final String src)
	{
		return Pipeline.launch(
				src + " name=src ! " +
				"decodebin2 buffer-duration=3 ! " +
				"audioconvert ! " +
				"equalizer-10bands name=equalizer ! " +
				"audiopanorama name=audiopanorama ! " +
				"volume name=volume ! " +
				"alsasink");
	}
	
	/**
	 * @see com.senselessweb.soundcloud.mediasupport.gstreamer.PipelineBridge#play()
	 */
	@Override
	public void play()
	{
		log.debug("Playing " + this);
		this.pipeline.play();
	}
	
	/**
	 * @see com.senselessweb.soundcloud.mediasupport.gstreamer.PipelineBridge#pause()
	 */
	@Override
	public void pause()
	{
		log.debug("Pausing " + this);
		this.pipeline.pause();
	}
	
	/**
	 * @see com.senselessweb.soundcloud.mediasupport.gstreamer.PipelineBridge#stop()
	 */
	@Override
	public void stop()
	{
		log.debug("Stopping " + this);
		this.pipeline.stop();
	}
	
	
	/**
	 * @see com.senselessweb.soundcloud.mediasupport.gstreamer.PipelineBridge#getState()
	 */
	@Override
	public State getState()
	{
		switch (this.pipeline.getState())
		{
			case PAUSED: return State.PAUSED;
			case PLAYING: return State.PLAYING;
			default: return State.STOPPED;
		}
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "PipelineBridge for " + this.pipeline;
	}
}

/**
 * Generic {@link Bus} message listener that forwards all messages to the logger.
 * 
 * @author thomas
 */
class BusMessageListener  
{
	
	/**
	 * The messageListener
	 */
	final MessageListenerService messageListener;
	
	/**
	 * Constructor
	 * 
	 * @param messageListener The {@link MessageListenerService} to redirect some messages to.
	 */
	BusMessageListener(final MessageListenerService messageListener)
	{
		this.messageListener = messageListener;
	}
	
	/**
	 * End of stream message listener
	 */
	Bus.EOS eosMessageListener = new Bus.EOS() {
		
		@Override
		public void endOfStream(final GstObject source)
		{
			AbstractPipeline.log.debug("EOS: " + source);
			BusMessageListener.this.messageListener.endOfStream();
		}
	};
	
	/**
	 * Error message listener
	 */
	Bus.ERROR errorMessageListener = new Bus.ERROR() {
		
		@Override
		public void errorMessage(final GstObject source, final int code, final String message)
		{
			AbstractPipeline.log.error(source + ", " + code + ", " + message);
			BusMessageListener.this.messageListener.error(message);
		}
	};
	
	/**
	 * Info message listener
	 */
	Bus.INFO infoMessageListener = new Bus.INFO() {
		
		@Override
		public void infoMessage(final GstObject source, final int code, final String message)
		{
			AbstractPipeline.log.info(source + ", " + code + ", " + message);		
		}
	};
	
	/**
	 * Warn message listener
	 */
	Bus.WARNING warnMessageListener = new Bus.WARNING() {
		
		@Override
		public void warningMessage(final GstObject source, final int code, final String message)
		{
			AbstractPipeline.log.warn(source + ", " + code + ", " + message);		
		}
	};
	
	/**
	 * Tag message listener
	 */
	Bus.TAG tagMessageListener = new Bus.TAG() {
		
		/**
		 * The tags that should be sent to the message listener
		 */
		private final Set<String> tagsToFilter = Sets.newHashSet(
				Tag.ARTIST.getId(), Tag.TITLE.getId(), Tag.ALBUM.getId(), 
				Tag.GENRE.getId(), Tag.BITRATE.getId());
		
		
		@Override
		public void tagsFound(final GstObject source, final TagList tagList)
		{
			AbstractPipeline.log.info("Tags found : " + tagList);		
			
			for (final String tag: Sets.intersection(this.tagsToFilter, new HashSet<String>(tagList.getTagNames())))
			{
				final Object o = tagList.getValue(tag);
				BusMessageListener.this.messageListener.tag(tag, o.toString());
			}
		}
	};
	
}


