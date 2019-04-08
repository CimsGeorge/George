package edu.tongji.cims.kgt.model.neo4j.request;

import edu.tongji.cims.kgt.util.Tokenizer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yue Lin
 * @version 0.0.1
 */
@AllArgsConstructor
@Getter
@Setter
public class Parameter {

    private Map<String, String> props;

    public Parameter(String ...props) {
        this.props = new HashMap<>();
        for (int i = 1; i <= props.length; i++)
            this.props.put("prop" + i, standardize(props[i - 1]));
    }

    private String standardize(String s) {
        return Tokenizer.replaceUnderlineWithSpace(s);
    }
}
