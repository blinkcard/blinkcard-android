package com.microblink.blinkcard.result.extract.blinkcard;

import com.microblink.blinkcard.entities.recognizers.blinkcard.BlinkCardRecognizer;
import com.microblink.blinkcard.entities.recognizers.blinkcard.DocumentLivenessCheckResult;
import com.microblink.blinkcard.image.Image;
import com.microblink.blinkcard.libutils.R;
import com.microblink.blinkcard.result.extract.BaseResultExtractor;
import com.microblink.blinkcard.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

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
        add(R.string.first_side_anonymized, result.isFirstSideAnonymized());
        add(R.string.second_side_anonymized, result.isSecondSideAnonymized());
        addDocumentLivenessCheck(result.getDocumentLivenessCheck());

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

    private void addDocumentLivenessCheck(DocumentLivenessCheckResult documentLivenessCheckResult) {
        JSONObject root = new JSONObject();
        JSONObject front = new JSONObject();
        JSONObject back = new JSONObject();
        try {
            front.put("frontScreenCheckResult", documentLivenessCheckResult.getFront().getScreenCheck().getCheckResult().name());
            front.put("frontScreenCheckMatchLevel", documentLivenessCheckResult.getFront().getScreenCheck().getMatchLevel().name());

            front.put("frontPhotocopyCheckResult", documentLivenessCheckResult.getFront().getPhotocopyCheck().getCheckResult().name());
            front.put("frontPhotocopyCheckMatchLevel", documentLivenessCheckResult.getFront().getPhotocopyCheck().getMatchLevel().name());

            front.put("handPresenceCheck", documentLivenessCheckResult.getFront().getHandPresenceCheck().name());
            root.put("front", front);

            back.put("backScreenCheckResult", documentLivenessCheckResult.getBack().getScreenCheck().getCheckResult().name());
            back.put("backScreenCheckMatchLevel", documentLivenessCheckResult.getBack().getScreenCheck().getMatchLevel().name());

            back.put("backPhotocopyCheckResult", documentLivenessCheckResult.getBack().getPhotocopyCheck().getCheckResult().name());
            back.put("backPhotocopyCheckMatchLevel", documentLivenessCheckResult.getBack().getPhotocopyCheck().getMatchLevel().name());

            back.put("handPresenceCheck", documentLivenessCheckResult.getBack().getHandPresenceCheck().name());
            root.put("back", back);

            add(R.string.document_liveness_check, root.toString(2));

        } catch (JSONException e) {
            Log.d(this, "Exception creating DocumentLivenessCheckResult!" + e.getMessage());
        }
    }


}
