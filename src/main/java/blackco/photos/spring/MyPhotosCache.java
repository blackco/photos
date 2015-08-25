package blackco.photos.spring;

import java.util.HashMap;

public class MyPhotosCache  {
	
		
	public MyPhotosCache(){
		photos = new HashMap<String, HashMap<String,MyPhoto>>();
	}
	
	public HashMap<String, HashMap<String, MyPhoto>> getPhotos() {
		return photos;
	}

	public void setPhotos(HashMap<String, HashMap<String, MyPhoto>> photos) {
		this.photos = photos;
	}

	private HashMap<String, HashMap<String,MyPhoto>> photos;
	
	

}
