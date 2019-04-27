package edu.tongji.cims.kgt.model.ontology;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Yue Lin
 * @version 0.0.1
 */

@AllArgsConstructor
@Getter
public enum RelationshipEnum {

    SUB_CLASS("subClass"),
//    SUB_CLASS("子类"),
    INDIVIDUAL("individual");
//    INDIVIDUAL("实例");

    private String name;

}
