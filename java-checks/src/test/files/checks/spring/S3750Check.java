import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

@Scope("prototype") // Noncompliant [[sc=1;ec=20]] {{Remove this "@Scope" annotation.}}
@Controller
public class HelloWorld {

}

@Scope("prototype") // Noncompliant [[sc=1;ec=20]] {{Remove this "@Scope" annotation.}}
@Service
class ServiceHelloWorld {

}

@Scope("prototype") // Noncompliant [[sc=1;ec=20]] {{Remove this "@Scope" annotation.}}
@Repository
class RepositoryHelloWorld {

}

@Scope(value = "singleton") // Compliant
@Controller
public class SingletonWithValueController {

}

@Scope(scopeName = "singleton") // Compliant
@Controller
public class SingletonWtihScopeNameController {

}

@Scope() // Compliant
@Controller
public class SingletonDefaultScopeController {

}

@Scope(proxyMode = ScopedProxyMode.DEFAULT) // Compliant
@Controller
public class SingletonProxyModeController {

}
