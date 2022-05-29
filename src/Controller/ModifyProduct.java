package Controller;

import Model.Inventory;
import Model.Part;
import Model.Product;
import UserExceptions.InventoryOutOfRangeException;
import UserExceptions.MinOverMaxException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ModifyProduct implements Initializable {
    public TextField ModifyProductMin;
    public TextField ModifyProductMax;
    public TextField ModifyProductID;
    public TextField ModifyProductName;
    public TextField ModifyProductInv;
    public TextField ModifyProductPrice;
    public Button ModifyProductSearchButton;
    public TextField ModifyProductSearch;
    public TableView ModifyProductAddTable;
    public TableColumn addPricePerUnit;
    public TableColumn addInvLevel;
    public TableColumn addPartName;
    public TableColumn addPartID;
    public Button ModifyProductAdd;
    public TableView ModifyProductDeleteTable;
    public TableColumn deletePricePerUnit;
    public TableColumn deleteInvLevel;
    public TableColumn deletePartName;
    public TableColumn deletePartID;
    public Button ModifyProductDelete;
    public Button ModifyProductSave;
    public Button ModifyProductCancel;

    Stage stage;
    Parent scene;

    private ObservableList<Part> partsToAdd = FXCollections.observableArrayList();

    //private int autoID = MainScreen.mainProdTableIndex;
    private int autoID;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //Populate Add Parts table
        ModifyProductAddTable.setItems(Inventory.getAllParts());
        addPartID.setCellValueFactory(new PropertyValueFactory<>("id"));
        addPartName.setCellValueFactory(new PropertyValueFactory<>("name"));
        addInvLevel.setCellValueFactory(new PropertyValueFactory<>("stock"));
        addPricePerUnit.setCellValueFactory(new PropertyValueFactory<>("price"));

        //Populate Delete Parts table
        ModifyProductDeleteTable.setItems(Inventory.getAllProducts().get(autoID).getAllAssociatedParts());
        //ModifyProductDeleteTable.setItems(Inventory.getAllProducts().get(ad));

        deletePartID.setCellValueFactory(new PropertyValueFactory<>("id"));
        deletePartName.setCellValueFactory(new PropertyValueFactory<>("name"));
        deleteInvLevel.setCellValueFactory(new PropertyValueFactory<>("stock"));
        deletePricePerUnit.setCellValueFactory(new PropertyValueFactory<>("price"));

        //Autofills the fields with the part's existing values
        ModifyProductID.setText(Integer.toString(autoID + 1 ));
        Product prodMod = Inventory.getAllProducts().get(autoID);
        ModifyProductName.setText(prodMod.getName());
        ModifyProductInv.setText( Integer.toString(prodMod.getStock()));
        ModifyProductMin.setText(Integer.toString(prodMod.getMin()));
        ModifyProductMax.setText(Integer.toString(prodMod.getMax()));
        ModifyProductPrice.setText(Double.toString(prodMod.getPrice()));
    }

    public void lookupPart(MouseEvent mouseEvent) {
        String search = ModifyProductSearch.getText();
        ObservableList<Part> found = FXCollections.observableArrayList();
        found = Inventory.lookupPart(search);

        if(found.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No part Found");
            alert.setContentText("No part found with name " +search);
            alert.getDialogPane().getScene().getRoot().setStyle("-fx-font-family:'Times New Roman';");
            alert.showAndWait();
        } else {
            ModifyProductAddTable.setItems(found);
        }

    }

    public void addPart(MouseEvent mouseEvent) throws  IOException{
        partsToAdd.add(Inventory.lookupPart((Inventory.getAllParts().indexOf((ModifyProductAddTable.getSelectionModel().getSelectedItem()))+ 1)));



    }

    public void deletePart(MouseEvent mouseEvent) {
        Part part = (Part) ModifyProductDeleteTable.getSelectionModel().getSelectedItem();

        Inventory.getAllProducts().get(autoID).deleteAssociatedPart(part);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Deleted");
        alert.setHeaderText("Part Removed");
        alert.setContentText("Part Successfully Deleted");
        alert.getDialogPane().getScene().getRoot().setStyle("-fx-font-family:'Times New Roman';");
        alert.showAndWait();
    }

    public void saveProduct(MouseEvent mouseEvent) throws IOException {
        //Fulfilling the validation requirement in Set 1, make sure min is not greater than max
        //Also making sure that strings aren't being entered where an int is required
        try {
            int pID = Integer.parseInt(ModifyProductID.getText());
            int pMin = Integer.parseInt(ModifyProductMin.getText());
            int pMax = Integer.parseInt(ModifyProductMax.getText());
            String pName = new String(ModifyProductName.getText());
            int pInv = Integer.parseInt(ModifyProductInv.getText());
            double pPrice = Double.parseDouble(ModifyProductPrice.getText());

            if (pMin > pMax) {
                throw new MinOverMaxException();
            }

            if (pInv < pMin || pInv > pMax) {
                throw new InventoryOutOfRangeException();
            } else {

                Product product = new Product(autoID, pName, pPrice, pInv, pMin, pMax);
                Inventory.updateProduct(autoID, product);
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
            alert.setHeaderText("Add product Failed");
            alert.setContentText("Product cannot be added, empty or invalid values in one or more fields");
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
    public void cancel(MouseEvent mouseEvent) throws IOException{
        stage = (Stage)((Button)mouseEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/View/MainScreen.fxml"));
        scene.setStyle("-fx-font-family: 'Times New Roman';");
        stage.setScene(new Scene (scene,850,400));
        stage.show();
    }
}
