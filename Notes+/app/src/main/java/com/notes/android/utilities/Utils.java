package com.notes.android.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.util.DisplayMetrics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by appstartmac1 on 17/12/16.
 */

public class Utils {


    public static byte[] ConvertFileToByte(File file) {
        byte[] b = new byte[(int) file.length()];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(b);
//            for (int i = 0; i < b.length; i++) {
//                System.out.print((char) b[i]);
//            }
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found.");
            e.printStackTrace();
        } catch (IOException e1) {
            System.out.println("Error Reading The File.");
            e1.printStackTrace();
        }
        return b;
    }



    public static List<String> getListOfString(String storedString) {
        String replace = storedString.replace("[", "");
        System.out.println(replace);
        String replace1 = replace.replace("]", "");
        System.out.println(replace1);
        List<String> myList = new ArrayList<String>(Arrays.asList(replace1.split(",")));
        System.out.println(myList.toString());
        return myList;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }


    public static boolean isConnected()
    {
        String command = "ping -c 1 google.com";
        try {
            return (Runtime.getRuntime().exec (command).waitFor() == 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }




    public static Date getDate(int year, int month, int day,int hours,int min) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hours);
        cal.set(Calendar.MINUTE, min);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static List<Uri> getListOfUri(String storedString) {

        JSONObject json = null;
        try {
            json = new JSONObject(storedString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray items = json.optJSONArray("Images");
        try {
            String imageList = (String) items.get(0);
            String replace = imageList.replace("[", "");
            System.out.println(replace);
            String replace1 = replace.replace("]", "");
            System.out.println(replace1);
            List<String> myList = new ArrayList<String>(Arrays.asList(replace1.split(",")));
            System.out.println(myList.toString());


            List<Uri> listOfUri = new ArrayList<>();
            for (String uri : myList) {
                if(!uri.isEmpty())
                    listOfUri.add(Uri.parse(uri.replace(" ", "")));
            }
            return listOfUri;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
