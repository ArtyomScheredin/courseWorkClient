package model;

import java.io.Serializable;

public record Subject(int subjectId,
                      String name) implements Serializable {

}