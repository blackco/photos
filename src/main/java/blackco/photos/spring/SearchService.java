package blackco.photos.spring;


public interface SearchService {

	public PageSummary search(SearchCriteria criteria);
	
	public void download(String path, PageSummary s);

}
