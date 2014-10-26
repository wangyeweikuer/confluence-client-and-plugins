package com.baidu.bkm.wiki.client.xmlrpc;

/**
 * 获取需要登录的用户名和密码
 * @author wangye04 笨笨
 * @email wangye04@baidu.com
 * @datetime Mar 19, 2014 10:11:17 AM
 */
public interface UserProvider {
	
	String getUsername();
	
	String getPassword();
}
