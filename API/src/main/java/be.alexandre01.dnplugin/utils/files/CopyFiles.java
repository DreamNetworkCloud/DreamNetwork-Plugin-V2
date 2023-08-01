package be.alexandre01.dnplugin.utils.files;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public class CopyFiles {
    public static void copyRessource(String ressource, File f) throws IOException {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(CopyFiles.class.getClassLoader().getResourceAsStream(ressource)));
                BufferedWriter out = Files.newBufferedWriter(f.toPath(), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        ) {
            String read;
            while ((read = in.readLine()) != null) {
                out.write(new String(read.getBytes(), StandardCharsets.UTF_8));
                out.newLine();
            }
        }
    }
}
