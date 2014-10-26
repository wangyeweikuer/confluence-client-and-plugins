package com.baidu.bkm.wiki.client.xmlrpc;

import java.util.Map;
import java.util.Vector;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.alibaba.fastjson.JSON;
import com.baidu.bkm.wiki.client.xmlrpc.entity.RemoteAttachment;
import com.baidu.bkm.wiki.client.xmlrpc.entity.RemotePage;
import com.baidu.bkm.wiki.client.xmlrpc.entity.RemoteSpace;
import com.baidu.bkm.wiki.client.xmlrpc.entity.RemoteUser;

/**
 * @author wangye04 笨笨
 * @email wangye04@baidu.com
 * @datetime Mar 18, 2014 2:29:51 PM
 */
@RunWith(JUnit4.class)
public class BizClient2Test {

    private BizClient client;
    private final RemoteUser user1 = new RemoteUser("testuser1", "testuser1", "testuser1");
    private final RemoteUser user2 = new RemoteUser("testuser2", "testuser2", "testuser2");
    private final String group1 = "testgroup1";
    private final String group2 = "testgroup2";
    private final String space1 = "testspace1";

    @Before
    public void init() throws Exception {
        // ClientSettings cs = new ClientSettings("http", "172.22.1.79", 8090);
        ClientSettings cs = new ClientSettings("http", "10.48.24.70", 8180);
        UserProvider userProvider = new UserProvider() {

            @Override
            public String getUsername() {
                return "admin";
            }

            @Override
            public String getPassword() {
                return "admin";
            }
        };
        client = new DefaultBizClient(cs, userProvider);
        // destroy();
        // client.addUser(user1, "p1");
        // client.addUser(user2, "p2");
        // client.addGroup(group1);
        // client.addGroup(group2);
        // client.addUserToGroup(user1.getName(), group1);
        // client.addUserToGroup(user2.getName(), group2);
        // client.addSpace(new RemoteSpace(space1, space1));
    }

    // @After
    public void destroy() throws Exception {
        try {
            client.deactivateUser(user1.getName());
        } catch (Exception e) {
        }
        try {
            client.deactivateUser(user2.getName());
        } catch (Exception e) {
        }
        try {
            client.removeGroup(group1, "");
        } catch (Exception e) {
        }
        try {
            client.removeGroup(group2, "");
        } catch (Exception e) {
        }
        try {
            client.removeSpace(space1);
        } catch (Exception e) {
        }
        Assert.assertFalse(client.hasUser(user1.getName()));
        client.logout();
        try {
            client.hasUser("yyyy");
        } catch (Exception e) {
        }
    }

    @Test
    public void login() throws Exception {
        String token = client.login();
        Assert.assertNotNull(token);
    }

    @Test
    public void hasUser() throws Exception {
        Assert.assertTrue(client.hasUser(user1.getName()));
        Assert.assertFalse(client.hasUser("yeshaotingxxxxxxxxx"));
    }

    @Test
    public void addUser() throws Exception {
        String name = "yyyyyyy";
        String passwd = name;
        RemoteUser user = new RemoteUser(name, name, name + "@baidu.com");
        Assert.assertFalse(client.hasUser(name));
        client.addUser(user, passwd);
        Assert.assertTrue(client.hasUser(name));
        RemoteUser nuser = client.getUser(name);
        System.err.println(JSON.toJSONString(nuser));
        client.deactivateUser(name);
    }

    @Test
    public void removeGroup() throws Exception {
        Vector<String> v1 = client.getUserGroups(user1.getName());
        Assert.assertTrue(v1.contains(group1));
        Vector<String> v2 = client.getUserGroups(user2.getName());
        Assert.assertTrue(v2.contains(group2));
        //
        client.removeGroup(group1, group2);
        //
        Assert.assertFalse(client.hasGroup(group1));
        Assert.assertTrue(client.hasGroup(group2));
        //
        v2 = client.getUserGroups(user1.getName());
        Assert.assertTrue(v2.contains(group2));
        //
    }

