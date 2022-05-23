package config;

public class StaticConfig {
    public final static String COMMAND_INFO =
            "help - show available commands,\n" +
            "exit - finish programm,\n" +
            "USER commands:\n" +
                    "groupList `name` (i.e. 754591944-0) - get people from this group\n" +
                    "groupAvg - average marks on groups\n" +
                    "subjectMarks `name` (i.e. Art) - show marks on subject\n" +
                    "teacherMarks `id` (i.e. 20) - get marks given by teacher\n" +
                    "studentMarks `id` (i.e. 23) - get marks of single student\n" +
                    "studentAvg `id` (i.e. 23) - average marks on subjects\n" +
                    "subjectsAvg - get average marks by all subjects\n" +
                    "personGeneralAvg (i.e. 47) - get average mark score over all subjects\n" +
                    "peopleOrderedByMark - all people ordered by avg mark\n" +
                    "peopleFromGroup `id` - all people from group id\n" +
                    "peopleWithMark `value` - all people having some mark\n" +
                    "teachersAvg - get average marks of teachers\n" +
                    "studentsAvg - get average marks of students\n" +
                    "Admin commands:\n" +
                    "addMark `studentId` `subjectId` `teacherId` `value` - add mark\n" +
                    "updateMark `studentId` `subjectId` `teacherId` `value` - update mark\n" +
                    "deleteMark `id` - delete mark by id\n" +
                    "deleteWorstStudent - delete student with the worst mark\n" +
                    "deleteWithAvgMarkLowerThan `value` (i.e. 2) - delete all people with average lower than\n" +
                    "addPerson `firstName` `lastName` `patronymic` `groupId` `type` - add person\n" +
                    "updatePerson `firstName` `lastName` `patronymic` `groupId` `type` - update person\n";
}
