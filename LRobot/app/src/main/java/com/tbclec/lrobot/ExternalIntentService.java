package com.tbclec.lrobot;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

/**
 * Created by Bogdan on 12/04/2016.
 */
public class ExternalIntentService {

	private Context context;

	public ExternalIntentService(Context context) {
		this.context = context;
	}

	public void openLink(String link) {

		try {
			Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
			context.startActivity(myIntent);
		}
		catch (ActivityNotFoundException e) {
			Toast.makeText(context, "No application can handle this request. " +
					"Please install a web browser", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}

	public void openYoutube(String song) {


	}
}
