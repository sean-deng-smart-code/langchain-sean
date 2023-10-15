package org.sean.langchain.core.document.source;



import org.sean.langchain.core.data.segment.Metadata;
import org.sean.langchain.core.document.Document;
import org.sean.langchain.core.document.DocumentSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import static org.sean.langchain.core.util.ValidationUtils.ensureNotNull;


public class UrlSource implements DocumentSource {

    private final URL url;

    public UrlSource(URL url) {
        this.url = ensureNotNull(url, "url");
    }

    @Override
    public InputStream inputStream() throws IOException {
        URLConnection connection = url.openConnection();
        return connection.getInputStream();
    }

    @Override
    public Metadata metadata() {
        return Metadata.from(Document.URL, url);
    }

    public static UrlSource from(String url) {
        try {
            return new UrlSource(new URL(url));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static UrlSource from(URL url) {
        return new UrlSource(url);
    }

    public static UrlSource from(URI uri) {
        try {
            return new UrlSource(uri.toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
