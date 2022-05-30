package steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import controllers.Logistics;
import data_models.StockBalance;
import data_models.TestContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import data_models.TPack;
import org.hamcrest.Matchers;
import org.testng.Assert;

import java.io.File;
import java.text.DecimalFormat;
import java.util.*;

public class LogisticsSteps extends BaseSteps{

    TestContext testContext;
    TPack tPack;
    DecimalFormat df = new DecimalFormat("0.0000");
    Response response;

    public LogisticsSteps(TestContext testContext){
        this.testContext = testContext;
    }

    @Then("A new Tpack is generated")
    public void aNewTpackIsGenerated() {
        response.then()
                .assertThat()
                .statusCode(200)
                .body("Result", Matchers.equalTo("OK"))
                .body(JsonSchemaValidator.matchesJsonSchema(new File("src/test/resources/schemas/SplitTPackSuccess.json")));
        JsonPath jsonResponse = response.jsonPath();
        testContext.setData("ToTPackNo", jsonResponse.get("TPackNo"));
        Assert.assertTrue(testContext.getData("ToTPackNo") != null);
    }

    @Then("The old Tpack is no longer exist")
    public void theOldTpackIsNoLongerExist(){
        Response response = Logistics.getTPackInfo("", "", tPack.Warehouse, tPack.TPackNo, "false", "", "");
        response.then()
                .assertThat()
                .statusCode(500);
    }

    @And("Use mixed Tpack {string}")
    public void useMixedTpack(String isMixed) {
        tPack = testContext.getTPack(isMixed);
    }

