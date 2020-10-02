package com.kozinets.sergey.lardi_parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



@Component
@EnableScheduling
//@Scope("prototype")
public class Parser {

    private String url = "";
    private List<List<String>> parsedDataList = null;
    private List<String> header = new ArrayList<>();
    private String cargoSiteStart = "https://lardi-trans.com/gruz/view/";

    private int lengthDefault = 41;
    private final static int FIXED_PARSE_RATE = 125000; //milliseconds
    private final static String RED_COLOR = "background-color: #FF9999";
    private final static String YELLOW_COLOR = "background-color: #FFCC66";
    private final static String BLUE_COLOR = "background-color: #C5E3F3";

    private LocalDateTime previousSearchTime;

    public LocalDateTime getPreviousSearchTime() {
        return previousSearchTime;
    }

    public void setPreviousSearchTime(LocalDateTime previousSearchTime) {
        this.previousSearchTime = previousSearchTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private Document getPage(String curUrl) throws IOException {
        Document page = Jsoup.parse(new URL(curUrl), 300000);
        return page;
    }

    public List<String> getHeader() {
        return header;
    }

    public boolean checkTimeGap(){
        if(previousSearchTime == null){
            previousSearchTime = LocalDateTime.now();
            return true;
        } else if(LocalDateTime.now().isAfter(previousSearchTime.plusMinutes(1))){
            previousSearchTime = LocalDateTime.now();
            return true;
        }
        return false;
    }

    private List<Elements> getFullTables() throws IOException {
        System.out.println("getFullTables()");
        List<Elements> pages = new ArrayList<Elements>();
        String curUrl = url;
        int length = url.length();
        int iterCount = 1;
        while(true){
            if(iterCount > 1){
                curUrl = url.substring(0, length - 7) + "x" + iterCount + url.substring(length - 7);
            }
            System.out.println("curUrlcurUrlcurUrlcurUrl: " + curUrl);
            Document page = getPage(curUrl);
            Elements tables = page.select("div[class=ps_search-result_data]");
            if(tables.size() == 0){
                break;
            }

            pages.add(tables);
            iterCount++;
        }
        return pages;
    }

    private void initParsedDataList(){
        System.out.println("initParsedDataList()");
        parsedDataList = new ArrayList<>();
    }

    @PostConstruct
    private void createHeader(){
        header = new ArrayList<>();
//        header.add("Свежесть");
        header.add("Дата создания");
//        header.add("Время создания");
        header.add("Дата загрузки");
        header.add("Транспорт");
        header.add("Откуда");
        header.add("Куда");
        header.add("Груз");
        header.add("Оплата");
        header.add("Детали оплаты");
        header.add("Страница заявки");
        header.add("ИД");
        header.add("Исходная страница");

    }

    private Elements parseDataItems(List<Elements> tables){
        System.out.println("parseDataItems(List<Elements> tables)");
        Elements dataItems = new Elements();

        for (Elements table : tables) {
            Elements curDataItems = table.select("div[class*=ps_search-result_data-item]");
            dataItems.addAll(curDataItems);
        }
        return dataItems;
    }

    private String parseFreshness(Element dataItem){
        Element checkbox = dataItem.selectFirst("div[class=ps_data ps_search-result_data-checkboxes]");
        String color = checkbox.attr("style");
        String freshness = "";
        if(color.equals(RED_COLOR)) {
            freshness = "1";
        } else if(color.equals(YELLOW_COLOR)) {
            freshness = "2";
        } else if(color.equals(BLUE_COLOR)) {
            freshness = "3";
        }
        return freshness;
    }

    private String parseLoadDate(Element dataItem){
        Element currDate = dataItem.selectFirst("div[class=ps_data_load_date__mobile-info]");
        return currDate.select("span").first().text();
    }

    private String parseTownFrom(Element dataItem){
        return dataItem.selectFirst("div[class=ps_data ps_search-result_data-from ps_data-from]").text();
    }

    private String parseTownTo(Element dataItem){
        return dataItem.selectFirst("div[class=ps_data ps_search-result_data-where ps_data-where]").text();
    }

    private String parseId(Element dataItem){
        return dataItem.attr("data-ps-id");
    }

    private String parseProposalDate(Element dataItem){
        Element script = null;
        String stringDate = "";
        try {
            script = dataItem.selectFirst("span[class=ps_data_proposal_date]").selectFirst("script");
            DataNode node = script.dataNodes().get(0);
            long milliseconds = Long.parseLong(node.getWholeData().substring(26, 39));
            Date date = new Date(milliseconds);
            stringDate = new SimpleDateFormat("dd.MM").format(date);
        } catch (Exception e){
            return "";
        }
        return stringDate;
    }

    private String parseProposalTime(Element dataItem){
        Element script = null;
        String stringTime = "";
        try {
            script = dataItem.selectFirst("span[class=ps_data_proposal_time]").selectFirst("script");
            DataNode node = script.dataNodes().get(0);
            long milliseconds = Long.parseLong(node.getWholeData().substring(26, 39));
            Date date = new Date(milliseconds);
            stringTime = new SimpleDateFormat("HH:mm:ss").format(date);
        } catch (Exception e){
            return "Ошибка чтения данных";
        }
        return stringTime;
    }

    private String parsePayment(Element dataItem){
        return dataItem.selectFirst("span[class=ps_data_payment_info]").text();
    }

    private String parsePaymentDetails(Element dataItem){
        return dataItem.selectFirst("span[class=ps_data_payment_details]").text();
    }

    private String parseTransport(Element dataItem){
        return dataItem.selectFirst("span[class=ps_data_transport]").text();
    }

    private Elements parseCargosInfo(Element dataItem){
        return dataItem.select("div[class=ps_data ps_search-result_data-cargo ps_data-cargo]");
    }

    private String parseCargoInfo(Elements cargoInfoList){
        return cargoInfoList.first().text();
    }

    private void parseData(Elements dataItems){
        System.out.println("parseData(Elements dataItems)");
        for (Element dataItem : dataItems) {

            String id = parseId(dataItem);
            String cargoInfo = parseCargoInfo(parseCargosInfo(dataItem));
            String dateTime = parseProposalDate(dataItem) + " - " + parseProposalTime(dataItem);

            List<String> result = new ArrayList<>();

//            result.add(parseFreshness(dataItem));       //Свежесть
            result.add(dateTime);    //Дата созданияRED_COLOR
//            result.add(parseProposalTime(dataItem));    //Время создания
            result.add(parseLoadDate(dataItem));        //Дата загрузки
            result.add(parseTransport(dataItem));       //Транспорт
            result.add(parseTownFrom(dataItem));        //Откуда
            result.add(parseTownTo(dataItem));          //Куда
            result.add(cargoInfo);                      //Груз
            result.add(parsePayment(dataItem));         //Оплата
            result.add(parsePaymentDetails(dataItem));  //Детали оплаты
            result.add(cargoSiteStart + id);            //Страница заявки
            result.add(id);                             //ИД
            result.add(getUrl());                       //Исходная страница

            addRow(result);
        }

//        addRow(fillWithObjectId());
    }

    private List<String> fillWithObjectId(){
        List<String> objectIds = new ArrayList<>();

//        objectIds.add(this.toString());  //Свежесть
        objectIds.add(this.toString());  //Дата создания
//            result.add(parseProposalTime(dataItem));    //Время создания
        objectIds.add(this.toString());  //Дата загрузки
        objectIds.add(this.toString());  //Транспорт
        objectIds.add(this.toString());  //Откуда
        objectIds.add(this.toString());  //Куда
        objectIds.add(this.toString());  //Груз
        objectIds.add(this.toString());  //Оплата
        objectIds.add(this.toString());  //Детали оплаты
        objectIds.add(this.toString());  //Страница заявки
        objectIds.add(this.toString());  //ИД
        objectIds.add(this.toString());  //Исходная страница

        return objectIds;
    }

    private void addRow(List<String> result){
        if(header.size() != result.size()){
            throw new RuntimeException("Размер хедера не соответствует размеру строки");
        }
        parsedDataList.add(result);
    }

    private void parseWithPagination() throws IOException {

        System.out.println("parseWithPagination()");

        initParsedDataList();

        List<Elements> tables = getFullTables();
        Elements dataItems = parseDataItems(tables);
        parseData(dataItems);

    }

    //@Scheduled(fixedRate = FIXED_PARSE_RATE)
    public List<List<String>> getParsedData() throws IOException {
        System.out.println("getParsedData(). url: " + url);
        if(! url.equals("")) {
            System.out.println("----------------------------------------");
            System.out.println("----------------------------------------");
            System.out.println("------------ Parser started ------------");
            System.out.println("----------------------------------------");
            System.out.println("----------------------------------------");
            parseWithPagination();
        }
        return parsedDataList;
    }

    @PostConstruct
    private void postConstruct(){
        System.out.println("postConstruct()");
    }

    @PreDestroy
    private void predestroy(){
        System.out.println("predestroy()");
    }

}
