package pers.louisj.Zwm.SaveLoad;

public class StateValue {
    // hit => 0; unhit => ++; >= 9 => del;
    public byte ageStatus = 0;

    // State
    public int vdIndex;
    public boolean isLayout;
}