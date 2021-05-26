package org.louisj.Zwm.Window;

import java.util.List;

import org.louisj.Zwm.Context;

public class WindowFilter {
    private Context context;

    public interface FilterFunc {
        boolean Invoke(Window window);
    }
    // public interface RouterFunc {
    //     Workspace Invoke(Window window);
    // }

    private List<FilterFunc> filters;
}
