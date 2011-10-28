package com.senselessweb.soundcloud.mediasupport.gstreamer.pipeline;

import java.io.File;

import com.senselessweb.soundcloud.mediasupport.gstreamer.elements.EqualizerBridge;
import com.senselessweb.soundcloud.mediasupport.gstreamer.elements.PanoramaBridge;
import com.senselessweb.soundcloud.mediasupport.gstreamer.elements.VolumeBridge;
import com.senselessweb.soundcloud.mediasupport.service.MessageListenerService;
import com.senselessweb.soundcloud.mediasupport.service.VolumeControl;

/**
 * Pipeline bridge that uses a file source.
 * 
 * @author thomas
 */
public class FileSrcPipeline extends AbstractPipeline
{

	/**
	 * Constructor
	 * 
	 * @param file The file that is used as source.
	 * @param volume The {@link VolumeControl}.
	 * @param equalizer The current {@link EqualizerBridge}.
	 * @param panoramaBridge The {@link PanoramaBridge}. 
	 * @param messageListener The {@link MessageListenerService}.
	 */
	public FileSrcPipeline(final File file, final VolumeBridge volume, final EqualizerBridge equalizer, 
			final PanoramaBridge panoramaBridge, final MessageListenerService messageListener)
	{
		super(createDefaultPipeline("filesrc"), volume, equalizer, panoramaBridge, messageListener);
		this.pipeline.getElementByName("src").set("location", file.getAbsolutePath());
	}
	
	
	/**
	 * @see com.senselessweb.soundcloud.mediasupport.gstreamer.PipelineBridge#resetInErrorCase()
	 */
	@Override
	public boolean resetInErrorCase()
	{
		return false;
	}
}
