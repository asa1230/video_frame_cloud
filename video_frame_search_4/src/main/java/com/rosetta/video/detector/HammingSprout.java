package com.rosetta.video.detector;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HammingSprout {

	/**
	 * return another Integer, bit in bitIndx of which is opposite to srcInt
	 * @param srcInt
	 * @param bitIdx
	 * @return
	 */
	private Integer flipOneBit(int srcInt, int bitIdx) {
		if (bitIdx<0 || bitIdx >= 32) return null;
		return (srcInt & (~(1<<bitIdx))) | ((~srcInt) & (1<<bitIdx));
	}


	/**
	 * get several numbers which hamming distance to srcInt are 1
	 * @param srcInt, int
	 * @param bitRange, bit range to modify bit
	 * @return List<Integer>, several numbers which hamming distance to srcInt are 1
	 */
	public List<Integer> getFirstLayer (int srcInt, int bitRange) {
		if (bitRange<=0 || bitRange > 32) return null;
		List<Integer> resList = new ArrayList<>();
		for (int i=0; i<bitRange; i++) {
			Integer firstLayerNode = flipOneBit(srcInt, i);
			resList.add(firstLayerNode);
		}
		return resList;
	}


	/**
	 * get several numbers which hamming distance to srcInt are 2
	 * @param srcInt, int
	 * @param bitRange, bit range to modify bit
	 * @return List<Integer>, several numbers which hamming distance to srcInt are 2
	 */
	public List<Integer> getSecondLayer (int srcInt, int bitRange) {
		if (bitRange<=0 || bitRange > 32) return null;
		List<Integer> resList = new ArrayList<>();
		for (int i=0; i<bitRange; i++) {
			Integer firstLayerNode = flipOneBit(srcInt, i);
			for (int j=i+1; j<bitRange; j++) {
				Integer secondLayerNode = flipOneBit(firstLayerNode, j);
				resList.add(secondLayerNode);
			}
		}
		return resList;
	}


	/**
	 * get several numbers which hamming distance to srcInt are 3
	 * @param srcInt, int
	 * @param bitRange, bit range to modify bit
	 * @return List<Integer>, several numbers which hamming distance to srcInt are 3
	 */
	public List<Integer> getThirdLayer (int srcInt, int bitRange) {
		if (bitRange<=0 || bitRange > 32) return null;
		List<Integer> resList = new ArrayList<>();
		for (int i=0; i<bitRange; i++) {
			Integer firstLayerNode = flipOneBit(srcInt, i);
			for (int j=i+1; j<bitRange; j++) {
				Integer secondLayerNode = flipOneBit(firstLayerNode, j);
				for (int k=j+1; k<bitRange; k++) {
					Integer thirdLayerNode = flipOneBit(secondLayerNode, k);
					resList.add(thirdLayerNode);
				}
			}
		}
		return resList;
	}


	/**
	 * get several numbers which hamming distance to srcInt are <=3
	 * @param srcInt, int
	 * @param bitRange, bit range to modify bit
	 * @return List<Integer>, several numbers which hamming distance to srcInt are <=3
	 */
	public List<Integer> getThreeLayers (int srcInt, int bitRange) {
		if (bitRange<=0 || bitRange > 32) return null;
		List<Integer> resList = new ArrayList<>();
		// get root layer
		resList.add(srcInt);
		// get first layer
		List<Integer> firstLayerList = getFirstLayer(srcInt, bitRange);
		resList.addAll(firstLayerList);
		// get second layer
		List<Integer> secondLayerList = getSecondLayer(srcInt, bitRange);
		resList.addAll(secondLayerList);
		//get third layer
		List<Integer> thirdLayerList = getThirdLayer(srcInt, bitRange);
		resList.addAll(thirdLayerList);
		return resList;
	}


	/**
	 * get several numbers which hamming distance to srcInt are 1
	 * @param srcInt, int
	 * @return List<Integer>, several numbers which hamming distance to srcInt are 1
	 */
	public List<Integer> getFirstLayer (int srcInt) {
		List<Integer> resList = new ArrayList<>();
		for (int i=0; i<16; i++) {
			Integer firstLayerNode = flipOneBit(srcInt, i);
			resList.add(firstLayerNode);
		}
		return resList;
	}
	
	
	/**
	 * get several numbers which hamming distance to srcInt are 2
	 * @param srcInt, int
	 * @return List<Integer>, several numbers which hamming distance to srcInt are 2
	 */
	public List<Integer> getSecondLayer (int srcInt) {
		List<Integer> resList = new ArrayList<>();
		for (int i=0; i<16; i++) {
			Integer firstLayerNode = flipOneBit(srcInt, i);
			for (int j=i+1; j<16; j++) {
				Integer secondLayerNode = flipOneBit(firstLayerNode, j);
				resList.add(secondLayerNode);
			}
		}
		return resList;
	}
	
	
	/**
	 * get several numbers which hamming distance to srcInt are 3
	 * @param srcInt, int
	 * @return List<Integer>, several numbers which hamming distance to srcInt are 3
	 */
	public List<Integer> getThirdLayer (int srcInt) {
		List<Integer> resList = new ArrayList<>();
		for (int i=0; i<16; i++) {
			Integer firstLayerNode = flipOneBit(srcInt, i);
			for (int j=i+1; j<16; j++) {
				Integer secondLayerNode = flipOneBit(firstLayerNode, j);
				for (int k=j+1; k<16; k++) {
					Integer thirdLayerNode = flipOneBit(secondLayerNode, k);
					resList.add(thirdLayerNode);
				}
			}
		}
		return resList;
	}

	
	/**
	 * get several numbers which hamming distance to srcInt are <=3
	 * @param srcInt, int
	 * @return List<Integer>, several numbers which hamming distance to srcInt are <=3
	 */
	public List<Integer> getThreeLayers (int srcInt) {
		List<Integer> resList = new ArrayList<>();
		// get root layer
		resList.add(srcInt);
		// get first layer
		List<Integer> firstLayerList = getFirstLayer(srcInt);
		resList.addAll(firstLayerList);
		// get second layer
		List<Integer> secondLayerList = getSecondLayer(srcInt);
		resList.addAll(secondLayerList);
		//get third layer
		List<Integer> thirdLayerList = getThirdLayer(srcInt);
		resList.addAll(thirdLayerList);
		return resList;
	}

	public static void main(String[] args) {
		HammingSprout hammingSprout = new HammingSprout();
		List<Integer> threeLayers = hammingSprout.getThreeLayers(23542525, 28);
		System.out.println(threeLayers.size());
	}

}
