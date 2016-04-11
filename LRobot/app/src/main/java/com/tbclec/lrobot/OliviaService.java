package com.tbclec.lrobot;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;
import java.util.List;

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

	public void askQuestion(List<String> question) {

		QuestionParser.ParserResponse response = QuestionParser.parseQuestion(question);

		if (response.requestType == QuestionParser.RequestType.REQ_GOOGLE) {
			askGoogleQuestion(response.content.get(0));
			Log.d(Constants.TAG_OLIVIA, "Google question asked: " + response.content);
		}
		else if (response.requestType == QuestionParser.RequestType.REQ_SONG) {
			callbackClient.notifyPlaySongRequest(response.content);
			Log.d(Constants.TAG_OLIVIA, "Play song asked: " + response.content);
		}
		else {
			askBasicQuestion(question);
			Log.d(Constants.TAG_OLIVIA, "Basic question asked: " + question);
		}
	}

	private void askBasicQuestion(List<String> question) {

		httpService.getBasicQuestion(question, new Callback<Message.BasicResponse>() {
			@Override
			public void success(Message.BasicResponse basicResponse, Response response) {

				Log.d(Constants.TAG_OLIVIA, "Basic request successful.");

				callbackClient.notifyBasicAnswerReceived(basicResponse.answer);
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

		httpService.postGoogleQuestion(message, new Callback<List<Message.GoogleResponse>>() {

			@Override
			public void success(List<Message.GoogleResponse> googleResponses, Response response) {

				Log.d(Constants.TAG_OLIVIA, "Google request successful.");

				callbackClient.notifyGoogleAnswerReceived(googleResponses);
			}

			@Override
			public void failure(RetrofitError error) {

				Log.d(Constants.TAG_OLIVIA, "Google request failed: " + error.toString());
			}
		});
	}
}
