public class outputCell implements Comparable<outputCell> {
    public String name1 = null;
    public String name2 = null;
    public Integer samePhraseCount = new Integer(0);

    public outputCell(String firstFile, String secondFile, int phraseCopied){
        this.name1 = firstFile;
        this.name2 = secondFile;
        this.samePhraseCount = phraseCopied;
    }


    @Override
    public int compareTo(outputCell a){
        if(samePhraseCount > a.samePhraseCount){
            return this.samePhraseCount;
        }else {return a.samePhraseCount;}
    }

    @Override
    public String toString(){
        return name1 + ":" + name2 + " " + samePhraseCount.toString();
    }

}
