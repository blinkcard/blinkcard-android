package com.microblink.blinkcard.result.extract;

import com.microblink.blinkcard.entities.recognizers.Recognizer;
import com.microblink.blinkcard.entities.recognizers.successframe.SuccessFrameGrabberRecognizer;
import com.microblink.blinkcard.libutils.R;
import com.microblink.blinkcard.result.ResultSource;

public class SuccessFrameGrabberResultExtractor extends BaseResultExtractor<SuccessFrameGrabberRecognizer.Result, SuccessFrameGrabberRecognizer> {

    @Override
    protected void extractData(SuccessFrameGrabberRecognizer.Result result) {
        Recognizer slaveRecognizer = mRecognizer.getSlaveRecognizer();
        BaseResultExtractor slaveExtractor = ResultExtractorFactoryProvider.get().createExtractor(slaveRecognizer);
        mExtractedData.addAll(slaveExtractor.extractData(mContext, slaveRecognizer, ResultSource.MIXED));
        mExtractedData.add(mBuilder.build(
                R.string.PPSuccessFrame,
                mRecognizer.getResult().getSuccessFrame()
        ));
    }
}
