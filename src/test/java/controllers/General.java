package controllers;

import data_models.Authentication;
import data_models.StockBalance;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class General {

    public static Response setAttribute(String userId, String userName, String attrNo, String attrID, String attrValue){

        Map<String, String> inputParas = new HashMap<>();
        inputParas.put("UserId", "");
        inputParas.put("UserName", "");
        inputParas.put("AttrNo", attrNo);
        inputParas.put("AttrID", attrID);
        inputParas.put("AttrValue", attrValue);
        Response response = given()
                .log().uri()
                .contentType("application/json")
                .params(inputParas)
                .auth().oauth2(Authentication.TOKEN)
                .when()
                .post("/General/SetAttribute");

        System.out.println(response.asString());
        return response;
    }
}
