package com.rodafleets.rodadriver.utils;

import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.rodafleets.rodadriver.model.VehicleRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    public String serializeObject(Object myObject) {
        String serializedObject = "";
        // serialize the object
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(myObject);
            so.flush();
            serializedObject = bo.toString();
        } catch (Exception e) {

        }

        return serializedObject;
    }

    public static Object deSerializeObject(String serializedString) {
        Object obj = null;
        // deserialize the object
        try {
            byte b[] = serializedString.getBytes();
            ByteArrayInputStream bi = new ByteArrayInputStream(b);
            ObjectInputStream si = new ObjectInputStream(bi);
            obj = (Object) si.readObject();
        } catch (Exception e) {

        }

        return obj;
    }

    public static void enableWindowActivity(Window window, Boolean enable) {
        if(enable) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else{
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    public static ArrayList<VehicleRequest> toVehicleRequestArrayList(JSONArray array) throws JSONException {
        ArrayList<VehicleRequest> list = new ArrayList();
        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonObject = array.getJSONObject(i);
            list.add(new VehicleRequest(jsonObject));

        }
        return list;
    }

    public static List toList(JSONArray array) throws JSONException {
        List list = new ArrayList();
        for (int i = 0; i < array.length(); i++) {
            list.add(fromJson(array.get(i)));
        }
        return list;
    }

    private static Object fromJson(Object json) throws JSONException {
        if (json == JSONObject.NULL) {
            return null;
        } else if (json instanceof JSONObject) {
            //return toMap((JSONObject) json);
            return null;
        } else if (json instanceof JSONArray) {
            return toList((JSONArray) json);
        } else {
            return json;
        }
    }
}
