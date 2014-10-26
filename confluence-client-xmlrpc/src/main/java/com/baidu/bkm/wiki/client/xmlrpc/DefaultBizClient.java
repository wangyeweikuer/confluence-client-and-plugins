package com.baidu.bkm.wiki.client.xmlrpc;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.apache.xmlrpc.XmlRpcClient;

import com.baidu.bkm.wiki.client.xmlrpc.entity.DateUtils;
import com.baidu.bkm.wiki.client.xmlrpc.entity.RemoteAttachment;
import com.baidu.bkm.wiki.client.xmlrpc.entity.RemoteBase;
import com.baidu.bkm.wiki.client.xmlrpc.entity.RemoteComment;
import com.baidu.bkm.wiki.client.xmlrpc.entity.RemotePage;
import com.baidu.bkm.wiki.client.xmlrpc.entity.RemoteSpace;
import com.baidu.bkm.wiki.client.xmlrpc.entity.RemoteSpaceAccessible;
import com.baidu.bkm.wiki.client.xmlrpc.entity.RemoteSpaceAdmin;
import com.baidu.bkm.wiki.client.xmlrpc.entity.RemoteUser;

/**
 * 默认的调用接口实现。非线程安全
 * 
 * @author wangye04 笨笨
 * @email wangye04@baidu.com
 * @datetime Nov 1, 2013 1:49:35 PM
 */
public class DefaultBizClient extends TokenCacheManager implements BizClient {

    private final XmlRpcClient client;
    private final UserProvider userProvider;

    /**
     * 定义rpc查询客户端
     * 
     * @param clientSettings rpc服务的对象
     * @param userProvider 提供访问系统的帐号信息
     */
    public DefaultBizClient(ClientSettings clientSettings, UserProvider userProvider) {
        this.client = new XmlRpcClient(clientSettings.getUrl());
        this.userProvider = userProvider;
    }

    @Override
    public String login() {
        return v2("login", userProvider.getUsername(), userProvider.getPassword());
    }

