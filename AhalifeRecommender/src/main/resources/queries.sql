/* Registered Checkouts with # > X */
		select bc.CUSTOMER_ID,bp.PRODUCT_ID,count(boi.ORDER_ITEM_ID)  from blc_customer bc
		join blc_order bo on bo.CUSTOMER_ID = bc.CUSTOMER_ID 
		join blc_order_item boi on boi.ORDER_ID = bo.ORDER_ID
		join blc_discrete_order_item bdoi on bdoi.ORDER_ITEM_ID = boi.ORDER_ITEM_ID 
		join blc_product bp on bp.PRODUCT_ID = bdoi.PRODUCT_ID 
		where bc.EMAIL_ADDRESS not like '%ahalife.com'
			/* and bc.IS_REGISTERED = true */
	     	and bo.ORDER_STATUS ='SUBMITTED'
	     	and bc.CUSTOMER_ID 
			in (
				select w.CUSTOMER_ID from 
					(select bc.CUSTOMER_ID,bp.PRODUCT_ID  from blc_customer bc
						join blc_order bo on bo.CUSTOMER_ID = bc.CUSTOMER_ID 
			     		join blc_order_item boi on boi.ORDER_ID = bo.ORDER_ID
			     		join blc_discrete_order_item bdoi on bdoi.ORDER_ITEM_ID = boi.ORDER_ITEM_ID 
			     		join blc_product bp on bp.PRODUCT_ID = bdoi.PRODUCT_ID 
			     			where bc.EMAIL_ADDRESS not like '%ahalife.com'
			     				/* and bc.IS_REGISTERED = true */
			     				and bo.ORDER_STATUS ='SUBMITTED'
								Group by bc.CUSTOMER_ID having count(bp.PRODUCT_ID) > 2) 
			as w ) 
		group by bc.CUSTOMER_ID,bp.PRODUCT_ID  order by bc.CUSTOMER_ID 
        
		
 /* Registered Add-To-Bags with # > X */
		select bc.CUSTOMER_ID,bp.PRODUCT_ID,count(boi.ORDER_ITEM_ID)  from blc_customer bc
		join blc_order bo on bo.CUSTOMER_ID = bc.CUSTOMER_ID 
		join blc_order_item boi on boi.ORDER_ID = bo.ORDER_ID
		join blc_discrete_order_item bdoi on bdoi.ORDER_ITEM_ID = boi.ORDER_ITEM_ID 
		join blc_product bp on bp.PRODUCT_ID = bdoi.PRODUCT_ID 
		where bc.EMAIL_ADDRESS not like '%ahalife.com'
			/* and bc.IS_REGISTERED = true */
	     	and bo.ORDER_STATUS ='IN_PROCESS'
	     	and bc.CUSTOMER_ID 
			in (
				select w.CUSTOMER_ID from 
					(select bc.CUSTOMER_ID,bp.PRODUCT_ID  from blc_customer bc
						join blc_order bo on bo.CUSTOMER_ID = bc.CUSTOMER_ID 
			     		join blc_order_item boi on boi.ORDER_ID = bo.ORDER_ID
			     		join blc_discrete_order_item bdoi on bdoi.ORDER_ITEM_ID = boi.ORDER_ITEM_ID 
			     		join blc_product bp on bp.PRODUCT_ID = bdoi.PRODUCT_ID 
			     			where bc.EMAIL_ADDRESS not like '%ahalife.com'
			     				/* and bc.IS_REGISTERED = true */
			     				and bo.ORDER_STATUS ='IN_PROCESS'
			     				
								Group by bc.CUSTOMER_ID having count(bp.PRODUCT_ID) > 2) 
			as w ) 
			group by bc.CUSTOMER_ID,bp.PRODUCT_ID  order by bc.CUSTOMER_ID 

/* Unregistered can't be used at this time, because they can't be clubbed by customer. */
	
		
select bcpr.CATEGORY_ID,bcpr.PRODUCT_ID from blc_product bp 
	join blc_category_product_xref bcpr using (PRODUCT_ID) 
	where bp.PRODUCT_ID in (1031539)
        
select bp.product_id, bs.name, bp.url from blc_product bp 
	join blc_sku bs on bp.`PRODUCT_ID`=bs.`DEFAULT_PRODUCT_ID`
	where bp.`PRODUCT_ID`= 1031539 ;