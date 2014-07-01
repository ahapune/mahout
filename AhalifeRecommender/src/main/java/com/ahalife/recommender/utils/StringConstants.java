package com.ahalife.recommender.utils;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class StringConstants {
	private static final String BUNDLE_NAME = "com.ahalife.recommender.utils.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private StringConstants() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
