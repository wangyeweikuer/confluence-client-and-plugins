package com.baidu.bkm.wiki.client.xmlrpc.entity;

import java.util.Date;
import java.util.Vector;

/**
 * 服务器端页面
 * @author wangye04 笨笨
 * @email wangye04@baidu.com
 * @datetime Nov 1, 2013 2:02:51 PM
 */
public class RemotePage extends RemoteBase {
	
	private long			id;
	private String			space;
	private String			title;
	private String			url;
	private long			parentId;
	private int				version;
	private String			content;
	private Date			created;
	private String			creator;
	private Date			modified;
	private String			modifier;
	private Vector<String>	label;
	private Vector<String>	users;
	private Vector<String>	groups;
	private String			contentType;
	
	public Vector<String> getUsers() {
		return users;
	}
	
	public void setUsers(Vector<String> users) {
		this.users = users;
	}
	
	public Vector<String> getGroups() {
		return groups;
	}
	
	public void setGroups(Vector<String> groups) {
		this.groups = groups;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public Date getCreated() {
		return created;
	}
	
	public void setCreated(Date created) {
		this.created = created;
	}
	
	public String getCreator() {
		return creator;
	}
	
	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	public Date getModified() {
		return modified;
	}
	
	public void setModified(Date modified) {
		this.modified = modified;
	}
	
	public String getModifier() {
		return modifier;
	}
	
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}
	
	public Vector<String> getLabel() {
		return label;
	}
	
	public void setLabel(Vector<String> label) {
		this.label = label;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public Long getParentId() {
		return parentId;
	}
	
	public void setParentId(Long parentId) {
		this.parentId = parentId;
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
	
	public int getVersion() {
		return version;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}
	
	public String getSpace() {
		return space;
	}
	
	public void setSpace(String space) {
		this.space = space;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}
