package data_models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StockBalance {
    public String ItemNo;
    public String Description;
    public String LotNo;
    public String Quantity;
    public String NetWeight;
    public String BasicUoM;
    public String ContainerMgt;
    public String AllocatedQty;
    public String Allocateable;
    public String ReceivingNo;
    public String OrderLine;
    public String PutAwayNo;
    public String TransQty;
    public String MfgDate;
    public String ExpiryDate;
    public String BBD;
    public String Customer;
    public String Status;
    public String StateCode;
    public String AttrNoStkBal;
    public String AttrNoLot;
    public String TextID;
    public String CatchWeightFlag;
    public AttributesStockBalance AttributesStockBalance;
    public AttributesLot AttributesLot;
}
