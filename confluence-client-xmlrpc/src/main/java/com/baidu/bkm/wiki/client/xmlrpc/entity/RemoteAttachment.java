package com.baidu.bkm.wiki.client.xmlrpc.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 远程附件对象
 * @author wangye04 笨笨
 * @email wangye04@baidu.com
 * @datetime Mar 20, 2014 3:54:10 PM
 */
public class RemoteAttachment extends RemoteBase {

	// 必须
	/** 未做urlencode的名称 */
	private String								fileName;
	private String								fileExtension;
	// 非必须
	private long								id;
	private int									version;
	/** 当前版本的创建时间 */
	private Date								created;
	private String								creator;
	/** 访问url */
	private String								viewurl;
	private Date								lastModifyDate;
	private int									latestVersion;
	private String								spaceKey;
	private long								pageId;
	private String								contentType;
	private String								comment;


	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {

			this.contentType = contentType;

	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getViewurl() {
		return viewurl;
	}

	public void setViewurl(String viewurl) {
		this.viewurl = viewurl;
	}

	public Date getLastModifyDate() {
		return lastModifyDate;
	}

	public void setLastModifyDate(Date lastModifyDate) {
		this.lastModifyDate = lastModifyDate;
	}

	public int getLatestVersion() {
		return latestVersion;
	}

	public void setLatestVersion(int latestVersion) {
		this.latestVersion = latestVersion;
	}

	public String getSpaceKey() {
		return spaceKey;
	}

	public void setSpaceKey(String spaceKey) {
		this.spaceKey = spaceKey;
	}

	public long getPageId() {
		return pageId;
	}

	public void setPageId(long pageId) {
		this.pageId = pageId;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}


}
