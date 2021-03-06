/**
 * 
 */
package com.senselessweb.soundcloud.mediasupport.service;

import com.senselessweb.soundcloud.domain.sources.MediaSource;
import com.senselessweb.soundcloud.mediasupport.service.MediaPlayer.State;
import com.senselessweb.soundcloud.mediasupport.service.Playlist.ChangeEvent;

/**
 * Generic message listener that can be attached to the media player.
 * 
 * Consider using the {@link MessageListenerService} interface when you are outside
 * of a request scope.
 *
 * @author thomas
 */
public interface MessageListener
{

	/**
	 * Is called when the playback state changes.
	 * 
	 * @param newState The new playback state.
	 */
	public void stateChanged(State newState);

	/**
	 * Is called when an error appears.
	 * 
	 * @param message The error message.
	 */
	public void error(String message);

	/**
	 * Is called when a new tag is found in the stream.
	 * 
	 * @param tag The tag
	 * @param value The value
	 */
	public void tag(String tag, String value);
	
	
	/**
	 * Is called when a new {@link MediaSource} is started.
	 * 
	 * @param source The new media source.
	 */
	public void newSource(MediaSource source);
	
	/**
	 * Is called as soon as the duration of a new song is known.
	 * 
	 * @param duration The duration in seconds.
	 */
	public void durationChanged(long duration);
	
	/**
	 * Is called when the position in the current stream changes.
	 * 
	 * @param position The new position in seconds.
	 */
	public void positionChanged(long position);
	
	/**
	 * Is called when the playlist changes.
	 * 
	 * @param event The {@link ChangeEvent}
	 * @param current The index of the current playlist title. 
	 */
	public void playlistChanged(Playlist.ChangeEvent event, int current);

	/**
	 * Is called when the end of the stream is reached.
	 */
	public void endOfStream();
}
