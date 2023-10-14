package org.sean.langchain.core.data.output;

import java.util.Objects;

import static org.sean.langchain.core.util.ValidationUtils.ensureNotNull;

public class Response<T> {

    private final T content;
    private final TokenUsage tokenUsage;
    private final FinishReason finishReason;

    public Response(T content) {
        this(content, null, null);
    }

    public Response(T content, TokenUsage tokenUsage, FinishReason finishReason) {
        this.content = ensureNotNull(content, "content");
        this.tokenUsage = tokenUsage;
        this.finishReason = finishReason;
    }

    public T content() {
        return content;
    }

    public TokenUsage tokenUsage() {
        return tokenUsage;
    }

    public FinishReason finishReason() {
        return finishReason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Response<?> that = (Response<?>) o;
        return Objects.equals(this.content, that.content)
                && Objects.equals(this.tokenUsage, that.tokenUsage)
                && Objects.equals(this.finishReason, that.finishReason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, tokenUsage, finishReason);
    }

    @Override
    public String toString() {
        return "Response {" +
                " content = " + content +
                ", tokenUsage = " + tokenUsage +
                ", finishReason = " + finishReason +
                " }";
    }

    public static <T> Response<T> from(T content) {
        return new Response<>(content);
    }

    public static <T> Response<T> from(T content, TokenUsage tokenUsage) {
        return new Response<>(content, tokenUsage, null);
    }

    public static <T> Response<T> from(T content, TokenUsage tokenUsage, FinishReason finishReason) {
        return new Response<>(content, tokenUsage, finishReason);
    }

}
