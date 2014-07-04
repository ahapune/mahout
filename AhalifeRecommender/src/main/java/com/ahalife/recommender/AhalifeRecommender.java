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
		
		System.out.print("\tBuilding Data Model & Recommender ...");
		long startTime = System.currentTimeMillis();
		DataModel model = new GenericBooleanPrefDataModel(
				GenericBooleanPrefDataModel.toDataMap(new FileDataModel(
						new File(AhalifeRecommender.class.getClassLoader()
								.getResource("aha.csv").getFile()))));
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
		long endTime = System.currentTimeMillis();
		System.out.println(" Done in " + (endTime - startTime) + "ms.");
		int userId = Integer.parseInt(StringConstants.getString("StringConstants.TEST_USER_ID"));
		startTime = System.currentTimeMillis();
		System.out.print("\tEvaluating model + recommender ... ");
		IRStatistics stats = evaluator.evaluate(recommenderBuilder,	modelBuilder, model, null, 5,
				GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 1);
		endTime = System.currentTimeMillis();
		
		long evaluationTime = endTime - startTime;
		System.out.println("done in " + evaluationTime + "ms");
		System.out.println("\tInitialization Complete.\n\n");
		System.out.print("\tRecommending 5 products ... ");
		startTime = System.currentTimeMillis();
		List<RecommendedItem> reco = recommenderBuilder.buildRecommender(model).recommend(userId, 5);
		endTime = System.currentTimeMillis();
		long recoTime = endTime-startTime;
		System.out.println("done in " + recoTime + "ms");
		
		
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
		
		Iterator<RecommendedItem> recoItr = reco.iterator();
		RecommendedItem r;
		Map<Long, Float> recoMap = new HashMap<Long, Float>();
		while (recoItr.hasNext()) {
			r = recoItr.next();
			recoMap.put(r.getItemID(), r.getValue());
		}
		List<Long> recommendedIds = new ArrayList<Long>();
		recommendedIds.addAll(recoMap.keySet());
		
		RecommenderTests.testForUser(ratingMap, recoMap, userId);
		
		
//		System.out.println("\n\nThe Precision and Recall was:"); 
//		System.out.println(stats.getPrecision());
//		System.out.println(stats.getRecall());
	}
}
