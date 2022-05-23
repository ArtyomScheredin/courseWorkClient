package DTO;

import java.io.Serializable;

public record PersonDTO(Long personId,
                        String firstName,
                        String lastName,
                        String patronymic,
                        Long groupId,
                        Character type) implements Serializable {
}
