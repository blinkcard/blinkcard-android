package com.microblink.result.extract.blinkcard;

import com.microblink.R;
import com.microblink.entities.recognizers.blinkcard.BlinkCardRecognizer;
import com.microblink.result.extract.BaseResultExtractor;
import com.microblink.result.extract.util.images.CombinedFullDocumentImagesExtractUtil;
import com.microblink.result.extract.util.signature.DigitalSignatureExtractUtil;

public class BlinkCardRecognitionResultExtractor extends BaseResultExtractor<BlinkCardRecognizer.Result, BlinkCardRecognizer> {

    @Override
    protected void extractData(BlinkCardRecognizer.Result result) {
        add(R.string.PPIssuer, result.getIssuer().name());
        add(R.string.PPPaymentCardNumber, result.getCardNumber());
        addIfNotEmpty(R.string.PPOwner, result.getOwner());
        add(R.string.PPValidThru, result.getValidThru().getOriginalDateString());
        add(R.string.PPCVV, result.getCvv());
        add(R.string.PPInventoryNumber, result.getInventoryNumber());
        CombinedFullDocumentImagesExtractUtil.extractCombinedFullDocumentImages(result, mExtractedData, mBuilder);
        DigitalSignatureExtractUtil.extractDigitalSignature(result, mExtractedData, mBuilder);
    }


}
