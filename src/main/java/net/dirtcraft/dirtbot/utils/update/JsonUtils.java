package net.dirtcraft.dirtbot.utils.update;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dirtcraft.dirtbot.DirtBot;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class JsonUtils {

    public static JsonObject getJsonFromURL(String url) {
        try {
            return new JsonParser().parse(get(url)).getAsJsonObject();
        } catch (IOException e) {
            DirtBot.pokeDevs(e);
            return new JsonObject();
        }
    }

    private static String get(String getUrl) throws IOException {
        URL url = new URL(getUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.addRequestProperty("Cache-Control", "no-transform");
        con.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        String location = con.getHeaderField("Location");
        if (location != null) {
            con = (HttpURLConnection) new URL(location).openConnection();
            con.setRequestMethod("GET");
            con.addRequestProperty("Cache-Control", "no-transform");
            con.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

        }
        return read(con.getInputStream());
    }

    private static String read(InputStream is) {
        BufferedReader in = null;
        String inputLine;
        StringBuilder body;
        try {
            in = new BufferedReader(new InputStreamReader(is));

            body = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                body.append(inputLine);
            }
            in.close();

            return body.toString();
        } catch(IOException ioe) {
            DirtBot.pokeDevs(ioe);
            return "";
        } finally {
            closeQuietly(in);
        }
    }

    private static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) closeable.close();
        } catch(IOException ex) {
            DirtBot.pokeDevs(ex);
        }
    }

}
