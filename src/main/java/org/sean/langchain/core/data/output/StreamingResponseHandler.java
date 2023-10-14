package org.sean.langchain.core.data.output;

public interface StreamingResponseHandler<T> {

    /**
     * Invoked each time the language model generates a new token in a textual response.
     * If the model executes a tool instead, this method will not be invoked; {@link #onComplete} will be invoked instead.
     *
     * @param token The newly generated token, which is a part of the complete response.
     */
    void onNext(String token);

    /**
     * Invoked when the language model has finished streaming a response.
     * If the model executes a tool, it is accessible via {@link dev.langchain4j.data.message.AiMessage#toolExecutionRequest()}.
     *
     * @param response The complete response generated by the language model.
     *                 For textual responses, it contains all tokens from {@link #onNext} concatenated.
     */
    default void onComplete(Response<T> response) {
    }

    /**
     * This method is invoked when an error occurs during streaming.
     *
     * @param error The error that occurred
     */
    void onError(Throwable error);
}
