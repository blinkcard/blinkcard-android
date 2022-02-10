package com.microblink.blinkcard.result.extract.blinkcard;

import com.microblink.blinkcard.entities.recognizers.blinkcard.BlinkCardRecognizer;
import com.microblink.blinkcard.image.Image;
import com.microblink.blinkcard.libutils.R;
import com.microblink.blinkcard.result.extract.BaseResultExtractor;

public class BlinkCardRecognizerResultExtractor extends BaseResultExtractor<BlinkCardRecognizer.Result, BlinkCardRecognizer> {

    @Override
    protected void extractData(BlinkCardRecognizer.Result result) {
        add(R.string.PPIssuer, result.getIssuer().name());
        add(R.string.PPPaymentCardNumber, result.getCardNumber());
        add(R.string.PPPaymentCardNumberValid, result.isCardNumberValid());
        add(R.string.PPPaymentCardNumberPrefix, result.getCardNumberPrefix());
        add(R.string.PPOwner, result.getOwner());
        add(R.string.PPDateOfExpiry, result.getExpiryDate());
        add(R.string.PPCVV, result.getCvv());
        add(R.string.PPIBAN, result.getIban());

        Image firstSideImage = result.getFirstSideFullDocumentImage();
        if (firstSideImage != null) {
            add(R.string.MBFullDocumentImageFirstSide, firstSideImage);
            add(R.string.MBDocumentFirstSideImageBlurred, result.isFirstSideBlurred());
            byte[] encodedFirstSide = result.getEncodedFirstSideFullDocumentImage();
            add(R.string.MBEncodedFullDocumentImageFirstSide, encodedFirstSide);
        }

        Image secondSideImage = result.getSecondSideFullDocumentImage();
        if (secondSideImage != null) {
            add(R.string.MBFullDocumentImageSecondSide, result.getSecondSideFullDocumentImage());
            add(R.string.MBDocumentSecondSideImageBlurred, result.isSecondSideBlurred());
            byte[] encodedSecondSide = result.getEncodedSecondSideFullDocumentImage();
            add(R.string.MBEncodedFullDocumentImageSecondSide, encodedSecondSide);
        }
    }


}
