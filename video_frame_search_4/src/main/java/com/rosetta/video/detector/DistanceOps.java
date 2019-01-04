package com.rosetta.video.detector;

import com.rosetta.video.exception.DetectException;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DistanceOps {

    /*******************************************************************************************************************
     * for opencv
     */

    public Double calcEuclideanDistance(Mat features1, Mat features2) {
        // calc norm L2
        return Core.norm(features1, features2, Core.NORM_L2);
    }

    public Double calcEuclideanDistance(float[] features1, float[] features2) throws DetectException {

        int length = features1.length;
        if (features2.length != length)
            throw new DetectException(
                    DetectException.CALC_EUCLIDEAN_DISTANCE_FAIL,
                    "calc Euclidean distance fail");

        Mat featMat1 = new Mat(length/3, 1, CvType.CV_32FC(3));
        Mat featMat2 = new Mat(length/3, 1, CvType.CV_32FC(3));
        featMat1.put(0,0, features1);
        featMat2.put(0,0, features2);
        // calc norm L2
        double distance = Core.norm(featMat1, featMat2, Core.NORM_L2);

        try {
            if (featMat1 != null) featMat1.release();
            if (featMat2 != null) featMat2.release();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return distance;
    }




    /*******************************************************************************************************************
     * for java
     */


    public int calcHammingDistance(int[] features1, int[] features2) {

        if (features1.length != features2.length) return -1;
        int distance = 0;
        for (int i=0; i<features1.length; i++) {
            int exclusiveOr = features1[i] ^ features2[i];
            while (exclusiveOr != 0) {
                distance += exclusiveOr & 1;
                // unsigned right shift
                exclusiveOr = exclusiveOr >>> 1;
            }
        }
        return distance;
    }

    /**
     * calculate Hamming distance.
     *
     * @param features1, Integer[]
     * @param features2, Integer[]
     * @return int, the Hamming distance
     */
    public int calcHammingDistance(Integer[] features1, Integer[] features2) {

        if (features1.length != features2.length) return -1;
        int distance = 0;
        for (int i=0; i<features1.length; i++) {
            int exclusiveOr = features1[i] ^ features2[i];
            while (exclusiveOr != 0) {
                distance += exclusiveOr & 1;
                // unsigned right shift
                exclusiveOr = exclusiveOr >>> 1;
            }
        }
        return distance;
    }

    /**
     * calculate Hamming distance.
     *
     * @param features1, List<Integer>
     * @param features2, List<Integer>
     * @return int, the Hamming distance
     */
    public int calcHammingDistance(List<Integer> features1, List<Integer> features2) {

        if (features1.size() != features2.size()) return -1;
        int distance = 0;
        for (int i=0; i<features1.size(); i++) {
            int exclusiveOr = features1.get(i) ^ features2.get(i);
            while (exclusiveOr != 0) {
                distance += exclusiveOr & 1;
                // unsigned right shift
                exclusiveOr = exclusiveOr >>> 1;
            }
        }
        return distance;
    }

    /**
     * calculate Hamming distance.
     *
     * @param features1, Integer
     * @param features2, Integer
     * @return int, the Hamming distance
     */
    public int calcHammingDistance(Integer features1, Integer features2) {

        if (null == features1 || null == features2) return -1;
        int distance = 0;
        int exclusiveOr = features1 ^ features2;
        while (exclusiveOr != 0) {
            distance += exclusiveOr & 1;
            // unsigned right shift
            exclusiveOr = exclusiveOr >>> 1;
        }
        return distance;
    }

    /**
     * calculate Hamming distance.
     *
     * @param featureStr1, String
     * @param featureStr2, String
     * @return int, the Hamming distance
     */
    public int calcHammingDistance(String featureStr1, String featureStr2) {
        if(null == featureStr1
                || 0 == featureStr1.length()
                || null == featureStr2
                || 0 == featureStr2.length())
            return -1;
        String[] features1 = featureStr1.trim().split(",");
        String[] features2 = featureStr2.trim().split(",");
        if(features1.length != features2.length) return -1;

        int distance = 0;
        try {
            for (int i=0; i<features1.length; i++) {
                int exclusiveOr = Integer.parseInt(features1[i]) ^ Integer.parseInt(features2[i]);
                while (exclusiveOr != 0) {
                    distance += exclusiveOr & 1;
                    // unsigned right shift
                    exclusiveOr = exclusiveOr >>> 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return distance;
    }

    /**
     * calculate 2-norm.
     *
     * @param feature1, Integer[]
     * @param feature2, Integer[]
     * @return int, the 2-norm
     */
    public int calcEuclideanDistance(Integer[] feature1, Integer[] feature2) {

        if (feature1.length != feature2.length) return -1;
        int distance = 0;
        for (int i=0; i<feature1.length; i++) {
            distance += Math.pow((feature1[i]-feature2[i]), 2);
        }
//		distance = Math.sqrt(distance);
        return distance;
    }
    /**
     * calculate 2-norm.
     *
     * @param feature1, List<Integer>
     * @param feature2, List<Integer>
     * @return int, the 2-norm
     */
    public int calcEuclideanDistance(List<Integer> feature1, List<Integer> feature2) {

        if (feature1.size() != feature2.size() ) return -1;
        int distance = 0;
        for (int i=0; i<feature1.size(); i++) {
            distance += Math.pow((feature1.get(i)-feature2.get(i)), 2);
        }
//		distance = Math.sqrt(distance);
        return distance;
    }

    /**
     * calculate relative entropy
     * it is also called Kullback-Leibler divergence
     *
     * @param baseFeatures, Float[]
     * @param compareFeatures, Float[]
     * @return float, the relative entropy
     */
    public float calcRelativeEntropy(Float[] baseFeatures, Float[] compareFeatures) {

        float eps = 10E-20f;
        if (baseFeatures.length != compareFeatures.length) return -1f;
        float distance = 0;
        for (int i=0; i<baseFeatures.length; i++) {
            distance += baseFeatures[i]*(Math.log10(baseFeatures[i]+eps) - Math.log10(compareFeatures[i]+eps));
        }
        return distance;
    }

    /**
     * calculate relative entropy
     * it is also called Kullback-Leibler divergence
     *
     * @param baseFeatures, List<Float>
     * @param compareFeatures, List<Float>
     * @return float, the relative entropy
     */
    public float calcRelativeEntropy(List<Float> baseFeatures, List<Float> compareFeatures) {

        float eps = 10E-20f;
        if (baseFeatures.size() != compareFeatures.size() ) return -1f;
        float distance = 0;
        for (int i=0; i<baseFeatures.size(); i++) {
            distance += baseFeatures.get(i)*(Math.log10(baseFeatures.get(i)+eps) - Math.log10(compareFeatures.get(i)+eps));
        }
        return distance;
    }

}
