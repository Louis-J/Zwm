package pers.louisj.Zwm.SaveLoad;

import java.util.Objects;

public class Character {
    // Individal Characters
    public Long hWndValue;
    public String windowClass;
    public String windowTitle;

    // Group Characters
    public String processName;
    public Integer processId;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Character)
            return Objects.equals(hWndValue, hWndValue) && Objects.equals(windowClass, windowClass)
                    && Objects.equals(windowTitle, windowTitle) && Objects.equals(processName, processName)
                    && Objects.equals(processId, processId);
        else
            return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(hWndValue) ^ Objects.hashCode(windowClass)
                ^ Objects.hashCode(windowTitle) ^ Objects.hashCode(processName)
                ^ Objects.hashCode(processId);
    }
}
