package edu.tongji.cims.kgt.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yue Lin
 * @version 0.0.1
 */
@Getter
@AllArgsConstructor
public enum RelationEnum {

    OTHER("", 2),
    SUB_CLASS("subClass", 0),
    INSTANCE("instance", 1);

    private String name;
    private int type;

    public static final Map<String, Integer> MAP = new HashMap<>();

    static {
        for (RelationEnum e : values())
            MAP.put(e.name, e.type);
    }
}
