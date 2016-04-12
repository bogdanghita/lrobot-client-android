package com.tbclec.lrobot;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;
import java.util.List;

/**
 * Created by Bogdan on 12/04/2016.
 */
public class SongService {

	private Context context;
	private MediaPlayer player;

	public SongService() {
		
		player = new MediaPlayer();
		player.setVolume(1.0f, 1.0f);
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public void playSong(List<String> song) {

		String[] files;
		try {
			files = context.getAssets().list("songs");
		}
		catch (IOException e) {
			e.printStackTrace();
			Log.d(Constants.TAG_SONG, "Error opening assets/songs: ");
			return;
		}

		for (String s : song) {

			String file = searchSong(s, files);

			if (file != null) {
				playSong(file);
				Log.d(Constants.TAG_SONG, "Song found: " + file);
				return;
			}
		}

		Log.d(Constants.TAG_SONG, "Song not found.");
		// TODO: start youtube
	}

	private String searchSong(String song, String[] files) {

		String songToLower = song.toLowerCase();

		for (String file : files) {
			if (file.toLowerCase().startsWith(songToLower)) {
				return file;
			}
		}
		return null;
	}

	private void playSong(String file) {

		AssetFileDescriptor descriptor;
		try {
			descriptor = context.getAssets().openFd("songs/" + file);
		}
		catch (IOException e) {
			e.printStackTrace();
			Log.d(Constants.TAG_SONG, "Error opening assets/songs/" + file);
			return;
		}

		playSong(descriptor);

		try {
			descriptor.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void playSong(AssetFileDescriptor descriptor) {

		stopPlayingSong();

//		player = new MediaPlayer();

		long start = descriptor.getStartOffset();
		long end = descriptor.getLength();

		try {
			player.setDataSource(descriptor.getFileDescriptor(), start, end);
			player.prepare();
		}
		catch (IOException e) {
			e.printStackTrace();
			Log.d(Constants.TAG_SONG, "Unable to play song");
			return;
		}

		player.start();
	}

	public void stopPlayingSong() {
		player.stop();
	}
}
