package com.tbclec.lrobot;

import android.util.Log;

import java.util.regex.Pattern;

/**
 * Created by Bogdan on 11/04/2016.
 */
public class QuestionParser {

	public static class ParserResponse {

		public boolean isGoogleQuestion;
		public String GoogleQuestion;

		public ParserResponse() {
		}

		public ParserResponse(boolean isGoogleQuestion, String googleQuestion) {
			this.isGoogleQuestion = isGoogleQuestion;
			GoogleQuestion = googleQuestion;
		}
	}

	private final static String[] googleQuestionPatterns = {
			"google", "Google",
			"ask google", "Ask Google", "Ask google", "ask Google"};

	public static ParserResponse isGoogleQuestion(String question) {

		ParserResponse response = new ParserResponse(false, "");

		for (String pattern : googleQuestionPatterns) {
			if (question.startsWith(pattern)) {

				response.isGoogleQuestion = true;
				response.GoogleQuestion = question.replaceFirst(Pattern.quote(pattern), "");
				response.GoogleQuestion = response.GoogleQuestion.trim();

				break;
			}
		}

		return response;
	}
}
