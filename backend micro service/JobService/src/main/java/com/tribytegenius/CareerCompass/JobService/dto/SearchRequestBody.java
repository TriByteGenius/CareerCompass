package com.tribytegenius.CareerCompass.JobService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequestBody {
    private String website;
    private List<String> type;
    private String location;
    private int time;
}
