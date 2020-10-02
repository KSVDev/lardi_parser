package com.kozinets.sergey.lardi_parser;

import org.springframework.stereotype.Service;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
//@Scope("prototype")
public class AppService {

    private Parser parser;
    private List<List<String>> previousDataList;
    private int counter = 0;
    private boolean isTesting = false;
    private String previousUrl;
    private List<List<String>> newDataList;
    private List<List<String>> messageList;

    public AppService(Parser parser) {
        this.parser = parser;
    }

    public Parser getParser() {
        return parser;
    }

    public AppService setParser(Parser parser) {
        this.parser = parser;
        return this;
    }

    public String getUrl() {
        return parser.getUrl();
    }

    private void printCargos(List<List<String>> data){
        int ind = 0;
        for (List<String> row : data) {
            System.out.print(ind++ + ": ");
            System.out.println(Arrays.toString(row.toArray()));
        }
    }

    private void fillPreviousDataList(List<List<String>> data){
        System.out.println("fillPreviousDataList(List<List<String>> data)");
        previousDataList = new ArrayList<>(data);

        if(isTesting){
            System.out.println("previousDataList.size(): " + previousDataList.size());
            previousDataList.remove(1);
            previousDataList.remove(1);
            previousDataList.remove(1);
            previousDataList.remove(1);
            previousDataList.remove(1);
            System.out.println("previousDataList.size() after test: " + previousDataList.size());
            return;
        }
        if(isTesting) {
            printCargos(listWithHeader(previousDataList));
        }
        System.out.println("previousDataList.size(): " + previousDataList.size());
    }

    private String parseUrl(String url){
        try{
            return url.substring(url.indexOf("http"), url.indexOf("html") + 4);
        } catch (Exception e){
            return "";
        }
    }

    private List<List<String>> getListIfNotTime(){
        List<List<String>> emptyRes = null;
        if(!parser.checkTimeGap()){
            emptyRes = new ArrayList<>();
            List<String> innerList = new ArrayList<>();
            innerList.add(getUrl());
            innerList.add("Еще не прошла минута после предыдущего запроса");

            Duration duration = Duration.between(parser.getPreviousSearchTime().plusMinutes(1), LocalDateTime.now());
            long diffLong = Math.abs(duration.toMillis() / 1000);
            int diffDouble = (int) Math.ceil( (double) diffLong);
            String diff = Integer.toString(diffDouble);
            innerList.add("Осталось: " + diff + " секунд");

            emptyRes.add(innerList);
        }
        return emptyRes;
    }

    public List<List<String>> setOldData(String url) throws IOException {
//        System.out.println("System.gc(); --- START: " + LocalDateTime.now());
//        System.gc();
//        System.out.println("System.gc(); --- FINISH: " + LocalDateTime.now());

        url = parseUrl(url);
        if(url.equals("")){
            return listWithHeader(previousDataList);
        }

        if(url.equals(getUrl())
                && previousDataList != null
                && previousDataList.size() > 0
                && newDataList != null
                && newDataList.size() > 0
//                && !newDataList.get(0).get(0).equals(getUrl())
        ){
            List<List<String>> newPreviousDataList = new ArrayList<>(newDataList);
            newPreviousDataList.addAll(previousDataList);

            newDataList = new ArrayList<>();
            messageList = new ArrayList<>();
            List<String> innerList = new ArrayList<>();
            innerList.add(url);
            messageList.add(innerList);
            previousDataList = newPreviousDataList;
            return listWithHeader(previousDataList);
        }

        List<List<String>> emptyRes = getListIfNotTime();
        if(emptyRes != null){
            return emptyRes;
        }

        parser.setUrl(url);
        System.out.println("setOldData(String url): " + url);
        List<List<String>> parsedDataList = parser.getParsedData();
        fillPreviousDataList(parsedDataList);
        newDataList = new ArrayList<>();
        messageList = new ArrayList<>();
        List<String> innerList = new ArrayList<>();
        innerList.add(url);
        messageList.add(innerList);
        return listWithHeader(parsedDataList);
    }

    public List<List<String>> getNewData(String url) throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        url = parseUrl(url);
        List<List<String>> emptyRes = getListIfNotTime();
        if(emptyRes != null){
            return emptyRes;
        }

        boolean fillPreviousData = false;
        if(! url.toLowerCase().equals(getUrl().toLowerCase())){
            fillPreviousData = true;
        }
        counter ++;
        System.out.println("getNewData(String url): " + url);
        parser.setUrl(url);
        System.out.println("parser.getUrl(): " + parser.getUrl());
        List<List<String>> parsedDataList = parser.getParsedData();

        //Fill out previousData with new data if url is changed
        if(fillPreviousData){
            fillPreviousDataList(parsedDataList);
            fillPreviousData = false;
        }

        newDataList = new ArrayList<>(parsedDataList);
        newDataList.removeAll(previousDataList);

        if(isTesting) {
            printCargos(newDataList);
            System.out.println("printCargos(newDataList); is finished");
        }

        if(newDataList.size() > 0){
            Alert.gav();
        }
        return listWithHeader(newDataList);
    }

    private List<List<String>> listWithHeader(List<List<String>> listIn){
        List<List<String>> listWithHeader = new ArrayList<>();
        listWithHeader.add(parser.getHeader());
        listWithHeader.addAll(listIn);

        return listWithHeader;
    }

    public List<List<String>> getCargos(){
        return listWithHeader(previousDataList);
    }

    public List<List<String>> getNewCargos(){
        if(newDataList == null || newDataList.size() == 0){
            return messageList;
        }
        return listWithHeader(newDataList);
    }

    public void off(){
        System.exit(0);
    }

}
