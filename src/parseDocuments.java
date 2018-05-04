import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.*;
import java.util.stream.Stream;


public class parseDocuments {
    private ArrayList<HashMap<String, ArrayList<String>>> dictAllFiles = new ArrayList<>();
    private ArrayList<Path> folder = new ArrayList<>();
    private HashSet<String> dictionary = new HashSet<>();
    private HashMap<String,ArrayList<Integer>> dic = new HashMap<>();
    private HashMap<Integer, ArrayList<Integer>> topCheaters = new HashMap<>();

    private int fileIndex = 0;
    private int N = 0;
    private boolean DEBUG = false;

    private int numFiles = 0;
    private int[][] matrix = null;
    private int topNum = 4;
    private int min = 0;

    /**
     * Constructor
     * Does everything - calls all other functions
     * @param folderPath - path to the folder with the documents.
     *                   Please wrap folder path in quotes/"", so
     *                   filepath spaces are recognized
     * @param N - length of copied strings you want to find
     */
    public parseDocuments(String folderPath, int N){
        this.N = N;
        try (Stream<Path> paths = Files.walk(Paths.get(folderPath))){
            paths.filter(Files::isRegularFile).forEach(x -> folder.add(x));
        }catch(Exception e){ e.printStackTrace(); }


        numFiles = folder.size();
        matrix = new int[numFiles][numFiles];
        while (!folder.isEmpty()) {
            createDictionary();
        }
        System.out.println("Processing Finished");
        compareDictionaries();

        /*
        System.out.println("Top Cheaters are:");
        Iterator it = topCheaters.entrySet().iterator();

        while(it.hasNext()){
            HashMap.Entry key = (HashMap.Entry)it.next();
            String name1 = dictAllFiles.get(((ArrayList<Integer>)key.getValue()).get(0)).get("SK_fileName").get(0);
            String name2 = dictAllFiles.get(((ArrayList<Integer>)key.getValue()).get(1)).get("SK_fileName").get(0);
            System.out.println(name1 + ":" + name2 + " " + key.getKey().toString());
        }
        */

        findTopCheaters();
    }

    /**
     * Testing Constructor before Command Line Arguments introduced.
     */
    public parseDocuments() {
        try (Stream<Path> paths = Files.walk(Paths.get("/Users/Ali/Desktop/UT Austin/EE 442C/project7_ama4943/src/sm_doc_set"))){
            //paths.filter(Files::isRegularFile).forEach(System.out::println);
            paths.filter(Files::isRegularFile).forEach(x -> folder.add(x));
        }catch(Exception e){ e.printStackTrace(); }

        if(DEBUG) {
            for (int i = 0; i < 19; i++) {
                createDictionary();
            }
        } else {
            while (!folder.isEmpty()) {
                createDictionary();
            }
        }
        System.out.println("Processing finished");

        compareDictionaries();
    }


    /**
     * Creates Dictionary of everyword in all files
     * Creates Dictionary for each file
     */
    public void createDictionary(){
        ArrayList<String> buffer = new ArrayList<>(N);
        HashMap<String, ArrayList<String>> dictPlus = new HashMap<>();
        File file = null;
        String filePath = new String(folder.remove(0).toString());
        String[] pathToFile = filePath.split("/");
        String fileName = pathToFile[pathToFile.length-1];
        try {file = new File(filePath);}
        catch(Exception e){ e.printStackTrace(); }

        Scanner scanner = null;
        try { scanner = new Scanner(new FileInputStream(file)).useDelimiter("\\s|\\W"); //\\.|,");
        } catch (FileNotFoundException e) { System.out.println("Messed Up"); e.printStackTrace(); }


        for(int z = 0; (z < N) && scanner.hasNext(); z++){
            String str = scanner.next().toLowerCase();

            while(str.equals("")){
                if(scanner.hasNext()) {
                    str = scanner.next().toLowerCase();
                }else{
                    System.out.println("Error: This file (" + fileName + ") has too few words given the desired length N = " + N);
                    return;
                }
            }

            buffer.add(z, str);
        }
        if(buffer.size() != N){
            System.out.println("Error: This file (" + fileName + ") has too few words given the desired length N = " + N);
            return;
        }
        dictionary.addAll(buffer);
        while(scanner.hasNext()){
            String str = buffer.remove(0);
            if(dictPlus.containsKey(str)){
                ArrayList<String> nexl = new ArrayList<>(dictPlus.get(str));
                nexl.addAll(buffer);
                dictPlus.put(str, new ArrayList<>(nexl));
            } else{ dictPlus.put(str, new ArrayList<>(buffer)); }

            String tmp = new String(scanner.next().toLowerCase());

            while(tmp.equals("")){
                if(scanner.hasNext()) {
                    tmp = scanner.next().toLowerCase();
                }else{
                    break;
                }
            }
            buffer.add(tmp);
            dictionary.add(buffer.get(buffer.size()-1));
        }
        if(buffer.size()==N){
            String str = buffer.remove(0);
            dictPlus.put(str, buffer);
        }

        ArrayList<String> fileNameHolder = new ArrayList<>();
        for(int z = 0; z < N-1; z++) {
            fileNameHolder.add(fileName);
        }
        dictPlus.put("SK_fileName", fileNameHolder);

//----------------------------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------
        Iterator it = dictPlus.entrySet().iterator();
        while(it.hasNext()){
            HashMap.Entry keyToAdd = (HashMap.Entry) it.next();
            if(dic.containsKey((String)keyToAdd.getKey())){
                dic.get(keyToAdd.getKey()).add(fileIndex);
            }else{
                ArrayList<Integer> tmp = new ArrayList<>();
                tmp.add(fileIndex);
                //tmp.add(fileName);
                dic.put((String)keyToAdd.getKey(),tmp);
            }
        }


        dictAllFiles.add(fileIndex, dictPlus);
        fileIndex = fileIndex + 1;
        scanner.close();
    }


