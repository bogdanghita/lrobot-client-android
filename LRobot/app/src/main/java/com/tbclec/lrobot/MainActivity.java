package com.tbclec.lrobot;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

	private TextView questionView, answerView;
	private Button askButton;

	private TextToSpeech tts;
	private SpeechRecognizer speechRecognizer;

	private OliviaService oliviaService;

	private GoogleResponseListAdapter googleResponseListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		questionView = (TextView) findViewById(R.id.question_view);
		answerView = (TextView) findViewById(R.id.answer_view);
		askButton = (Button) findViewById(R.id.ask_button);

		initRecyclerView();

		oliviaService = new OliviaService(oliviaResponseCallbackClient);
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
	}

	private void initTextToSpeech() {

		tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {

				if (status == TextToSpeech.SUCCESS) {

					Log.d(Constants.TAG_TSS, "TTS GOOD - status code: " + status);

					int result = tts.setLanguage(Locale.UK);
					tts.setPitch(0.7F);

					if (result == TextToSpeech.LANG_MISSING_DATA) {
						Log.e(Constants.TAG_TSS, "LANG_MISSING_DATA");

						Intent intent = new Intent();
						intent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
						startActivityForResult(intent, Constants.RC_INSTALL_TTS_DATA);
					}
					else {
						speakInitMessage();
					}
				}
				else {
					Log.d(Constants.TAG_TSS, "TTS ERROR - status code: " + status);
				}
			}
		});
	}

	private void clearTextToSpeech() {

		if (tts != null) {
			tts.stop();
			tts.shutdown();
			tts = null;
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

// -------------------------------------------------------------------------------------------------
// ACTIVITY RESULT
// -------------------------------------------------------------------------------------------------

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == Constants.RC_INSTALL_TTS_DATA) {

			if (resultCode == RESULT_OK) {
				speakInitMessage();
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
		String text = getString(R.string.tts_init);
		speak(text);
	}

	private void speak(String text) {
		tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
		answerView.setText(getString(R.string.answer) + " " + text);
	}

// -------------------------------------------------------------------------------------------------
// SPEECH RECOGNIZER ACTIONS
// -------------------------------------------------------------------------------------------------

	private void startListening() {

		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());

		speechRecognizer.startListening(intent);
		Log.d(Constants.TAG_TSS, "startListening");

		new CountDownTimer(5000, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {
				//do nothing, just let it tick
			}

			@Override
			public void onFinish() {
				stopListening();
			}
		}.start();
	}

	private void stopListening() {

		speechRecognizer.stopListening();
		askButton.setEnabled(true);
		Log.d(Constants.TAG_TSS, "stopListening");
	}

	private void listenResultReady(ArrayList<String> results) {

		stopListening();

		questionView.setText(getString(R.string.question) + " " + results.get(0));

		oliviaService.askQuestion(results.get(0));
	}

// -------------------------------------------------------------------------------------------------
// LISTENERS
// -------------------------------------------------------------------------------------------------

	public void buttonAsk(View v) {

		startListening();
	}

	private RecognitionListener recognitionListener = new RecognitionListener() {

		@Override
		public void onBeginningOfSpeech() {
			Log.d("Speech", "onBeginningOfSpeech");
		}

		@Override
		public void onBufferReceived(byte[] buffer) {
			Log.d("Speech", "onBufferReceived");
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
			askButton.setEnabled(false);
		}

		@Override
		public void onResults(Bundle results) {
			Log.d("Speech", "onResults");

			ArrayList<String> resultsArray = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
			listenResultReady(resultsArray);
		}

		@Override
		public void onRmsChanged(float rmsdB) {
			Log.d("Speech", "onRmsChanged");
		}
	};

	private OliviaResponseCallbackClient oliviaResponseCallbackClient = new OliviaResponseCallbackClient() {
		@Override
		public void notifyBasicAnswerReceived(final String answer) {

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
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
				}
			});
		}

		@Override
		public void notifyRequestFailed() {

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					speak(getString(R.string.request_failed));
				}
			});
		}
	};
}
