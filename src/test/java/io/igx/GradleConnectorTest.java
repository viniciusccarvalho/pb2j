package io.igx;

import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.junit.jupiter.api.Test;

import java.io.File;

public class GradleConnectorTest {

	@Test
	public void testBuild() throws Exception {
		ProjectConnection connection = GradleConnector.newConnector()
				.forProjectDirectory(new File("/Users/vinnyc/tmp/proto-sample/hello-grpc/java-project"))
				.connect();
		connection.newBuild().forTasks("build").run();
		connection.close();
	}
}
