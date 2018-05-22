package Evolution.generator;

import java.util.ArrayList;

public class RepeatedLevelSlicesLibrary implements SlicesLibrary{
    private ArrayList<String> slices;
    private String[] arrayedSlices;

    public RepeatedLevelSlicesLibrary(){
        this.slices = new ArrayList<String>();
        this.arrayedSlices = new String[0];
    }

    @Override
    public void addLevel(String level){
        String[] lines = level.split("\n");
        this.addLevel(lines);
    }

    @Override
    public void addLevel(String[] lines){
        for(int i=0; i<lines[0].length(); i++){
            String slice = "";
            for(int j=0; j<lines.length; j++){
                slice += lines[j].charAt(i);
            }
            this.slices.add(slice);
        }
        this.arrayedSlices = this.slices.toArray(new String[0]);
    }

    @Override
    public int getNumberOfSlices(){
        return this.slices.size();
    }

    @Override
    public String getSlice(int index){
        return this.arrayedSlices[index];
    }
}
