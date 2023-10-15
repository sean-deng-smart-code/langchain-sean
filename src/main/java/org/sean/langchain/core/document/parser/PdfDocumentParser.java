package org.sean.langchain.core.document.parser;



import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.sean.langchain.core.data.segment.Metadata;
import org.sean.langchain.core.document.Document;
import org.sean.langchain.core.document.DocumentParser;

import java.io.IOException;
import java.io.InputStream;

import static org.sean.langchain.core.document.Document.DOCUMENT_TYPE;
import static org.sean.langchain.core.document.DocumentType.PDF;


public class PdfDocumentParser implements DocumentParser {

    @Override
    public Document parse(InputStream inputStream) {
        try {
            PDDocument pdfDocument = PDDocument.load(inputStream);
            PDFTextStripper stripper = new PDFTextStripper();
            String content = stripper.getText(pdfDocument);
            pdfDocument.close();
            return Document.from(content, Metadata.from(DOCUMENT_TYPE, PDF));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
