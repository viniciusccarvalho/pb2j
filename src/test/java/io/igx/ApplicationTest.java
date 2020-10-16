package io.igx;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@MicronautTest
public class ApplicationTest {

	@Inject
	@Client("https://cdn.jsdelivr.net")
	RxHttpClient client;

	@Test
	public void testDownload() {
		byte[] data = client.retrieve(HttpRequest.GET("/gh/viniciusccarvalho/nano-proxy/README.adoc"), byte[].class).blockingFirst();
		System.out.println(data.length);
	}

}