    /**
     * 将参数列表转化成vector
     * 
     * @param objs
     * @return
     */
    private Vector<Object> params(Object...objs) {
        Vector<Object> v = new Vector<Object>();
        for (Object o : objs) {
            if (o instanceof RemoteBase) {
                try {
                    v.add(((RemoteBase) o).toHashtable());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                continue;
            } else if (o.getClass().isArray()) {
                v.add(o);
            } else if (List.class.isAssignableFrom(o.getClass())) {
                v.add(o);
            } else if (Boolean.class.isAssignableFrom(o.getClass())) {
                v.add(o);
            } else if (long.class == o.getClass() || Long.class == o.getClass()) {
                // v.add(o + "");
                v.add(o);
            } else if (int.class == o.getClass() || Integer.class == o.getClass()) {
                v.add(o);
            } else {
                v.add(String.valueOf(o));
            }
        }
        return v;
    }

    /**
     * 将返回结果反射成需要的结果对象
     * 
     * @param clz
     * @param ht
     * @return
     */
    private <T extends RemoteBase> T returns(Class<T> clz, Hashtable<String, ?> ht) {
        try {
            return clz.newInstance().fromHashtable(ht);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将返回的结果列表反射成需要的对象列表
     * 
     * @param clz
     * @param vht
     * @return
     */
    private <T extends RemoteBase> Vector<T> returns(Class<T> clz, Vector<Hashtable<String, ?>> vht) {
        Vector<T> vt = new Vector<T>();
        for (Hashtable<String, ?> ht : vht) {
            vt.add(returns(clz, ht));
        }
        return vt;
    }

    /**
     * v2 版本的xmlrpc调用
     * 
     * @param method
     * @param params
     * @return @
     */
    private <T> T v2(String method, Object...params) {
        return call(CONFLUENCE_REMOTE_API_VERSION, method, params);
    }

    /**
     * bkm 版本的xmlrpc调用
     * 
     * @param method
     * @param params
     * @return
     * @return @
     */
    private <T> T bkm(String method, Object...params) {
        return call(BKM_API_VERSION, method, params);
    }

    /**
     * 调用不同的版本
     * 
     * @param method
     * @param params
     * @return
     */
    @SuppressWarnings("unchecked")
    private <T> T call(String apiVersion, String method, Object...params) {
        params = autoAddToken(method, params);
        try {
            return (T) client.execute(apiVersion + "." + method, params(params));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 目前任何方法都需要带token，除了login()方法，因此自动给方法参数中加上token
     * 
     * @param method
     * @param params
     * @return
     */
    private Object[] autoAddToken(String method, Object...params) {
        if (method.equals("login")) {
            return params;
        }
        Object[] ps = new Object[params == null ? 1 : params.length + 1];
        ps[0] = getToken();
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                ps[i + 1] = params[i];
            }
        }
        return ps;
    }

    @Override
    public void logout() {
        v2("logout");
        afterLogout();
    }

    @Override
    public RemoteUser getUser(String username) {
        Hashtable<String, ?> ht = v2("getUser", username);
        return returns(RemoteUser.class, ht);
    }

    @Override
    public void addUser(RemoteUser user, String password) {
        v2("addUser", user, password);
    }

    @Override
    public void renameUser(String oldUsername, String newUsername) {
        v2("renameUser", oldUsername, newUsername);
    }

    @Override
    public boolean hasUser(String username) {
        return v2("hasUser", username);
    }

    @Override
    public boolean editUser(RemoteUser user) {
        return v2("editUser", user);
    }

    @Override
    public boolean isActiveUser(String username) {
        return v2("isActiveUser", username);
    }

    @Override
    public Vector<String> getActiveUsers(boolean viewAll) {
        return v2("getActiveUsers", viewAll);
    }

    @Override
    public boolean deactivateUser(String username) {
        return v2("deactivateUser", username);
    }

    @Override
    public boolean reactivateUser(String username) {
        return v2("reactivateUser", username);
    }

    @Override
    public boolean hasGroup(String groupName) {
        return v2("hasGroup", groupName);
    }

    @Override
    public boolean addGroup(String groupName) {
        return v2("addGroup", groupName);
    }

    @Override
    public boolean removeGroup(String groupName, String moveToGroupName) {
        if (StringUtils.isBlank(moveToGroupName)) {
            moveToGroupName = "";
        }
        return v2("removeGroup", groupName, moveToGroupName);
    }

    @Override
    public Vector<String> getGroups() {
        return v2("getGroups");
    }

    // 使用自定义的增加用户到组的方法,来处发监听
    @Override
    public boolean addUserToGroup(String username, String groupName) {
        return v2("addUserToGroup", username, groupName);
        // return bkm("addUserToGroup", username, groupName);
    }

    @Override
    public Vector<String> getUserGroups(String username) {
        return v2("getUserGroups", username);
    }

    // 使用自定义的从组中删除用户,来处发监听
    @Override
    public boolean removeUserFromGroup(String username, String groupName) {
        return v2("removeUserFromGroup", username, groupName);
        // return bkm("removeUserFromGroup", username, groupName);
    }

    @Override
    public boolean removeUserFromGroupNoEvent(String username, String groupName) {
        return v2("removeUserFromGroup", username, groupName);
    }

    @Override
    public RemoteSpace addSpace(RemoteSpace space) {
        Hashtable<String, ?> ht = v2("addSpace", space);
        return returns(RemoteSpace.class, ht);
    }

    @Override
    public RemoteSpace getSpace(String spaceKey) {
        // Hashtable<String, ?> ht = v2("getSpace", spaceKey); //信息不够完整
        Hashtable<String, ?> rs = bkm("getSpace", spaceKey);
        return returns(RemoteSpace.class, rs);
    }

    @Override
    public Vector<RemoteSpace> getSpaces() {
        // Vector<Hashtable<String, ?>> vs = v2("getSpaces");//信息不够完整
        Vector<Hashtable<String, ?>> vs = bkm("getSpaces");
        return returns(RemoteSpace.class, vs);
    }

    @Override
    public boolean removeSpace(String spaceKey) {
        return v2("removeSpace", spaceKey);
    }

    @Override
    public String getSpaceStatus(String spaceKey) {
        return v2("getSpaceStatus", spaceKey);
    }

    @Override
    public Vector<RemoteSpaceAdmin> getSpaceAdmins(String spaceKey) {
        Vector<Hashtable<String, ?>> vectors = bkm("getSpaceAdmins", spaceKey);
        return returns(RemoteSpaceAdmin.class, vectors);
    }

    // 使用自定义的获取页面完整信息
    @Override
    public Vector<RemotePage> getPages(String spaceKey) {
        // Vector<Hashtable<String, ?>> vs = v2("getPages", spaceKey);
        Vector<Hashtable<String, ?>> vs = bkm("getPages", spaceKey);
        return returns(RemotePage.class, vs);
    }

    @Override
    public RemotePage storePage(RemotePage page) {
        Hashtable<String, ?> rp = v2("storePage", page);
        return returns(RemotePage.class, rp);
    }

    @Override
    public RemotePage storePageAndChangeCreator(RemotePage page) {
        RemotePage newPage = storePage(page);
        // 修正页面的创建人和创建时间
        bkm("modifyPageInfo", newPage.getId() + "", page.getCreator(), DateUtils.format(page.getCreated()));
        return newPage;
    }

    @Override
    public Vector<String> getPermissions(String spaceKey) {
        return v2("getPermissions", spaceKey);
    }

    @Override
    public Vector<String> getPermissionsForUser(String spaceKey, String username) {
        return v2("getPermissionsForUser", spaceKey, username);
    }

    @Override
    public Vector<String> getSpaceLevelPermissions() {
        return v2("getSpaceLevelPermissions");
    }

    @Override
    public RemoteAttachment addAttachment(long contentId, RemoteAttachment remoteAttachment, byte[] attachmentData) {
        Hashtable<String, ?> rs = v2("addAttachment", contentId + "", remoteAttachment, attachmentData);// 不支持long型id
        return returns(RemoteAttachment.class, rs);
    }

    @Override
    public RemoteAttachment getAttachment(long contentId, String fileName, int version) {
        return v2("getAttachment", contentId + "", fileName, version);// 不支持long型id
    }

    @Override
    public RemoteAttachment getAttachment(long attachmentId) {
        Hashtable<String, ?> rs = bkm("getAttachment", attachmentId + "");
        return returns(RemoteAttachment.class, rs);
    }

    @Override
    public Vector<RemoteAttachment> getPageAttachments(long contentId) {
        Vector<Hashtable<String, ?>> vs = bkm("getPageAttachments", contentId + "");// 不支持long型id
        return returns(RemoteAttachment.class, vs);
    }

    /**
     * 批量获取附件
     * 
     * @param contentIds
     * @return
     */
    @Override
    public Map<Long, Vector<RemoteAttachment>> getPageAttachments(Vector<Long> contentIds) {
        Vector<String> pageIds = new Vector<String>();
        for (Long id : contentIds) {
            pageIds.add(id + "");
        }
        Hashtable<String, Vector<Hashtable<String, ?>>> hashtable = bkm("getPageAttachments", pageIds);
        HashMap<Long, Vector<RemoteAttachment>> result = new HashMap<Long, Vector<RemoteAttachment>>();
        for (Long pageId : contentIds) {
            Vector<Hashtable<String, ?>> vs = hashtable.get(pageId + "");
            if (vs != null) {
                result.put(pageId, returns(RemoteAttachment.class, vs));
            }
        }
        return result;
    }

    @Override
    public Vector<RemoteAttachment> getAttachments(long contentId) {
        Vector<Hashtable<String, ?>> vs = v2("getAttachments", contentId + "");// 不支持long型id
        return returns(RemoteAttachment.class, vs);
    }

    @Override
    public byte[] getAttachmentData(long contentId, String fileName, int version) {
        return v2("getAttachmentData", contentId + "", fileName, version + "");// 不支持long型id,不支持int的version
    }

    @Override
    public boolean removeAttachment(long contentId, String fileName) {
        return v2("removeAttachment", contentId + "", fileName);// 不支持long型id
    }

    @Override
    public boolean moveAttachment(long originalContentId, String originalFileName, long newContentId, String newFileName) {
        return v2("moveAttachment", originalContentId + "", originalFileName, newContentId + "", newFileName);// 不支持long型id
    }

    @Override
    public Vector<String> getUsersWithSpacePermissions(String spaceKey) {
        return bkm("getUsersWithPermissions", spaceKey);
    }

    @Override
    public Vector<String> getGroupsWithSpacePermissions(String spaceKey) {
        return bkm("getGroupsWithPermissions", spaceKey);
    }

    @Override
    public Map<String, Vector<String>> getUsersWithSpacePermissions(Vector<String> spaceKeys) {
        // TODO 分页查询
        return bkm("getUsersWithPermissions", spaceKeys);
    }

    @Override
    public Map<String, Vector<String>> getGroupsWithSpacePermissions(Vector<String> spaceKeys) {
        // TODO 分页查询
        return bkm("getGroupsWithPermissions", spaceKeys);
    }

    @Override
    public Map<Long, Map<String, Vector<String>>> getUsersAndGroupsWithPagePermissions(Vector<Long> pageIds) {
        // TODO 必要时分页处理
        Vector<String> pageIdStrs = new Vector<String>();// xmlrpc后端接口不支持long类型的vector的pageIds
        for (Long pid : pageIds) {
            pageIdStrs.add(pid + "");
        }
        Map<String, Map<String, Vector<String>>> map = bkm("getUsersAndGroupsWithPagePermissions", pageIdStrs);
        Map<Long, Map<String, Vector<String>>> rmap = new HashMap<Long, Map<String, Vector<String>>>();
        for (Entry<String, Map<String, Vector<String>>> e : map.entrySet()) {
            Long pid = Long.valueOf(e.getKey());
            rmap.put(pid, e.getValue());
        }
        return rmap;
    }

    @Override
    public Map<String, Vector<String>> getUsersAndGroupsWithPagePermissions(long pageId) {
        Vector<Long> pageIds = new Vector<Long>();
        pageIds.add(pageId);
        Map<Long, Map<String, Vector<String>>> map = getUsersAndGroupsWithPagePermissions(pageIds);
        return map.containsKey(pageId) ? map.get(pageId) : new HashMap<String, Vector<String>>();
    }

    @Override
    public Vector<String> getUsersWithGlobalPermissions() {
        return bkm("getUsersWithGlobalPermissions");
    }

    @Override
    public Vector<String> getGroupsWithGlobalPermissions() {
        return bkm("getGroupsWithGlobalPermissions");
    }

    /**
     * 判断匿名用户是否可以访问当前空间
     * 
     * @param spaceKey
     * @return
     */
    @Override
    public boolean hasSpacePermissionAsAnonymousUser(String spaceKey) {
        return bkm("hasSpacePermissionAsAnonymousUser", spaceKey);
    }

    @Override
    public Map<String, Boolean> hasSpacePermissionAsAnonymousUser(Vector<String> spaceKeys) {
        return bkm("hasSpacePermissionAsAnonymousUser", spaceKeys);
    }

    /**
     * 判断匿名用户是否可以访问整个系统
     * 
     * @return
     */
    @Override
    public boolean hasGlobalPermissionAsAnonymousUser() {
        return bkm("hasGlobalPermissionAsAnonymousUser");
    }

    @Override
    public Vector<String> getSystemAdminGroups() {
        return bkm("getSystemAdminGroups");
    }

    @Override
    public Vector<String> getSystemAdminUsers() {
        return bkm("getSystemAdminUsers");
    }

    @Override
    public Vector<String> getAllUsers(boolean viewAll) {
        return v2("getActiveUsers", viewAll);
    }

    @Override
    public Vector<String> getUsersInGroup(String groupname) {
        return bkm("getUsersInGroup", groupname);
    }

    @Override
    public Vector<RemoteSpaceAccessible> getAllSpaceAccessible() {
        Vector<Hashtable<String, ?>> vector = bkm("getAllSpaceAccessible");
        return returns(RemoteSpaceAccessible.class, vector);
    }

    // 使用自定义的获取页面完整信息
    @Override
    public RemotePage getPage(long pageId) {
        // Hashtable<String, ?> rp = v2("getPage", pageId);
        Hashtable<String, ?> rp = bkm("getPage", String.valueOf(pageId));
        return returns(RemotePage.class, rp);
    }

    // confluence提供的取得页面的方法
    @Override
    public RemotePage getRawPage(long pageId) {
        Hashtable<String, ?> rp = v2("getPage", String.valueOf(pageId));
        return returns(RemotePage.class, rp);
    }

    /**
     * 删除用户拥有的所有权限并将用户从组中删除
     * 
     * @param username
     * @return
     */
    @Override
    public boolean removeUserAllPermission(String username) {
        return bkm("removeUserAllPermission", username);
    }

    @Override
    public boolean removeUser(String username) {
        return v2("removeUser", username);
    }

    @Override
    public RemotePage getPage(String spaceKey, String pageTitle) {
        Hashtable<String, ?> rp = bkm("getPage", spaceKey, pageTitle);
        return returns(RemotePage.class, rp);
    }

    @Override
    public RemoteComment addComment(RemoteComment comment) {
        Hashtable<String, ?> rp = v2("addComment", comment);
        return returns(RemoteComment.class, rp);
    }

    @Override
    public RemoteComment addCommentAndChangeCreator(RemoteComment comment) {
        RemoteComment newComment = addComment(comment);
        // 修正评论的创建人和创建时间
        bkm("modifyCommentInfo", newComment.getId() + "", comment.getCreator(), DateUtils.format(comment.getCreated()));
        return newComment;
    }

    @Override
    public boolean removePage(long pageId) {
        return bkm("removePageCompletely", pageId + "");// v2中不支持强制删除
    }

    @Override
    public boolean trashPage(long pageId) {
        return v2("removePage", pageId + "");
    }

    @Override
    public boolean addLabelByName(String labelNames, long pageId) {
        return v2("addLabelByName", labelNames, pageId + "");
    }

    @Override
    public boolean setContentPermissions(long contentId, String permissionType,
            Vector<Hashtable<String, String>> remoteContentPermission) {
        return v2("setContentPermissions", contentId + "", permissionType, remoteContentPermission);
    }

    @Override
    public boolean removePageVersionByVersion(long pageId, int version) {
        return v2("removePageVersionByVersion", pageId + "", version);
    }

    @Override
    public boolean removePermissionFromPage(long contentId, String permissionType,
            Vector<Hashtable<String, String>> permissions) {

        return false;
    }
}
