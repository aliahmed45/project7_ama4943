public class cheaters {

    public static void main(String[] args) {
        System.out.println("Hello World!");


        String pathToFolder = args[0];
        //String[] pathToFile = pathToFolder.split("/");
        int N = new Integer(args[1]);
        parseDocuments test1 = new parseDocuments(pathToFolder, N);
        /*
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
        parseDocuments test2 = new parseDocuments();
        */
        System.out.println("Finished");

    }
}
