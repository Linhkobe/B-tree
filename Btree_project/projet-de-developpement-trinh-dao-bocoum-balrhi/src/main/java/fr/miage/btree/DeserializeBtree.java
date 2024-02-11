package fr.miage.btree;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
public class DeserializeBtree {
    public static Btree<?, ?> loadBTree(String filePath) {
        Btree<?, ?> btree = null;
        try (FileInputStream fileIn = new FileInputStream(filePath);
             ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
            btree = (Btree<?, ?>) objectIn.readObject();
            System.out.println("The B+ tree has been loaded from " + filePath);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return btree;
    }
}
