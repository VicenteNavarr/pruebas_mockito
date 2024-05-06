package org.iesvdm.employee;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test doubles that are "fakes" must be tested
 *
 *
 */
public class EmployeeInMemoryRepositoryTest {

	private EmployeeInMemoryRepository employeeRepository;

	private List<Employee> employees;

	@BeforeEach
	public void setup() {
		employees = new ArrayList<>();
		employeeRepository = new EmployeeInMemoryRepository(employees);
	}

	/**
	 * Descripcion del test:
	 * crea 2 Employee diferentes
	 * aniadelos a la coleccion de employees
	 * comprueba que cuando llamas a employeeRepository.findAll
	 * obtienes los empleados aniadidos en el paso anterior
	 */
	@Test
	public void testEmployeeRepositoryFindAll() {

		//creo dos empleados
		Employee empleado1 = new Employee("1", 50.00);
		Employee empleado2 = new Employee("2", 150.00);

		//añado a la lista
		employees.add(empleado1);
		employees.add(empleado2);

		//compruebo
		assertThat(employeeRepository.findAll()).contains(empleado1, empleado2);



	}

	/**
	 * Descripcion del test:
	 * salva un Employee mediante el metodo
	 * employeeRepository.save y comprueba que la coleccion
	 * employees contiene solo ese Employee
	 */
	@Test
	public void testEmployeeRepositorySaveNewEmployee() {

		//creo otro empleado
		Employee empleadoSalvar = new Employee("1", 50.00);

		//lo salvo
		employeeRepository.save(empleadoSalvar);

		//compruebo que está en la lista
		assertThat(employeeRepository.findAll()).contains(empleadoSalvar);



	}

	/**
	 * Descripcion del tets:
	 * crea un par de Employee diferentes
	 * aniadelos a la coleccion de employees.
	 * A continuacion, mediante employeeRepository.save
	 * salva los Employee anteriores (mismo id) con cambios
	 * en el salario y comprueba que la coleccion employees
	 * los contiene actualizados.
	 */
	@Test
	public void testEmployeeRepositorySaveExistingEmployee() {
//reutilizo código:

		//creo dos empleados
		Employee empleado1 = new Employee("1", 50.00);
		Employee empleado2 = new Employee("2", 150.00);

		//añado a la lista
		employees.add(empleado1);
		employees.add(empleado2);

		//compruebo
		System.out.println(employees.toString());

		//seteo salarios
		empleado1.setSalary(300);
		empleado2.setSalary(500);

		//guardo
		employeeRepository.save(empleado1);
		employeeRepository.save(empleado2);


		//compruebo actualización
		assertThat(employees.get(0).getSalary()).isEqualTo(300);
		assertThat(employees.get(1).getSalary()).isEqualTo(500);












	}
}
