package fr.AxelVatan.CMWLink.Common.HttpClient;

import fr.AxelVatan.CMWLink.Common.Config.ConfigFile;
import lombok.Getter;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.lang.Object;

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
     * @return StringBuilder
     */
    public Object getWebsiteData(String slug) {

        URL url = null;
        try {
            url = new URL(this.config.getSettings().getDomain() + slug);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        try (InputStream input = url.openStream()) {
            InputStreamReader isr = new InputStreamReader(input);
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder json = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                json.append((char) c);
            }
            return new JSONParser().parse(json.toString());
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
