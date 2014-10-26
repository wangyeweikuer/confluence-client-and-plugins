package com.baidu.bkm.wiki.client.xmlrpc.entity;

/**
 * 空间管理员信息（用户或者组）
 * @author wangleifeng
 * @email wangleifeng@baidu.com
 * @datetime 14-7-4
 */
public class RemoteSpaceAdmin extends RemoteBase{
    private String name;
    private String fullName;
    private String email;
    private String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
