package org.sean.langchain.core.document.parser;


import org.sean.langchain.core.data.segment.Metadata;
import org.sean.langchain.core.document.Document;
import org.sean.langchain.core.document.DocumentParser;
import org.sean.langchain.core.document.DocumentType;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;


import static java.nio.charset.StandardCharsets.UTF_8;
import static org.sean.langchain.core.document.Document.DOCUMENT_TYPE;
import static org.sean.langchain.core.util.ValidationUtils.ensureNotNull;

public class TextDocumentParser implements DocumentParser {

    private final DocumentType documentType;
    private final Charset charset;

    public TextDocumentParser(DocumentType documentType) {
        this(documentType, UTF_8);
    }

    public TextDocumentParser(DocumentType documentType, Charset charset) {
        this.documentType = ensureNotNull(documentType, "documentType");
        this.charset = ensureNotNull(charset, "charset");
    }

    @Override
    public Document parse(InputStream inputStream) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();

            String text = new String(buffer.toByteArray(), charset);

            return Document.from(text, Metadata.from(DOCUMENT_TYPE, documentType.toString()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
