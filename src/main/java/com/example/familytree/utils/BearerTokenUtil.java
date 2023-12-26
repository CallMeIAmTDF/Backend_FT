package com.example.familytree.utils;

import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Base64;

public class BearerTokenUtil {
    public static String getUserName(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;


        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);

            Base64.Decoder decoder = Base64.getUrlDecoder();
            String[] chunks = token.split("\\.");
            String payload = new String(decoder.decode(chunks[1]));
            JsonObject jsonObject = new JsonParser().parse(payload).getAsJsonObject();

            username = jsonObject.get("sub").getAsString();
        }
        return username;
    }

    public static String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String token = null;


        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        return token;
    }
}
