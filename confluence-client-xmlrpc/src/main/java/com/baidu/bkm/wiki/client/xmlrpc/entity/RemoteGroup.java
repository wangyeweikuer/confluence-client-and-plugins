package com.baidu.bkm.wiki.client.xmlrpc.entity;

/**
 * 远程用户 {@link com.atlassian.confluence.rpc.soap.beans.RemoteConfluenceUser}
 * @author wangye04 笨笨
 * @email wangye04@baidu.com
 * @datetime Nov 1, 2013 3:09:56 PM
 */
public class RemoteGroup extends RemoteBase {
	
	// 主键，必须小写
	private String	name;
	
	public RemoteGroup() {
	}
	
	public RemoteGroup(String name) {
		this.name = name.toLowerCase();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name.toLowerCase();
	}
}
