package fr.miage.btree;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.http.converter.json.GsonBuilderUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
public class SerializeBtree {
    public static void saveBTree(Btree<?, ?> btree, String filePath) {
        try (FileOutputStream fileOut = new FileOutputStream(filePath);
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
            System.out.println(btree.toString());
            objectOut.writeObject(btree);
            System.out.println("The B+ tree has been serialized and saved in " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
