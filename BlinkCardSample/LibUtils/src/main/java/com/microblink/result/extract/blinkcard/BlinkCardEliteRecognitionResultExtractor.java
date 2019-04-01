package com.microblink.result.extract.blinkcard;

import com.microblink.R;
import com.microblink.entities.recognizers.blinkcard.BlinkCardEliteRecognizer;
import com.microblink.result.extract.BaseResultExtractor;
import com.microblink.result.extract.util.images.CombinedFullDocumentImagesExtractUtil;
import com.microblink.result.extract.util.signature.DigitalSignatureExtractUtil;

public class BlinkCardEliteRecognitionResultExtractor extends BaseResultExtractor<BlinkCardEliteRecognizer.Result, BlinkCardEliteRecognizer> {
    @Override
    protected void extractData(BlinkCardEliteRecognizer.Result result) {
        add(R.string.PPPaymentCardNumber, result.getCardNumber());
        add(R.string.PPOwner, result.getOwner());
        add(R.string.PPValidThru, result.getValidThru());
        add(R.string.PPCVV, result.getCvv());
        add(R.string.PPInventoryNumber, result.getInventoryNumber());
        CombinedFullDocumentImagesExtractUtil.extractCombinedFullDocumentImages(result, mExtractedData, mBuilder);
        DigitalSignatureExtractUtil.extractDigitalSignature(result, mExtractedData, mBuilder);
    }
}
