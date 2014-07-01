package com.ahalife.recommender.tests;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecommenderTests {

	public boolean testRecommendations () {
		
		return false;
	}
	
	public static boolean testDups (List<Long> ids, List<Long> recommendedIds) {
		Set<Long> set = new HashSet<Long>();
		set.addAll(recommendedIds);
		if (set.size() != recommendedIds.size()) {
			System.out.println("\t xxxxx Recommended Products has dups");
			return false;
		}
		System.out.println("\t ----- No Dups in recommended products");
		return true;
	}
	
	/**
	 * 
	 * @param ids
	 * @param recommendedIds
	 * @return
	 * 
	 * NOTE: THIS TEST IS INVALID WHEN WE EXTRAPOLATE THE DATA, IF WE DO.
	 */
	public static boolean testAlreadyRated (List<Long> ids,  List<Long> recommendedIds) {
		int size = ids.size();
		
		if (size > recommendedIds.size()) 
			ids.removeAll(recommendedIds);
		else 
			recommendedIds.removeAll(ids);
		
		if (ids.size() != size) {
			System.out.println("\t xxxxx System recommended already bought products");
			return false;
		}
		System.out.println("\t ----- No intersection between already bought and recommendations");
		return true;
	}
}
