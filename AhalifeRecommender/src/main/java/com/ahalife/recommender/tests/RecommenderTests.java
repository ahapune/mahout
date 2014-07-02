package com.ahalife.recommender.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;

import com.ahalife.recommender.JDBCDao;
import com.ahalife.recommender.utils.StringConstants;

public class RecommenderTests {

	public static void testForUser (Map<Long, Float> ratingMap, Map<Long, Float> recoMap, long userId) throws TasteException {
		
		List<Long> ids = new ArrayList<Long>();
		ids.addAll(ratingMap.keySet());
		List<Map<String, Object>> productInfo = JDBCDao.getProductInfo(ids);

		System.out.println("The User ID " + userId + " has bought following items, following times:"); 
		System.out.println("ID \t\t\t\t NAME \t\t\t\t #BUYS \t\t\t\t\t URL"); 
		System.out.println("== \t\t\t\t ==== \t\t\t\t ===== \t\t\t\t\t ==="); 

		Iterator<Map<String, Object>> lItr = productInfo.iterator();
		Long productId;
		String url, name;
		Map<String, Object> map;
		
		while (lItr.hasNext()) {
			map = lItr.next();
			productId = ((Long) map.get(StringConstants.getString("StringConstants.PRODUCT_ID"))); 
			url = (String) map.get(StringConstants.getString("StringConstants.URL")); 
			name = ((String) map.get(StringConstants.getString("StringConstants.NAME"))).trim();
			System.out.println(productId + " \t\t\t " + name + " \t\t\t " + ratingMap.get(productId) + " \t\t\t " + "www.ahalife.com/" + url); 
		}
		
		System.out.println("\n\nThe Recommended Products for this user were:"); 

		
		List<Long> recommendedIds = new ArrayList<Long>();
		recommendedIds.addAll(recoMap.keySet());
		productInfo = JDBCDao.getProductInfo(recommendedIds);
		System.out.println("ID \t\t\t\t VALUE \t\t\t NAME \t\t\t\t\t URL"); 
		System.out.println("== \t\t\t\t ===== \t\t\t ==== \t\t\t\t\t ==="); 
		lItr = productInfo.iterator();
		Float value;
		
		while (lItr.hasNext()) {
			map = lItr.next();
			productId = ((Long) map.get(StringConstants.getString("StringConstants.PRODUCT_ID"))); 
			url = (String) map.get(StringConstants.getString("StringConstants.URL")); 
			name = ((String) map.get(StringConstants.getString("StringConstants.NAME"))).trim(); 
			value = recoMap.get(productId);
			System.out.println(productId + " \t\t\t " + value + " \t\t " + name + " \t\t\t " + "www.ahalife.com/products/" + productId); 

		}
		runTests(ids, recommendedIds);
	}
	
	private static void runTests(List<Long> ids, List<Long> recommendedIds) {
		System.out.println("\n\nRUNNING TESTS");
		System.out.println("_____________");
		testDups(ids, recommendedIds);
		testAlreadyRated(ids, recommendedIds);
	}
	
	private static boolean testDups (List<Long> ids, List<Long> recommendedIds) {
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
	private static boolean testAlreadyRated (List<Long> ids,  List<Long> recommendedIds) {
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
