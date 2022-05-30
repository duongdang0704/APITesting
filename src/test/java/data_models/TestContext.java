package data_models;

import java.util.*;

public class TestContext {

    public List<TPack> getTPacks() {
        return tPacks;
    }

    List<TPack> tPacks;

    public void setTPacks(TPack[] tPacks){
        this.tPacks = Arrays.asList(tPacks);
    }

    public void addTPack(TPack tPack){
        tPacks.add(tPack);
    }

    public TPack getTPack(String isMixed){
        for (TPack tPack:tPacks
             ) {
            if(tPack.IsMixed.equals(isMixed)){
                return tPack;
            }
        }
        return null;
    }
    public TPack getTPack(String catchWeightFlag, String isMixed){
        if(catchWeightFlag.equals("true")){
            for (TPack tPack:tPacks
            ) {
                if(tPack.IsMixed.equals(isMixed) && tPack.getStockBalance("true") != null){
                   return tPack;
                }
            }
        }else {
            for (TPack tPack:tPacks
            ) {
                if(tPack.IsMixed.equals(isMixed) && tPack.getStockBalance("false") != null){
                    return tPack;
                }
            }
        }
        return null;
    }
    public String getScenario() {
        return scenario;
    }

    public void setScenario(String scenario) {
        this.scenario = scenario;
    }

    String scenario;
    Map<String, Object> testData = new HashMap();
    public void setData(String key, Object value){
        testData.put(key, value);
    }

    public Object getData(String key){
        return testData.get(key);
    }
}
