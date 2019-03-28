package edu.tongji.cims.kgt.model;

import edu.tongji.cims.kgt.util.Tokenizer;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yue Lin
 * @version 0.0.1
 */

@Getter
@Setter
public class QueryProp {

    private Map<String, String> props;

    public QueryProp(String name) {
        props = new HashMap<>();
        props.put("name", standardize(name));
    }

    public QueryProp(String param1, String param2) {
        props = new HashMap<>();
        props.put("param1", standardize(param1));
        props.put("param2", standardize(param2));
    }

    public QueryProp(String from, String rel, String to) {
        props = new HashMap<>();
        props.put("from", standardize(from));
        props.put("rel", standardize(rel));
        props.put("to", standardize(to));
    }

    private String standardize(String s) {
        return Tokenizer.replaceUnderlineWithSpace(s);
    }
}
