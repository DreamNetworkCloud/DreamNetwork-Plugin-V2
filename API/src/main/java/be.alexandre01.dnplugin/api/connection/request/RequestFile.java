package be.alexandre01.dnplugin.api.connection.request;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class RequestFile {
    String s = "";
    String encoded;

    public void loadFile(String path) {
        try {
            File file = new File(path);
            if(file.exists()){
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                String line;
                while ((line = br.readLine()) != null) {
                    s += line;
                }
                br.close();
                fr.close();
            }
        } catch (IOException e) {
            System.out.println("Error while loading file: " + path);
        }
        //BASE64
        s = new String(Base64.getDecoder().decode(s));
        String[] split = s.split("\\|");

        for (String s : split) {
            if(s.isEmpty()) continue;
            String[] info = s.split("\\:");
            String id = info[0];
            int parseId;
            try {
                parseId = Integer.parseInt(id);
            } catch (NumberFormatException e) {
                System.out.println("Invalid id: " + id);
                continue;
            }
            String name = info[1];

            RequestType.addCacheRequestInfo(name,parseId);
        }
    }


    public void put(RequestInfo requestInfo) {
        s += "|" + requestInfo.id + ":" + requestInfo.name;
    }

    public void encode(){
        encoded = Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }
    public void write(String path) throws IOException {
        File file = new File(path +"/requests.dream");
        System.out.println(path +"/requests.dream");

        if (!file.exists()) {
            file.createNewFile();
        }

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        bufferedWriter.write(encoded);
        bufferedWriter.flush();
        bufferedWriter.close();
    }


}
