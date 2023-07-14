package be.alexandre01.dnplugin.utils.files;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;

public class CopyFiles {
    public static void copyRessource(String ressource, File f) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(Objects.requireNonNull(CopyFiles.class.getClassLoader().getResourceAsStream(ressource)), StandardCharsets.UTF_8));
        BufferedWriter out = Files.newBufferedWriter(f.toPath(), StandardCharsets.UTF_8);
        String read;
        while ((read = in.readLine()) != null) {
            read = new String(read.getBytes(), StandardCharsets.UTF_8);
            System.out.println(read);
            out.write(read);
            out.newLine();
        }
        out.flush();
        out.close();
        in.close();
    }
}
