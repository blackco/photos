package blackco.photos;

import java.util.ArrayList;
import java.util.HashMap;

public class IdentifyDuplicates {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Services.main(args);
		GetInfo info = new GetInfo();
		String photoSetId = "72157651322092960";

		Search s = new Search();
		s.min_taken_date = "2014-01-01 00:00:00";
		s.max_taken_date = "2014-01-31 23:00:00";


		int i;

		for (i = 0; i < args.length; i++) {
			switch (args[i]) {
			
			case "-photoSetId":
				if (i < args.length)
					photoSetId = args[++i];

			case "-min_date_taken":
				if (i < args.length)
					s.min_taken_date = args[++i];
				break;

			case "-max_date_taken":
				if (i < args.length)
					s.max_taken_date = args[++i];
				break;

			}
		}

		try {

			HashMap<String, ArrayList> map = new HashMap<String, ArrayList>();

			PageSummary summary = Search.search(s);

			System.out.println("IdentifyDuplicates.main(): Search Criteria = "
					+ s);
			System.out.println(summary);

			/*
			 * Build HashMap of photos keyed by date Any key with more than one
			 * photo is a taken at the same time and a potential dupe
			 */

			for (Photo p : summary.photos) {
				GetInfo.getInfo(p.id);

				ArrayList<Photo> list = map.get(p.dateTaken);

				if (list == null) {
					list = new ArrayList<Photo>();
					map.put(p.dateTaken, list);
				}

				list.add(p);

				System.out
						.println("IdentifyDuplicates.main(): Unique timestamps"
								+ map.size());
			}

			/*
			 * 
			 * Iterate through all the photos taken at the same time, If they
			 * are taken on the same camera, they are a duplicate
			 */
			for (ArrayList<Photo> list : map.values()) {

				if (list.size() > 1) {
					HashMap<String, ArrayList<Photo>> camera = 
							new HashMap<String, ArrayList<Photo>>();

					for (Photo p : list) {

						GetExif.getExif(p.id).toString();

						ArrayList<Photo> match = camera.get(p.camera);

						if (match == null) {
							match = new ArrayList<Photo>();
							camera.put(p.camera, match);
						}

						match.add(p);
					}

					System.out.println("Potential Duplicates " + camera);

					for (ArrayList<Photo> match : camera.values()) {

						if (match.size() > 1) {
							for (Photo p : match) {
								PhotoSetAddPhoto.add(p, photoSetId);
							}
						}
					}
				}

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
