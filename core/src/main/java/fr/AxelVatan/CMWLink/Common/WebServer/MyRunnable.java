package fr.AxelVatan.CMWLink.Common.WebServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MyRunnable implements Runnable {

	private int runPos;
	
	public MyRunnable(int runPos) {
		this.runPos = runPos;
	}
	
	@Override
	public void run() {
		HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:24102/monitoring/opList"))
                .GET()
                .header("User", "admin")
                .header("Pwd", "changeme")
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = null;
		try {
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

        System.out.println("Reponse " + runPos + " " + response.body());
	}

}
