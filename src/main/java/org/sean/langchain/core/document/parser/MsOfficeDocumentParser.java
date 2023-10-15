package org.sean.langchain.core.document.parser;



import org.apache.poi.extractor.ExtractorFactory;
import org.apache.poi.extractor.POITextExtractor;
import org.sean.langchain.core.data.segment.Metadata;
import org.sean.langchain.core.document.Document;
import org.sean.langchain.core.document.DocumentParser;
import org.sean.langchain.core.document.DocumentType;

import java.io.IOException;
import java.io.InputStream;

import static org.sean.langchain.core.document.Document.DOCUMENT_TYPE;
import static org.sean.langchain.core.util.ValidationUtils.ensureNotNull;


/**
 * Extracts text from a Microsoft Office document.
 * This parser supports various file formats, including ppt, pptx, doc, docx, xls, and xlsx.
 * For detailed information on supported formats, please refer to the <a href="https://poi.apache.org/">official Apache POI website</a>.
 */
public class MsOfficeDocumentParser implements DocumentParser {

    private final DocumentType documentType;

    public MsOfficeDocumentParser(DocumentType documentType) {
        this.documentType = ensureNotNull(documentType, "documentType");
    }

    @Override
    public Document parse(InputStream inputStream) {
        try (POITextExtractor extractor = ExtractorFactory.createExtractor(inputStream)) {
            return new Document(extractor.getText(), Metadata.from(DOCUMENT_TYPE, documentType));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}