package io.igx;

import io.igx.util.FileUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

@Singleton
public class ResourceService {

	@Inject
	@Client("https://cdn.jsdelivr.net")
	RxHttpClient client;

	private final File resourceFolder = new File(System.getProperty("user.home"), ".pb2j");

	public File fetchResource(String resource) throws IOException {
		File target = new File(resourceFolder, resource);
		if(!target.exists()){
			byte[] data = client.retrieve(HttpRequest.GET("/gh/viniciusccarvalho/pb2j/"+resource), byte[].class).blockingFirst();
			FileUtils.copy(new ByteArrayInputStream(data), target);
		}
		return target;
	}
}
