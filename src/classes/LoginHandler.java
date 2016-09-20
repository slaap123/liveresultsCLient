package classes;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author woutermkievit
 */
import gui.AtletiekNuPanel;
import classes.ParFile;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LoginHandler {

    private String cookies;
    private HttpClient client = HttpClientBuilder.create().build();
    private final String USER_AGENT = "Mozilla/5.0";
    private String nuid;

    public LoginHandler(String nuid) throws Exception {
        String url = "https://www.atletiek.nu/login/";
        this.nuid = nuid;

        // make sure cookies is turn on
        CookieHandler.setDefault(new CookieManager());

        String page = this.GetPageContent(url);

        List<NameValuePair> postParams
                = this.getFormParams(page, "wouterkievit@gmail.com", "deen123");
        this.sendPost(url, postParams);
    }

    public void getZip(String id) throws Exception {
        String url = "https://www.atletiek.nu/feeder.php?page=exportstartlijstentimetronics&event_id=" + nuid+"&forceAlleenGeprinteLijsten=1";
        System.out.println(url);
        HttpGet request = new HttpGet(url);

        request.setHeader("User-Agent", USER_AGENT);
        request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        request.setHeader("Accept-Language", "en-US,en;q=0.5");
        request.setHeader("Cookie", getCookies());

        HttpResponse response = client.execute(request);
        int responseCode = response.getStatusLine().getStatusCode();

        System.out.println("Response Code : " + responseCode);

        BufferedInputStream bis = new BufferedInputStream(response.getEntity().getContent());
        String filePath = "tmp.zip";
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
        int inByte;
        while ((inByte = bis.read()) != -1) {
            bos.write(inByte);
        }
        bis.close();
        bos.close();

        // set cookies
        setCookies(response.getFirstHeader("Set-Cookie") == null ? "" : response.getFirstHeader("Set-Cookie").toString());
        request.releaseConnection();
    }

    public void submitResults(ArrayList<ParFile> files) throws Exception {
        String url = "https://www.atletiek.nu/feeder.php?page=resultateninvoer&do=uploadresultaat&event_id=" + nuid + "";
        System.out.println(url);
        HttpPost post = new HttpPost(url);

        post.setHeader("User-Agent", USER_AGENT);
        post.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        post.setHeader("Accept-Language", "en-US,en;q=0.5");
        post.setHeader("Cookie", getCookies());
        MultipartEntity mpEntity = new MultipartEntity();
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i).resultFile;
            ContentBody cbFile = new FileBody(file, "text/plain");
            mpEntity.addPart("files", cbFile);
        }
        post.setEntity(mpEntity);

        HttpResponse response = client.execute(post);
        int responseCode = response.getStatusLine().getStatusCode();

        System.out.println("Response Code submit: " + responseCode);
        HttpEntity responseEntity = response.getEntity();
        String responseString="";
        if (responseEntity != null) {
            responseString = EntityUtils.toString(responseEntity);
        }
        JSONObject obj=new JSONObject(responseString);
        for(Object FileObj: (JSONArray)obj.get("files")){
            JSONObject JSONFile=(JSONObject)FileObj;
            AtletiekNuPanel.panel.jTextPane1.setText("Uploaded "+JSONFile.get("name")+" met "+JSONFile.get("totaalverwerkt")+" atleten");
        }
        // set cookies
        setCookies(response.getFirstHeader("Set-Cookie") == null ? "" : response.getFirstHeader("Set-Cookie").toString());
        post.releaseConnection();
        //getZip(nuid);
    }

    private String sendPost(String url, List<NameValuePair> postParams)
            throws Exception {

        HttpPost post = new HttpPost(url);

        // add header
        post.setHeader("User-Agent", USER_AGENT);
        post.setHeader("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        post.setHeader("Accept-Language", "en-US,en;q=0.5");
        post.setHeader("Cookie", getCookies());
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");

        post.setEntity(new UrlEncodedFormEntity(postParams));

        HttpResponse response = client.execute(post);

        int responseCode = response.getStatusLine().getStatusCode();

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        post.releaseConnection();
        return result.toString();
        //System.out.println(result.toString());

    }

    private String GetPageContent(String url) throws Exception {

        HttpGet request = new HttpGet(url);

        request.setHeader("User-Agent", USER_AGENT);
        request.setHeader("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        request.setHeader("Accept-Language", "en-US,en;q=0.5");
        request.setHeader("Cookie", getCookies());

        HttpResponse response = client.execute(request);
        int responseCode = response.getStatusLine().getStatusCode();

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        // set cookies
        setCookies(response.getFirstHeader("Set-Cookie") == null ? ""
                : response.getFirstHeader("Set-Cookie").toString());
        request.releaseConnection();
        return result.toString();

    }

    public List<NameValuePair> getFormParams(
            String html, String username, String password)
            throws UnsupportedEncodingException {

        Document doc = Jsoup.parse(html);

        // Google form id
        Element loginform = doc.getElementById("primarycontent");
        Elements inputElements = loginform.getElementsByTag("input");

        List<NameValuePair> paramList = new ArrayList<NameValuePair>();

        for (Element inputElement : inputElements) {
            String key = inputElement.attr("name");
            String value = inputElement.attr("value");

            if (key.equals("email")) {
                value = username;
            } else if (key.equals("password")) {
                value = password;
            }

            paramList.add(new BasicNameValuePair(key, value));

        }

        return paramList;
    }

    public String getCookies() {
        return cookies;
    }

    public void setCookies(String cookies) {
        //if(cookies!=""){
        this.cookies = cookies;
        //}
    }

}
