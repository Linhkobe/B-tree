package fr.miage.btree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ControllerBtree {
    @Autowired
    private final Btree<String, String> bplustree;
    public ControllerBtree(Btree<String, String> bplustree) {
        this.bplustree = bplustree;
    }

    @GetMapping("/serialize")
    //http://localhost:8080/serialize?filePath=serializedata.txt
    public void saveBTree(@RequestParam String filePath) {
        System.out.println(bplustree.toString());
        SerializeBtree.saveBTree(bplustree, filePath);
    }


    @GetMapping("/deserialize")
    //http://localhost:8080/deserialize?filePath=serializedata.txt
    public Btree<String, String> loadBTree(@RequestParam String filePath) {
        return (Btree<String, String>) DeserializeBtree.loadBTree(filePath);
    }
}
