package com.baidu.bkm.wiki.client.xmlrpc;

import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.baidu.bkm.wiki.client.xmlrpc.entity.RemoteAttachment;
import com.baidu.bkm.wiki.client.xmlrpc.entity.RemoteComment;
import com.baidu.bkm.wiki.client.xmlrpc.entity.RemotePage;
import com.baidu.bkm.wiki.client.xmlrpc.entity.RemoteSpace;
import com.baidu.bkm.wiki.client.xmlrpc.entity.RemoteSpaceAccessible;
import com.baidu.bkm.wiki.client.xmlrpc.entity.RemoteSpaceAdmin;
import com.baidu.bkm.wiki.client.xmlrpc.entity.RemoteUser;

/**
 * 通用的操作confluence的接口，利用默认用户。必须为不同的业务场景创建单独的帐号
 * @author wangye04 笨笨
 * @email wangye04@baidu.com
 * @datetime Nov 1, 2013 1:38:19 PM
 */
public interface BizClient {
	
	// indicates the version of the API being used:
	// https://developer.atlassian.com/display/CONFDEV/Confluence+XML-RPC+and+SOAP+APIs#ConfluenceXML-RPCandSOAPAPIs-XMLRPCInformation
	// public String CONFLUENCE_REMOTE_API_VERSION = "confluence1";
	String	CONFLUENCE_REMOTE_API_VERSION	= "confluence2";
	String	BKM_API_VERSION					= "bkm-xmlrpc";
	
	/**
	 * 生成对应的token，返回给用户
	 * @return
	 */
	String login();
	
	/**
	 * 退出系统
	 * @return
	 */
	void logout();
	
	/**
	 * 查询用户
	 */
	RemoteUser getUser(String username);
	
	/**
	 * 增加用户
	 */
	void addUser(RemoteUser user, String password);
	
	/**
	 * 用户是否存在
	 */
	boolean hasUser(String username);
	
	/**
	 * 更新远程的用户对象
	 * @param user
	 */
	boolean editUser(RemoteUser user);
	
	/**
	 * 修改登录名称
	 * @param oldUsername
	 * @param newUsername
	 */
	void renameUser(String oldUsername, String newUsername);
	
	/**
	 * 禁用某个用户(这里可以代替删除用户的功能）
	 * @param username
	 */
	boolean deactivateUser(String username);
	
	/**
	 * 重新激活某个用户
	 * @param username
	 */
	boolean reactivateUser(String username);
	
	/**
	 * 判断用户是否激活
	 * @param username
	 */
	boolean isActiveUser(String username);
	
	/**
	 * 查看所有激活状态的用户
	 * @param viewAll 是否可以查看所有的用户，
	 */
	Vector<String> getActiveUsers(boolean viewAll);
	
	/**
	 * 获取拥有系统管理角色的全部组
	 * @return
	 */
	Vector<String> getSystemAdminGroups();
	
	/**
	 * 获取拥有系统管理角色的全部用户
	 * @return
	 */
	Vector<String> getSystemAdminUsers();
	
	/**
	 * 获取系统中所有的用户
	 * @return
	 */
	Vector<String> getAllUsers(boolean viewAll);
	
	/**
	 * 删除用户
	 * @param username
	 * @return
	 */
	boolean removeUser(String username);
	
	/**
	 * @param userName
	 * @param fileName
	 * @param mimeType
	 * @param pictureData
	 */
	// boolean addProfilePicture(String token, String userName, String fileName, String mimeType, byte[] pictureData)
	// ;
	/**
	 * @param userInfo
	 */
	// boolean setUserInformation(String token, UserInfo userInfo) ;
	/**
	 * 用户组是否存在
	 * @param groupName
	 */
	boolean hasGroup(String groupName);
	
	/**
	 * 创建新组
	 */
	boolean addGroup(String groupName);
	
	/**
	 * 删除组，并且需要的话把原来组内的用户移动到另一个存在的组；（可以完成组的重命名）
	 * @param groupName 需要删除的组
	 * @param moveToGroupName 如果不为空，则原组删除之后的用户转移到这个组中去
	 */
	boolean removeGroup(String groupName, String moveToGroupName);
	
	/**
	 * 获取所有的组
	 */
	Vector<String> getGroups();
	
	/**
	 * 将用户添加到某个组中
	 * @param username
	 * @param groupName
	 */
	boolean addUserToGroup(String username, String groupName);
	
	/**
	 * 获取用户所在的组
	 * @param username
	 */
	Vector<String> getUserGroups(String username);
	
	/**
	 * 从组中移除用户
	 * @param username
	 * @param groupname
	 */
	boolean removeUserFromGroup(String username, String groupName);
	
