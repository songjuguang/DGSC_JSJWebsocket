package com.mkoteam.until;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HttpUtil {

    public static String getData() {
        BufferedReader in = null;
        List<String> datas = null;
        String inputLine;
        String datastr = "";
        try {
            URL yahoo = new URL("http://icamera.extremevision.com.cn/v1/config-relations?per-page=50&page=1");
            in = new BufferedReader(new InputStreamReader(yahoo.openStream()));

            datas = new ArrayList<>();

            while ((inputLine = in.readLine()) != null) datas.add(inputLine);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (datas.size() == 0) {
            return null;
        } else if (datas.size() == 1) {
            return datas.get(0);
        } else {
            for (String str : datas) {
                datastr += str;
            }
            return datastr;
        }
    }
}
