package cl.ucn.disc.isof.fivet.domain.service.ebean;

import cl.ucn.disc.isof.fivet.domain.model.Control;
import cl.ucn.disc.isof.fivet.domain.model.Paciente;
import cl.ucn.disc.isof.fivet.domain.model.Persona;
import cl.ucn.disc.isof.fivet.domain.service.BackendService;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.junit.*;
import org.junit.experimental.theories.internal.ParameterizedAssertionError;
import org.junit.rules.Timeout;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase de testing del {@link BackendService}.
 */
@Slf4j
@FixMethodOrder(MethodSorters.DEFAULT)
public class TestEbeanBackendService {

    /**
     * Todos los test deben terminar antes de 60 segundos.
     */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(60);

    /**
     * Configuracion de la base de datos:  h2, hsql, sqlite
     * WARN: hsql no soporta ENCRYPT
     */
    private static final String DB = "h2";

    /**
     * Backend
     */
    private BackendService backendService;

    /**
     * Cronometro
     */
    private Stopwatch stopWatch;

    /**
     * Antes de cada test
     */
    @Before
    public void beforeTest() {

        stopWatch = Stopwatch.createStarted();
        log.debug("Initializing Test Suite with database: {}", DB);

        backendService = new EbeanBackendService(DB);
        backendService.initialize();
    }

    /**
     * Despues del test
     */
    @After
    public void afterTest() {

        log.debug("Test Suite done. Shutting down the database ..");
        backendService.shutdown();

        log.debug("Test finished in {}", stopWatch.toString());
    }

    /**
     * Test de la persona
     */
    @Test
    public void testPersona() {

        final String rut = "1-1";
        final String nombre = "Este es mi nombre";

        // Insert into backend
        {
            final Persona persona = Persona.builder()
                    .nombre(nombre)
                    .rut(rut)
                    .password("durrutia123")
                    .tipo(Persona.Tipo.CLIENTE)
                    .build();

            persona.insert();

            log.debug("Persona to insert: {}", persona);
            Assert.assertNotNull("Objeto sin id", persona.getId());
        }

        // Get from backend v1
        {
            final Persona persona = backendService.getPersona(rut);
            log.debug("Persona founded: {}", persona);
            Assert.assertNotNull("Can't find Persona", persona);
            Assert.assertNotNull("Objeto sin id", persona.getId());
            //Assert.assertEquals("Nombre distintos!", rut, persona.getNombre());
            Assert.assertNotNull("Pacientes null", persona.getPacientes());
            Assert.assertTrue("Pacientes != 0", persona.getPacientes().size() == 0);

            // Update nombre
            persona.setNombre(nombre);
            persona.update();
        }

        // Get from backend v2
        {
            final Persona persona = backendService.getPersona(rut);
            log.debug("Persona founded: {}", persona);
            Assert.assertNotNull("Can't find Persona", persona);
            //Assert.assertEquals("Nombres distintos!", nombre, persona.getNombre());
        }

    }

    /**
     * Test del Paciente
     */
    @Test
    public void testPacientes() {
        //Insertar en el backend
        final Paciente paciente = Paciente.builder()
                .numero(24500)
                .nombre("Diana")
                .sexo(Paciente.Sexo.HEMBRA)
                .Controles(new ArrayList<>())
                .build();
        final Paciente paciente1 = Paciente.builder()
                .numero(24501)
                .nombre("Chocolate")
                .sexo(Paciente.Sexo.MACHO)
                .Controles(new ArrayList<>())
                .build();
        paciente.insert();
        paciente1.insert();
        //Obtener la lista de los pacientes
        List<Paciente> Pacientes = backendService.getPacientes();
        Assert.assertTrue(Pacientes != null);
        Assert.assertTrue("Paciente =! 2", Pacientes.size() == 2);

        // Get from backend
        {
            //Obtener  paciente a partir de su numero de fisha
            final Paciente paciente1bs = backendService.getPaciente(24500);
            log.debug("Paciente founded: {}", paciente1bs);
            Assert.assertNotNull("Can't find Paciente", paciente);
            //verificamos que el paciente obtenido es el que ingresamos
            Assert.assertEquals(paciente.getNumero(), paciente1bs.getNumero());

            //Obtener paciente a partir de su numero de fisha
            final Paciente paciente2bs = backendService.getPaciente(24501);
            log.debug("Paciente founded: {}", paciente2bs);
            Assert.assertNotNull("Can't find Paciente", paciente1);
            //verificamos que el paciente obtenido es el que ingresamos
            Assert.assertEquals(paciente1.getNumero(), paciente2bs.getNumero());
            log.debug("La prueba testPacientes ha finalizado con exito");
        }
    }

    /**
     * Test de obtener pacientes por nombres
     */
    @Test
    public void testPacientesPorNombres(){
        //insertar en el backend
        final Paciente paciente = Paciente.builder()
                .numero(1234)
                .nombre("Mariana")
                .sexo(Paciente.Sexo.HEMBRA)
                .build();

        final Paciente paciente1 = Paciente.builder()
                .numero(4321)
                .nombre("Maria")
                .sexo(Paciente.Sexo.INDETERMINADO)
                .build();

        final Paciente paciente2 = Paciente.builder()
                .numero(2341)
                .nombre("Mariavilla")
                .sexo(Paciente.Sexo.HEMBRA)
                .build();

        paciente.insert();
        paciente1.insert();
        paciente2.insert();

        // Get from backend
        {
            List<Paciente> pacientes = backendService.getPacientesPorNombre("Maria");
            Assert.assertTrue(pacientes != null);
            Assert.assertTrue("Pacientes Maria =! 3",pacientes.size() == 3);
            log.debug("La prueba testPacientesPorNombre ha finalizado con exito");
        }
    }

    /**
     * Test de obtener los controles realizados por el veterinario
     */
    @Test
    public void testControlesVeterinarios() {
        //El veterinario Alam Brito ha realizado 2 controles al paciente con id 666
        //insertar veterinario
        final String rut = "12-1";
        final Persona veterinario = Persona.builder()
                .rut(rut)
                .nombre("Alam Brito")
                .password("1991")
                .tipo(Persona.Tipo.VETERINARIO)
                .build();

        veterinario.insert();

        //insertar paciente
        final Paciente paciente = Paciente.builder()
                .numero(666)
                .nombre("Saza")
                .Controles(new ArrayList<>())
                .build();

        paciente.insert();

        //insertar control
        final Control control = Control.builder()
                .Identificador("345")
                .veterinario(veterinario)
                .build();

        //Agregamos el control al paciente
        this.backendService.agregarControl(control, 666);
        final Control control2 = control.builder()
                .Identificador("540")
                .veterinario(veterinario)
                .build();

        //Agregamos los controles al paciente
        this.backendService.agregarControl(control2, 666);

        //Get from backend
        {
            List<Control> controles = backendService.getControlesVeterinario(rut);
            Assert.assertTrue(controles != null);
            Assert.assertTrue("Controles veterinario rut:12-1  =! 2",controles.size() == 2);
            log.debug("La prueba ha finalizado con exito");
        }






    }




}
