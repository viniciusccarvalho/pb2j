package io.igx;

import io.micronaut.configuration.picocli.PicocliRunner;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Command(name = "pb2j", description = "...",
        mixinStandardHelpOptions = true)
public class Pb2jCommand implements Runnable {

    @Option(names = { "-i", "--input" }, description = "Input folder containing all proto files", required = true)
    File inputDir;

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
        }
    }

    private void createInitDir() {
        List<File> configFolders = new LinkedList<>();
        File init = new File(System.getProperty("user.home"), ".pb2j");
        configFolders.add(init);
        configFolders.add(new File(init, "config"));
        configFolders.add(new File(init, "artifacts"));
        configFolders.add(new File(init, "templates"));
        for(File f : configFolders){
            if(!f.exists()){
                f.mkdirs();
            }
        }
    }
}
