package com.tbclec.lrobot;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Bogdan on 11/04/2016.
 */
public class QuestionParser {

	public enum RequestType {REQ_BASIC, REQ_GOOGLE, REQ_SONG}

	public static class ParserResponse {

		public RequestType requestType;
		public List<String> content;

		public ParserResponse() {
		}

		public ParserResponse(RequestType requestType, List<String> content) {
			this.requestType = requestType;
			this.content = content;
		}
	}

	private final static String[] googleQuestionPatterns = {
			"google",
			"ask google"};
	private final static String[] playSongPatterns = {
			"play song",
			"play a song",
			"play me a song"};

	public static ParserResponse parseQuestion(List<String> question) {

		ParserResponse response = new ParserResponse();
		response.content = new LinkedList<>();

		if (parseQuestion(question, response, googleQuestionPatterns, RequestType.REQ_GOOGLE)) {
			return response;
		}

		if (parseQuestion(question, response, playSongPatterns, RequestType.REQ_SONG)) {
			return response;
		}

		return new ParserResponse(RequestType.REQ_BASIC, null);
	}

	private static boolean parseQuestion(List<String> question, ParserResponse response,
	                                     String[] patterns, RequestType type) {

		for (String q : question) {
			String questionLoweCase = q.toLowerCase();

			for (String pattern : patterns) {
				if (questionLoweCase.startsWith(pattern)) {

					String content = q.substring(pattern.length());
					content = content.trim();

					response.requestType = type;
					response.content.add(content);
				}
			}
		}

		return !response.content.isEmpty();
	}
}
