package com.technocracy.app.aavartan.model;

/**
 * Created by nsn on 10/9/2015.
 */
public class RegisterEventCheckboxes {

    String name = null;
    boolean selected = false;

    public RegisterEventCheckboxes(String name, boolean selected) {
        super();
        this.name = name;
        this.selected = selected;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