	/**
	 * 从组中移除用户不发送事件
	 * @param username
	 * @param groupname
	 */
	boolean removeUserFromGroupNoEvent(String username, String groupName);
	
	/**
	 * 获取组中激活和非激活的用户
	 * @return
	 */
	Vector<String> getUsersInGroup(String groupname);
	
	/**
	 * @param space
	 * @return
	 */
	RemoteSpace addSpace(RemoteSpace space);
	
	/**
	 * 获取全部的空间列表
	 */
	Vector<RemoteSpace> getSpaces();
	
	/**
	 * @param spaceKey
	 * @return
	 */
	RemoteSpace getSpace(String spaceKey);
	
	/**
	 * @param spaceKey
	 * @return
	 */
	boolean removeSpace(String spaceKey);
	
	/**
	 * @param spaceKey
	 * @return
	 */
	String getSpaceStatus(String spaceKey);
	
	/**
	 * 获取某个space的管理员信息
	 * @param spaceKey
	 * @return
	 */
	public Vector<RemoteSpaceAdmin> getSpaceAdmins(String spaceKey);
	
	/**
	 * 获取空间查看权限类型的（用户数量、群组用户数量）统计信息
	 * @return
	 */
	public Vector<RemoteSpaceAccessible> getAllSpaceAccessible();
	
	/**
	 * 获取一个空间中所有的页面
	 * @param spaceKey
	 * @return
	 */
	Vector<RemotePage> getPages(String spaceKey);
	
	/**
	 * 获取一个页面
	 * @param pageId
	 * @return
	 */
	RemotePage getPage(long pageId);
	
	/**
	 * 获取一个confluence版本页面
	 * @param pageId
	 * @return
	 */
	RemotePage getRawPage(long pageId);
	
	/**
	 * 查看某个空间下的页面
	 * @param spaceKey
	 * @param pageTitle
	 * @return
	 */
	RemotePage getPage(String spaceKey, String pageTitle);
	
	/**
	 * 存储页面（如果id==0,则为创建，否则为更新）
	 * @param page
	 * @return
	 */
	RemotePage storePage(RemotePage page);
	
	/**
	 * 创建页面并修改创建人信息
	 * @param page
	 * @return
	 */
	RemotePage storePageAndChangeCreator(RemotePage page);
	
	/**
	 * 真实的删除页面
	 * @param pageId
	 */
	boolean removePage(long pageId);
	
	
	/**
	 * 删除页面版本
	 * @param username
	 * @return
	 */
	boolean removePageVersionByVersion(long pageId,int version);
	
	
	/**
	 * 将页面放入回收站
	 * @param pageId
	 * @return
	 */
	boolean trashPage(long pageId);
	
	/**
	 * @param labelNames 如果label中有空格或英文逗号，则会被拆分开当成多个标签
	 * @param pageId
	 * @return
	 */
	boolean addLabelByName(String labelNames, long pageId);
	
	/**
	 * 获取当前用户在空间中的权限，顶多就是四种：view,modify,comment,admin
	 * @param spaceKey
	 * @return
	 */
	Vector<String> getPermissions(String spaceKey);
	
	/**
	 * 获取用户在空间中的权限，顶多就是四种：view,modify,comment,admin
	 * @param spaceKey
	 * @return
	 */
	Vector<String> getPermissionsForUser(String spaceKey, String username);
	
	/**
	 * 获取空间权限的关键字
	 * @return
	 */
	Vector<String> getSpaceLevelPermissions();
	
	/**
	 * 创建评论
	 * @param comment
	 * @return
	 */
	RemoteComment addComment(RemoteComment comment);
	
	/**
	 * 创建评论并修改创建人信息
	 * @param page
	 * @return
	 */
	RemoteComment addCommentAndChangeCreator(RemoteComment comment);
	
	/**
	 * 增加附件
	 * @param contentId 所属宿主的id，比如page等
	 * @param remoteAttachment 存储对象
	 * @param attachmentData 存储数据
	 * @return
	 */
	RemoteAttachment addAttachment(long contentId, RemoteAttachment remoteAttachment, byte[] attachmentData);
	
	/**
	 * 通过实体id（page blogpost） 文件名称 ，版本 获取单个附件元数据 调用CONFLUENCE V2方法
	 * @param contentId
	 * @param fileName
	 * @param version
	 * @return
	 */
	RemoteAttachment getAttachment(long contentId, String fileName, int version);
	
	/**
	 * 根据实体id（page blogpost）获取此实体的所有附件 调用confluence v2 方法
	 * @param contentId
	 * @return
	 */
	Vector<RemoteAttachment> getAttachments(long contentId);
	
	/**
	 * <ul>
	 * <li>根据附件id 获取单个附件信息 自定义接口</li>
	 * <li>远程对象中增加 fileExtension，viewurl， lastModifyDate，latestVersion，spaceKey，pageId</li>
	 * </ul>
	 * @param contentId
	 * @param fileName
	 * @param version
	 * @return
	 */
	RemoteAttachment getAttachment(long attachmentId);
	
