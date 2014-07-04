/**
 * 
 */
package com.ahalife.recommender.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import com.ahalife.recommender.AhalifeRecommender;

/**
 * @author shubhanan
 *
 */
public class DataConvert {


		public static void main(String[] args) throws IOException {

				
			BufferedReader br = new BufferedReader(new FileReader(AhalifeRecommender.class.getClassLoader()
					.getResource("addtobag_20140702.csv").getPath()));
			BufferedWriter bw = new BufferedWriter(new FileWriter("src/main/resources/CombinedData.csv"));
			HashMap<String, String> ratingMap = new HashMap<String, String>();
			
			String line;
			while((line = br.readLine()) != null) {
				String[] values = line.split(",", -1);
				bw.write(values[0] + "," + values[1] + "," + values[2] + "\n");
				ratingMap.put(values[0]+":"+values[1], values[2]);
			}
			
			br.close();
			
			br = new BufferedReader(new FileReader(AhalifeRecommender.class.getClassLoader()
					.getResource("checkouts_20140703.csv").getPath()));
			int count = 0;
			while((line = br.readLine()) != null) {
				String[] values = line.split(",", -1);
				bw.write(values[0] + "\t" + values[1] + "\t" + "5" + "\n");
				if (ratingMap.containsKey(values[0]+":"+values[1])) {
					System.out.println(++count + " -- " + values[0]+":"+values[1] + " repeated.");
				}
				ratingMap.put(values[0]+":"+values[1], "5");
			}

			br.close();
			bw.close();

		}
}
