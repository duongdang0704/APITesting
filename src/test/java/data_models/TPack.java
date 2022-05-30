package data_models;

import io.cucumber.core.internal.com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class TPack {
    public String TPackStateCode;
    public String TPackNo;
    public String Warehouse;
    public String StockZone;
    public String PackagingType;
    public String StoragePosition;
    public String IsMixed;
    public List<StockBalance> StockBalance;

    public StockBalance getStockBalance(String catchWeightFlag){
        for (StockBalance sb: StockBalance
             ) {
            if (catchWeightFlag.equals("true") && sb.CatchWeightFlag.equals("2"))
            {
                return sb;
            }
            else if(catchWeightFlag.equals("false") && (sb.CatchWeightFlag.equals("") || sb.CatchWeightFlag.equals("0"))){
                return sb;
            }
        }
        return null;
    }
    public List<StockBalance> getAllStockBalances(){
        List<StockBalance> sbs = new ArrayList<>();
        for (StockBalance sb: StockBalance)
        {
            sbs.add(sb);
        }
        return sbs;
    }
    public List<StockBalance> getStockBalances(String catchWeightFlag){
        List<StockBalance> sbs = new ArrayList<>();
        for (StockBalance sb: StockBalance
        ) {
            if (catchWeightFlag.equals("true") && sb.CatchWeightFlag.equals("2"))
            {
                sbs.add(sb);
            }
            else if(catchWeightFlag.equals("false") && (sb.CatchWeightFlag.equals("") || sb.CatchWeightFlag.equals("0"))){
                sbs.add(sb);
            }
        }
        return sbs;
    }
    public StockBalance getStockBalance(String attrType, String attrNo){
        for (StockBalance sb: StockBalance
        ) {
            String values;
            if (attrType.equals("StockBalance") && sb.AttributesStockBalance.AttrNo.equals(attrNo)){
                return sb;
            }else if(attrType.equals("Lot") && sb.AttributesLot.AttrNo.equals(attrNo)) {
                return sb;
            }
        }
        return null;
    }
}
