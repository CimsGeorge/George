package edu.tongji.cims.kgt.model;

import edu.tongji.cims.kgt.util.Tokenizer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yue Lin
 * @since 2018-12-15
 */

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

    public Map<String, String> getProps() {
        return props;
    }

    public void setProps(Map<String, String> props) {
        this.props = props;
    }

    // todo 解决本体空格转换成下划线问题，若非空格转化的下划线该如何处理？
    private String standardize(String s) {
        return Tokenizer.replaceUnderlineWithSpace(s);
    }
}
