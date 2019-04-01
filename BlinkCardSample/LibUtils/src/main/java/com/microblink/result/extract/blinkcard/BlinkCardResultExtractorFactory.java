package com.microblink.result.extract.blinkcard;

import com.microblink.entities.recognizers.blinkcard.BlinkCardEliteRecognizer;
import com.microblink.entities.recognizers.blinkcard.BlinkCardRecognizer;
import com.microblink.result.extract.BaseResultExtractorFactory;

public class BlinkCardResultExtractorFactory extends BaseResultExtractorFactory {
    @Override
    protected void addExtractors() {
        add(BlinkCardRecognizer.class,
                new BlinkCardRecognitionResultExtractor());
        add(BlinkCardEliteRecognizer.class,
                new BlinkCardEliteRecognitionResultExtractor());
    }
}
