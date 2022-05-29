package Controller;

import Model.InHouse;
import Model.Inventory;
import Model.Outsourced;
import Model.Part;
import UserExceptions.InventoryOutOfRangeException;
import UserExceptions.MinOverMaxException;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ModifyPart implements Initializable {

    public RadioButton ModifyPartInHouse;
    public ToggleGroup InOrOut;
    public RadioButton ModifyPartOutsourced;
    public TextField ModifyPartMin;
    public TextField ModifyPartMax;
    public TextField ModifyPartID;
    public TextField ModifyPartName;
    public TextField ModifyPartInv;
    public TextField ModifyPartCost;
    public TextField ModifyPartVarField;
    public Label PartType;
    public Button ModifyPartSave;
    public Button ModifyPartCancel;

    Stage stage;
    Parent scene;

    //flag to keep track of which part type is selected
    private boolean trueInHouse;

    //Keeps track of the index, which is needed for the Inventory.updatePart() method
    //This is the part counter in the inventory that we use to auto assign a part ID
    protected int autoID = MainScreen.mainPartTableIndex;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Auto the fields with the part's existing values
        Part partMod = Inventory.getAllParts().get(autoID);

        ModifyPartID.setText(Integer.toString(autoID + 1));
        ModifyPartName.setText(partMod.getName());
        ModifyPartInv.setText( Integer.toString(partMod.getStock()));
        ModifyPartMin.setText(Integer.toString(partMod.getMin()));
        ModifyPartMax.setText(Integer.toString(partMod.getMax()));
        ModifyPartCost.setText(Double.toString(partMod.getPrice()));

        if(partMod instanceof InHouse){
            ModifyPartInHouse.setSelected(true);
            PartType.setText("Machine ID");
            //partMod is of class Part not InHouse so I have to grab cast this to anInHouse part
            ModifyPartVarField.setText(Integer.toString(((InHouse)Inventory.getAllParts().get(autoID)).getMachineID()));
        } if (partMod instanceof Outsourced) {
            ModifyPartInHouse.setSelected(false);
            PartType.setText("Company Name");
            //partMod is of class Part not InHouse so I have to grab cast this to Outsourced part
            ModifyPartVarField.setText(((Outsourced)Inventory.getAllParts().get(autoID)).getCompanyName());
        }

    }
    public void setInHouse(MouseEvent mouseEvent) {
        PartType.setText("Machine ID");
        trueInHouse = true;
    }

    public void setOutsourced(MouseEvent mouseEvent) {
        PartType.setText("Company Name");
        trueInHouse = false;
    }

    public void savePart(MouseEvent mouseEvent) throws IOException {
        stage = (Stage)((Button)mouseEvent.getSource()).getScene().getWindow();

        try {

            //Grab whatever the text fields contain to validate them and modify the part Accordingly
            int pMin = Integer.parseInt(ModifyPartMin.getText());
            int pMax = Integer.parseInt(ModifyPartMax.getText());
            String pName = new String(ModifyPartName.getText());
            int pInv = Integer.parseInt(ModifyPartInv.getText());
            double pPrice = Double.parseDouble(ModifyPartCost.getText());
            String pType = ModifyPartVarField.getText();

            if (pMin > pMax) {
                throw new MinOverMaxException();
            }

            if (pInv < pMin || pInv > pMax) {
                throw new InventoryOutOfRangeException();
            }

            if (trueInHouse) {
                int machineID = Integer.parseInt(pType);
                InHouse part = new InHouse(autoID, pName, pPrice, pInv, pMin, pMax, machineID);
                Inventory.updatePart(autoID, part);
            } else {
                String companyName = new String(pType);
                Outsourced part = new Outsourced(autoID, pName, pPrice, pInv, pMin, pMax, companyName);
                Inventory.updatePart(autoID, part);
            }

            Parent root = FXMLLoader.load(getClass().getResource("/View/MainScreen.fxml"));
            Scene scene = new Scene(root, 850, 400);
            stage.setTitle("Main Screen");
            root.setStyle("-fx-font-family: 'Times New Roman';");
            stage.setScene(scene);
            stage.show();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Add Part Failed");
            alert.setContentText("Part cannot be added, empty or invalid values in one or more fields");
            alert.getDialogPane().getScene().getRoot().setStyle("-fx-font-family:'Times New Roman';");
            alert.showAndWait();
        } catch (MinOverMaxException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Add Part Failed");
            alert.setContentText("Min cannot be more than Max");
            alert.getDialogPane().getScene().getRoot().setStyle("-fx-font-family:'Times New Roman';");
            alert.showAndWait();
        } catch (InventoryOutOfRangeException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Add Part Failed");
            alert.setContentText("Inventory must be between Min and Max");
            alert.getDialogPane().getScene().getRoot().setStyle("-fx-font-family:'Times New Roman';");
            alert.showAndWait();
        }
    }

    public void cancel(MouseEvent mouseEvent) throws IOException {
        stage = (Stage)((Button)mouseEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/View/MainScreen.fxml"));
        scene.setStyle("-fx-font-family: 'Times New Roman';");
        stage.setScene(new Scene(scene, 850, 400));
        stage.show();
    }
}
