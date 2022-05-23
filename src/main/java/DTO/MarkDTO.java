package DTO;

public record MarkDTO(long markId,
                      long studentId,
                      long subjectId,
                      long teacherId,
                      int value) {
    public MarkDTO(long markId, String studentId, String subjectId, String teacherId, String value) {
        this(markId, Integer.parseInt(studentId), Integer.parseInt(subjectId),
                Integer.parseInt(teacherId), Integer.parseInt(value));
    }
}