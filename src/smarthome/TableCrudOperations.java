package smarthome;

import javax.swing.JPanel;

public interface TableCrudOperations {
   JPanel getCrudPanel();
    void insert();
    void update();
    void delete();
    void export();
    void clear();
}
