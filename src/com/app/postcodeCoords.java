package com.app;

import java.io.*;
import java.net.*;

import org.json.JSONException;
import org.json.JSONObject;

public class postcodeCoords {

    public static double[] getCoords(String postcode) {
        // Remove hardcoded postcode
        postcode = postcode.toUpperCase();
        String apiKey = "AIzaSyAwT8rf08-7uZk2EIMC11TQNFQoaLRFmM4";  // Replace with your Google Maps API Key
        try {
            // Build the URL for the Google Maps API request
            String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + URLEncoder.encode(postcode, "UTF-8") + "&key=" + apiKey;

            // Create URL object and open a connection
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");

            // Read the response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parse the JSON response to get the latitude and longitude
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
                return null;
            }
        } catch (IOException | JSONException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Return null in case of an error
        return null;
    }
}
