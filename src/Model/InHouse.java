
package Model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;


public class InHouse extends Part {
    public IntegerProperty machineID;
    
    public InHouse(int id, String name, double price, int stock, int min, int max, int machineID){
        super(id, name, price, stock, min, max);
        this.machineID = new SimpleIntegerProperty(machineID);
    }
    
    public final void setMachineID(int id){
        this.machineID.set(id);
    }
    
    public int getMachineID(){
        return this.machineID.get();
    }
}
