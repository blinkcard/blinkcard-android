package com.microblink.blinkcard.result.extract.documentverification;


import com.microblink.blinkcard.entities.recognizers.blinkid.generic.BlinkIdCombinedRecognizer;
import com.microblink.blinkcard.entities.recognizers.documentverification.DocumentVerificationRecognizer;
import com.microblink.blinkcard.result.extract.BaseResultExtractorFactory;
import com.microblink.blinkcard.result.extract.blinkid.generic.BlinkIDCombinedRecognizerResultExtractor;

public class DocumentVerificationResultExtractorFactory extends BaseResultExtractorFactory {

    @Override
    protected void addExtractors() {
        add(BlinkIdCombinedRecognizer.class,
                new BlinkIDCombinedRecognizerResultExtractor());
        add(DocumentVerificationRecognizer.class,
                new DocumentVerificationRecognizerResultExtractor());
    }
}
