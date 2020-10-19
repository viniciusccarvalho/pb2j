package io.igx;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class TemplateService {

	private final MustacheFactory mf = new DefaultMustacheFactory();
	private final File templateDir = new File(System.getProperty("user.home"),"/.pb2j/templates");
	private Map<String, Mustache> templates = new HashMap<>();
	private final ResourceService resourceService;

	public TemplateService(ResourceService resourceService) {
		this.resourceService = resourceService;
	}

	@PostConstruct
	public void setup() throws IOException {
		templates.put("build.gradle", mf.compile(new FileReader(resourceService.fetchResource("templates/build.gradle.mustache")), "build.gradle"));
		templates.put("gradle.properties", mf.compile(new FileReader(resourceService.fetchResource("templates/gradle.properties.mustache")), "gradle.properties"));
		templates.put("settings.gradle", mf.compile(new FileReader(resourceService.fetchResource("templates/settings.gradle.mustache")), "settings.gradle"));
	}

	public void writeTemplate(String templateName, Map<String, Object> context, File target) throws IOException {
		Mustache m = templates.get(templateName);
		FileWriter writer = new FileWriter(target);
		m.execute(writer, context);
		writer.flush();
	}


}
