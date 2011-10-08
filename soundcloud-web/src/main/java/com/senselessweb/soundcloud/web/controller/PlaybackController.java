package com.senselessweb.soundcloud.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.senselessweb.soundcloud.mediasupport.service.MediaPlayer;

/**
 * Web controller interface for the playback control
 * 
 * @author thomas
 */
@Controller
@RequestMapping("/playback/*")
public class PlaybackController
{
	
	/**
	 * The mediaplayer
	 */
	@Autowired MediaPlayer mediaPlayer;

	/**
	 * Starts the playback
	 */
	@RequestMapping("/play") 
	@ResponseStatus(HttpStatus.OK)
	public void play()
	{
		this.mediaPlayer.play();
	}

	/**
	 * Pauses the playback
	 */
	@RequestMapping("/pause") 
	@ResponseStatus(HttpStatus.OK)
	public void pause()
	{
		this.mediaPlayer.pause();
	}
	
	/**
	 * Stops the playback
	 */
	@RequestMapping("/stop") 
	@ResponseStatus(HttpStatus.OK)
	public void stop()
	{
		this.mediaPlayer.stop();
	}
	
	/**
	 * Jumps to the previous song
	 */
	@RequestMapping("/previous")
	@ResponseStatus(HttpStatus.OK)
	public void previous()
	{
		if (this.mediaPlayer.getCurrentPlaylist().previous())
		{
			this.mediaPlayer.stop();
			this.mediaPlayer.play();
		}
	}
	
	/**
	 * Jumps to the next song
	 */
	@RequestMapping("/next") 
	@ResponseStatus(HttpStatus.OK)
	public void next()
	{
		if (this.mediaPlayer.getCurrentPlaylist().next())
		{
			this.mediaPlayer.stop();
			this.mediaPlayer.play();
		}
	}
	
	/**
	 * Returns the current playback values.
	 * 
	 * @param model The current model.
	 * 
	 * @return A map with the current initial data.
	 */
	@RequestMapping("/getData")
	@ResponseBody
	public Model getData(final Model model)
	{
		model.addAttribute("state", this.mediaPlayer.getState());
		return model;
	}
	
}
