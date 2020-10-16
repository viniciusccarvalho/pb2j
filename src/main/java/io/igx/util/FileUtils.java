package io.igx.util;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class FileUtils {

	public static void recursiveDelete(File folder) throws IOException {
		Files.walk(Paths.get(folder.getAbsolutePath()))
				.sorted(Comparator.reverseOrder())
				.map(Path::toFile)
				.forEach(File::delete);
	}

	public static void copy(String resource, File target) throws IOException{
		InputStream in = FileUtils.class.getClassLoader().getResourceAsStream(resource);
		copy(in, target);
	}

	public static void copy(InputStream in, File target) throws IOException {
		byte[] data = new byte[in.available()];
		in.read(data);
		OutputStream os = new FileOutputStream(target);
		os.write(data);
	}

	public static void writeToFile(String contents, File target) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(target));
		writer.write(contents);
		writer.close();
	}

}
