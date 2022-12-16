# DEPENDENCY
https://github.com/Crispeds/ProcessorManagement/releases/tag/v1.0.0

# auto_controller_implementation
This project aim to create an annotation for implement interface controller for create less java file and error-prone code.

# DOC
The following annotation are available in the project:
## @AutoImplementController(serviceToBind = ...)
This Annotation will bind your controller to the specified service class, for default the controller will call the method of the service class with the same method     name of the controller interface.
## @Mapping(methodCall = "...") E.g. @Mapping(methodCall = "getAdmin(id)")
For bind a method of the class to a specific method call of the service
## @HandleException(exceptionMapper = ...)
To use custom exception handling provided by the project for bind your controller to a specific exception handling using the procided interface ExceptionMapper.

### E.g. 1

<code>
    
    @RestController
    @RequestMapping("/users")
    @AutoImplementController(serviceToBind = ServiceController.class)
    public interface Controller {

        @GetMapping("/{id}")
        String getUser(@PathVariable long id);

    }
    
</code>

Produce:

<code>
    
    @RestController
    @RequestMapping("/users")
    public class ControllerImpl implements Controller {
    
      @Autowired
      private ServiceController serviceController;
    
      @Override
      @GetMapping("/{id}")
      public String getUser(long id) {
          return serviceController.getUser(id);
      }
    
    }
</code>

### E.g. 2

<code>
    
    @RestController
    @RequestMapping("/users")
    @AutoImplementController(serviceToBind = ServiceController.class)
    @HandleException(exceptionMapper = ExceptionMappers.class)
    public interface Controller {

        @GetMapping("/{id}")
        @Mapping(methodCall = "getAdmin(id)")
        String getUser(@PathVariable long id);

    }

    @Service
    public class ExceptionMappers implements ExceptionMapper {
        @Override
        public Map<Class<? extends Exception>, Response> exceptionMapper() {
            Map<Class<? extends Exception>, Response> map = new HashMap<>();
            map.put(IOException.class,new Response(500"IOEXCEPTION"));
            return map;
        }
    }
</code>

Produce:
    
<code>
    
    @RestController
    @RequestMapping("/users")
    public class ControllerImpl implements Controller {
      @Autowired
      private ServiceController serviceController;

      @Autowired
      private ExceptionMappers exceptionMappers;

      @Override
      @GetMapping("/{id}")
      public String getUser(long id) {
        try {
          return serviceController.getAdmin(id);
        } catch (Exception e) {
          ExceptionHandler.handle(exceptionMappers,e);
        }
        return null;
      }
    }
    
</code>
