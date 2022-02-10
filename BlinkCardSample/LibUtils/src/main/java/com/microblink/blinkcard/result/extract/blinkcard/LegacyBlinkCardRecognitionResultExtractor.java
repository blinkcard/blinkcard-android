package com.microblink.blinkcard.result.extract.blinkcard;

import com.microblink.blinkcard.entities.recognizers.blinkcard.legacy.LegacyBlinkCardRecognizer;
import com.microblink.blinkcard.libutils.R;
import com.microblink.blinkcard.result.extract.BaseResultExtractor;
import com.microblink.blinkcard.result.extract.util.images.CombinedFullDocumentImagesExtractUtil;

public class LegacyBlinkCardRecognitionResultExtractor extends BaseResultExtractor<LegacyBlinkCardRecognizer.Result, LegacyBlinkCardRecognizer> {

    @Override
    protected void extractData(LegacyBlinkCardRecognizer.Result result) {
        add(R.string.PPIssuer, result.getIssuer().name());
        add(R.string.PPPaymentCardNumber, result.getCardNumber());
        addIfNotEmpty(R.string.PPOwner, result.getOwner());
        add(R.string.PPValidThru, result.getValidThru().getOriginalDateString());
        add(R.string.PPCVV, result.getCvv());
        add(R.string.PPIBAN, result.getIban());
        add(R.string.PPInventoryNumber, result.getInventoryNumber());
        CombinedFullDocumentImagesExtractUtil.extractCombinedFullDocumentImages(result, mExtractedData, mBuilder);
    }


}
