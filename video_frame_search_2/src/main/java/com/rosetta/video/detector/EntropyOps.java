package com.rosetta.video.detector;

import com.rosetta.video.exception.DetectException;
import org.springframework.stereotype.Service;

@Service
public class EntropyOps {

    // Double运算误差的修正值
    public static final Float EPSILON = 0.00001f;

    /**
     * calc entropy of float[]
     * @param histHSV
     * @return
     */
    public Double calcImageEntropy(float[] histHSV) throws DetectException {
        Double entropy = 0.0;
        for (float h : histHSV) {
            if (h < 0) throw new DetectException(DetectException.CALC_ENTROPY_FAIL,
                    "the f in float[] can not less than 0");
            h = Math.max(h, EPSILON);
            entropy += (-1.0) * h * Math.log10(h);
        }
        return entropy;
    }



}
