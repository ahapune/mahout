package com.ahalife.recommender;

import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.RefreshHelper;
import org.apache.mahout.cf.taste.impl.similarity.AbstractItemSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

public class AHAItemSimilarity extends AbstractItemSimilarity implements
		ItemSimilarity {
	public AHAItemSimilarity(DataModel dataModel) {
		super(dataModel);
	}

	@Override
	public void refresh(Collection<Refreshable> alreadyRefreshed) {
		alreadyRefreshed = RefreshHelper.buildRefreshed(alreadyRefreshed);
		RefreshHelper.maybeRefresh(alreadyRefreshed, getDataModel());

	}

	@Override
	public double itemSimilarity(long itemID1, long itemID2)
			throws TasteException {
		List<Long> itemCategories1 = JDBCDao.getProductCategories(itemID1);
		List<Long> itemCategories2 = JDBCDao.getProductCategories(itemID2);
		int pref1and2 = getDataModel().getNumUsersWithPreferenceFor(itemID1, itemID2);
		int pref1 = getDataModel().getNumUsersWithPreferenceFor(itemID1);
		int pref2 = getDataModel().getNumUsersWithPreferenceFor(itemID2);
		double coeff = (double) pref1and2 / (double) (pref1 + pref2 - pref1and2);
		if (CollectionUtils.containsAny(itemCategories1, itemCategories2)) {
			double newCoeff = (coeff + 0.5);
			if (newCoeff > 1) {
				return 1;
			} else {
				return newCoeff;
			}

		} else {
			return coeff;
		}
	}

	@Override
	public double[] itemSimilarities(long itemID1, long[] itemID2s)
			throws TasteException {
		int length = itemID2s.length;
		double[] result = new double[length];
		for (int i = 0; i < length; i++) {
			result[i] = itemSimilarity(itemID1, itemID2s[i]);
		}
		return result;
	}

}
