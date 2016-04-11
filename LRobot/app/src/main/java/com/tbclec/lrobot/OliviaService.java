package com.tbclec.lrobot;

import android.util.Log;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bogdan on 11/04/2016.
 */
public class OliviaService {

	private OliviaResponseCallbackClient callbackClient;
	private HttpService httpService;

	public OliviaService(OliviaResponseCallbackClient callbackClient) {

		this.callbackClient = callbackClient;

		RestAdapter retrofit = new RestAdapter.Builder()
				.setEndpoint(Constants.SERVER_ADDRESS)
				.build();

		httpService = retrofit.create(HttpService.class);
	}

	public void askQuestion(String question) {

		if (isGoogleQuestion(question)) {
			askGoogleQuestion(question);
		}
		else {
			askBasicQuestion(question);
		}
	}

	private boolean isGoogleQuestion(String question) {

		return false;
	}

	private void askBasicQuestion(String question) {

		httpService.getBasicQuestion(question, new Callback<Message.BasicResponse>() {
			@Override
			public void success(Message.BasicResponse basicResponse, Response response) {

				Log.d(Constants.TAG_OLIVIA, "Basic request successful.");

				callbackClient.notifyAnswerReceived(basicResponse.answer);
			}

			@Override
			public void failure(RetrofitError error) {

				Log.d(Constants.TAG_OLIVIA, "Basic request failed: " + error.toString());

				callbackClient.notifyRequestFailed();
			}
		});
	}

	private void askGoogleQuestion(String question) {

		Message.GoogleQuestion message = new Message.GoogleQuestion();
		message.query = question;

		httpService.postGoogleQuestion(message, new Callback<Message.GoogleResponse>() {
			@Override
			public void success(Message.GoogleResponse googleResponse, Response response) {

				Log.d(Constants.TAG_OLIVIA, "Google request successful.");
			}

			@Override
			public void failure(RetrofitError error) {

				Log.d(Constants.TAG_OLIVIA, "Google request failed: " + error.toString());
			}
		});
	}
}
