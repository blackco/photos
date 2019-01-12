package blackco.photos;

import java.util.Date;


public class Photo  {
	
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


	public String getDateTaken() {
		return dateTaken;
	}


	public void setDateTaken(String dateTaken) {
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


	public Photo getMatchedPhoto() {
		return matchedPhoto;
	}


	public void setMatchedPhoto(Photo matchedPhoto) {
		this.matchedPhoto = matchedPhoto;
	}


	public String owner;
	public String secret;
	public String server;
	public int farm;
	public String title;
	public int ispublic;
	public int isfamily;
	public String camera;
	public String dateTaken;
	public String originalFileName;
	public Date originalDateTaken;
	public Photo matchedPhoto;
	
	
	public String toString(){
		return "id=" + id +" ,title=" + title + ",camera="+ camera + ", taken=" 
				+ dateTaken + ",origFileName=" + originalFileName + ",matchedTo= " + matchedPhoto;
	}

}