    int iterations = 0;

    /**
     *
     */
    public void compareDictionaries(){
        for(int firstDictIndex = 0; firstDictIndex < dictAllFiles.size()-1; firstDictIndex++){
            String name1 = dictAllFiles.get(firstDictIndex).get("SK_fileName").get(0);
            Iterator  firstDictionary = dictAllFiles.get(firstDictIndex).entrySet().iterator();

            System.out.println("Checking: " + name1);
            while (firstDictionary.hasNext()) {
                HashMap.Entry firstWord = (HashMap.Entry) firstDictionary.next();
                ArrayList<Integer> filesToLookThrough = dic.get(firstWord.getKey());
                Boolean remove = filesToLookThrough.remove((Object)firstDictIndex);

                dic.put((String)firstWord.getKey(), filesToLookThrough);

                if(filesToLookThrough.isEmpty() || firstWord.getKey().equals("SK_filename") ){// || firstWord.getKey().equals("the")){
                    continue;
                }else{
                    for(int ind = 0; ind < filesToLookThrough.size(); ind++) {
                        int secondDictIndex = filesToLookThrough.get(ind);
                        if(secondDictIndex==firstDictIndex){
                            continue;
                        }
                        //String name2 = dictAllFiles.get(secondDictIndex).get("SK_fileName").get(0);

                        int currentPhraseCopied = matrix[firstDictIndex][secondDictIndex];

                        String secondWord = firstWord.getKey().toString();

                        //ArrayList<String> firstList =  (ArrayList<String>) firstWord.getValue();
                        ArrayList<String> firstList =  dictAllFiles.get(firstDictIndex).get(secondWord);
                        ArrayList<String> secondList = dictAllFiles.get(secondDictIndex).get(secondWord);
                        int total = compareStrings(firstList, secondList, currentPhraseCopied);

                        matrix[firstDictIndex][secondDictIndex] = total;
                        iterations +=1;

                    }

                }

            }

        }

    }

    List<outputCell> cheats = new ArrayList<>();
    public void findTopCheaters(){
        if(topNum>numFiles*numFiles){
            System.out.println("Error: Requesting too many cheaters.");
            return;
        }
        int min = 0;
        int xcoord = 0;
        int ycoord = 0;
        while(cheats.size()<topNum){
            int cellValue = matrix[xcoord][ycoord];
            cheats.add(new outputCell(xcoord,ycoord,cellValue));
            if(cellValue>min)
                min = cellValue;

            if(xcoord==numFiles-1)
            {ycoord++; xcoord = 0;}
            else
                xcoord++;

        }
        Collections.sort(cheats);

        for(int x = xcoord; x<numFiles; x++){
            for(int y = ycoord; y<numFiles; y++){
                int cellValue = matrix[x][y];
                if (min < cellValue) {
                    cheats.add(new outputCell(x,y,cellValue));
                    Collections.sort(cheats);
                    cheats.remove(0);
                }
            }
        }
        System.out.println(cheats);
    }

    /*
    private int insertValue(int value){
        for(int z = topCheaters.size()-1; z > -1; z--){
            if(list.get(z)<value){

            }
        }
        return 0;
    }
    */

