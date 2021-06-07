package com.cosmetics;

public class CosmeticsData {
    public long getFetchedTime() {
        return fetchedTime;
    }

    public void setFetchedTime(long fetchedTime) {
        this.fetchedTime = fetchedTime;
    }

    public CosmeticsPlayer getPlayer() {
        return cp;

    }
    private long fetchedTime;


    public CosmeticsData(long fetchedTime, CosmeticsPlayer cp) {
        this.fetchedTime = fetchedTime;
        this.cp = cp;
    }

    private final CosmeticsPlayer cp;
}
