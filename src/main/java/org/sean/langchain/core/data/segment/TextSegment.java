package org.sean.langchain.core.data.segment;

import java.util.Objects;

import static org.sean.langchain.core.util.Utils.quoted;
import static org.sean.langchain.core.util.ValidationUtils.ensureNotBlank;
import static org.sean.langchain.core.util.ValidationUtils.ensureNotNull;

public class TextSegment {
    private final String text;
    private final Metadata metadata;

    public TextSegment(String text, Metadata metadata) {
        this.text = ensureNotBlank(text, "text");
        this.metadata = ensureNotNull(metadata, "metadata");
    }



    public String text() {
        return text;
    }

    public Metadata metadata() {
        return metadata;
    }

    public String metadata(String key) {
        return metadata.get(key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextSegment that = (TextSegment) o;
        return Objects.equals(this.text, that.text)
                && Objects.equals(this.metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, metadata);
    }

    @Override
    public String toString() {
        return "TextSegment {" +
                " text = " + quoted(text) +
                " metadata = " + metadata.asMap() +
                " }";
    }

    public static TextSegment from(String text) {
        return new TextSegment(text, new Metadata());
    }

    public static TextSegment from(String text, Metadata metadata) {
        return new TextSegment(text, metadata);
    }

    public static TextSegment textSegment(String text) {
        return from(text);
    }

    public static TextSegment textSegment(String text, Metadata metadata) {
        return from(text, metadata);
    }

}
