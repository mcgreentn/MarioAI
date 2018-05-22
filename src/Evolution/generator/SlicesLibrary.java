package Evolution.generator;

public interface SlicesLibrary {
    void addLevel(String level);
    void addLevel(String[] lines);
    int getNumberOfSlices();
    String getSlice(int index);
}
