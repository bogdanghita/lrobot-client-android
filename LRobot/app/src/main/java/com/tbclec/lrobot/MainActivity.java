package com.tbclec.lrobot;

import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class MainActivity extends AppCompatActivity {

	private TextView questionView, answerView;
	private ImageView askButton;
	private LinearLayout googleResultsLayout;

	private GoogleResponseListAdapter googleResponseListAdapter;

	private TextToSpeech textToSpeech;
	private SpeechRecognizer speechRecognizer;

	private OliviaService oliviaService;

	private final Object speechSync = new Object();
	private final Object voiceListeningSync = new Object();
	private boolean voiceListeningStopped = true;

	private enum MicState {DISABLED, ON, RECORDING}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		questionView = (TextView) findViewById(R.id.question_view);
		answerView = (TextView) findViewById(R.id.answer_view);
		askButton = (ImageView) findViewById(R.id.ask_button);
		googleResultsLayout = (LinearLayout) findViewById(R.id.google_results_layout);

		ImageView imageView = (ImageView) findViewById(R.id.FaceImageView);
		Picasso.with(this)
				.load("file:///android_asset/faces/intro.png")
				.into(imageView);

		initRecyclerView();

		ServiceManager serviceManager = ServiceManager.getInstance();
		serviceManager.setContext(this);
		oliviaService = serviceManager.getOliviaService();
		oliviaService.setCallbackClient(oliviaResponseCallbackClient);

		setMicStatus(MicState.DISABLED);

		setOliviaImage(getString(R.string.olivia_intro_image));
	}

	@Override
	public void onResume() {
		super.onResume();

		initTextToSpeech();
		initSpeechRecognizer();
	}

	@Override
	public void onPause() {
		super.onPause();

		clearTextToSpeech();
		clearSpeechRecognizer();

		ServiceManager.getInstance().getSongService().stopPlayingSong();
	}

	@Override
	public void onBackPressed() {

		if (googleResultsLayout.getVisibility() == View.VISIBLE) {
			hideGoogleResponseLayout();
		}
		else {
			super.onBackPressed();
		}
	}

// -------------------------------------------------------------------------------------------------
// SPEECH & VOICE INIT & CLEANUP
// -------------------------------------------------------------------------------------------------

	private void initTextToSpeech() {

		textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {

				if (status == TextToSpeech.SUCCESS) {

					Log.d(Constants.TAG_TSS, "TTS GOOD - status code: " + status);

					int result = textToSpeech.setLanguage(Locale.UK);
					textToSpeech.setPitch(0.7F);

					if (result == TextToSpeech.LANG_MISSING_DATA) {
						Log.e(Constants.TAG_TSS, "LANG_MISSING_DATA");

						Intent intent = new Intent();
						intent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
						startActivityForResult(intent, Constants.RC_INSTALL_TTS_DATA);
					}
					else {
						speakInitMessage();
						setMicStatus(MicState.ON);
					}
				}
				else {
					Log.d(Constants.TAG_TSS, "TTS ERROR - status code: " + status);
				}
			}
		});
	}

	private void clearTextToSpeech() {

		if (textToSpeech != null) {
			textToSpeech.stop();
			textToSpeech.shutdown();
			textToSpeech = null;
		}
	}

	private void initSpeechRecognizer() {

		speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
		speechRecognizer.setRecognitionListener(recognitionListener);
	}

	private void clearSpeechRecognizer() {

		if (speechRecognizer != null) {
			speechRecognizer.stopListening();
			speechRecognizer.cancel();
			speechRecognizer.destroy();
			speechRecognizer = null;
		}
	}

// -------------------------------------------------------------------------------------------------
// GUI
// -------------------------------------------------------------------------------------------------

	private void initRecyclerView() {

		RecyclerView recyclerView = (RecyclerView) findViewById(R.id.google_response_holder);

		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(layoutManager);

		googleResponseListAdapter = new GoogleResponseListAdapter(this);
		recyclerView.setAdapter(googleResponseListAdapter);
	}

	private void enableSpeakButton() {
		askButton.setEnabled(true);
	}

	private void disableSpeakButton() {
		askButton.setEnabled(false);
	}

	private void setMicStatus(MicState micState) {
		ImageView iv = (ImageView) findViewById(R.id.ask_button);
		switch (micState) {
			case DISABLED:
				iv.setColorFilter(Color.GRAY);
				break;
			case ON:
				iv.setColorFilter(Color.BLACK);
				break;

			case RECORDING:
				iv.setColorFilter(Color.RED);
				break;
			default:
				iv.setColorFilter(Color.BLACK);
		}
	}

	private void setOliviaImage(String image) {
		ImageView imageView = (ImageView) findViewById(R.id.FaceImageView);

		if (image == null) {
			image = getString(R.string.olivia_default_image);
		}

		Picasso.with(this)
				.load("file:///android_asset/faces/" + image)
				.transform(new CropCircleTransformation())
				.into(imageView);
	}

	private void showGoogleResponseLayout() {
		googleResultsLayout.setVisibility(View.VISIBLE);
	}

	private void hideGoogleResponseLayout() {
		googleResultsLayout.setVisibility(View.GONE);
	}

	private void showStatusView() {
		findViewById(R.id.status_view).setVisibility(View.VISIBLE);
		findViewById(R.id.ask_button).setVisibility(View.GONE);
	}

	private void hideStatusView() {
		findViewById(R.id.status_view).setVisibility(View.GONE);
		findViewById(R.id.ask_button).setVisibility(View.VISIBLE);
	}

