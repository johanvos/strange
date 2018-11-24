package com.gluonhq.strange.cloud;

//import com.gluonhq.connect.converter.InputStreamInputConverter;
//import com.gluonhq.strange.Complex;
//import com.gluonhq.strange.Qubit;
//import com.gluonhq.strange.Result;
//
//import javax.json.Json;
//import javax.json.JsonNumber;
//import javax.json.JsonObject;
//import javax.json.JsonReader;
//import javax.json.JsonValue;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.StringReader;
//import java.util.ArrayList;
//import java.util.List;

public class ResultConverter {
//        extends InputStreamInputConverter<Result> {
//
//    @Override
//    public Result read() {
//        List<Qubit> qubits = new ArrayList<>();
//        InputStreamReader reader = null;
//        try {
//            reader = new InputStreamReader(getInputStream());
//            StringBuilder b = new StringBuilder();
//            char[] buffer = new char[4096];
//            int read;
//            while ((read = reader.read(buffer, 0, 4096)) != -1) {
//                b.append(buffer, 0, read);
//            }
//
//            System.out.println("result = " + b.toString());
//
//            JsonReader jsonReader = Json.createReader(new StringReader(b.toString()));
//            JsonObject jsonResult = jsonReader.readObject();
//            JsonValue jsonQubits = jsonResult.get("qubits");
//            if (jsonQubits.getValueType() == JsonValue.ValueType.ARRAY) {
//                jsonResult.getJsonArray("qubits").getValuesAs(JsonNumber.class)
//                        .stream()
//                        .map(probability -> {
//                            Qubit qubit = new Qubit();
//                            qubit.setProbability(probability.doubleValue());
//                            return qubit;
//                        }).forEach(qubits::add);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        return new Result(qubits.toArray(new Qubit[0]), new Complex[0]);
//    }
}
