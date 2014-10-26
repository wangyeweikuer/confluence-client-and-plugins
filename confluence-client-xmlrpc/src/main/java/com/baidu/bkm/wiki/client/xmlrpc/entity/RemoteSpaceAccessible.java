package com.baidu.bkm.wiki.client.xmlrpc.entity;


import java.util.Hashtable;
import java.util.Vector;

/**
 * @author wangleifeng
 * @email wangleifeng@baidu.com
 * @datetime 14-7-29
 */
public class RemoteSpaceAccessible extends RemoteBase {

    private String spaceKey;//空间标识
    private String spaceName;//空间标识
    private Hashtable<String, Integer> groups;//空间有权限群组
    private Integer userCount;//单独授权用户数
    private Boolean anonymous;//匿名访问;

    public String getSpaceKey() {
        return spaceKey;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    public Hashtable<String, Integer> getGroups() {
        return groups;
    }

    public void setGroups(Hashtable<String, Integer> groups) {
        this.groups = groups;
    }

    public Integer getUserCount() {
        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    public Boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        this.anonymous = anonymous;
    }
}
