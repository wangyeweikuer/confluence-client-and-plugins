package com.baidu.bkm.wiki.client.xmlrpc;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.alibaba.fastjson.JSON;
import com.baidu.bkm.wiki.client.xmlrpc.entity.RemoteAttachment;
import com.baidu.bkm.wiki.client.xmlrpc.entity.RemotePage;
import com.baidu.bkm.wiki.client.xmlrpc.entity.RemoteSpace;

/**
 * @author wangye04 笨笨
 * @email wangye04@baidu.com
 * @datetime Mar 18, 2014 2:29:51 PM
 */
@RunWith(JUnit4.class)
public class InitSpacesAndPagesTest {

    private BizClient client;

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
    }

    @Test
    public void initSpaces2() throws InterruptedException {
        Vector<RemoteSpace> spaces = client.getSpaces();
        final int childrenLength = 5;
        final int level = 3;
        ExecutorService es = Executors.newFixedThreadPool(10);
        List<Callable<Void>> tasks = new ArrayList<Callable<Void>>();
        for (final RemoteSpace s : spaces) {
            tasks.add(new Callable<Void>() {

                @Override
                public Void call() throws Exception {
                    RemotePage page = client.getPage(s.getHomePage());
                    recursivelyCreateChildrenPages(s.getKey(), page, childrenLength, level);
                    return null;
                }
            });
        }
        es.invokeAll(tasks);
    }

    @Test
    public void initSpaces() throws InterruptedException {
        final int childrenLength = 5;
        ExecutorService es = Executors.newFixedThreadPool(100);
        List<Callable<Void>> tasks = new ArrayList<Callable<Void>>();
        int[][] a = new int[3][3];
        a[0] = new int[] { 1701, 1990, 2 };
        a[1] = new int[] { 1991, 1999, 4 };
        a[2] = new int[] { 2000, 2000, 5 };
        for (int i = 1701; i <= 2000; i++) {
            int level0 = -1;
            for (int k = 0; k < 3; k++) {
                if (a[k][0] <= i && a[k][1] >= i) {
                    level0 = a[k][2];
                    break;
                }
            }
            final int level = level0;
            final int ii = i;
            tasks.add(new Callable<Void>() {

                @Override
                public Void call() throws Exception {
                    String spaceKey = "SPACE" + ii;
                    RemoteSpace space = client.addSpace(new RemoteSpace(spaceKey, spaceKey, spaceKey));
                    List<RemotePage> pages = new ArrayList<RemotePage>();
                    long hp = space.getHomePage();
                    RemotePage page = client.getPage(hp);
                    page.setTitle(spaceKey + "_" + "page1");
                    page = client.storePage(page);
                    pages.add(page);
                    recursivelyCreateChildrenPages(spaceKey, page, childrenLength, level);
                    //
                    for (int k = 2; k <= childrenLength; k++) {
                        RemotePage page2 = new RemotePage();
                        page2.setSpace(spaceKey);
                        page2.setTitle(spaceKey + "_" + "page" + k);
                        page2.setParentId(0l);
                        page2.setContent("page" + k);
                        page2.setVersion(1);
                        page2 = client.storePage(page2);
                        pages.add(page2);
                        recursivelyCreateChildrenPages(spaceKey, page2, childrenLength, level);
                    }
                    return null;
                }
            });
        }
        //
        es.invokeAll(tasks);
    }

    void recursivelyCreateChildrenPages(String spaceKey, RemotePage topPage, int childrenLength, int level) {
        if (level <= 0) {
            return;
        }
        for (int k = 1; k <= childrenLength; k++) {
            RemotePage page2 = new RemotePage();
            page2.setSpace(spaceKey);
            page2.setTitle(topPage.getTitle() + "_" + "page" + k);
            page2.setParentId(topPage.getId());
            page2.setContent(page2.getTitle());
            page2.setVersion(1);
            page2 = client.storePage(page2);
            recursivelyCreateChildrenPages(spaceKey, page2, childrenLength, level - 1);
        }
    }

    @Test
    public void testGetSpace() {
        RemoteSpace space = client.getSpace("MVNO");
        System.err.println(space.getId());
        Vector<RemoteSpace> spaces = client.getSpaces();
        for (RemoteSpace rs : spaces) {
            System.err.println(rs.getId());
        }
    }

    @Test
    public void testGetAttachment() {
        Vector<RemoteAttachment> as = client.getAttachments(6160517l);
        for (RemoteAttachment ra : as) {
            System.err.println(JSON.toJSONString(ra));
        }
    }
}
