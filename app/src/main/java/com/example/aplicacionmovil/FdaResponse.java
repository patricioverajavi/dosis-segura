package com.example.aplicacionmovil;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class FdaResponse {

    @SerializedName("results")
    public List<DrugResult> results;

    public static class DrugResult {

        @SerializedName("openfda")
        public OpenFda openfda;

        @SerializedName("indications_and_usage")
        public List<String> indicationsAndUsage;

        @SerializedName("warnings")
        public List<String> warnings;

        @SerializedName("dosage_and_administration")
        public List<String> dosageAndAdministration;
    }

    public static class OpenFda {

        @SerializedName("brand_name")
        public List<String> brandName;

        @SerializedName("manufacturer_name")
        public List<String> manufacturerName;
    }
}