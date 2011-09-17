package com.senselessweb.soundcloud.mediasupport.gstreamer;


/**
 * Encapsulates the gstreamer pipeline and contains basic methods to 
 * control the playback.
 * 
 * A pipeline represents always only one source. 
 * 
 * @author thomas
 *
 */
public interface PipelineBridge
{

	/**
	 * Starts the playback.
	 */
	public void play();

	
	/**
	 * Stops the playback.
	 */
	public void stop();

	
	/**
	 * Pauses the playback.
	 */
	public void pause();
	
	
	/**
	 * If an error occurs during the playback, should this pipeline just 
	 * be recreated? This may be helpful when playing stream sources (that
	 * somtimes just crashes wihtout reason...)
	 * 
	 * @return True if this source should be resetted in error case, false otherwise.
	 */
	public boolean resetInErrorCase();
	
}
