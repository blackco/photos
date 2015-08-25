package blackco.photos.spring;

import java.util.HashMap;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class Contexts {

	public enum Context{ PHOTOS};
	
	private static HashMap<Context, AnnotationConfigApplicationContext> services 
	= new HashMap<Context, AnnotationConfigApplicationContext>();
	
	public static void addService(Context service, AnnotationConfigApplicationContext obj){
		services.put(service, obj);
	}

	public static AnnotationConfigApplicationContext getContext(Context service){
		
		return services.get(service);
	}
	
	
}