    @And("Stock balance of the splited Tpack is correct, mixed Tpack {string}")
    public void stockBalanceOfTheSplitedTpackIsCorrectMixedTpack(String isMixed) throws JsonProcessingException {
        Response response = Logistics.getTPackInfo("", "", tPack.Warehouse, tPack.TPackNo, "false", "", "");
        response.then()
                .assertThat()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchema(new File("src/test/resources/schemas/GetTPackInfoSuccess.json")));
        ObjectMapper mapper = new ObjectMapper();
        TPack[] tPacks = mapper.readValue(response.getBody().asString(), TPack[].class);
        TPack currentTPack = tPacks[0];
        StockBalance currentSB = currentTPack.getStockBalance((String)testContext.getData("CatchWeightFlag"));
        StockBalance expectedSB = tPack.getStockBalance((String)testContext.getData("CatchWeightFlag"));
        double remainingQty = Double.parseDouble(expectedSB.Quantity) - Double.parseDouble(testContext.getData("QuantityToMove").toString());
        double remainingCW = Double.parseDouble(expectedSB.NetWeight) - Double.parseDouble(testContext.getData("CatchWeightToMove").toString());
        Assert.assertEquals(currentTPack.TPackNo, tPack.TPackNo);
        Assert.assertEquals(currentTPack.Warehouse, tPack.Warehouse);
        Assert.assertEquals(currentTPack.StoragePosition, tPack.StoragePosition);
        Assert.assertEquals(currentTPack.IsMixed, isMixed);
        Assert.assertEquals(currentSB.ItemNo, expectedSB.ItemNo);
        Assert.assertEquals(currentSB.LotNo, expectedSB.LotNo);
        Assert.assertEquals(df.format(Double.parseDouble(currentSB.Quantity)), df.format(remainingQty));
        Assert.assertEquals(df.format(Double.parseDouble(currentSB.NetWeight)), df.format(remainingCW));
        Assert.assertEquals(currentSB.BasicUoM, expectedSB.BasicUoM);
    }

    @And("Stock balance of the new Tpack is correct, mixed Tpack {string}")
    public void stockBalanceOfTheNewTpackIsCorrectMixedTpack(String isMixed) throws JsonProcessingException {
        String warehouse = (String)testContext.getData("Warehouse");
        String toTPackNo = (String) testContext.getData("ToTPackNo");
        Response response = Logistics.getTPackInfo("", "", warehouse, toTPackNo, "false", "", "");
        response.then()
                .assertThat()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchema(new File("src/test/resources/schemas/GetTPackInfoSuccess.json")));
        ObjectMapper mapper = new ObjectMapper();
        TPack[] tPacks = mapper.readValue(response.getBody().asString(), TPack[].class);
        TPack currentTPack = tPacks[0];
        StockBalance stockBalance = currentTPack.getStockBalance((String)testContext.getData("CatchWeightFlag"));
        Assert.assertEquals(currentTPack.TPackNo, toTPackNo);
        Assert.assertEquals(currentTPack.Warehouse, (String)testContext.getData("Warehouse"));
        Assert.assertEquals(currentTPack.StoragePosition, (String)testContext.getData("Location"));
        Assert.assertEquals(currentTPack.PackagingType, (String) testContext.getData("PackagingType"));
        Assert.assertEquals(currentTPack.IsMixed, isMixed);
        Assert.assertEquals(stockBalance.ItemNo, (String)testContext.getData("ItemNo"));
        Assert.assertEquals(stockBalance.LotNo, (String)testContext.getData("LotNo"));
        Assert.assertEquals(df.format(Double.parseDouble(stockBalance.Quantity)), (String)testContext.getData("QuantityToMove"));
        Assert.assertEquals(df.format(Double.parseDouble(stockBalance.NetWeight)), (String)testContext.getData("CatchWeightToMove"));
        Assert.assertEquals(stockBalance.BasicUoM, (String)testContext.getData("UnitOfMeasure"));
    }

    @And("Stock balances of the new Tpack is correct, mixed Tpack")
    public void stockBalancesOfTheNewTpackIsCorrectMixedTpack() throws JsonProcessingException {
        String warehouse = (String)testContext.getData("Warehouse");
        String location = (String)testContext.getData("Location");
        String toTPackNo = (String) testContext.getData("ToTPackNo");
        Response response = Logistics.getTPackInfo("", "", warehouse, toTPackNo, "false", "", "");
        response.then()
                .assertThat()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchema(new File("src/test/resources/schemas/GetTPackInfoSuccess.json")));
        ObjectMapper mapper = new ObjectMapper();
        TPack[] tPacks = mapper.readValue(response.getBody().asString(), TPack[].class);
        TPack tPack = tPacks[0];
        Assert.assertEquals(tPack.TPackNo, toTPackNo);
        Assert.assertEquals(tPack.Warehouse, (String)testContext.getData("Warehouse"));
        Assert.assertEquals(tPack.StoragePosition, (String)testContext.getData("Location"));
        Assert.assertEquals(tPack.IsMixed, "true");
        List<StockBalance> currentSBs = tPack.getStockBalances((String)testContext.getData("CatchWeightFlag"));
        List<StockBalance> expectedSBs = (List<StockBalance>) testContext.getData("StockBalances");
        Assert.assertEquals(currentSBs.size(), expectedSBs.size());
        for(int i = 0; i < currentSBs.size(); i++){
            Assert.assertEquals(currentSBs.get(i).ItemNo, expectedSBs.get(i).ItemNo);
            Assert.assertEquals(currentSBs.get(i).LotNo, expectedSBs.get(i).LotNo);
            Assert.assertEquals(currentSBs.get(i).Quantity, expectedSBs.get(i).Quantity);
            Assert.assertEquals(currentSBs.get(i).BasicUoM, expectedSBs.get(i).BasicUoM);
        }
    }

    @When("Call API SplitTpack to split all stock balances except one")
    public void callAPISplitTpackToSplitAllStockBalancesExceptOne() {
        List<StockBalance> sbs = tPack.getAllStockBalances();
        sbs.remove(0);
        testContext.setData("StockBalances", sbs);
        testContext.setData("FromTPackNo", tPack.TPackNo);
        testContext.setData("Warehouse", tPack.Warehouse);
        testContext.setData("Location", tPack.StoragePosition);
        testContext.setData("PrintLabel", "false");
        response = Logistics.splitTPack("", "", tPack.Warehouse, tPack.TPackNo, "",
                "", "","false", sbs);

    }

    @When("Call API SplitTPack to split all stock balances")
    public void callAPISplitTPackToSplitAllStockBalances() {
        List<StockBalance> sbs = tPack.getAllStockBalances();
        testContext.setData("StockBalances", sbs);
        testContext.setData("FromTPackNo", tPack.TPackNo);
        testContext.setData("Warehouse", tPack.Warehouse);
        testContext.setData("Location", tPack.StoragePosition);
        testContext.setData("PrintLabel", "false");
        response = Logistics.splitTPack("", "", tPack.Warehouse, tPack.TPackNo, "",
                "", "","false", sbs);

    }

    @And("There is only a stock balance in the old Tpack, non mixed Tpack")
    public void thereIsOnlyStockBalanceInTheOldTpackNonMixedTpack() throws JsonProcessingException {
        Response response = Logistics.getTPackInfo("", "", tPack.Warehouse, tPack.TPackNo, "false", "", "");
        response.then()
                .assertThat()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchema(new File("src/test/resources/schemas/GetTPackInfoSuccess.json")));
        ObjectMapper mapper = new ObjectMapper();
        TPack[] tPacks = mapper.readValue(response.getBody().asString(), TPack[].class);
        TPack currentTPack = tPacks[0];
        List<StockBalance> currentSBs = currentTPack.getAllStockBalances();
        Assert.assertEquals(currentSBs.size(), 1);
        Assert.assertEquals(currentTPack.IsMixed, "false");

    }

    @And("Stock balances of the new Tpack is correct")
    public void stockBalancesOfTheNewTpackIsCorrect() throws JsonProcessingException {
        String warehouse = (String)testContext.getData("Warehouse");
        String location = (String)testContext.getData("Location");
        String toTPackNo = (String) testContext.getData("ToTPackNo");
        Response response = Logistics.getTPackInfo("", "", warehouse, toTPackNo, "false", "", "");
        response.then()
                .assertThat()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchema(new File("src/test/resources/schemas/GetTPackInfoSuccess.json")));
        ObjectMapper mapper = new ObjectMapper();
        TPack[] tPacks = mapper.readValue(response.getBody().asString(), TPack[].class);
        TPack currentTPack = tPacks[0];
        Assert.assertEquals(currentTPack.TPackNo, toTPackNo);
        Assert.assertEquals(currentTPack.Warehouse, (String)testContext.getData("Warehouse"));
        Assert.assertEquals(currentTPack.StoragePosition, (String)testContext.getData("Location"));
        List<StockBalance> currentSBs = currentTPack.getStockBalances((String)testContext.getData("CatchWeightFlag"));
        List<StockBalance> expectedSBs = (List<StockBalance>) testContext.getData("StockBalances");
        Assert.assertEquals(currentSBs.size(), expectedSBs.size());
        for(int i = 0; i < currentSBs.size(); i++){
            Assert.assertEquals(currentSBs.get(i).ItemNo, expectedSBs.get(i).ItemNo);
            Assert.assertEquals(currentSBs.get(i).LotNo, expectedSBs.get(i).LotNo);
            Assert.assertEquals(currentSBs.get(i).Quantity, expectedSBs.get(i).Quantity);
            Assert.assertEquals(currentSBs.get(i).BasicUoM, expectedSBs.get(i).BasicUoM);
        }
    }

    @When("Call API SplitTpack where quantityToMove = {string} quantity and catchWeightToMove = {string} net weight of stock balance")
    public void callAPISplitTpackToSplitStockBalance(String quantityPortion, String catchWeightPortion) {
        StockBalance sb = tPack.getStockBalance((String) testContext.getData("CatchWeightFlag"));
        String quantityToMove = df.format(Double.parseDouble(sb.Quantity)*Double.parseDouble(quantityPortion));
        String catchWeightToMove = df.format(Double.parseDouble(sb.NetWeight)*Double.parseDouble(catchWeightPortion));
        testContext.setData("QuantityToMove", quantityToMove);
        testContext.setData("ItemNo", sb.ItemNo);
        testContext.setData("LotNo", sb.LotNo);
        testContext.setData("UnitOfMeasure", sb.BasicUoM);
        testContext.setData("FromTPackNo", tPack.TPackNo);
        testContext.setData("Warehouse", tPack.Warehouse);
        testContext.setData("Location", tPack.StoragePosition);
        testContext.setData("PackagingType", tPack.PackagingType);
        testContext.setData("PrintLabel", "false");
        testContext.setData("CatchWeightToMove", catchWeightToMove);
        response = Logistics.splitTPack("", "", tPack.Warehouse, tPack.TPackNo, "",
                "", "","false", sb.ItemNo, sb.LotNo, quantityToMove, sb.BasicUoM, catchWeightToMove);
    }

    @Given("A list of TPack is available on {string} and {string}")
    public void getTPackList(String warehouseId, String location) throws JsonProcessingException {
        Response response = Logistics.getTPackInfo("", "", warehouseId, "", "false","", location);
        response.then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchema(new File("src/test/resources/schemas/GetTPackInfoSuccess.json")));
        ObjectMapper mapper = new ObjectMapper();
        TPack[] tPacks = mapper.readValue(response.getBody().asString(), TPack[].class);
        testContext.setTPacks(tPacks);
        //Validate Warehouse and Location are correct
        for (TPack tPack:tPacks
        ) {
            Assert.assertEquals(tPack.Warehouse, warehouseId);
            Assert.assertEquals(tPack.StoragePosition, location);
        }
        testContext.setData("Warehouse", warehouseId);
        testContext.setData("Location", location);
    }

    @And("Use Tpack catch weight {string}and mixed Tpack {string}")
    public void useTpackCatchWeightAndMixedTpack(String catchWeightFlag, String isMixed) {
        tPack = testContext.getTPack(catchWeightFlag, isMixed);
        testContext.setData("CatchWeightFlag", catchWeightFlag);
    }

    @And("The stock balance does not exist in the old Tpack")
    public void theStockBalanceDoesNotExistInTheOldTpack() throws JsonProcessingException {
        Response response = Logistics.getTPackInfo("", "", tPack.Warehouse, tPack.TPackNo, "false", "", "");
        response.then()
                .assertThat()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchema(new File("src/test/resources/schemas/GetTPackInfoSuccess.json")));
        ObjectMapper mapper = new ObjectMapper();
        TPack[] tPacks = mapper.readValue(response.getBody().asString(), TPack[].class);
        TPack currentTPack = tPacks[0];
        List<StockBalance> sbs = currentTPack.StockBalance;
        for (StockBalance sb:sbs
             ) {
            String expectedItemLot = testContext.getData("ItemNo") + "-" + testContext.getData("LotNo");
            String currentItemLot = sb.ItemNo + "-" + sb.LotNo;
            Assert.assertNotEquals(currentItemLot, expectedItemLot);
        }

    }

    @When("Call API SplitTPack to split the new Tpack to the old one")
    public void callAPISplitTPackToSplitTheNewTpackToTheOldOne() {
        String toTPackNo = (String)testContext.getData("FromTPackNo");
        String fromTPackNo = (String)testContext.getData("ToTPackNo");
        String warehouse = (String)testContext.getData("Warehouse");
        String itemNo = (String)testContext.getData("ItemNo");
        String lotNo = (String)testContext.getData("LotNo");
        String unitOfMeasure = (String)testContext.getData("UnitOfMeasure");
        String quantityToMove = (String) testContext.getData("QuantityToMove");
        String catchWeightToMove = (String)testContext.getData("CatchWeightToMove");
        List<StockBalance> sbs = (List<StockBalance>) testContext.getData("StockBalances");
        if (sbs == null){
            response = Logistics.splitTPack("", "", warehouse, fromTPackNo, toTPackNo, "", "",
                    "false", itemNo, lotNo, quantityToMove, unitOfMeasure, catchWeightToMove);
        }else{
            response = Logistics.splitTPack("", "", warehouse, fromTPackNo, toTPackNo,
                    "", "","false", sbs);
        }
    }

    @Then("The stock balance does not change")
    public void theStockBalanceDoesNotChange() throws JsonProcessingException{
        Response response = Logistics.getTPackInfo("", "", tPack.Warehouse, tPack.TPackNo, "false", "", "");
        response.then()
                .assertThat()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchema(new File("src/test/resources/schemas/GetTPackInfoSuccess.json")));
        ObjectMapper mapper = new ObjectMapper();
        TPack[] tPacks = mapper.readValue(response.getBody().asString(), TPack[].class);
        TPack currentTPack = tPacks[0];
        List<StockBalance> currentSBs = currentTPack.StockBalance;
        List<StockBalance> expectedSBs = tPack.StockBalance;
        Assert.assertEquals(currentSBs.size(), expectedSBs.size());
        for(int i = 0; i < currentSBs.size(); i++){
            Assert.assertEquals(currentSBs.get(i).ItemNo, expectedSBs.get(i).ItemNo);
            Assert.assertEquals(currentSBs.get(i).LotNo, expectedSBs.get(i).LotNo);
            Assert.assertEquals(currentSBs.get(i).Quantity, expectedSBs.get(i).Quantity);
            Assert.assertEquals(currentSBs.get(i).BasicUoM, expectedSBs.get(i).BasicUoM);
        }
    }

    @When("Call API SplitTPack to split all stock balances with packaging type {string}")
    public void callAPISplitTPackToSplitAllStockBalancesWithPackagingType(String packagingType) {
        List<StockBalance> sbs = tPack.getAllStockBalances();
        testContext.setData("StockBalances", sbs);
        testContext.setData("FromTPackNo", tPack.TPackNo);
        testContext.setData("Warehouse", tPack.Warehouse);
        testContext.setData("Location", tPack.StoragePosition);
        testContext.setData("PrintLabel", "false");
        response = Logistics.splitTPack("", "", tPack.Warehouse, tPack.TPackNo, "",
                "", packagingType,"false", sbs);

    }

    @And("The packaging type {string} is correct")
    public void thePackagingTypeIsCorrect(String packingType) throws JsonProcessingException {
        String warehouse = (String)testContext.getData("Warehouse");
        String toTPackNo = (String) testContext.getData("ToTPackNo");
        Response response = Logistics.getTPackInfo("", "", warehouse, toTPackNo, "false", "", "");
        response.then()
                .assertThat()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchema(new File("src/test/resources/schemas/GetTPackInfoSuccess.json")));
        ObjectMapper mapper = new ObjectMapper();
        TPack[] tPacks = mapper.readValue(response.getBody().asString(), TPack[].class);
        TPack currentTPack = tPacks[0];
        Assert.assertEquals(currentTPack.PackagingType, packingType);
    }

    @When("Call API SplitTPack to split a stock balance with input parameters {string} {string} {string}")
    public void callAPISplitTPackToSplitStockBalanceWithInputParameters(String toLocation, String packagingType, String printLabel) {
        List<StockBalance> sbs = tPack.getAllStockBalances();
        List<StockBalance> useSBs =new ArrayList<>();
        useSBs.add(sbs.get(0));
        testContext.setData("StockBalances", useSBs);
        testContext.setData("FromTPackNo", tPack.TPackNo);
        testContext.setData("Warehouse", tPack.Warehouse);
        testContext.setData("ToLocation", toLocation);
        testContext.setData("PackagingType", packagingType);
        testContext.setData("PrintLabel", printLabel);
        response = Logistics.splitTPack("", "", tPack.Warehouse, tPack.TPackNo, "",
                toLocation, packagingType,"false", useSBs);

    }

    @And("Its information is correct")
    public void itsInformationIsCorrect() throws JsonProcessingException {
        String warehouse = (String)testContext.getData("Warehouse");
        String toTPackNo = (String) testContext.getData("ToTPackNo");
        Response response = Logistics.getTPackInfo("", "", warehouse, toTPackNo, "false", "", "");
        response.then()
                .assertThat()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchema(new File("src/test/resources/schemas/GetTPackInfoSuccess.json")));
        ObjectMapper mapper = new ObjectMapper();
        TPack[] tPacks = mapper.readValue(response.getBody().asString(), TPack[].class);
        TPack currentTPack = tPacks[0];
        Assert.assertEquals(currentTPack.PackagingType, testContext.getData("PackagingType"));
        Assert.assertEquals(currentTPack.StoragePosition, testContext.getData("ToLocation"));
    }

    @Then("The error {string} should display")
    public void theErrorShouldDisplay(String arg0) {
        response.then()
                .assertThat()
                .statusCode(500)
                .body("Result", Matchers.equalTo("NOK"));
    }

    @When("Call API SplitTpack with invalid quantity or catch weight {string} {string}")
    public void callAPISplitTpackWithInvalidQuantityOrCatchWeight(String quantity, String catchWeight) {
        StockBalance sb = tPack.getStockBalance((String) testContext.getData("CatchWeightFlag"));
        response = Logistics.splitTPack("", "", tPack.Warehouse, tPack.TPackNo, "", "", "",
                "false", sb.ItemNo, sb.LotNo, quantity, sb.BasicUoM, catchWeight);
    }

    @Given("Get TPacks by criteria")
    public void getTPacksByCriteria(io.cucumber.datatable.DataTable data) throws JsonProcessingException {
        Map<String, String> criteria = data.transpose().asMap(String.class, String.class);
        Response response = Logistics.getTPackInfo(criteria.get("UserId"), criteria.get("UserName"),
                            criteria.get("Warehouse"), criteria.get("TPackNo"), criteria.get("IncludeAttributes"),
                            criteria.get("ApiVariant"), criteria.get("Location"));
        ObjectMapper mapper = new ObjectMapper();
        TPack[] tPacks = mapper.readValue(response.getBody().asString(), TPack[].class);
        testContext.setTPacks(tPacks);
    }
}
