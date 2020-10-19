package io.igx;

import io.micronaut.configuration.picocli.PicocliRunner;

import io.micronaut.core.util.CollectionUtils;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import javax.inject.Inject;
import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Command(name = "pb2j", description = "...",
        mixinStandardHelpOptions = true)
public class Pb2jCommand implements Runnable {

    @Option(names = { "-i", "--input" }, description = "Input folder containing all proto files", required = true)
    File inputDir;

    @Parameters
    List<String> parameters;

    @Inject
    ProjectGenerator projectGenerator;

    public static void main(String[] args) throws Exception {
        PicocliRunner.run(Pb2jCommand.class, args);
    }

    public void run() {
        createInitDir();
        if(!inputDir.isDirectory()) {
            throw new IllegalStateException("Input parameter is not a directory");
        }
        for(File protoFolder : inputDir.listFiles((dir, name) -> dir.isDirectory())) {
            try {
                projectGenerator.visit(protoFolder);
                if(!CollectionUtils.isEmpty(parameters)) {
                    List<String> commands = new LinkedList<>();
                    commands.add("./gradlew");
                    commands.add("-Dorg.gradle.daemon.debug=true");
                    commands.addAll(parameters);
                    ProcessBuilder processBuilder = new ProcessBuilder(commands);
                    processBuilder.directory(new File(protoFolder, "java-project"));
                    Process process = processBuilder.start();
                    InputStream is = process.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String line;
                    while ((line = br.readLine()) != null) {
                        System.out.println(line);
                    }
                    int exitCode = process.waitFor();
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void createInitDir() {
        List<File> configFolders = new LinkedList<>();
        File init = new File(System.getProperty("user.home"), ".pb2j");
        configFolders.add(init);
        configFolders.add(new File(init, "config"));
        configFolders.add(new File(init, "artifacts/gradle/wrapper"));
        configFolders.add(new File(init, "templates"));
        for(File f : configFolders){
            if(!f.exists()){
                f.mkdirs();
            }
        }
    }
}
