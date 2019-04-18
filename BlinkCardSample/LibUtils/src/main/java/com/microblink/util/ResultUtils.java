package com.microblink.util;

import com.microblink.entities.Entity;
import com.microblink.entities.recognizers.Recognizer;
import com.microblink.entities.recognizers.successframe.SuccessFrameGrabberRecognizer;

public class ResultUtils {

    public static CharSequence getEntitySimpleName(Entity<?, ?> entity) {
        if (entity instanceof SuccessFrameGrabberRecognizer) {
            return getEntitySimpleName(((SuccessFrameGrabberRecognizer) entity).getSlaveRecognizer());
        } else {
            return entity.getClass().getSimpleName();
        }
    }
}
