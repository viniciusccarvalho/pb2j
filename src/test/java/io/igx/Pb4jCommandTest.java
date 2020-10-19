package io.igx;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;


public class Pb4jCommandTest {

    @Test
    public void testWithCommandLineOption() throws Exception {
        File protoFolder = new File("/Users/vinnyc/tmp/proto-sample/micronaut");
        List<String> commands = new LinkedList<>();
        commands.add("./gradlew");
        commands.add("-Dorg.gradle.daemon.debug=true");
        commands.add("build");
        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        processBuilder.directory(new File(protoFolder, "java-project"));
        Process process = processBuilder.start();
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        System.out.println("Output of running");
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
        int exitCode = process.waitFor();
        System.out.println("Exit code :" + exitCode);
    }



}
