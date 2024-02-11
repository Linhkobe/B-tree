package fr.miage.btree;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "BPlusTree") // Spécifiez le nom de la collection dans MongoDB
public class KeyValueData {

    @Id
    private String id; // L'ID généré automatiquement par MongoDB
    private String key; // La clé
    private String value; // La valeur

    // Constructeurs, getters et setters

    public KeyValueData() {
    }

    public KeyValueData(String key, String value) {
        this.key = key;
        this.value = value;
    }

    // Getters et setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
