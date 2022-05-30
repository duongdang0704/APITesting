package controllers;

import data_models.Authentication;
import data_models.StockBalance;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class Logistics {

    public static Response splitTPack(String userId, String userName, String warehouseId, String fromTPackNo,
                                      String toTPackNo, String toLocation, String packagingType, String printLabel, List<StockBalance> sbs){
        String sbString = "[";
        for (StockBalance sb:sbs
             ) {
            sbString += "{itemNo:\"" + sb.ItemNo +
                    "\", lotNo: \"" + sb.LotNo +
                    "\",quantityToMove: \"" + sb.Quantity +
                    "\", unitOfMeasure: \"" + sb.BasicUoM +
                    "\", catchWeightToMove: \"" + sb.NetWeight + "\"},";

        }
        String requestBody = sbString.substring(0, sbString.length() - 1) + "]";
        System.out.println(requestBody);
        Map<String, String> inputParas = new HashMap<>();
        inputParas.put("UserId", "");
        inputParas.put("UserName", "");
        inputParas.put("WarehouseId", warehouseId);
        inputParas.put("FromTPackNo", fromTPackNo);
        inputParas.put("ToTPackNo", toTPackNo);
        inputParas.put("ToLocation", toLocation);
        inputParas.put("PackagingType", packagingType);
        inputParas.put("PrintLabel", printLabel);
        Response response = given()
                .log().uri()
                .contentType("application/json")
                .queryParams(inputParas)
                .body(requestBody)
                .auth().oauth2(Authentication.TOKEN)
                .when()
                .post("/Logistics/SplitTPack");

        System.out.println(response.asString());
        return response;
    }
    public static Response splitTPack(String userId, String userName, String warehouseId, String fromTPackNo,
                               String toTPackNo, String toLocation, String packagingType, String printLabel,
                               String itemNo, String lotNo, String quantityToMove, String unitOfMeasure, String catchWeightToMove){

        Map<String, String> inputParas = new HashMap<>();
        inputParas.put("UserId", "");
        inputParas.put("UserName", "");
        inputParas.put("WarehouseId", warehouseId);
        inputParas.put("FromTPackNo", fromTPackNo);
        inputParas.put("ToTPackNo", toTPackNo);
        inputParas.put("ToLocation", toLocation);
        inputParas.put("PackagingType", packagingType);
        inputParas.put("PrintLabel", printLabel);
        String requestBody = "[{itemNo:\"" + itemNo +
                "\", lotNo: \"" + lotNo +
                "\",quantityToMove: \"" + quantityToMove +
                "\", unitOfMeasure: \"" + unitOfMeasure +
                "\", catchWeightToMove: \"" + catchWeightToMove + "\"}]";
        Response response = given()
                .log().uri()
                .contentType("application/json")
                .queryParams(inputParas)
                .body(requestBody)
                .auth().oauth2(Authentication.TOKEN)
                .when()
                .post("/Logistics/SplitTPack");
        return response;
    }

    public static Response getTPackInfo(String userId, String userName, String warehouseId, String tPackNo,
                                 String includeAttributes, String apiVariant, String location){
        Map<String, String> inputParas = new HashMap<>();
        inputParas.put("UserId", "");
        inputParas.put("UserName", "");
        inputParas.put("WarehouseId", warehouseId);
        inputParas.put("TPackNo", tPackNo);
        inputParas.put("IncludeAttributes", includeAttributes);
        inputParas.put("ApiVariant", apiVariant);
        inputParas.put("Location", location);

        Response response = given()
                .log().uri()
                .contentType("application/json")
                .params(inputParas)
                .auth().oauth2(Authentication.TOKEN)
                .when()
                .get("/Logistics/GetTPackInfo");
        return response;
    }
    public static Response splitTPack(String userId, String userName, String warehouseId, String fromTPackNo,
                                      String toTPackNo, String toLocation, String packagingType, String printLabel, StockBalance sb){
        String requestBody = "[{itemNo:\"" + sb.ItemNo +
                    "\", lotNo: \"" + sb.LotNo +
                    "\",quantityToMove: \"" + sb.Quantity +
                    "\", unitOfMeasure: \"" + sb.BasicUoM +
                    "\", catchWeightToMove: \"" + sb.NetWeight + "\"}]";
        Map<String, String> inputParas = new HashMap<>();
        inputParas.put("UserId", "");
        inputParas.put("UserName", "");
        inputParas.put("WarehouseId", warehouseId);
        inputParas.put("FromTPackNo", fromTPackNo);
        inputParas.put("ToTPackNo", toTPackNo);
        inputParas.put("ToLocation", toLocation);
        inputParas.put("PackagingType", packagingType);
        inputParas.put("PrintLabel", printLabel);
        Response response = given()
                .log().uri()
                .contentType("application/json")
                .queryParams(inputParas)
                .body(requestBody)
                .auth().oauth2(Authentication.TOKEN)
                .when()
                .post("/Logistics/SplitTPack");
        return response;
    }
}
