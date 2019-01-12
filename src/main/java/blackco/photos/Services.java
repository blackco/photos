package blackco.photos;

import java.util.HashMap;


public class Services {

	
		public enum Service{ FLICKR_AUTH, PHOTOS};
		
		private static HashMap<Service, Object> services = new HashMap<Service, Object>();
		
		private static synchronized void initServices(){
			
			if ( services == null){
				services = new HashMap<Service,Object>();
			}
		}
		
		public static HashMap<Service, Object> getServices(){

			if ( services == null){
		
				initServices();
				
			}
			
			return services;
			
		}
		
		public static void addService(Service service, Object obj){
			services.put(service, obj);
		}

		public static Object getService(Service service){
			
			return getServices().get(service);
		}
		
		public static void main(String[] args) {

			String requestKey = null;
			String requestSecret = null;
			String userId = null;
			String accessKey = null;
			String accessSecret = null;
			
			int i;

			for (i = 0; i < args.length; i++) {
				switch (args[i]) {
				

				case "-reqKey":
					if (i < args.length)
						requestKey = args[++i];
					break;

				case "-reqSecret":
					if (i < args.length)
						requestSecret = args[++i];
					break;

				case "-accessKey":
					if ( i< args.length)
						accessKey = args[++i];
					break;
				
				case "-accessSecret":
					if ( i< args.length)
						accessSecret = args[++i];
					break;
					
				case "-userId":
					if (i < args.length)
						userId = args[++i];
					break;
					
				
				}
			}
			
			if ( requestSecret != null &&  requestKey != null && userId != null  
					&& accessKey != null & accessSecret != null){
			
				 Services.addService(Service.FLICKR_AUTH, new FlickrAuth(requestKey, requestSecret
						 , userId, accessKey, accessSecret));	
			} else {
				throw new RuntimeException("Flickr Authentication information is mandatory");
			}
				
			Services.addService(Service.PHOTOS, new Photos());
			
		}
		
		
	
}
