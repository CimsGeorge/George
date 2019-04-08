package edu.tongji.cims.kgt.model.ontology;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Yue Lin
 * @version 0.0.1
 */

@AllArgsConstructor
@Getter
public enum ComponentEnum {

    OWL_THING("owl:Thing"),
    CLASS("class"),
    INDIVIDUAL("individual"),
    RELATIONSHIP("relationship");

    private String name;

}
