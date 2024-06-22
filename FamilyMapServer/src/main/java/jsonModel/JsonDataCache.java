package jsonModel;

import java.util.Random;

public class JsonDataCache {
    FNamesData fNamesData;
    MNamesData mNamesData;
    SNamesData sNamesData;
    LocationData locationData;
    private static JsonDataCache instance = new JsonDataCache();
    Random rand = new Random();
    public static JsonDataCache getInstance() {
        if(instance == null) {
            instance = new JsonDataCache();
        }
        return instance;
    }

    private JsonDataCache() {}

    public void loadData(FNamesData fNamesData, MNamesData mNamesData, SNamesData sNamesData, LocationData locationData) {
        this.fNamesData = fNamesData;
        this.mNamesData = mNamesData;
        this.sNamesData = sNamesData;
        this.locationData = locationData;
    }

    public String randomFemaleName() {
        int index = rand.nextInt(fNamesData.getData().length - 1); //do I need the minus 1?
        return fNamesData.getData()[index];
    }
    public String randomMaleName() {
        int index = rand.nextInt(mNamesData.getData().length - 1);
        return mNamesData.getData()[index];
    }
    public String randomLastName() {
        int index = rand.nextInt(sNamesData.getData().length - 1);
        return sNamesData.getData()[index];
    }
    public Location randomLocation() {
        int index = rand.nextInt(locationData.getData().length - 1);
        return locationData.getData()[index];
    }
}