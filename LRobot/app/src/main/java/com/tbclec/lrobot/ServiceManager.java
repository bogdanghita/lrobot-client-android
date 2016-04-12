package com.tbclec.lrobot;

import android.content.Context;

/**
 * Created by Bogdan on 12/04/2016.
 */
public class ServiceManager {

	private static ServiceManager instance = null;

	private SongService songService;
	private OliviaService oliviaService;
	private ExternalIntentService externalIntentService;

	private ServiceManager() {
		songService = new SongService();
		oliviaService = new OliviaService();
		externalIntentService = new ExternalIntentService();
	}

	public static ServiceManager getInstance() {
		if (instance == null) {
			instance = new ServiceManager();
		}
		return instance;
	}

	public void setContext(Context context) {
		songService.setContext(context);
		externalIntentService.setContext(context);
	}

	public SongService getSongService() {
		return songService;
	}

	public OliviaService getOliviaService() {
		return oliviaService;
	}

	public ExternalIntentService getExternalIntentService() {
		return externalIntentService;
	}
}
