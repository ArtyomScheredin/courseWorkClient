package model;

import java.io.Serializable;

public record Mark(int markID,
                   Person student,
                   Subject subject,
                   Person teacher,
                   int value) implements Serializable {
}
