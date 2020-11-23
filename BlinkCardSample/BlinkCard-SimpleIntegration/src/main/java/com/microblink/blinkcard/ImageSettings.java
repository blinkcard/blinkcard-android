package com.microblink.blinkcard;

import com.microblink.blinkcard.entities.recognizers.Recognizer;
import com.microblink.blinkcard.entities.recognizers.blinkid.imageoptions.FullDocumentImageOptions;

public class ImageSettings {

    public static Recognizer enableAllImages(Recognizer recognizer) {
        if(recognizer instanceof FullDocumentImageOptions) {
            FullDocumentImageOptions options = (FullDocumentImageOptions) recognizer;
            options.setReturnFullDocumentImage(true);
        }
        return recognizer;
    }

}
