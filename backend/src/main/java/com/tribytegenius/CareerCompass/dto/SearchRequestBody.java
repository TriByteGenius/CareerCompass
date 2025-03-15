package com.tribytegenius.CareerCompass.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequestBody {
    private String website;
    private List<String> type;
    private String location;
    private int time;
}