// -------------------------------------------------------------------------------------------------
// ACTIVITY RESULT
// -------------------------------------------------------------------------------------------------

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == Constants.RC_INSTALL_TTS_DATA) {

			if (resultCode == RESULT_OK) {
				speakInitMessage();
				setMicStatus(MicState.ON);
			}
			else {
				Log.d(Constants.TAG_TSS, "TTS ERROR - result code: " + resultCode);
			}
		}
	}

// -------------------------------------------------------------------------------------------------
// TEXT TO SPEECH ACTIONS
// -------------------------------------------------------------------------------------------------

	private void speakInitMessage() {
		String text = getString(R.string.tts_init_msg);
		speak(text);
	}

	private void speak(String text) {
		synchronized (speechSync) {
			textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
			answerView.setText(getString(R.string.answer) + " " + text);
		}
	}

	private void stopSpeech() {

		synchronized (speechSync) {
			if (textToSpeech.isSpeaking()) {
				textToSpeech.stop();
				Log.d(Constants.TAG_TSS, "textToSpeech.stop()");
			}
		}
	}

// -------------------------------------------------------------------------------------------------
// SPEECH RECOGNIZER ACTIONS
// -------------------------------------------------------------------------------------------------

	private void startVoiceListening() {

		synchronized (voiceListeningSync) {

			voiceListeningStopped = false;

			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());

			speechRecognizer.startListening(intent);
			Log.d(Constants.TAG_TSS, "startVoiceListening");

			new CountDownTimer(5000, 1000) {

				@Override
				public void onTick(long millisUntilFinished) {
					//do nothing, just let it tick
				}

				@Override
				public void onFinish() {
					stopVoiceListening();
				}
			}.start();
			setMicStatus(MicState.RECORDING);
		}
	}

	private void stopVoiceListening() {

		synchronized (voiceListeningSync) {

			if (voiceListeningStopped) {
				return;
			}
			voiceListeningStopped = true;

			speechRecognizer.stopListening();
			enableSpeakButton();
			Log.d(Constants.TAG_TSS, "stopVoiceListening");
			setMicStatus(MicState.ON);
		}
	}

	private void notifyVoiceListeningResultReady(ArrayList<String> results) {

		stopVoiceListening();

		questionView.setText(getString(R.string.question) + " " + results.get(0));

		oliviaService.askQuestion(results);

		showStatusView();
	}

// -------------------------------------------------------------------------------------------------
// LISTENERS
// -------------------------------------------------------------------------------------------------

	public void buttonAsk(View v) {
		stopSpeech();
		ServiceManager.getInstance().getSongService().stopPlayingSong();

		startVoiceListening();
	}

	private RecognitionListener recognitionListener = new RecognitionListener() {

		@Override
		public void onBeginningOfSpeech() {
			Log.d("Speech", "onBeginningOfSpeech");
		}

		@Override
		public void onBufferReceived(byte[] buffer) {
//			Log.d("Speech", "onBufferReceived");
		}

		@Override
		public void onEndOfSpeech() {
			Log.d("Speech", "onEndOfSpeech");
		}

		@Override
		public void onError(int error) {
			Log.d("Speech", "onError: " + error);
		}

		@Override
		public void onEvent(int eventType, Bundle params) {
			Log.d("Speech", "onEvent");
		}

		@Override
		public void onPartialResults(Bundle partialResults) {
			Log.d("Speech", "onPartialResults");
		}

		@Override
		public void onReadyForSpeech(Bundle params) {
			Log.d("Speech", "onReadyForSpeech");
			disableSpeakButton();
		}

		@Override
		public void onResults(Bundle results) {
			Log.d("Speech", "onResults");

			ArrayList<String> resultsArray = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
			notifyVoiceListeningResultReady(resultsArray);
		}

		@Override
		public void onRmsChanged(float rmsdB) {
//			Log.d("Speech", "onRmsChanged");
		}
	};

	private OliviaResponseCallbackClient oliviaResponseCallbackClient = new OliviaResponseCallbackClient() {
		@Override
		public void notifyBasicAnswerReceived(final String answer, final String image) {

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					setOliviaImage(image);
					hideStatusView();
					speak(answer);
				}
			});
		}

		@Override
		public void notifyGoogleAnswerReceived(final List<Message.GoogleResponse> answer) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					googleResponseListAdapter.setItems(answer);
					googleResponseListAdapter.notifyDataSetChanged();
					showGoogleResponseLayout();
					hideStatusView();
					speak(getString(R.string.google_result_msg));
				}
			});
		}

		@Override
		public void notifyRequestFailed() {

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					hideStatusView();
					speak(getString(R.string.request_failed_msg));
				}
			});
		}

		@Override
		public void notifyPlaySongRequest(List<String> song) {
			hideStatusView();
			ServiceManager.getInstance().getSongService().playSong(song);
		}
	};
}
