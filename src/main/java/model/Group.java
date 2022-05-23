package model;

import java.io.Serializable;

public record Group(Long groupId, String name) implements Serializable {
}
