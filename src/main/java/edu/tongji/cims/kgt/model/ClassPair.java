package edu.tongji.cims.kgt.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.semanticweb.owlapi.model.OWLClass;

/**
 * @author Yue Lin
 * @version 0.0.1
 */

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ClassPair {

    private OWLClass clazz;
    private String name;

}
