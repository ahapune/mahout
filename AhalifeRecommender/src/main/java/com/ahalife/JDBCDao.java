package com.ahalife;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;


public class JDBCDao {

  private static NamedParameterJdbcTemplate namedJdbcTemplate;
  static DriverManagerDataSource dataSource;
  private static String GET_PRODUCTS_CATEGORIES =
      "select bcpr.CATEGORY_ID,bcpr.PRODUCT_ID from blc_product bp " +
      "join blc_category_product_xref bcpr using (PRODUCT_ID) " +
      "where bp.PRODUCT_ID in (:ids)";
  private static String GET_PRODUCT_INFO = "select bp.`product_id`, bs.`name`, bp.`url` from blc_product bp " +
  		"join blc_sku bs on bp.`PRODUCT_ID`=bs.`DEFAULT_PRODUCT_ID` " +
  		"where bp.`PRODUCT_ID`in (:ids)";

  private static Multimap<Long, Long> categoryList;
  static List<Long> productIds ;
  public JDBCDao() {


  }


  private static NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
    if (namedJdbcTemplate == null) {
      namedJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
    }
    return namedJdbcTemplate;
  }

  private static DataSource getDataSource() {
    if (dataSource == null) {
      dataSource = new DriverManagerDataSource();
      dataSource.setDriverClassName("com.mysql.jdbc.Driver");

      dataSource.setUsername("apivalve");
      dataSource.setUrl("jdbc:mysql://sql.qa.ahalife.com:3306/broadleaf_mobile?autoReconnect=true");
      dataSource.setPassword("iexda1dXgAMa");
    }
    return dataSource;
  }
  public static void setProductList( LongPrimitiveIterator longPrimitiveIterator){
    productIds = new ArrayList<Long>();
    while(longPrimitiveIterator.hasNext()){
      productIds.add(longPrimitiveIterator.next());
    }
  }
  public static Multimap<Long, Long> getCategoriesList() {
    if (categoryList == null) {
      Map<String, List<Long>> namedParameters = new HashMap<String, List<Long>>();
      categoryList = ArrayListMultimap.create();
      namedParameters.put("ids", productIds);
     List<Map<String,Object>> result =  getNamedParameterJdbcTemplate().queryForList(GET_PRODUCTS_CATEGORIES, namedParameters);
     for(Map<String,Object> productMap:result){
       
       categoryList.put((Long)productMap.get("PRODUCT_ID"), (Long)productMap.get("CATEGORY_ID"));
     }
    }
    return categoryList;
  }

  public static List<Map<String, Object>> getProductInfo (List<Long> ids) {
	  
	  //get IDs
	  Map<String, List<Long>> productInfoParam = new HashMap<String, List<Long>>();
	  
	  	productInfoParam.put("ids", ids);
	  List<Map<String, Object>> productInfo = getNamedParameterJdbcTemplate().queryForList(GET_PRODUCT_INFO, productInfoParam);
	  
	  
	  return productInfo;
  }
  
  public static List<Long> getProductCategories(Long id) {
   
    return (List<Long>) getCategoriesList().get(id);
  }

}
