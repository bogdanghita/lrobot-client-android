package com.tbclec.lrobot;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by Bogdan on 11/04/2016.
 */
public interface HttpAPI {

	@POST("/api/olivia/")
	public void getBasicQuestion(@Body List<String> question, Callback<Message.BasicResponse> response);

	@POST("/api/olivia/google")
	public void postGoogleQuestion(@Body Message.GoogleQuestion question, Callback<List<Message.GoogleResponse>> response);
}
