package com.huawei;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
public class FileUtils {

    public static List<String> readFileIntoList(String fileName){
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))){
            List<String> result = new ArrayList<>();
            String line;
            while ((line = reader.readLine())!=null)
                result.add(line);
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static void writeFileFromList(List<String> strings,String fileName){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))){
            for (String str:strings
                 ) {
                writer.write(str);
                writer.newLine();
            }
            writer.flush();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