    @Test
    public void getGroups() throws Exception {
        Vector<String> vs = client.getGroups();
        int size = vs.size();
        Assert.assertTrue(size > 0);
        Assert.assertTrue(vs.contains("confluence-users"));
        //
        client.removeGroup(group1, "");
        vs = client.getGroups();
        Assert.assertEquals(size - 1, vs.size());
    }

    @Test
    public void removeUserFromGroup() throws Exception {
        Vector<String> vs = client.getUserGroups(user1.getName());
        Assert.assertTrue(vs.contains(group1));
        client.removeUserFromGroup(user1.getName(), group1);
        vs = client.getUserGroups(user1.getName());
        Assert.assertFalse(vs.contains(group1));
    }

    @Test
    public void addUserToGroup() throws Exception {
        boolean res = client.addUserToGroup(user1.getName(), group1);
        Assert.assertTrue(res);
        res = client.addUserToGroup(user1.getName(), group1);
        Assert.assertTrue(res);
        res = client.removeUserFromGroup(user1.getName(), group1);
        Assert.assertTrue(res);
    }

    @Test
    public void getPermissions() {
        String spaceKey = space1;
        RemoteSpace newspace = client.getSpace(spaceKey);
        System.err.println(JSON.toJSONString(newspace));
        Vector<String> p = client.getPermissions(spaceKey);
        System.err.println(p);
        p = client.getPermissionsForUser(spaceKey, "yeshaoting");
        System.err.println(p);
        p = client.getPermissionsForUser(spaceKey, "dongfangfang");
        System.err.println(p);
        p = client.getSpaceLevelPermissions();
        System.err.println(p);
        client.removeSpace(spaceKey);
    }

    // @Test
    public void testAttachments() {
        for (int i = 0; i < 10; i++) {
            long t1 = System.currentTimeMillis();
            long contentId = 819248l;
            Vector<RemoteAttachment> vs = client.getAttachments(contentId);
            // System.err.println(JSON.toJSONString(vs));
            for (RemoteAttachment r : vs) {
                byte[] bs = client.getAttachmentData(contentId, r.getFileName(), 1);
            }
            t1 = System.currentTimeMillis() - t1;
            System.err.println("download-" + i + ",time:" + t1);
        }
    }

    @Test
    public void getUsersWithPermissions() {
        String spaceKey = "test";
        try {
            Vector<String> users = client.getUsersWithSpacePermissions(spaceKey);
            System.err.println(users);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Vector<String> users = client.getGroupsWithSpacePermissions(spaceKey);
            System.err.println(users);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Vector<String> users = client.getGroupsWithGlobalPermissions();
            System.err.println(users);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Vector<String> users = client.getPermissions(spaceKey);
            System.err.println(users);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Vector<String> users = client.getUsersWithGlobalPermissions();
            System.err.println(users);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Vector<String> users = client.getPermissionsForUser(spaceKey, "admin");
            System.err.println(users);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getSystemInfos() {
        Vector<String> vs = client.getSystemAdminGroups();
        System.err.println(vs);
        vs = client.getSystemAdminUsers();
        System.err.println(vs);
    }

    // @Test
    public void getUsersWithPermissionsUsingSpaces() {
        Vector<String> spaceKeys = new Vector<String>();
        spaceKeys.add("2014");
        spaceKeys.add("CESJ");
        Map<String, Vector<String>> map = client.getGroupsWithSpacePermissions(spaceKeys);
        System.err.println(map);
        map = client.getUsersWithSpacePermissions(spaceKeys);
        System.err.println(map);
    }

    // @Test
    public void getUsersAndGroupsWithPagePermissions() {
        Vector<Long> pageIds = new Vector<Long>();
        pageIds.add(884929l);
        pageIds.add(3604488l);
        Map<Long, Map<String, Vector<String>>> map = client.getUsersAndGroupsWithPagePermissions(pageIds);
        System.err.println(map);
    }

    @Test
    public void getPages() {
        Vector<RemotePage> vs = client.getPages("2014");
        System.err.println(JSON.toJSONString(vs));
        System.err.println(vs.size());
    }

    @Test
    public void getPage() {
        RemotePage page = client.getPage(884764l);
        System.err.println(JSON.toJSONString(page));
    }
}
