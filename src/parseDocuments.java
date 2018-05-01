import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.HashSet;
import java.util.Scanner;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Stream;
import java.util.Iterator;

import java.util.Collections;


public class parseDocuments {
    private ArrayList<HashMap<String, ArrayList<String>>> dictAllFiles = new ArrayList<>();
    private ArrayList<Path> folder = new ArrayList<>();
    private HashSet<String> dictionary = new HashSet<>();
    private HashMap<String,ArrayList<Integer>> dic = new HashMap<>();
    private int fileIndex = 0;
    private int N = 0;
    private boolean DEBUG = false;

    private int[][] matrix = null;
    private int topNum = 4;

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

        int numFiles = folder.size();
        matrix = new int[numFiles][numFiles];
        while (!folder.isEmpty()) {
            createDictionary();
        }
        System.out.println("Processing Finished");
        compareDictionaries();

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

                if(filesToLookThrough.isEmpty() || firstWord.getKey().equals("SK_filename") || firstWord.getKey().equals("the")){
                    continue;
                }else{
                    for(int ind = 0; ind < filesToLookThrough.size(); ind++) {
                        int secondDictIndex = filesToLookThrough.get(ind);
                        if(secondDictIndex==firstDictIndex){
                            continue;
                        }
                        String name2 = dictAllFiles.get(secondDictIndex).get("SK_fileName").get(0);

                        int currentPhraseCopied = matrix[firstDictIndex][secondDictIndex];

                        String secondWord = firstWord.getKey().toString();

                        //ArrayList<String> firstList =  (ArrayList<String>) firstWord.getValue();
                        ArrayList<String> firstList =  dictAllFiles.get(firstDictIndex).get(secondWord);
                        ArrayList<String> secondList = dictAllFiles.get(secondDictIndex).get(secondWord);
                        int total = compareStrings(firstList, secondList, currentPhraseCopied);

                        matrix[firstDictIndex][secondDictIndex] = total;
                        if(total> 50){

                        }
                    }

                }

            }

        }

    }

    private void insertSort(outputCell newTopCopier){

        int z = 0;
        while(newTopCopier.samePhraseCount>topCopiers.get(z+1).samePhraseCount && z < topCopiers.size()-2){
            topCopiers.set(z, topCopiers.get(z + 1));
            z = z + 1;

        }


    }

    private int checkCombo(String name1, String name2){
        for(int z = 0; z < holdingList.size(); z++){
            outputCell tmp = holdingList.get(z);
            if(tmp.name1.equals(name1) && tmp.name2.equals(name2)){
                return z;
            }
        }
        return -1;
    }

    private void sortList(ArrayList<outputCell> list){
        for(int z = 0; z < list.size()-1; z = z + 1){
            for(int j = 0; j < list.size()-z-1; j = j + 1) {
                if (list.get(j).samePhraseCount > list.get(j + 1).samePhraseCount) {
                    outputCell tmp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, tmp);
                }
            }
        }
    }

    private void checkList(outputCell possibleCell){
        for(int z = 0; z < holdingList.size(); z++){
            outputCell tmp = holdingList.get(z);
            if(false){
            }
        }
        return ;
    }


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
                    }
                }

                if(count == (N-1)){
                    samePhrase = samePhrase + 1;
                    /*
                    System.out.println("Strings Matched.");
                    System.out.println("List 1: " + firstList.get(aa) + " " + firstList.get(aa+ 1) + " " +
                            " " + firstList.get(aa + 2) + " " + firstList.get(aa + 3) + " " +
                            firstList.get(aa + 4));
                    System.out.println("List 2: " + secondList.get(bb) + " " + secondList.get(bb+ 1) + " " +
                            " " + secondList.get(bb + 2) + " " + secondList.get(bb + 3) + " " +
                            secondList.get(bb + 4));
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
