package com.microblink.blinkcard.result.extract.blinkcard;

import com.microblink.blinkcard.entities.recognizers.blinkcard.legacy.LegacyBlinkCardEliteRecognizer;
import com.microblink.blinkcard.libutils.R;
import com.microblink.blinkcard.result.extract.BaseResultExtractor;
import com.microblink.blinkcard.result.extract.util.images.CombinedFullDocumentImagesExtractUtil;

public class LegacyBlinkCardEliteRecognitionResultExtractor extends BaseResultExtractor<LegacyBlinkCardEliteRecognizer.Result, LegacyBlinkCardEliteRecognizer> {
    @Override
    protected void extractData(LegacyBlinkCardEliteRecognizer.Result result) {
        add(R.string.PPPaymentCardNumber, result.getCardNumber());
        add(R.string.PPOwner, result.getOwner());
        add(R.string.PPValidThru, result.getValidThru());
        add(R.string.PPCVV, result.getCvv());
        add(R.string.PPInventoryNumber, result.getInventoryNumber());
        CombinedFullDocumentImagesExtractUtil.extractCombinedFullDocumentImages(result, mExtractedData, mBuilder);
    }
}
