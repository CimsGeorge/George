package edu.tongji.cims.kgt.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Yue Lin
 * @version 0.0.1
 */

@Getter
@Setter
public class RequestBody {

    private List<Statement> statements;

    public RequestBody(List<Statement> statements) {
        this.statements = statements;
    }

}
