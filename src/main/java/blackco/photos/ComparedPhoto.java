package blackco.photos;
public class ComparedPhoto {

	private Photo onDisk;
	public Photo getOnDisk() {
		return onDisk;
	}

	public void setOnDisk(Photo onDisk) {
		this.onDisk = onDisk;
	}

	public Photo getOnFlickr() {
		return onFlickr;
	}

	public void setOnFlickr(Photo onFlickr) {
		this.onFlickr = onFlickr;
	}

	public boolean isLookedForMatch() {
		return lookedForMatch;
	}

	public void setLookedForMatch(boolean lookedForMatch) {
		this.lookedForMatch = lookedForMatch;
	}

	private Photo onFlickr;
	private boolean lookedForMatch = false;
	
	public String toString(){
		return "Processed="+ lookedForMatch + ",onDisk=" + onDisk + ",onFlickr=" + onFlickr;
	}

}
