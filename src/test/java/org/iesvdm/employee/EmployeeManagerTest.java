package org.iesvdm.employee;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmployeeManagerTest {

	@Mock
	private EmployeeRepository employeeRepository;

	@Mock
	private BankService bankService;

	/**
	 * Explica en este comentario que efecto tiene
	 * esta anotacion @InjectMocks:
	 *
	 * "Inyecta" mocks a la clase EmployeeManager para poder hacer las pruebas --> utilizaremos employeeManager
	 *
	 */
	@InjectMocks
	private EmployeeManager employeeManager;

	@Captor
	private ArgumentCaptor<String> idCaptor;

	@Captor
	private ArgumentCaptor<Double> amountCaptor;

	@Spy
	private Employee notToBePaid = new Employee("1", 1000);

	@Spy
	private Employee toBePaid = new Employee("2", 2000);

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Descripcion del test:
	 * Crea un stub when-thenReturn para employeeRepository.findAll
	 * que devuelva una coleccion vacia.
	 * Comprueba que al invocar employeeManagar.payEmployees
	 * con el stub anterior no se paga a ningun empleado.
	 */
	@Test
	public void testPayEmployeesReturnZeroWhenNoEmployeesArePresent() {

		//Creo el mock
		EmployeeRepository employeeRepository1 = mock(EmployeeRepository.class);
		//creo lista cvacía
		List<Employee> lista = new ArrayList<>();

		//retorna lista vacía
		when(employeeRepository1.findAll()).thenReturn(lista);

		//con assertthat
		assertThat(employeeManager.payEmployees()).isEqualTo(0);




	}

	/**
	 * Descripcion del test:
	 * Crea un stub when-thenReturn que devuelva una lista con un empleado
	 * para employeeRepository.findAll.
	 * Comprueba que al invocar employeeManager.payEmployess con el stub
	 * anterior se paga a un empleado.
	 * Tambien comprueba con verify que se hace una llamada a bankService.pay
	 * con los datos de pago del Employ del stub when-thenReturn inicialmente
	 * creado.
	 */
	@Test
	public void testPayEmployeesReturnOneWhenOneEmployeeIsPresentAndBankServicePayPaysThatEmployee() {


		//creo un empleado
		Employee empleado1 = new Employee("1", 50.00);

		when(employeeRepository.findAll()).thenReturn(asList(empleado1));

		//pagamos
		employeeManager.payEmployees();


		//verificamos que el banco paga su sueldo al empleado1
		verify(bankService, times(1)).pay("1", 50.00);


		//verificamos
		assertThat(empleado1.isPaid()).isTrue();

		//No mas interacciones
		verifyNoMoreInteractions(bankService);


	}


	/**
	 * Descripcion del test:
	 * Crea un stub when-thenReturn para employeeRepository.findAll
	 * que devuelva una coleccion con 2 empleados diferentes.
	 * Comprueba que employeeManager.payEmployees paga a 2 empleados.
	 * Verifica la interaccion (con verify) de que se hacen 2 invocaciones
	 * con las caracteristicas de pago de cada Employee que creaste en el stub
	 * primero when-thenReturn.
	 * Por último, verificea que no hay más interacciones con el mock de bankService
	 * -pista verifyNoiMoreInteractions.
	 */
	@Test
	public void testPayEmployeesWhenSeveralEmployeeArePresent() {
//reutilizo codigo:





		//creo un empleado
		Employee empleado1 = new Employee("1", 50.00);
		Employee empleado2 = new Employee("2", 150.00);

		when(employeeRepository.findAll()).thenReturn(asList(empleado1, empleado2));

		//pagamos
		employeeManager.payEmployees();



		//verificamos que el banco paga su sueldo al empleado1
		verify(bankService).pay("1", 50.00);
		verify(bankService).pay("2", 150.00);

		//verificamos
		assertThat(empleado1.isPaid()).isTrue();
		assertThat(empleado2.isPaid()).isTrue();


		// verify(bankService, times(2)).pay(anyString(), anyDouble()); ...para muchos empleados

		//No mas interacciones
		verifyNoMoreInteractions(bankService);

	}

	/**
	 * Descripcion del test:
	 * Crea un stub when-thenReturn para employeeRepository.findAll
	 * que devuelva una coleccion de 2 empleados.
	 * Comprueba que cuando llamas a employeeManager.payEmployee pagas a 2 empleados.
	 * Para el mock de bankService mediante InOrder e inOrder.verify verifica
	 * que se pagan en orden a los 2 empleados con sus caracteristicas invocando
	 * a pay en el orden de la coleccion.
	 * Por ultimo, verifica que despues de pagar no hay mas interacciones.
	 */
	@Test
	public void testPayEmployeesInOrderWhenSeveralEmployeeArePresent() {
	//reutilizo codigo:

		//creo un empleado
		Employee empleado1 = new Employee("1", 50.00);
		Employee empleado2 = new Employee("2", 150.00);

		List<Employee>lista = Arrays.asList(empleado1, empleado2);

		when(employeeRepository.findAll()).thenReturn(lista);

		//pagamos
		assertThat(employeeManager.payEmployees()).isEqualTo(2);



		//verificamos pago en orden -- Necesitamos instanciar un objeto de In Order para comprobar.
		// Si lo hacemos por separado no comprueba el orden -->hacerlo con mockito(no lo sabía)
		InOrder orden = inOrder(bankService);
		orden.verify(bankService).pay("1", 50.00);
		orden.verify(bankService).pay("2", 150.00);

		//verificamos
		assertThat(empleado1.isPaid()).isTrue();
		assertThat(empleado2.isPaid()).isTrue();

		//verificamos no mas interacciones depués de pagar
		verifyNoMoreInteractions(bankService);


		//No mas interacciones
		verifyNoMoreInteractions(bankService);

	}

	/**
	 * Descripcion del test:
	 * Misma situacion que el test anterior solo que al inOrder le aniades tambien employeeRepository
	 * para verificar que antes de hacer el pago bankService.pay para cada empleado
	 * se realiza la invocacion de employeeRepository.findAll.
	 * Pista: utiliza un InOrder inOrder = inOrder(bankService, employeeRepository) para
	 * las verificaciones (verify).
	 */
	@Test
	public void testExampleOfInOrderWithTwoMocks() {

		//creo un empleado
		Employee empleado1 = new Employee("1", 50.00);
		Employee empleado2 = new Employee("2", 150.00);

		List<Employee>lista = Arrays.asList(empleado1, empleado2);

		when(employeeRepository.findAll()).thenReturn(lista);

		//pagamos
		assertThat(employeeManager.payEmployees()).isEqualTo(2);




		//verificamos pago en orden -- Necesitamos instanciar un objeto de In Order para comprobar.
		// Si lo hacemos por separado no comprueba el orden -->hacerlo con mockito(no lo sabía)
		InOrder inOrder = inOrder(employeeRepository, bankService);
		inOrder.verify(employeeRepository).findAll();
		inOrder.verify(bankService).pay("1", 50.00);
		inOrder.verify(bankService).pay("2", 150.00);
		verifyNoMoreInteractions(bankService);

		//verificamos
		assertThat(empleado1.isPaid()).isTrue();
		assertThat(empleado2.isPaid()).isTrue();




	}


	/**
	 * Descripcion del test:
	 * Crea un stub when-thenReturn para employeeRepository.findAll que devuelva
	 * una coleccion con 2 Employee diferentes. Comprueba que employeesManager.payEmployees paga
	 * a 2 Employee.
	 * Seguidamente utiliza los Captor: idCaptor y amountCaptor para capturar todos los
	 * id's y amounts que se han invocado cuando has comprobado que employManager.payEmployees pagaba a 2,
	 * sobre el mock de bankService en un verify para el metodo pay -puedes aniadir cuantas veces se invoco
	 * al metodo pay en el VerificationMode.
	 * Comprueba los valores de los captor accediendo a ellos mediante captor.getAllValues y comparando
	 * con lo que se espera.
	 * Por ultimo verifica que no hay mas interacciones con el mock de bankService.
	 */
	@Test
	public void testExampleOfArgumentCaptor() {

		//creo un empleado
		Employee empleado1 = new Employee("1", 50.00);
		Employee empleado2 = new Employee("2", 150.00);

		List<Employee>lista = Arrays.asList(empleado1, empleado2);

		when(employeeRepository.findAll()).thenReturn(lista);

		//pagamos
		assertThat(employeeManager.payEmployees()).isEqualTo(2);

		//verificamos que el banco paga su sueldo al empleado1
		verify(bankService).pay("1", 50.00);
		verify(bankService).pay("2", 150.00);


		//verificamos
		assertThat(empleado1.isPaid()).isTrue();
		assertThat(empleado2.isPaid()).isTrue();

		//Comprobamos que el captor contiene los dos empleados y los dos salarios

		//Primero verificcamos las veces -- no se porque, pero sin esto, los assert de abajo no funcionan...
		verify(bankService, times(2)).pay(idCaptor.capture(), amountCaptor.capture());

		//comprobamos datos
		assertThat(idCaptor.getAllValues()).contains(empleado1.getId(), empleado2.getId());
		assertThat(amountCaptor.getAllValues()).contains(empleado1.getSalary(), empleado2.getSalary());

		verifyNoMoreInteractions(bankService);

	}
//o
	/**
	 * Descripcion del test:
	 * Utiliza el spy toBePaid de los atributos de esta clase de test para
	 * crear un stub when-thenReturn con 1 solo Employee.
	 * Comprueba que al invocar a employeeManager.payEmployees solo paga a 1 Employee.
	 * Por ultimo, mediante un inOrder para 2 mocks: InOrder inOrder = inOrder(bankService, toBePaid)
	 * verifica que la interaccion se realiza en el orden de bankService.pay las caracteristicas
	 * del Employee toBePaid y a continuacion verifica tambien que se invoca toBePaid.setPaid true.
	 */
	@Test
	public void testEmployeeSetPaidIsCalledAfterPaying() {

		//Preguntar, el erro me decía que tobepaid no era un mock..pues lo hago mock
		Employee toBePaid = mock(Employee.class);


		//doy valor a los atributos
		when(toBePaid.getId()).thenReturn("2");
		when(toBePaid.getSalary()).thenReturn(2000.0);

		//lista
		List<Employee>lista = Arrays.asList(toBePaid);

		//compruebo lista
		when(employeeRepository.findAll()).thenReturn(lista);

		//aseguramos un solo pago
		assertThat(employeeManager.payEmployees()).isEqualTo(1);

		//orden
		InOrder inOrder = inOrder(bankService, toBePaid);

		inOrder.verify(bankService).pay("2", 2000);
		inOrder.verify(toBePaid).setPaid(true);

		verifyNoMoreInteractions(bankService);

	}


	/**
	 * Descripcion del test:
	 * Crea un stub when-thenReturn para employeeRepository.findAll que devuelva
	 * una coleccion solo con el spy de atributo de la clase notToBePaid.
	 * Seguidamente, crea un stub doThrow-when para bankService.pay con ArgumentMatcher
	 * any como entradas para el metodo pay. La exception a lanzar sera una RuntimeException
	 * Comprueba que cuando invocas employeeManager.payEmployees con bankService lanzando
	 * una RuntimeException en el stub anterior, los Employee pagados son 0.
	 * Tambien, verifica sobre el spy notToBePaid que se llamo a setPaid false como
	 * efecto de no pago.
	 *
	 */
	@Test
	public void testPayEmployeesWhenBankServiceThrowsException() {

		//hacemos lo mismo, pero con el not
		Employee notToBePaid = mock(Employee.class);


		//doy valor a los atributos
		when(notToBePaid.getId()).thenReturn("2");
		when(notToBePaid.getSalary()).thenReturn(2000.0);

		//lista
		List<Employee>lista = Arrays.asList(toBePaid);

		//compruebo lista
		when(employeeRepository.findAll()).thenReturn(lista);

		//Arrroja excepción si pago -- con lo del  argumentmatcher se referia en este caso a los any
		doThrow(new RuntimeException()).when(bankService).pay(anyString(), anyDouble());

		//ejecutamos para ver la exc.
		employeeManager.payEmployees();

		assertThat(notToBePaid.isPaid()).isEqualTo(false);



	}

	/**
	 * Descripcion del test:
	 * 	Crea un stub when-thenReturn para employeeRepository.findAll que devuelva
	 * 	una coleccion 2 Employee con el spy de atributo de la clase notToBePaid y toBePaid.
	 * 	Seguidamente, crea un stub con encademaniento para 2 llamadas doThrow.doNothing-when
	 * 	para bankService.pay de modo que en la primera invocacion de pay (para notToBePaid) se lance una RuntimeException
	 * 	y en la segunda invocacion de pay (para toBePaid) no haga nada. El metodo pay acepta cualquier argumento
	 * 	indicado mediante ArgumentMatcher any.
	 * 	Comprueba que al invocar employeeManager.payEmployees se paga a solo 1 Employee.
	 *  A continuacion, verifica las interacciones (verify) sobre el spy notToBePaid primer mock de la coleccion
	 *  para el que se lanza la RuntimeException y el spy toBePaid segundo mock de la coleccion que si recibe el pago
	 *  chequeando la interaccion con el metodo setPaid a false y true respectivamente.
	 */
	@Test
	public void testOtherEmployeesArePaidWhenBankServiceThrowsException() {

		//reutilizacion codigo

		Employee toBePaid = mock(Employee.class);

		when(toBePaid.getId()).thenReturn("1");
		when(toBePaid.getSalary()).thenReturn(2000.0);

		Employee notToBePaid = mock(Employee.class);

		when(notToBePaid.getId()).thenReturn("2");
		when(notToBePaid.getSalary()).thenReturn(3000.0);


		//lista
		List<Employee>lista = Arrays.asList(toBePaid, notToBePaid);

		//compruebo lista
		when(employeeRepository.findAll()).thenReturn(lista);



		//exc para el nottobepaid
		doThrow(new RuntimeException()).when(bankService).pay(eq("2"), anyDouble());
		//ninguna exc para el tobepaid
		doNothing().when(bankService).pay((eq("1")), anyDouble());


		// Llam0
		int paidEmployees = employeeManager.payEmployees();

		// verifico solo un pago
		assertThat(paidEmployees).isEqualTo(1);


		verify(notToBePaid).setPaid(false);
		verify(toBePaid).setPaid(true);



	}


	/**
	 * Descripcion del test:
	 * 	Crea un stub when-thenReturn para employeeRepository.findAll que devuelva
	 * 	una coleccion 2 Employee con el spy de atributo de la clase notToBePaid y toBePaid.
	 * 	Seguidamente, crea un stub con encademaniento para 2 llamadas doThrow-when emplea argThat
	 *  argThat(s -> s.equals("1")), anyDouble como firma de invocacion en el stub para pay
	 * 	de modo que en la primera invocacion de pay (para notToBePaid) se lance una RuntimeException
	 * 	y en la segunda invocacion de pay (para toBePaid) no haga nada. El metodo pay acepta cualquier argumento
	 * 	indicado mediante ArgumentMatcher any.
	 * 	Comprueba que al invocar employeeManager.payEmployees se paga a solo 1 Employee.
	 *  A continuacion, verifica las interacciones (verify) sobre el spy notToBePaid primer mock de la coleccion
	 *  para el que se lanza la RuntimeException y el spy toBePaid segundo mock de la coleccion que si recibe el pago
	 *  chequeando la interaccion con el metodo setPaid a false y true respectivamente.
	 */
	@Test
	public void testArgumentMatcherExample() {

		//reutilizacion codigo

		Employee toBePaid = mock(Employee.class);

		when(toBePaid.getId()).thenReturn("1");
		when(toBePaid.getSalary()).thenReturn(2000.0);

		Employee notToBePaid = mock(Employee.class);

		when(notToBePaid.getId()).thenReturn("2");
		when(notToBePaid.getSalary()).thenReturn(3000.0);


		//lista
		List<Employee>lista = Arrays.asList(toBePaid, notToBePaid);

		//compruebo lista
		when(employeeRepository.findAll()).thenReturn(lista);

		doThrow(new RuntimeException()).when(bankService).pay(argThat(s -> s.equals("2")), anyDouble());
		doNothing().when(bankService).pay(argThat(s -> s.equals("1")), anyDouble());


		int paidEmployees = employeeManager.payEmployees();


		assertThat(paidEmployees).isEqualTo(1);


		verify(notToBePaid).setPaid(false);
		verify(toBePaid).setPaid(true);


	}

}
