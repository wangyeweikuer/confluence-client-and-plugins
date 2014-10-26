package com.baidu.bkm.wiki.client.xmlrpc;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.baidu.bkm.wiki.client.xmlrpc.entity.RemoteSpace;

/**
 * @author wangye04 笨笨
 * @email wangye04@baidu.com
 * @datetime Mar 18, 2014 2:29:51 PM
 */
@RunWith(JUnit4.class)
public class ClearUsersTest {

    private BizClient client;

    @Before
    public void init() throws Exception {
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
    }

    // @Test
    public void addUserToGroup() {
        // RemoteUser user = new RemoteUser("wangye04", "王烨", "wangye04@baidu.com");
        // client.addUser(user, "xx");
        System.err.println(client.addUserToGroup("wangye04", "confluence-administrators"));
        System.err.println(client.addUserToGroup("caizhicun", "confluence-administrators"));
    }

    // @Test
    public void clearUsers() throws InterruptedException {
        Vector<String> users = client.getAllUsers(true);
        ExecutorService es = Executors.newFixedThreadPool(30);
        List<Callable<Void>> cs = new ArrayList<Callable<Void>>();
        for (final String u : users) {
            if ("admin".equals(u)) {
                continue;
            }
            cs.add(new Callable<Void>() {

                @Override
                public Void call() {
                    try {
                        client.removeUser(u);
                    } catch (Exception e) {
                        System.out.println(" ======== " + u + " ========");
                        e.printStackTrace();
                    }
                    return null;
                }
            });
        }
        es.invokeAll(cs);
    }

    // @Test
    public void clearSpaces() throws InterruptedException {
        Vector<RemoteSpace> spaces = client.getSpaces();
        ExecutorService es = Executors.newFixedThreadPool(1);
        List<Callable<Void>> cs = new ArrayList<Callable<Void>>();
        for (final RemoteSpace space : spaces) {
            cs.add(new Callable<Void>() {

                @Override
                public Void call() {
                    try {
                        client.removeSpace(space.getKey());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            });
        }
        es.invokeAll(cs);
    }

    // @Test
    public void clearGroups() throws InterruptedException {
        Vector<String> groups = client.getGroups();
        ExecutorService es = Executors.newFixedThreadPool(20);
        List<Callable<Void>> cs = new ArrayList<Callable<Void>>();
        for (final String group : groups) {
            if ("confluence-administrators".equals(group) || "confluence-users".equals(group)) {
                continue;
            }
            cs.add(new Callable<Void>() {

                @Override
                public Void call() throws Exception {
                    try {
                        client.removeGroup(group, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            });
        }
        es.invokeAll(cs);
    }
}
