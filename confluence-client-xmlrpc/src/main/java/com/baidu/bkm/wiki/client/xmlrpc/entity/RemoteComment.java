package com.baidu.bkm.wiki.client.xmlrpc.entity;

import java.util.Date;

/**
 * 评论信息
 * @author caizhicun 才志存
 * @email caizhicun@baidu.com
 * @datetime 2014-6-19 上午11:41:56
 */
public class RemoteComment extends RemoteBase {
	
	private long	id;
	private long	pageId;
	private long	parentId	= 0;
	private Date	created;
	private Date	modified;
	private String	title;
	private String	url;
	private String	creator;
	private String	content;
	private String	modifier;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public long getPageId() {
		return pageId;
	}
	
	public void setPageId(long pageId) {
		this.pageId = pageId;
	}
	
	public long getParentId() {
		return parentId;
	}
	
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	
	public Date getCreated() {
		return created;
	}
	
	public void setCreated(Date created) {
		this.created = created;
	}
	
	public Date getModified() {
		return modified;
	}
	
	public void setModified(Date modified) {
		this.modified = modified;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getCreator() {
		return creator;
	}
	
	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getModifier() {
		return modifier;
	}
	
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}
}
