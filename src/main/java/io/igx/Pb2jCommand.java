package io.igx;

import io.micronaut.configuration.picocli.PicocliRunner;

import io.micronaut.core.util.CollectionUtils;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
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
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(!CollectionUtils.isEmpty(parameters)) {
                ProjectConnection connection = GradleConnector.newConnector()
                        .forProjectDirectory(new File(protoFolder, "java-project"))
                        .connect();
                try {
                    connection.newBuild().forTasks("build").run();
                } finally {
                    connection.close();
                }
            }
        }
    }

    private void createInitDir() {
        List<File> configFolders = new LinkedList<>();
        File init = new File(System.getProperty("user.home"), ".pb2j");
        configFolders.add(init);
        configFolders.add(new File(init, "config"));
        configFolders.add(new File(init, "templates"));
        for(File f : configFolders){
            if(!f.exists()){
                System.out.println("Creating folder: " + f.getPath());
                f.mkdirs();
            }
        }
    }
}
