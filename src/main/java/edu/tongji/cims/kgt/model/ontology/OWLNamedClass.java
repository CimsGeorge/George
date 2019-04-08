package edu.tongji.cims.kgt.model.ontology;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.semanticweb.owlapi.model.OWLClass;

/**
 * @author Yue Lin
 * @version 0.0.1
 */

@AllArgsConstructor
@Getter
@Setter
@ToString
public class OWLNamedClass {

    private String className;
    private OWLClass owlClass;

}
