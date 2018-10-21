package com.calabar.dec.rds.loader.entry;

import lombok.Data;

import java.util.Map;

@Data
public class KKSEntity {

    private String power_type;
    private String power_code;
    private String power_unit_code;
    private int collect_time;
    private Map<String, PacketData> packeted_data;

}
