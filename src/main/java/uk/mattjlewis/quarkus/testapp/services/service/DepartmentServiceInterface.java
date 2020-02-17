package uk.mattjlewis.quarkus.testapp.services.service;

import java.util.List;

import uk.mattjlewis.quarkus.testapp.model.Department;
import uk.mattjlewis.quarkus.testapp.model.Employee;

public interface DepartmentServiceInterface {
	String getImplementation();

	Department create(final Department department);

	List<Department> getAll();

	Department get(final int id);

	Department findByName(final String name);

	Department update(final Department department);

	void delete(int id);

	void addEmploye(int departmentId, Employee employee);

	void removeEmployee(int departmentId, int employeeId);
}
