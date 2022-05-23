package model;

import java.io.Serializable;

public record Person(Long personId,
                     String firstName,
                     String lastName,
                     String patronymic,
                     Group group,
                     Character type) implements Serializable {

}
