package com.tbclec.lrobot;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by Bogdan on 11/04/2016.
 */
public interface HttpService {

	@POST("/api/olivia/")
	public void getBasicQuestion(@Query("question") String question, Callback<Message.BasicResponse> response);

//	@GET("/api/olivia/question")
//	public void getBasicQuestion(@Query("question") String question, Callback<Message.BasicResponse> response);

	@POST("/api/olivia/google")
	public void postGoogleQuestion(@Body Message.GoogleQuestion question, Callback<Message.GoogleResponse> response);
}
