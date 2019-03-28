package edu.tongji.cims.kgt.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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

}
