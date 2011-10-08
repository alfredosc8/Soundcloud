package com.senselessweb.soundcloud.mediasupport.service;

import com.senselessweb.soundcloud.domain.sources.MediaSource;
import com.senselessweb.soundcloud.mediasupport.service.MediaPlayer.State;
import com.senselessweb.soundcloud.mediasupport.service.Playlist.ChangeEvent;


/**
 * Generic message listener that can be attached to the media player.
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
	 * Is called when the playlist changes.
	 * 
	 * @param event The {@link ChangeEvent}
	 * @param current The index of the current playlist title. 
	 */
	public void playlistChanged(Playlist.ChangeEvent event, int current);
}
