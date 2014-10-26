package com.baidu.bkm.wiki.client.xmlrpc.entity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * date format
 * @author caizhicun 才志存
 * @email caizhicun@baidu.com
 * @datetime 2014-7-4 下午1:56:33
 */
public class DateUtils {
	
	public static String format(Date date) {
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");
		return sdf.format(date);
	}
}
