package uk.mattjlewis.testapp.services.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import uk.mattjlewis.testapp.model.Department;
import uk.mattjlewis.testapp.model.Employee;
import uk.mattjlewis.testapp.services.jpa.BaseEntityRepository;

@ApplicationScoped
@Default
public class DepartmentService implements DepartmentServiceInterface {
	@PersistenceContext
	EntityManager entityManager;

	@Override
	public String getImplementation() {
		return "Department service - container managed EM & JTA";
	}

	@Override
	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public Department create(final Department department) {
		// Make sure the many to one relationship is set
		if (department.getEmployees() != null) {
			department.getEmployees().forEach(emp -> emp.setDepartment(department));
		}
		return BaseEntityRepository.create(entityManager, department);
	}

	@Override
	@Transactional(Transactional.TxType.SUPPORTS)
	public List<Department> getAll() {
		return BaseEntityRepository.findAll(entityManager, Department.class);
	}

	@Override
	@Transactional(Transactional.TxType.SUPPORTS)
	public Department get(final int id) {
		return BaseEntityRepository.findById(entityManager, Department.class, Integer.valueOf(id));
	}

	@Override
	@Transactional(Transactional.TxType.SUPPORTS)
	public Department findByName(final String name) {
		return entityManager.createNamedQuery("Department.findByName", Department.class).setParameter("name", name)
				.getSingleResult();
	}

	@Override
	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public Department update(final Department department) {
		//return BaseEntityRepository.update(entityManager, department.getId(), department);
		Department current = entityManager.find(Department.class, department.getId(), LockModeType.OPTIMISTIC);

		if (current == null) {
			throw new EntityNotFoundException("Department not found with id " + department.getId());
		}

		System.out.println("current version: " + current.getVersion());
		System.out.println("department version: " + department.getVersion());

		current.setLocation(department.getLocation());
		current.setName(department.getName());
		current.setLastUpdated(new Date());
		current.setVersion(department.getVersion());

		// Do something about the employees?
		//current.setEmployees(department.getEmployees());

		return current;
	}

	@Override
	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public void delete(final int id) {
		BaseEntityRepository.delete(entityManager, Department.class, Integer.valueOf(id));
	}

	@Override
	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public void addEmploye(int departmentId, Employee employee) {
		Department dept = entityManager.find(Department.class, Integer.valueOf(departmentId), LockModeType.OPTIMISTIC);
		if (dept == null) {
			throw new EntityNotFoundException("Department not found for id " + departmentId);
		}
		employee.setDepartment(dept);
		entityManager.persist(employee);
		dept.getEmployees().add(employee);
		dept.setLastUpdated(new Date());
		// FIXME Do I need to do this?
		//entityManager.merge(dept);
	}

	@Override
	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public void removeEmployee(int departmentId, int employeeId) {
		Department dept = entityManager.find(Department.class, Integer.valueOf(departmentId),
				LockModeType.OPTIMISTIC_FORCE_INCREMENT);
		if (dept == null) {
			throw new EntityNotFoundException("Department not found for id " + departmentId);
		}
		Optional<Employee> opt_emp = dept.getEmployees().stream().filter(emp -> emp.getId().intValue() == employeeId)
				.findFirst();
		Employee emp = opt_emp.orElseThrow(() -> new EntityNotFoundException(
				"No such Employee with id " + employeeId + " in department " + departmentId));
		emp.setDepartment(null);
		dept.getEmployees().remove(emp);
		dept.setLastUpdated(new Date());
	}
}
