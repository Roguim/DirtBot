package net.dirtcraft.dirtbot.utils.verification;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.dirtcraft.dirtbot.DirtBot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class VerificationWebApiHelper {
    public static String getCurrentUsername(String uuid) {
        uuid = uuid.replace("-","");
        String currentName = null;
        HttpURLConnection con = null;
        URL url;
        try {
            url = new URL("https://api.mojang.com/user/profiles/"+uuid+"/names");
            con = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
            DirtBot.pokeDevs(e);
            if (con != null) con.disconnect();
            return null;
        }

        try (
                InputStream in = con.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(inputStreamReader);
        ) {
            String buffer;
            StringBuilder sb = new StringBuilder();
            while ((buffer = br.readLine())!=null){
                sb.append(buffer);
            }
            JsonArray x = new JsonParser().parse(sb.toString()).getAsJsonArray();
            for (JsonElement obj : x){
                String name = obj.getAsJsonObject().get("name").getAsString();
                currentName = name;
            }

        } catch (IOException e) {
            e.printStackTrace();
            DirtBot.pokeDevs(e);
        } finally {
            con.disconnect();
        }
        return currentName;
    }

    public static List<String> getUsernames(String uuid) {
        uuid = uuid.replace("-","");
        List<String> names = new ArrayList<>();
        HttpURLConnection con = null;
        URL url;
        try {
            url = new URL("https://api.mojang.com/user/profiles/"+uuid+"/names");
            con = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
            DirtBot.pokeDevs(e);
            if (con != null) con.disconnect();
            return null;
        }

        try (
                InputStream in = con.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(inputStreamReader);
        ) {
            String buffer;
            StringBuilder sb = new StringBuilder();
            while ((buffer = br.readLine())!=null){
                sb.append(buffer);
            }
            JsonArray x = new JsonParser().parse(sb.toString()).getAsJsonArray();
            for (JsonElement obj : x){
                String name = obj.getAsJsonObject().get("name").getAsString();
                names.add(name);
            }

        } catch (IOException e) {
            e.printStackTrace();
            DirtBot.pokeDevs(e);
        } finally {
            con.disconnect();
        }
        return names;
    }
}
