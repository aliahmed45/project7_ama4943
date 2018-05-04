public class outputCell implements Comparable<outputCell> {
    private Integer name1 = null;
    private Integer name2 = null;
    private Integer samePhraseCount = 0;


    public outputCell(Integer firstFile, Integer secondFile, int phraseCopied){
        this.name1 = firstFile;
        this.name2 = secondFile;
        this.samePhraseCount = phraseCopied;
    }


    @Override
    public int compareTo(outputCell compareCell){
        return this.samePhraseCount - compareCell.samePhraseCount;
    }

    @Override
    public String toString(){
        return name1 + ":" + name2 + " " + samePhraseCount.toString();
    }

}
