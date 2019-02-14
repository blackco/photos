package blackco.photos.spring;

import java.util.Date;

//import blackco.photos.Photo;

public class FlickrPhoto {
		
		public static String UNPROCESSED = "UNPROCESSED";
		
		public static String UNMATCHED = "UNMATCHED";
		
		public static String INSUFFICIENT_METADATA_NO_DATE = "INSUFFICIENT_METADATA_NO_DATE";
		
		public static String INSUFFICIENT_METADATA_NO_CAMERA = "INSUFFICIENT_METADATA_NO_CAMERA";
		
	
		public FlickrPhoto(){
			
		}
		
		public FlickrPhoto(String filename){
			setTitle(filename);
			id = filename;
		}
		
		public String id;
		public String getId() {
			return id;
		}


		public void setId(String id) {
			this.id = id;
		}


		public String getOwner() {
			return owner;
		}


		public void setOwner(String owner) {
			this.owner = owner;
		}


		public String getSecret() {
			return secret;
		}


		public void setSecret(String secret) {
			this.secret = secret;
		}


		public String getServer() {
			return server;
		}


		public void setServer(String server) {
			this.server = server;
		}


		public int getFarm() {
			return farm;
		}


		public void setFarm(int farm) {
			this.farm = farm;
		}


		public String getTitle() {
			return title;
		}


		public void setTitle(String title) {
			this.title = title;
		}


		public int getIspublic() {
			return ispublic;
		}


		public void setIspublic(int ispublic) {
			this.ispublic = ispublic;
		}


		public int getIsfamily() {
			return isfamily;
		}


		public void setIsfamily(int isfamily) {
			this.isfamily = isfamily;
		}


		public String getCamera() {
			return camera;
		}


		public void setCamera(String camera) {
			this.camera = camera;
		}


		public Date getDateTaken() {
			return dateTaken;
		}


		public void setDateTaken(Date dateTaken) {
			this.dateTaken = dateTaken;
		}


		public String getOriginalFileName() {
			return originalFileName;
		}


		public void setOriginalFileName(String originalFileName) {
			this.originalFileName = originalFileName;
		}


		public Date getOriginalDateTaken() {
			return originalDateTaken;
		}


		public void setOriginalDateTaken(Date originalDateTaken) {
			this.originalDateTaken = originalDateTaken;
		}

		public String getDownloadUrl(){ return downloadUrl;}

		public void setDownloadUrl(String _downloadUrl){ this.downloadUrl = _downloadUrl;}




		public String owner;
		public String secret;
		public String server;
		public int farm;
		public String title;
		public int ispublic;
		public int isfamily;
		public String camera;
		public Date dateTaken;
		public String originalFileName;
		public Date originalDateTaken;
		public boolean downloaded;
		public String downloadUrl;

		
		
		public String toString(){
			return "id=" + id +" ,title=" + title + ",camera="+ camera + ", taken=" 
					+ dateTaken + ",origFileName=" + originalFileName ;
		}
		
		
}
