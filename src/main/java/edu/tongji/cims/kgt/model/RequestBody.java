package edu.tongji.cims.kgt.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Yue Lin
 * @since 2018-11-10
 */

@Getter
@Setter
public class RequestBody {

    private List<Statement> statements;

    public RequestBody(List<Statement> statements) {
        this.statements = statements;
    }

}
