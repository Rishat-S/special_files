package ru.netology;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String rootDir = "D:\\Work\\Netology\\src\\javacore\\special_files\\src\\main\\java\\ru\\netology\\";
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        // task1
        List<Employee> employeeListFromCSV = parseCSV(columnMapping, rootDir.concat("data.csv"));
        String json = listToJson(employeeListFromCSV);
        writeString(json, rootDir, "data");
        // task2
        List<Employee> employeeListFromXML = parseXML(rootDir.concat("data.xml"));
        String json2 = listToJson(employeeListFromXML);
        writeString(json2, rootDir, "data2");
        // task3
        List<Employee> employeeListFromJSON = parseJSON(rootDir.concat("data.json"));

        for (Employee employee : employeeListFromJSON) {
            System.out.println(employee);
        }
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> result = new ArrayList<>();
        try (CSVReader csvDataReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> mappingStrategy = new ColumnPositionMappingStrategy<>();
            mappingStrategy.setType(Employee.class);
            mappingStrategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> employeeCsvToBean = new CsvToBeanBuilder<Employee>(csvDataReader)
                    .withMappingStrategy(mappingStrategy)
                    .build();
            result = employeeCsvToBean.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static List<Employee> parseXML(String fileNameXML) {
        List<Employee> result = new ArrayList<>();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(fileNameXML));
            Element root = doc.getDocumentElement();
            NodeList nl = root.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                Node node = nl.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    result.add(
                            new Employee(
                                    Long.parseLong(element.getElementsByTagName("id").item(0).getChildNodes().item(0).getNodeValue()),
                                    element.getElementsByTagName("firstName").item(0).getChildNodes().item(0).getNodeValue(),
                                    element.getElementsByTagName("lastName").item(0).getChildNodes().item(0).getNodeValue(),
                                    element.getElementsByTagName("country").item(0).getChildNodes().item(0).getNodeValue(),
                                    Integer.parseInt(element.getElementsByTagName("age").item(0).getChildNodes().item(0).getNodeValue())
                            )
                    );
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static List<Employee> parseJSON(String fileNameJSON) {
        List<Employee> result = new ArrayList<>();
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        try (FileInputStream fileInputStream = new FileInputStream(fileNameJSON)) {
            JsonReader jsonReader = new JsonReader(new InputStreamReader(fileInputStream));
            Employee[] employees = gson.fromJson(jsonReader, Employee[].class);
            result.addAll(Arrays.asList(employees));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String listToJson(List<Employee> employeeTypeList) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        return gson.toJson(employeeTypeList, listType);
    }

    private static void writeString(String json, String rootDir, String fileName) {
        try (FileWriter file = new FileWriter(rootDir.concat(fileName).concat(".json"))) {
            file.write((json));
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
