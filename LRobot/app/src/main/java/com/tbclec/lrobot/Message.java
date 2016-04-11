package com.tbclec.lrobot;

import com.google.gson.annotations.Expose;

/**
 * Created by Bogdan on 11/04/2016.
 */
public class Message {

	public static class BasicResponse {

		@Expose
		public String answer;
	}

	public static class GoogleQuestion {

		@Expose
		public String query;
	}

	public static class GoogleResponse {

		@Expose
		public String title;
		@Expose
		public String description;
		@Expose
		public String link;
	}
}
