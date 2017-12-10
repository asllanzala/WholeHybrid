package com.honeywell.wholesale;

import android.app.Application;
import android.content.Context;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.android.volley.Response;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.framework.utils.FileManager;

import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.CountDownLatch;

/**
 * Created by xiaofei on 7/8/16.
 *
 */
public class DownloadTest extends ApplicationTestCase<Application> {
    private static String downloadPath = "/storage/emulated/0/Wholesale/html/okHttp.zip";

    CountDownLatch signal = null;
    public DownloadTest() {
        super(Application.class);
    }

    @Override
    public void setUp() throws Exception {
        createApplication();
        signal = new CountDownLatch(1);
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetDownloadFolder() throws Exception{
        String path = FileManager.getHtmlFolder();
        Log.e("DownloadTest", path);
        assertNotNull(path);
    }

    public void testDownloadFile() throws Exception{
        Context context = getContext();
        String downloadUrl = "https://github.com/square/okhttp/archive/master.zip";
        String fileName = "okHttp.zip";
        String path = FileManager.getHtmlFolder();
        assertNotNull(path);
        assertNotNull(context);
        FileManager.downloadHtmlFile(context, downloadUrl, fileName);

        try {
            signal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void testUnzipDownloadFile() throws Exception{
        FileManager.unzip("/storage/emulated/0/Wholesale/html/okHttp.zip", "/storage/emulated/0/Wholesale/html/");
        File file  = new File("/storage/emulated/0/Wholesale/html/okHttp");
        assertTrue(file.exists());
    }

    public void testRemoveUnzipFile() throws Exception{
        FileManager.removeFolder("/storage/emulated/0/Wholesale/html/okhttp-master");
        File file  = new File("/storage/emulated/0/Wholesale/html/okhttp-master/");
        assertFalse(file.exists());
    }

    public void testMoveAssetsToSDCard() throws Exception{
        Context context = getContext();
        FileManager.moveAssets2HtmlPath(context, "");
    }

    public void testUpgradeApp() throws Exception{
        WebClient webClient = new WebClient();
        webClient.httpCheckAppUpgrade(1, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {

            }
        });

        try {
            signal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
