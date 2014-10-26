package com.baidu.bkm.wiki.client.xmlrpc.entity;

/**
 * 远程用户 {@link com.atlassian.confluence.rpc.soap.beans.RemoteConfluenceUser}
 * @author wangye04 笨笨
 * @email wangye04@baidu.com
 * @datetime Nov 1, 2013 3:09:56 PM
 */
public class RemoteUser extends RemoteBase {
	
	// 主键，必须小写
	private String	name;
	private String	fullname;
	private String	email;
	//
	private String	key;
	private String	url;
	
	public RemoteUser() {
	}
	
	public RemoteUser(String name, String fullname, String email) {
		this.name = name.toLowerCase();
		this.fullname = fullname;
		this.email = email;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name.toLowerCase();
	}
	
	public String getFullname() {
		return fullname;
	}
	
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
}
