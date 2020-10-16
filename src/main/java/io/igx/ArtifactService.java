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
public class ArtifactService {

	@Inject
	@Client("https://cdn.jsdelivr.net")
	RxHttpClient client;

	private final File artifactFolder = new File(System.getProperty("user.home"), ".pb2j/artifacts");

	public File fetchResource(String resource) throws IOException {
		File target = new File(artifactFolder, resource);
		if(!target.exists()){
			byte[] data = client.retrieve(HttpRequest.GET(""), byte[].class).blockingFirst();
			FileUtils.copy(new ByteArrayInputStream(data), target);
		}
		return target;
	}
}
