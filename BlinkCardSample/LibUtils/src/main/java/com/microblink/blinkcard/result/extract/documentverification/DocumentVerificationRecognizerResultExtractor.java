/*
 * Copyright (c) 2021 Microblink Ltd. All rights reserved.
 *
 * ANY UNAUTHORIZED USE OR SALE, DUPLICATION, OR DISTRIBUTION
 * OF THIS PROGRAM OR ANY OF ITS PARTS, IN SOURCE OR BINARY FORMS,
 * WITH OR WITHOUT MODIFICATION, WITH THE PURPOSE OF ACQUIRING
 * UNLAWFUL MATERIAL OR ANY OTHER BENEFIT IS PROHIBITED!
 * THIS PROGRAM IS PROTECTED BY COPYRIGHT LAWS AND YOU MAY NOT
 * REVERSE ENGINEER, DECOMPILE, OR DISASSEMBLE IT.
 */

package com.microblink.blinkcard.result.extract.documentverification;

import com.microblink.blinkcard.entities.recognizers.blinkid.generic.BlinkIdCombinedRecognizer;
import com.microblink.blinkcard.entities.recognizers.documentverification.DocumentLivenessAnalysisResult;
import com.microblink.blinkcard.entities.recognizers.documentverification.DocumentVerificationRecognizer;
import com.microblink.blinkcard.entities.recognizers.documentverification.StaticSecurityFeatureAnalysisResult;
import com.microblink.blinkcard.entities.recognizers.documentverification.TiltStep;
import com.microblink.blinkcard.image.Image;
import com.microblink.blinkcard.libutils.R;
import com.microblink.blinkcard.result.ResultSource;
import com.microblink.blinkcard.result.extract.BaseResultExtractor;
import com.microblink.blinkcard.result.extract.ResultExtractorFactoryProvider;
import com.microblink.blinkcard.result.extract.blinkid.BlinkIdExtractor;

import androidx.annotation.StringRes;

public class DocumentVerificationRecognizerResultExtractor extends BlinkIdExtractor<DocumentVerificationRecognizer.Result, DocumentVerificationRecognizer> {

    @Override
    protected void extractData(DocumentVerificationRecognizer.Result result, ResultSource source) {
        add(R.string.MBDocumentVerificationConsistencyScore, Float.toString(result.getConsistencyScore()));

        DocumentLivenessAnalysisResult livenessAnalysisResult = result.getDocumentLivenessAnalysisResult();
        add(R.string.MBDocumentLivenessAnalysisStatus, livenessAnalysisResult.getProcessingStatus().name());
        if (livenessAnalysisResult.getProcessingStatus() == DocumentLivenessAnalysisResult.ProcessingStatus.Performed) {
            add(R.string.MBDocumentLivenessAnalysisScore, Float.toString(livenessAnalysisResult.getScore()));
        }

        StaticSecurityFeatureAnalysisResult staticSecurityAnalysisResult = result.getStaticSecurityFeatureAnalysisResult();
        add(R.string.MBDocumentStaticSecurityFeatureAnalysisResultStatus, staticSecurityAnalysisResult.getProcessingStatus().name());
        if (staticSecurityAnalysisResult.getProcessingStatus() == StaticSecurityFeatureAnalysisResult.ProcessingStatus.Performed) {
            add(R.string.MBDocumentStaticSecurityFeatureAnalysisResultScore, Float.toString(staticSecurityAnalysisResult.getScore()));
            add(R.string.MBDocumentStaticSecurityFeatureAnalysisResultFullImage, staticSecurityAnalysisResult.getFullDocumentImage());
            for (int i = 0; i < staticSecurityAnalysisResult.getSize(); ++i) {
                StaticSecurityFeatureAnalysisResult.SegmentResult segment = staticSecurityAnalysisResult.getSegmentResult(i);
                add(R.string.MBDocumentStaticSecurityFeatureAnalysisSegmentImage, segment.getSegmentImage());
                add(R.string.MBDocumentStaticSecurityFeatureAnalysisSegmentRelativeBox, segment.getRelativeBox().toString());
                add(R.string.MBDocumentStaticSecurityFeatureAnalysisSegmentStatus, segment.isVerified());
            }
        }

        for (TiltStep step: TiltStep.values()) {
            Image stepImage = result.getTiltStepImage(step);
            if (stepImage != null) {
                add(getTiltStepImageString(step), stepImage);
            }
        }
        BlinkIdCombinedRecognizer extractionRecognizer = mRecognizer.getExtractionRecognizer();
        BaseResultExtractor extractionRecognizerExtractor = ResultExtractorFactoryProvider.get().createExtractor(extractionRecognizer);
        mExtractedData.addAll(extractionRecognizerExtractor.extractData(mContext, extractionRecognizer, source));
    }

    @StringRes
    private int getTiltStepImageString(TiltStep step) {
        switch (step) {
            case TiltNeutral:
                return R.string.MBVerificationTiltNeutralStepImage;
            case TiltLeft:
                return R.string.MBVerificationTiltLeftStepImage;
            case TiltRight:
                return R.string.MBVerificationTiltRightStepImage;
            case TiltUp:
                return R.string.MBVerificationTiltUpStepImage;
            case TiltDown:
                return R.string.MBVerificationTiltDownStepImage;
        }
        throw new IllegalStateException("Unknown tilt step: " + step.toString());
    }

    @Override
    protected void extractData(DocumentVerificationRecognizer.Result result) {
        extractData(result, ResultSource.MIXED);
    }
}
