package edu.tongji.cims.kgt.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Yue Lin
 * @version 0.0.1
 */

@AllArgsConstructor
@Data
public class Link {

    private String source;
    private String target;
    private String name;

}
