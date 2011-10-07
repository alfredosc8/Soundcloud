package com.senselessweb.soundcloud.mediasupport.service.impl;

import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import com.senselessweb.soundcloud.domain.sources.MediaSource;
import com.senselessweb.soundcloud.mediasupport.service.MediaPlayer.State;
import com.senselessweb.soundcloud.mediasupport.service.MessageListener;
import com.senselessweb.soundcloud.mediasupport.service.Playlist.ChangeEvent;

/**
 * Message Mediator that contains all the message listeners.
 * 
 * @author thomas
 */
public class MessageMediator implements MessageListener
{
	
	/**
	 * The logger
	 */
	private static final Log log = LogFactory.getLog(MessageMediator.class);
	
	/**
	 * The attached listeners
	 */
	private final Collection<MessageListener> messageListeners = new HashSet<MessageListener>();
	
	/**
	 * Attaches a new listener
	 * 
	 * @param listener The new listener
	 */
	public void addMessageListener(MessageListener listener)
	{
		this.messageListeners.add(listener);
	}

	/**
	 * @see com.senselessweb.soundcloud.mediasupport.service.MessageListener#stateChanged(com.senselessweb.soundcloud.mediasupport.service.MediaPlayer.State)
	 */
	@Override
	public void stateChanged(final State newState)
	{
		log.debug("State changed event. New state: " + newState);
		for (final MessageListener listener : this.messageListeners)
			listener.stateChanged(newState);
	}

	/**
	 * @see com.senselessweb.soundcloud.mediasupport.service.MessageListener#error(java.lang.String)
	 */
	@Override
	public void error(final String message)
	{
		log.debug("Error event. Message: " + message);
		for (final MessageListener listener : this.messageListeners)
			listener.error(message);
	}

	/**
	 * @see com.senselessweb.soundcloud.mediasupport.service.MessageListener#tag(java.lang.String, java.lang.String)
	 */
	@Override
	public void tag(final String tag, final String value)
	{
		for (final MessageListener listener : this.messageListeners)
			listener.tag(tag, value);
	}

	/**
	 * @see com.senselessweb.soundcloud.mediasupport.service.MessageListener#newSource(com.senselessweb.soundcloud.domain.sources.MediaSource)
	 */
	@Override
	public void newSource(final MediaSource source)
	{
		for (final MessageListener listener : this.messageListeners)
			listener.newSource(source);
	}

	/**
	 * @see com.senselessweb.soundcloud.mediasupport.service.MessageListener#playlistChanged(com.senselessweb.soundcloud.mediasupport.service.Playlist.ChangeEvent)
	 */
	@Override
	public void playlistChanged(final ChangeEvent event)
	{
		for (final MessageListener listener : this.messageListeners)
			listener.playlistChanged(event);
	}

}
