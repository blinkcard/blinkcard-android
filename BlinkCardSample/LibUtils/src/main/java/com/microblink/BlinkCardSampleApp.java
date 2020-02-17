package com.microblink;

import com.microblink.result.extract.BaseResultExtractorFactory;
import com.microblink.result.extract.blinkcard.BlinkCardResultExtractorFactory;

public final class BlinkCardSampleApp extends SampleApplication {

    @Override
    protected BaseResultExtractorFactory createResultExtractorFactory() {
        return new BlinkCardResultExtractorFactory();
    }

    @Override
    protected String getLicenceFilePath() {
        return "com.microblink.blinkcard.mblic";
    }

}
