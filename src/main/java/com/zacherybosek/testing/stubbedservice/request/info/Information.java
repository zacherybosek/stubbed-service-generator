package com.zacherybosek.testing.stubbedservice.request.info;

import com.zacherybosek.testing.stubbedservice.controller.MetricsController.InfoTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Zachery on 7/25/2016.
 */
public class Information {
    private InfoTypes infoType;
    private long totalHits = 0;
    private long expectedHits = 0;
    private long unexpectedHits = 0;

    private List<String> expectedCalls = new ArrayList<>();
    private List<String> unexpectedCalls = new ArrayList<>();

    private Map<Integer, Integer> expectedResponseCodeCounts = new HashMap<>();
    private Map<Integer, Integer> unexpectedResponseCodeCounts = new HashMap<>();

    public Information(InfoTypes infoType) {
        this.infoType = infoType;
    }

    public void addExpectedInvoke(SizeStoredSizeLimitedLinkedList<InvokeInformation> infos) {
        if(infos != null) {
            totalHits+=infos.size();
            expectedHits+=infos.size();
            for(InvokeInformation info: infos) {
                if(InfoTypes.LONG.equals(infoType)) {
                    expectedCalls.add(info.getIncomingUrl());
                    addExpectedResponseCodeCount(info.getResponseCode());
                }
            }
        }
    }

    public void addUnexpectedInvoke(SizeStoredSizeLimitedLinkedList<InvokeInformation> infos) {
        if(infos != null) {
            totalHits+=infos.size();
            unexpectedHits+=infos.size();
            for(InvokeInformation info: infos) {
                if(InfoTypes.LONG.equals(infoType)) {
                    unexpectedCalls.add(info.getIncomingUrl());
                    addUnexpectedResponseCodeCount(info.getResponseCode());
                }
            }
        }
    }

    private void addUnexpectedResponseCodeCount(int responseCodeIn) {
        Integer responseCode = Integer.valueOf(responseCodeIn);
        if(unexpectedResponseCodeCounts.containsKey(responseCode)) {
            int newValue = unexpectedResponseCodeCounts.get(responseCode).intValue()+1;
            unexpectedResponseCodeCounts.put(responseCode, newValue);
        }
        else {
            unexpectedResponseCodeCounts.put(responseCode, 1);
        }
    }

    private void addExpectedResponseCodeCount(int responseCodeIn) {
        Integer responseCode = Integer.valueOf(responseCodeIn);
        if(expectedResponseCodeCounts.containsKey(responseCode)) {
            int newValue = expectedResponseCodeCounts.get(responseCode).intValue()+1;
            expectedResponseCodeCounts.put(responseCode, newValue);
        }
        else {
            expectedResponseCodeCounts.put(responseCode, 1);
        }
    }

    public InfoTypes getInfoType() {
        return infoType;
    }

    public long getTotalHits() {
        return totalHits;
    }

    public long getExpectedHits() {
        return expectedHits;
    }

    public long getUnexpectedHits() {
        return unexpectedHits;
    }

    public List<String> getExpectedCalls() {
        return expectedCalls;
    }

    public List<String> getUnexpectedCalls() {
        return unexpectedCalls;
    }

    public Map<Integer, Integer> getExpectedResponseCodeCounts() {
        return expectedResponseCodeCounts;
    }

    public Map<Integer, Integer> getUnexpectedResponseCodeCounts() {
        return unexpectedResponseCodeCounts;
    }
}

