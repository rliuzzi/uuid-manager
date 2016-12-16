package Util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rominaliuzzi on 15/12/2016.
 */
public class Util {

    public static final String ENCODING = "UTF-8";

    public static void walk(String pattern, String path, List<File> files) throws IOException {
        File root = new File(path);
        File[] list = root.listFiles();

        if (list == null) return;

        for ( File f : list ) {
            if ( f.isDirectory() ) {
                walk( pattern, f.getAbsolutePath(), files);
            }
            else {
                if(f.getName().endsWith(pattern)){
                    files.add(f);
                    System.out.println(f.getName());
                }
            }
        }
    }

    public static List<String> read(File file){
        List<String> lines = new ArrayList<>();
        Path file_path = Paths.get(file.getParent(), file.getName());
        Charset charset = Charset.forName(ENCODING);
        try {
            lines = Files.readAllLines(file_path, charset);
        } catch (IOException e){
            e.printStackTrace();
        }
        return lines;
    }

    public static void write(String header, List<String> content, File file) throws IOException{
        PrintWriter writer = new PrintWriter(file, ENCODING);
        writer.println(header);
        for(String line : content){
            writer.println(line);
        }
        writer.close();
    }

}
