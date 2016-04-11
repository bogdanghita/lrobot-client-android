package com.tbclec.lrobot;

import java.util.List;

/**
 * Created by Bogdan on 11/04/2016.
 */
public interface OliviaResponseCallbackClient {

	void notifyBasicAnswerReceived(String answer);

	void notifyGoogleAnswerReceived(List<Message.GoogleResponse> answer);

	void notifyRequestFailed();
}
