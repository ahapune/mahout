package com.ahalife;

import java.io.File;

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
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.common.RandomUtils;

public class AhalifeRecommender {

  public static void main(String[] args) throws Exception {
    RandomUtils.useTestSeed();
    DataModel model =
        new GenericBooleanPrefDataModel(GenericBooleanPrefDataModel.toDataMap(new FileDataModel(
            new File(AhalifeRecommender.class.getClassLoader().getResource("aha.csv").getFile()))));
    JDBCDao.setProductList(model.getItemIDs());
    RecommenderIRStatsEvaluator evaluator = new GenericRecommenderIRStatsEvaluator();
    RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {
      @Override
      public Recommender buildRecommender(DataModel model) throws TasteException {
        ItemSimilarity similarity = new AHAItemSimilarity(model);
        //UserNeighborhood neighborhood = new NearestNUserNeighborhood(5, similarity, model);
        //return new GenericBooleanPrefUserBasedRecommender(model, neighborhood, similarity);
        return new GenericBooleanPrefItemBasedRecommender(model,  similarity);
      }
    };
    DataModelBuilder modelBuilder = new DataModelBuilder() {
      public DataModel buildDataModel(FastByIDMap<PreferenceArray> trainingData) {
        return new GenericBooleanPrefDataModel(GenericBooleanPrefDataModel.toDataMap(trainingData));
      }
    };
    IRStatistics stats =
        evaluator.evaluate(recommenderBuilder, modelBuilder, model, null, 1,
            GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 0.8);
    System.out.println( recommenderBuilder.buildRecommender(model).recommend(177, 3));
    System.out.println(stats.getPrecision());
    System.out.println(stats.getRecall());
  }
}
