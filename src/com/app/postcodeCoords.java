package com.app;

import java.io.*;
import java.net.*;

import org.json.JSONException;
import org.json.JSONObject;

public class postcodeCoords {

    public static double[] getCoords(String postcode) {
        //set postcode to uppercase to make sure its format is correct
        postcode = postcode.toUpperCase();
        //free api key from google cloud
        String apiKey = "AIzaSyAwT8rf08-7uZk2EIMC11TQNFQoaLRFmM4";
        try {
            //put url together
            String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + URLEncoder.encode(postcode, "UTF-8") + "&key=" + apiKey;

            //url object, open connection
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");

            //get response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //get latitude and longitude from json
            JSONObject jsonObj = new JSONObject(response.toString());
            if ("OK".equalsIgnoreCase(jsonObj.getString("status"))) {
                JSONObject location = jsonObj.getJSONArray("results")
                        .getJSONObject(0)
                        .getJSONObject("geometry")
                        .getJSONObject("location");

                double latitude = location.getDouble("lat");
                double longitude = location.getDouble("lng");

                return new double[]{latitude, longitude};
            } else {
                return null; //null if the postcode was invalid or other error
            }
        } catch (IOException | JSONException e) {
            System.out.println("Error: " + e.getMessage());
        }

        //error handling
        return null;
    }
}
