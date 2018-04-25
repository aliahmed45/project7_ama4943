import java.io.File;
import java.io.FileNotFoundException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.HashSet;
import java.util.Scanner;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Stream;
import java.util.Iterator;

public class parseDocuments {
    ArrayList<HashMap<String, ArrayList<String>>> dictAllFiles = new ArrayList<>();
    ArrayList<Path> folder = new ArrayList<>();
    HashSet<String> dictionary = new HashSet<>();
    int N = 6;
    boolean flag = true;

    public parseDocuments() {


        try (Stream<Path> paths = Files.walk(Paths.get("/Users/Ali/Desktop/UT Austin/EE 442C/project7_ama4943/src/sm_doc_set"))){//"/Users/Ali/Desktop/UT Austin/EE 422C/project7_ama4943/src/sm_doc_set/"))) {
//            paths.filter(Files::isRegularFile).forEach(System.out::println);
            paths.filter(Files::isRegularFile).forEach(x -> folder.add(x));

        }catch(Exception e){ e.printStackTrace(); }
        for(int i = 0; i< 2; i++){
            createDictionary();
        }
        /*
        while(!folder.isEmpty()) {
            createDictionary();
        }
        */
    }

    public void createDictionary(){
        ArrayList<String> buffer = new ArrayList<>(N);
        HashMap<String, ArrayList<String>> dictPlus = new HashMap<>();
        File file = null;
        try {file = new File(folder.remove(0).toString());}
        catch(Exception e){ e.printStackTrace(); }

        Scanner scanner = null;
        try { scanner = new Scanner(file).useDelimiter(",|\\s|\\.");
        } catch (FileNotFoundException e) { System.out.println("Messed Up"); e.printStackTrace(); }


        for(int z = 0; (z < N) /*&& scanner.hasNext()*/; z++){
            String str = scanner.next().toLowerCase();
            buffer.add(z, str);
        }
        dictionary.addAll(buffer);
        while(scanner.hasNext()){
            String str = buffer.remove(0);
            dictPlus.put(str, buffer);
            buffer.add(scanner.next());
            System.out.println(buffer);
        }
        if(buffer.size()==N){
            String str = buffer.remove(0);
            dictPlus.put(str, buffer);
        }
        System.out.println("--------------------------------------------------------------");
        dictAllFiles.add(dictPlus);
        System.out.println("--------------------------------------------------------------");
        //System.out.println(dictPlus);
        printMap(dictPlus);
        scanner.close();
    }

    public static void printMap(HashMap mp) {
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
    }
}
