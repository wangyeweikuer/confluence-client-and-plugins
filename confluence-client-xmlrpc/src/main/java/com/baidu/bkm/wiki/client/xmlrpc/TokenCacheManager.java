package com.baidu.bkm.wiki.client.xmlrpc;

/**
 * 用来缓存token，减少持续登录的次数
 * 
 * @author wangye04 笨笨
 * @email wangye04@baidu.com
 * @datetime Mar 25, 2014 5:55:51 PM
 */
public abstract class TokenCacheManager {

    private String token;
    private long lastAccessed = -1;

    /**
     * 对某个固定的帐号，做一定的缓存，减少无用的登录
     * 
     * @return
     */
    protected String getToken() {
        long now = System.currentTimeMillis();
        if (token == null || now - lastAccessed > 60000) {// 一分钟缓存
            token = login();
            if (token == null) {
                throw new RuntimeException("Can't login() to get a valid token!");
            }
        }
        lastAccessed = now;
        return token;
    }

    /**
     * 对某个固定的帐号，做一定的缓存，减少无用的登录
     * 
     * @return
     */
    protected abstract String login();

    /**
     * 用户退出之后的事情
     */
    protected void afterLogout() {
        lastAccessed = -1;
        token = null;
    }
}
