package com.baidu.bkm.wiki.client.xmlrpc.entity;

/**
 * {@link com.atlassian.confluence.rpc.soap.beans.RemoteSpace}
 * @author wangye04 笨笨
 * @email wangye04@baidu.com
 * @datetime Nov 1, 2013 3:09:56 PM
 */
public class RemoteSpace extends RemoteBase {
	
	/** 主键，但不常用，主要为附件而用 */
	private long	id;
	/** 主键 */
	private String	key;
	/** 展示名 */
	private String	name;
	/** 类型 */
	private String	type;
	//
	/** 描述 */
	private String	description;
	private String	url;
	private Long	homePage;
	
	public RemoteSpace() {
	}
	
	public RemoteSpace(String key, String name) {
		this.key = key;
		this.name = name;
	}
	
	public RemoteSpace(String key, String name, String description) {
		this(key, name);
		this.description = description;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name.toLowerCase();
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public Long getHomePage() {
		return homePage;
	}
	
	public void setHomePage(Long homePage) {
		this.homePage = homePage;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
