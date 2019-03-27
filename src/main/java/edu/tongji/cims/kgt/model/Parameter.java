package edu.tongji.cims.kgt.model;

import java.util.Map;

/**
 * @author Yue Lin
 * @since 2018-12-15
 */

public class Parameter {

    private Map<String, String> props;

    public Parameter(Map<String, String> props) {
        this.props = props;
    }

    public Map<String, String> getProps() {
        return props;
    }

    public void setProps(Map<String, String> props) {
        this.props = props;
    }
}
