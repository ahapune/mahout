package com.ahalife.recommender;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.DataModelBuilder;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.model.GenericBooleanPrefDataModel;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefItemBasedRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.common.RandomUtils;

import com.ahalife.recommender.tests.RecommenderTests;
import com.ahalife.recommender.utils.StringConstants;

public class AhalifeRecommender {

	public static void main(String[] args) throws Exception {
		RandomUtils.useTestSeed();
		DataModel model = new GenericBooleanPrefDataModel(
				GenericBooleanPrefDataModel.toDataMap(new FileDataModel(
						new File(AhalifeRecommender.class.getClassLoader()
								.getResource("aha.csv").getFile())))); //$NON-NLS-1$
		JDBCDao.setProductList(model.getItemIDs());
		RecommenderIRStatsEvaluator evaluator = new GenericRecommenderIRStatsEvaluator();
		RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {
			@Override
			public Recommender buildRecommender(DataModel model)
					throws TasteException {
				ItemSimilarity similarity = new AHAItemSimilarity(model);
				return new GenericBooleanPrefItemBasedRecommender(model, similarity);
			}
		};
		
		
		DataModelBuilder modelBuilder = new DataModelBuilder() {
			public DataModel buildDataModel(
					FastByIDMap<PreferenceArray> trainingData) {
				return new GenericBooleanPrefDataModel(
						GenericBooleanPrefDataModel.toDataMap(trainingData));
			}
		};

		int userId = Integer.parseInt(StringConstants.getString("StringConstants.TEST_USER_ID"));
		PreferenceArray p = model.getPreferencesFromUser(userId);
		Iterator<Preference> pItr = p.iterator();
		Map<Long, Float> ratingMap = new HashMap<Long, Float>();
		Preference pref;
		
		while (pItr.hasNext()) {
			pref = pItr.next();
			ratingMap.put(pref.getItemID(), pref.getValue());
		}
		
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

		IRStatistics stats = evaluator.evaluate(recommenderBuilder,	modelBuilder, model, null, 1,
				GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 0.8);
		List<RecommendedItem> reco = recommenderBuilder.buildRecommender(model).recommend(userId, 5);

		System.out.println("\n\nThe Recommended Products for this user were:"); 
		Iterator<RecommendedItem> recoItr = reco.iterator();
		RecommendedItem r;
		Map<Long, Float> recoMap = new HashMap<Long, Float>();
		
		while (recoItr.hasNext()) {
			r = recoItr.next();
			recoMap.put(r.getItemID(), r.getValue());
		}
		
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
		
		System.out.println("\n\nRUNNING TESTS");
		System.out.println("_____________");
		RecommenderTests.testDups(ids, recommendedIds);
		RecommenderTests.testAlreadyRated(ids, recommendedIds);
		
		System.out.println("\n\nThe Precision and Recall in the data-set was:"); 
		System.out.println(stats.getPrecision());
		System.out.println(stats.getRecall());
	}
}
