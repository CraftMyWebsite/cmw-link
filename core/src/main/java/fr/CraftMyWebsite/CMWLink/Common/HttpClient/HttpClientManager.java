package fr.CraftMyWebsite.CMWLink.Common.HttpClient;

import fr.CraftMyWebsite.CMWLink.Common.Config.ConfigFile;
import lombok.Getter;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpClientManager {
    private @Getter ConfigFile config;

    public HttpClientManager(ConfigFile config) {
        this.config = config;
    }

    /**
     * @param slug String
     * @return JsonElement
     */
    public JsonElement getWebsiteData(String slug) {

        URL url = null;
        try {
            url = new URL(this.config.getSettings().getDomain() + slug);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        try (InputStream input = url.openStream();
             InputStreamReader isr = new InputStreamReader(input);
             BufferedReader reader = new BufferedReader(isr)) {

            return new Gson().fromJson(reader, JsonElement.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
