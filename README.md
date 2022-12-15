# auto_controller_implementation
Constraint:
    - I metodi richiamati dalla implentazione avranno il nome:
        - uguale a quello dell'interface del controller (default).
        - in caso di presenza della annotazione mapping avranno il nome specificato.
    - Sarà possibile specificare un metodo con @Before e @After da eseguire prima o
      dopo il richiamo del service (default non è presente).
    - Vi sarà un handle delle eccezioni attraverso la annotazione a livello di classe
      @Manage(Exception = eccezione; HTTP_CODE = codice di risposta; msg = Mesaggio contenuto
      nella risposta).