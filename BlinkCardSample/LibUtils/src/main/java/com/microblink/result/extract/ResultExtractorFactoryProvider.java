package com.microblink.result.extract;

import com.microblink.result.extract.blinkcard.BlinkCardResultExtractorFactory;

public class ResultExtractorFactoryProvider {

    private static final BlinkCardResultExtractorFactory extractorFactory = new BlinkCardResultExtractorFactory();

    public static BaseResultExtractorFactory get() {
        return extractorFactory;
    }

}
