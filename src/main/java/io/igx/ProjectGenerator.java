package io.igx;

import com.github.mustachejava.Mustache;
import io.igx.util.CopyFileVisitor;
import io.igx.util.FileUtils;

import javax.inject.Singleton;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Singleton
public class ProjectGenerator {



	private final ResourceService resourceService;
	private final TemplateService templateService;

	public ProjectGenerator(ResourceService resourceService, TemplateService templateService) {
		this.resourceService = resourceService;
		this.templateService = templateService;
	}

	public void visit(File protoFolder) throws IOException {
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
		File gradlew = new File(projectFolder, "gradlew");
		FileUtils.copy(new FileInputStream(resourceService.fetchResource("artifacts/gradlew")), gradlew);
		gradlew.setExecutable(true, false);
		FileUtils.copy(new FileInputStream(resourceService.fetchResource("artifacts/gradle/wrapper/gradle-wrapper.jar")), new File(wrapperFolder, "gradle-wrapper.jar"));
		FileUtils.copy(new FileInputStream(resourceService.fetchResource("artifacts/gradle/wrapper/gradle-wrapper.properties")), new File(wrapperFolder, "gradle-wrapper.properties"));
		Map<String, Object> context = new HashMap<>();
		context.put("projectName", projectName);
		context.put("group", "com.example");
		templateService.writeTemplate("build.gradle", context, new File(projectFolder, "build.gradle"));
		templateService.writeTemplate("gradle.properties", context, new File(projectFolder, "gradle.properties"));
		templateService.writeTemplate("settings.gradle", context, new File(projectFolder, "settings.gradle"));
	}


}