	/**
	 * <ul>
	 * <li>获取页面或者blog的所有最新版本附件 自定义接口</li>
	 * <li>远程对象中增加 fileExtension，viewurl， lastModifyDate，latestVersion，spaceKey，pageId</li>
	 * </ul>
	 * @param contentId
	 * @return
	 */
	public Vector<RemoteAttachment> getPageAttachments(long contentId);

    /**
     * 批量获取附件
     * @param contentIds
     * @return
     */
    public Map<Long,Vector<RemoteAttachment>> getPageAttachments(Vector<Long> contentIds);

	
	/**
	 * 获取附件的实际数据
	 * @param contentId
	 * @param fileName
	 * @param version
	 * @return
	 */
	byte[] getAttachmentData(long contentId, String fileName, int version);
	
	/**
	 * 删除附件
	 * @param contentId
	 * @param fileName
	 * @return
	 */
	boolean removeAttachment(long contentId, String fileName);
	
	/**
	 * 移动附件
	 * @param originalContentId
	 * @param originalFileName
	 * @param newContentId
	 * @param newFileName
	 * @return
	 */
	boolean moveAttachment(long originalContentId, String originalFileName, long newContentId, String newFileName);
	
	/**
	 * 获取在某个空间中有读空间权限的所有用户
	 * @param spaceKey
	 * @return 返回用户loginName列表
	 */
	Vector<String> getUsersWithSpacePermissions(String spaceKey);
	
	/**
	 * 获取在某个空间中有读空间权限的所有组
	 * @param spaceKey
	 * @return 返回组名列表
	 */
	Vector<String> getGroupsWithSpacePermissions(String spaceKey);
	
	/**
	 * 判断匿名用户是否可以访问当前空间
	 * @param spaceKey
	 * @return
	 */
	boolean hasSpacePermissionAsAnonymousUser(String spaceKey);
	
	/**
	 * 批量处理数据，提高性能！<br>
	 * 获取在某个空间中有读空间权限的所有用户
	 * @param spaceKeys
	 * @return 返回spaceKey和对应用户loginName列表的Map
	 */
	Map<String, Vector<String>> getUsersWithSpacePermissions(Vector<String> spaceKeys);
	
	/**
	 * 批量处理数据，提高性能！<br>
	 * 获取在某个空间中有读空间权限的所有组
	 * @param spaceKeys
	 * @return 返回spaceKey和对应组名列表的Map
	 */
	Map<String, Vector<String>> getGroupsWithSpacePermissions(Vector<String> spaceKeys);
	
	/**
	 * 批量处理数据，提高性能！<br>
	 * 获取空间是否支持匿名访问
	 * @param spaceKeys
	 * @return 返回spaceKey和对应组名列表的Map
	 */
	Map<String, Boolean> hasSpacePermissionAsAnonymousUser(Vector<String> spaceKeys);
	
	/**
	 * 对如下页面进行查询，看是否有访问方面的限制（用户和组列表）
	 * @param pageIds 因为xmlrpc不支持long数据类型的传递，因此转型成string传递过去
	 * @return map的第一个key为页面id，第二个key为groups或者users
	 */
	Map<Long, Map<String, Vector<String>>> getUsersAndGroupsWithPagePermissions(Vector<Long> pageIds);
	
	/**
	 * 对如下页面进行查询，看是否有访问方面的限制（用户和组列表）
	 * @param pageId 页面id
	 * @return map的key为groups或者users
	 */
	Map<String, Vector<String>> getUsersAndGroupsWithPagePermissions(long pageId);
	
	/**
	 * 获取全局有读空间权限的所有用户
	 * @return 返回用户loginName列表
	 */
	Vector<String> getUsersWithGlobalPermissions();
	
	
	/**
	 * 获取全局有读空间权限的所有组
	 * @return 返回组名列表
	 */
	Vector<String> getGroupsWithGlobalPermissions();
	
	/**
	 * 判断匿名用户是否可以访问整个系统
	 * @return
	 */
	boolean hasGlobalPermissionAsAnonymousUser();
	
	/**
	 * 删除用户拥有的所有权限并将用户从组中删除
	 * @param username
	 * @return
	 */
	boolean removeUserAllPermission(String username);
	
	
	/**
	 * 设置页面权限
	 * @return
	 */
	boolean setContentPermissions(long contentId, String permissionType, Vector<Hashtable<String, String>> permissions);
	
	
	/**
	 * 从页面删除权限
	 */
	boolean removePermissionFromPage(long contentId,String permissionType, Vector<Hashtable<String, String>> permissions);
	
}
