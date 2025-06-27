
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author tanruidong
 * @date 2022/04/13 14:55
 */
class DepartmentTest {

    public static void main(String[] args) {
        List<Department> allDepartment = new ArrayList<>();
        Department dep1 = new Department(1, 0, "总部");
        Department dep3 = new Department(3, 1, "研发中心");
        Department dep4 = new Department(4, 3, "后端研发组");
        Department dep6 = new Department(6, 4, "后端实习生组");
        Department dep7 = new Department(7, 3, "前端研发组");
        Department dep8 = new Department(8, 1, "产品部");

        allDepartment.add(dep6);
        allDepartment.add(dep7);
        allDepartment.add(dep8);
        allDepartment.add(dep1);
        allDepartment.add(dep3);
        allDepartment.add(dep4);

        List<Department> subDepartments = DepartmentTest.getSub(4, allDepartment);
        for (Department subDepartment : subDepartments) {
            System.out.println(subDepartment);
        }
    }


    /**
     * 返回所有子部门（包括隔代子部门）
     *
     * @param id
     * @param allDepartment
     * @return
     */
    public static List<Department> getSub(int id, List<Department> allDepartment) {
        Map<Integer, List<Department>> allDepartmentByParent = allDepartment.stream().collect(Collectors.groupingBy(Department::getPid));
        List<Department> result = new ArrayList<>();
        Map<Integer, Department> idToDepartmentMap = allDepartment.stream().collect(Collectors.toMap(Department::getId, Function.identity()));
        List<Department> departments = allDepartmentByParent.get(id);
        departments.forEach(department -> getSub(department.getId(), idToDepartmentMap, allDepartmentByParent, result));
        return result;
    }

    private static void getSub(int id, Map<Integer, Department> allDepartment, Map<Integer, List<Department>> allDepartmentByParent, List<Department> result) {
        if (allDepartment.get(id) == null) {
            return;
        }
        Department department = allDepartment.get(id);
        result.add(department);
        List<Department> subDepartments = allDepartmentByParent.get(id);
        if (subDepartments == null || subDepartments.isEmpty()) {
            return;
        }
        for (Department subDepartment : subDepartments) {
            getSub(subDepartment.getId(), allDepartment, allDepartmentByParent, result);
        }
    }

}


class Department {
    /** id */
    private int id;
    /** parent id */
    private int pid;
    /** 名称 */
    private String name;
    public Department(int id, int pid, String name) {
        this.id = id;
        this.pid = pid;
        this.name = name;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getPid() {
        return pid;
    }
    public void setPid(int pid) {
        this.pid = pid;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", pid=" + pid +
                ", name='" + name + '\'' +
                '}';
    }
}
