package Controller;

import Model.InHouse;
import Model.Inventory;
import Model.Outsourced;
import Model.Part;
import Controller.ModifyPart;
import UserExceptions.CompanyNameNullException;
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
import java.util.Optional;
import java.util.ResourceBundle;

public class AddPart implements Initializable {

    Stage stage;
    Parent scene;

    public RadioButton SelectInHouse;
    public ToggleGroup PartType;
    public RadioButton SelectOutsourced;
    public TextField AddPartMax;
    public TextField AddPartMin;
    public TextField AddPartID;
    public TextField AddPartName;
    public TextField AddPartInv;
    public TextField AddPartPrice;
    public TextField AddPartDynamicField;
    public Label TypeLabel;
    public Button addPartSave;
    public Button addPartCancel;

    //This is true if InHouse is selected, false if Outsourced
    //this lets us see which is selected later so we can change which part type to make add
    private boolean trueInHouse;

    //This is the part counter in the inventory that we use to auto assign a part ID
    private int autoID;

    @Override

    public void initialize(URL location, ResourceBundle resources) {
        autoID = Inventory.assignPartID();
        AddPartID.setText(Integer.toString(autoID));

        trueInHouse = true;
        TypeLabel.setText("Machine ID");

    }

    public void renderMachineID(MouseEvent mouseEvent) {
        TypeLabel.setText("Machine ID");
        trueInHouse = true;
        System.out.println("true");
    }

    public void renderOutsourced(MouseEvent mouseEvent) {
        TypeLabel.setText("Company Name");
        trueInHouse = false;
        System.out.println("false");
    }

    public void addPart(MouseEvent mouseEvent) throws IOException {
        System.out.println(Inventory.assignPartID());
        try {

            //Grab whatever the text fields contain to validate them and then create a part with them
            int pMin = Integer.parseInt(AddPartMin.getText());
            int pMax = Integer.parseInt(AddPartMax.getText());
            String pName = (AddPartName.getText());
            int pInv = Integer.parseInt(AddPartInv.getText());
            double pPrice = Double.parseDouble(AddPartPrice.getText());
            String pType = (AddPartDynamicField.getText());

            if (pMin > pMax) {
                throw new MinOverMaxException();
            }
            /*LOGICAL ERROR checking Inv is between min and max. Created a Custom UserExceptions to handle error and
                error window
            */

            if (pInv < pMin || pInv > pMax) {
                throw new InventoryOutOfRangeException();
            }

            if (trueInHouse) {
                int machineID = Integer.parseInt(pType);
                InHouse part = new InHouse(autoID, pName, pPrice, pInv, pMin, pMax, machineID);
                Inventory.addPart(part);

            } else {
                String companyName = (pType);
                Outsourced part = new Outsourced(autoID, pName, pPrice, pInv, pMin, pMax, companyName);
                Inventory.addPart(part);
            }

            Parent root = FXMLLoader.load(getClass().getResource("/View/MainScreen.fxml"));
            stage = (Stage) ((Button) mouseEvent.getSource()).getScene().getWindow();
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
