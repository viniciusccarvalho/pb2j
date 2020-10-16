package io.igx;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import io.igx.util.CopyFileVisitor;
import io.igx.util.FileUtils;

import javax.inject.Singleton;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Singleton
public class ProjectGenerator {

	private final MustacheFactory mf = new DefaultMustacheFactory();
	private List<Mustache> templates = new LinkedList<>();



	private void initTemplates() throws Exception {
		templates.add(mf.compile(new InputStreamReader(ProjectGenerator.class.getClassLoader().getResourceAsStream("artifacts/templates/build.gradle.mustache")), "build.gradle"));
	}

	public void visit(File protoFolder) throws IOException {
		if(templates.isEmpty()){
			try {
				initTemplates();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String projectName = protoFolder.getName();
		File projectFolder = new File(protoFolder, "java-project");
		if(projectFolder.exists()){
			FileUtils.recursiveDelete(projectFolder);
		}
		File[] protos = protoFolder.listFiles((dir, name) -> !dir.isHidden());
		projectFolder.mkdirs();
		prepareGradle(projectFolder, projectName);
		File protoSource = new File(projectFolder, "src/main/proto");
		protoSource.mkdirs();
		for(File p : protos) {
			if(p.isDirectory()) {
				Files.walkFileTree(Paths.get(p.getAbsolutePath()), new CopyFileVisitor(Paths.get(p.getAbsolutePath()), Paths.get(protoSource.getAbsolutePath())));
			}else{
				Files.copy(Paths.get(p.getAbsolutePath()), Paths.get(new File(protoSource, p.getName()).getAbsolutePath()));
			}
		}

	}

	private void prepareGradle(File projectFolder, String projectName) throws IOException {
		File wrapperFolder = new File(projectFolder, "gradle/wrapper");
		wrapperFolder.mkdirs();
		FileUtils.copy("artifacts/gradlew", new File(projectFolder, "gradlew"));
		FileUtils.copy("artifacts/gradle/wrapper/gradle-wrapper.bin", new File(wrapperFolder, "gradle-wrapper.jar"));
		FileUtils.copy("artifacts/gradle/wrapper/gradle-wrapper.properties", new File(wrapperFolder, "gradle-wrapper.properties"));
		Map<String, Object> context = new HashMap<>();
		context.put("projectName", projectName);
		context.put("group", "com.example");
		for(Mustache m : templates) {
			FileWriter writer = new FileWriter(new File(projectFolder, m.getName()));
			m.execute(writer, context);
			writer.flush();
		}
		FileUtils.writeToFile("rootProject.name="+projectName, new File(projectFolder, "settings.gradle"));
	}


}
