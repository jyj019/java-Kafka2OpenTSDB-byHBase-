package com.calabar.dec.rds.loader.entry;

import lombok.Data;


@Data
public class PacketData {


    private String src_code;
    private String substd_code;
    private String src_desc;
    private Double src_value;
    private long src_time;
    private int src_data_quality;
    private String std_code;
    private long std_time;
    private Double std_value;
    private int std_data_quality;
    private String set_code;
}