    /*
    boolean flag = false;

    private void addValue(int firstDictIndex, int secondDictIndex, int total) {


        ArrayList<Integer> tmp = new ArrayList<>();
        tmp.add(0, firstDictIndex);
        tmp.add(1,secondDictIndex);


        //Check if Pair is already amongst Top Cheaters
        Iterator it = topCheaters.entrySet().iterator();
        while(it.hasNext()) {
            HashMap.Entry coord = (HashMap.Entry) it.next();
            if (tmp.equals(coord.getValue())) {
                it.remove();
                topCheaters.put(total,tmp);
                return;
            }
        }
        // Current Pair is not already in Top Cheaters
        if(topCheaters.size() < topNum){
            topCheaters.put(total,tmp);
            if(min>total){
                min = total;
            }

        } else if(!flag) {
            flag = true;
            list.addAll(topCheaters.keySet());
            list.sort(Comparator.naturalOrder());
            System.out.println(list);
        } else{
            //This is what is suppose to happen when need to check if total is greater than all other total so far
            if(total>min){
                list.remove(0);
                list.add(total);
                list.sort(Comparator.naturalOrder());
                topCheaters.remove(min);
                topCheaters.put(total,tmp);
                min = list.get(0);
                System.out.println(list);
            }
        }
    }
*/

    /**

    public void compareDictionaries(){
        for(int firstDictIndex = 0; firstDictIndex < dictAllFiles.size()-1; firstDictIndex++){
            String name1 = dictAllFiles.get(firstDictIndex).get("SK_fileName").get(0);



            for(int secondDictIndex = firstDictIndex+1; secondDictIndex < dictAllFiles.size(); secondDictIndex++) {
                String name2 = dictAllFiles.get(secondDictIndex).get("SK_fileName").get(0);
                //System.out.println("Comparing: " + name1 + ":" + name2);

                Iterator  firstDictionary = dictAllFiles.get(firstDictIndex).entrySet().iterator();
                int phraseCopied = 0;
                while (firstDictionary.hasNext()) {
                    HashMap.Entry firstWord = (HashMap.Entry) firstDictionary.next();
                    Iterator secondDictionary = dictAllFiles.get(secondDictIndex).entrySet().iterator();
                    while (secondDictionary.hasNext()) {
                        HashMap.Entry secondWord = (HashMap.Entry) secondDictionary.next();



                        if(firstWord.getKey().equals("the")){
                            if(secondWord.getKey().equals("the")){
                                System.out.println("Debug Time");
                            }
                        }


                        if (firstWord.getKey().equals(secondWord.getKey())) {
                            //System.out.println("Matched: " + firstWord.getKey());
                            ArrayList<String> firstList = (ArrayList<String>) firstWord.getValue();
                            ArrayList<String> secondList = (ArrayList<String>) secondWord.getValue();
                            phraseCopied =compareStrings(firstList, secondList, phraseCopied);
                        }

                    }
                }
                outputCell tmp = new outputCell(name1, name2, phraseCopied);

                /*

                System.out.println("Compared: " + name1 + ":" + name2 + " " +phraseCopied);
                numCopied.add(tmp);
                if(topCopiers.size()<10){
                    topCopiers.add(tmp);
                    Collections.sort(topCopiers);
                }else {
                    for (int ind = 0; ind < topCopiers.size(); ind++) {
                        topCopiers.add(tmp);
                        Collections.sort(topCopiers);
                        topCopiers.remove(0);
                    }
                }

            }
        }
    }

    */
    private int compareStrings(ArrayList<String> firstList, ArrayList<String> secondList, int samePhrase) {
        for(int aa = 0; aa < firstList.size(); aa += N-1){
            for(int bb = 0; bb < secondList.size(); bb += N-1){
                int count = 0;
                for(int z = 0; z < N-1; z++){
                    if(firstList.get(aa+z).equals(secondList.get(bb+z))){
                        count = count + 1;
                    }else{
                        break;
                    }
                }

                if(count == (N-1)){
                    samePhrase = samePhrase + 1;

                    /*
                    System.out.println("-----------------------------------------------------------------------------");
                    System.out.println("Strings Matched.");
                    System.out.println("List 1: " + firstList.get(aa) + " " + firstList.get(aa+ 1) + " " +
                            " " + firstList.get(aa + 2) + " " + firstList.get(aa + 3) + " " +
                            firstList.get(aa + 4));
                    System.out.println("List 2: " + secondList.get(bb) + " " + secondList.get(bb+ 1) + " " +
                            " " + secondList.get(bb + 2) + " " + secondList.get(bb + 3) + " " +
                            secondList.get(bb + 4));
                    System.out.println("-----------------------------------------------------------------------------");
                    */
                }
            }
        }
        return samePhrase;
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
