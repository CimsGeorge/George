package edu.tongji.cims.kgt.model.neo4j.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yue Lin
 * @since 0.0.1
 */

@Data
public class Row {

    private String label;
    private List<String> attribute = new ArrayList<>();
    private Map<String, String> properties = new HashMap<>();
}
