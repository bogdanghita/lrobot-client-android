package com.tbclec.lrobot;

/**
 * Created by Bogdan on 11/04/2016.
 */
public interface OliviaResponseCallbackClient {

	void notifyAnswerReceived(String answer);

	void notifyRequestFailed();
}
