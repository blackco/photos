package blackco.photos.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class Search {


	private SearchService service;
	
	@Autowired
	public Search(SearchService service){
		this.service = service;
	}
	
	public PageSummary search(SearchCriteria criteria){
		return service.search(criteria);
	}
}
